package engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

class CooldownTest {
    private static final int TEST_COOLDOWN = 100;
    private Cooldown testCooldown;

    @BeforeEach
    void setUp() {
        testCooldown = new Cooldown(TEST_COOLDOWN);
    }

    @Test
    void checkFinished() throws InterruptedException {
        sleep(TEST_COOLDOWN);
        assertTrue(testCooldown.checkFinished());
    }

    @Test
    void reset() {
        testCooldown.reset();
        assertFalse(testCooldown.checkFinished());
    }
}