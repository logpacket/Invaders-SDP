package engine.network;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NetworkManagerTest {
    private NetworkManager networkManager;

    @BeforeEach
    void setUp() {
        networkManager = NetworkManager.getInstance();
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
        long expectedLatency = 100;

        long sendTime = System.currentTimeMillis() - expectedLatency;
        Ping testPing = new Ping(sendTime);
        Event pingEvent = new Event("ping", testPing, Status.OK, System.currentTimeMillis());

        // run dispatch
        //networkManager.dispatch(pingEvent);

    }
}