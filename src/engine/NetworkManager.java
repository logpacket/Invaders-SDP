package engine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

// NetworkManager 클래스
public final class NetworkManager {
    private static NetworkManager instance;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    PrintWriter writer;
    Scanner reader;

    private final Map<String, EventHandler> eventHandlers = new HashMap<>();

    private NetworkManager() {
        try {
            socket = new Socket("localhost", 8080);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            eventHandlers.put("message", new MessageEventHandler());
            eventHandlers.put("error", new ErrorEventHandler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static NetworkManager getInstance() {
        if (instance == null)
            instance = new NetworkManager();

        return instance;
    }

    public void sendRequest() {

    }

    public void listen() {

    }

    private void dispatchEvent(String message) {
        if (message.startsWith("error:")) {
            eventHandlers.get("error").handleEvent(inputStream);
        } else {
            eventHandlers.get("message").handleEvent(inputStream);
        }
    }

    public void close() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerEventHandler(String key, EventHandler handler) {
        eventHandlers.put(key, handler);
    }
}
