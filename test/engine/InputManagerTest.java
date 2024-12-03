package engine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.awt.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.*;

class InputManagerTest {

    private InputManager inputManager;

    @BeforeEach
    void setUp() {
        inputManager = InputManager.getInstance();
    }

    @Test
    void getInstance_ReturnsTheSameInstance() {
        InputManager firstInstance = InputManager.getInstance();
        InputManager SecondInstance = InputManager.getInstance();

        assertSame(firstInstance, SecondInstance, "getInstance should return the same instance");
    }

    @Test
    void isKeyDown_InitiallyFalse() {
        assertFalse(inputManager.isKeyDown(KeyEvent.VK_A));
        assertFalse(inputManager.isKeyDown(KeyEvent.VK_SPACE));
    }

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

    @Test
    void keyTyped_DoesNothing() {
        // Not implemented since we don't need to handle typed key events for this input manager
        KeyEvent keyEvent = new KeyEvent(
                new java.awt.Component(){},
                KeyEvent.KEY_TYPED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_A,
                'A'
        );

        boolean initialState = inputManager.isKeyDown(KeyEvent.VK_A);
        inputManager.keyTyped(keyEvent);
        assertEquals(initialState, inputManager.isKeyDown(KeyEvent.VK_A),
                "keyTyped should not change key state");
    }
}