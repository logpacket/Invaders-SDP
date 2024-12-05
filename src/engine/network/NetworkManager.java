package engine.network;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import engine.Core;
import message.Ping;
import org.reflections.Reflections;
import org.slf4j.LoggerFactory;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class NetworkManager {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(NetworkManager.class);
    private static NetworkManager instance;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private final Logger logger = Core.getLogger();
    private ObjectMapper mapper;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final Map<String, EventHandler> eventHandlers = new HashMap<>();
    private static long latency = 0L;
    private final Set<UUID> requestSet = new HashSet<>();

    private NetworkManager() {
        try {
            socket = new Socket("localhost", 1105);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            mapper = new ObjectMapper();
            mapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
            mapper.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);

            Reflections reflections = new Reflections("message");
            for (Class<? extends Body> bodyClass : reflections.getSubTypesOf(Body.class)) {
                mapper.registerSubtypes(bodyClass);
            }

            eventHandlers.put("ping", event -> {
                latency = System.currentTimeMillis() - ((Ping) event.body()).sendTimestamp();
                logger.info("Network latency: " + latency + "ms");
            });

            executor.execute(this::listen);
            executor.execute(this::trackLatency);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Network IO Exception", e);
            // Server connection failed
            showErrorPopup("Failed to connect to the server. Please check your connection and try again.");
        }
    }

    public static NetworkManager getInstance() {
        if (instance == null)
            instance = new NetworkManager();
        return instance;
    }

    public static long getLatency() {
        return latency;
    }

    private void dispatch(Event event) {
        requestSet.remove(event.id());
        eventHandlers.get(event.name()).handle(event);
    }

    private void listen() {
        try {
            while (socket.isConnected()) {
                if (reader.ready()) {
                    Event event = mapper.readValue(reader, Event.class);
                    if (!event.name().equals("ping"))
                        logger.info("Received event: " + event);
                    dispatch(event);
                }
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            logger.log(Level.WARNING, "Packet receive failed", e);
            showErrorPopup("Connection lost. Please try reconnecting.");
        }
    }

    private void trackLatency() {
        while (socket.isConnected()) {
            sendEvent("ping", new Ping(System.currentTimeMillis()));
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                logger.log(Level.WARNING, "Ping thread interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    public void registerEventHandler(String key, EventHandler handler) {
        eventHandlers.put(key, handler);
    }

    public UUID sendEvent(String eventName, Body body) {
        UUID requestId = UUID.randomUUID();
        requestSet.add(requestId);
        Event event = new Event(eventName, body, requestId, System.currentTimeMillis());
        executor.execute(() -> {
            try {
                mapper.writeValue(writer, event);
                if (!eventName.equals("ping"))
                    logger.info("Event sent: " + eventName);
            } catch (IOException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                logger.log(Level.WARNING, "Packet send failed", e);
                showErrorPopup("Failed to send data to the server.");
            }
        });
        return requestId;
    }

    public boolean isDone(UUID requestId) {
        return !requestSet.contains(requestId);
    }

    public boolean isRequested(UUID requestId) {
        return requestSet.contains(requestId);
    }

    public void close() {
        try {
            executor.shutdown();
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Network IO Exception", e);
        }
    }

    // Error pop-up
    private void showErrorPopup(String message) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, message, "Connection Error", JOptionPane.ERROR_MESSAGE));
    }
}
