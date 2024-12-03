package engine.network;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class NetworkManagerTest {
    private NetworkManager networkManager;
    private Method dispatchMethod;
    private Field eventHandlersField;

    @BeforeEach
    void setUp() {
        try {
            networkManager = NetworkManager.getInstance();

            dispatchMethod = NetworkManager.class.getDeclaredMethod("dispatch", Event.class);
            dispatchMethod.setAccessible(true);

            eventHandlersField = NetworkManager.class.getDeclaredField("eventHandlers");
            eventHandlersField.setAccessible(true);

            @SuppressWarnings("unchecked")
            Map<String, EventHandler> handlers = (Map<String, EventHandler>) eventHandlersField.get(networkManager);
            handlers.put("ping", event -> {
                long latency = System.currentTimeMillis() - ((Ping) event.body()).sendTimestamp();
                try {
                    Field latencyField = NetworkManager.class.getDeclaredField("latency");
                    latencyField.setAccessible(true);
                    latencyField.setLong(null, latency);
                } catch (Exception e) {
                    fail("Failed to set latency: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            fail("Test setup failed: " + e.getMessage());
        }
    }

    @org.junit.jupiter.api.Test
    void getInstance() {
    }

    @org.junit.jupiter.api.Test
    void getLatency() {
    }

    @org.junit.jupiter.api.Test
    void registerEventHandler() {
    }

    @org.junit.jupiter.api.Test
    void sendEvent() {
    }

    @org.junit.jupiter.api.Test
    void close() {
    }

    @org.junit.jupiter.api.Test
    void pingTest() {
        try {
            long initialLatency = NetworkManager.getLatency();

            long expectedLatency = 100;

            long sendTime = System.currentTimeMillis() - expectedLatency;
            Ping testPing = new Ping(sendTime);
            Event pingEvent = new Event("ping", testPing, Status.OK, System.currentTimeMillis());

            dispatchMethod.invoke(networkManager, pingEvent);

            assertNotEquals(initialLatency, NetworkManager.getLatency(),
                    "Latency should be updated after ping");

            long actualLatency = NetworkManager.getLatency();
            assertTrue(Math.abs(actualLatency - expectedLatency) <= 10,
                    "Expected latency around " + expectedLatency + "ms, but got " + actualLatency + "ms");

        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }
}