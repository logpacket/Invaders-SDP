package engine;

import org.junit.jupiter.api.*;

import java.awt.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InputManagerTest {

    private InputManager inputManager;

    @BeforeEach
    void setUp() {
        inputManager = InputManager.getInstance();
    }

    @Order(1)
    @Test
    void getInstance_ReturnsTheSameInstance() {
        InputManager firstInstance = InputManager.getInstance();
        InputManager SecondInstance = InputManager.getInstance();

        assertSame(firstInstance, SecondInstance, "getInstance should return the same instance");
    }

    @Order(2)
    @Test
    void isKeyDown_InitiallyFalse() {
        assertFalse(inputManager.isKeyDown(KeyEvent.VK_A));
        assertFalse(inputManager.isKeyDown(KeyEvent.VK_SPACE));
    }

    @Order(3)
    @Test
    void keyPressed_UpdatesKeyState() {
        KeyEvent keyEvent = new KeyEvent(
                new java.awt.Component(){}, // Dummy component
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_A,
                'A'
        );

        inputManager.keyPressed(keyEvent);
        assertTrue(inputManager.isKeyDown(KeyEvent.VK_A), "Key should be marked as pressed");
    }

    @Order(4)
    @Test
    void keyReleased_UpdatesKeyState() {
        KeyEvent pressEvent = new KeyEvent(
                new java.awt.Component(){},
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_A,
                'A'
        );
        inputManager.keyPressed(pressEvent);

        // Then test key release
        KeyEvent releaseEvent = new KeyEvent(
                new java.awt.Component(){},
                KeyEvent.KEY_RELEASED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_A,
                'A'
        );
        inputManager.keyReleased(releaseEvent);

        assertFalse(inputManager.isKeyDown(KeyEvent.VK_A), "Key should be marked as released");
    }

    @Order(5)
    @Test
    void keyPressed_OutOfBoundsKey_DoesNotThrowException() {
        // Test handling of key codes outside valid range
        KeyEvent keyEvent = new KeyEvent(
                new java.awt.Component(){},
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0,
                999, // Invalid key code
                'X'
        );

        assertDoesNotThrow(() -> inputManager.keyPressed(keyEvent));
    }
}