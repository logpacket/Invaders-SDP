package engine.network;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.net.ConnectException;
import java.util.Map;
import java.util.UUID;

import message.Ping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        }
        catch (Exception e) {
            fail("Test setup failed: " + e.getMessage());
        }
    }

    @org.junit.jupiter.api.Test
    void pingTest() {
        try {
            long initialLatency = networkManager.getLatency();

            long expectedLatency = 100;

            long sendTime = System.currentTimeMillis() - expectedLatency;
            Ping testPing = new Ping(sendTime);
            UUID id = UUID.randomUUID();
            Event pingEvent = new Event("ping", testPing, id, System.currentTimeMillis());

            dispatchMethod.invoke(networkManager, pingEvent);

            assertNotEquals(initialLatency, networkManager.getLatency(),
                    "Latency should be updated after ping");

            long actualLatency = networkManager.getLatency();
            assertTrue(Math.abs(actualLatency - expectedLatency) >= 1,
                    "Expected latency around " + expectedLatency + "ms, but got " + actualLatency + "ms");

        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }

    @Test
    void showErrorPopup_shouldShowErrorPopupWithoutException() throws Exception {
        NetworkManager networkManager = NetworkManager.getInstance();

        Method method = NetworkManager.class.getDeclaredMethod("showErrorPopup", String.class);
        method.setAccessible(true);

        assertDoesNotThrow(() -> method.invoke(networkManager, "Test Error"));
    }

}