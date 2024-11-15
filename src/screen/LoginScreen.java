package screen;

import engine.*;

import java.awt.event.KeyEvent;

public class LoginScreen extends Screen {

    /** Milliseconds between changes in user selection. */
    private static final int SELECTION_TIME = 200;
    private static final int ALERT_TIME = 1500;

    /** Singleton instance of SoundManager */
    private final SoundManager soundManager = SoundManager.getInstance();

    /** Login input fields */
    private String usernameInput;
    private String passwordInput;
    private boolean isUsernameActive;
    private boolean isPasswordActive;

    /** Time until alert message disappears */
    private Cooldown selectionCooldown;
    private Cooldown alertCooldown;

    /** Option selected (0 = Username Input, 1 = Password Input, 2 = Login Button, 3 = Sign Up Button) */
    private int selectedOption;

    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param width  Screen width.
     * @param height Screen height.
     * @param fps    Frames per second, frame rate at which the game is run.
     */
    public LoginScreen(final int width, final int height, final int fps) {
        super(width, height, fps);

        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.usernameInput = "";
        this.passwordInput = "";
        this.isUsernameActive = true;
        this.isPasswordActive = false;
        this.alertCooldown = Core.getCooldown(ALERT_TIME);
        this.selectedOption = 0;
        this.menu = Menu.LOGIN;

        if (!soundManager.isSoundPlaying(Sound.BGM_LOGIN))
            soundManager.loopSound(Sound.BGM_LOGIN);
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected final void update() {
        super.update();
        draw();
        handleInput();
    }

    /**
     * Handles user input for the login screen.
     */
    private void handleInput() {
        if (this.selectionCooldown.checkFinished() && this.inputDelay.checkFinished()) {
            if (inputManager.isKeyDown(KeyEvent.VK_UP)) {
                previousMenuItem();
                this.selectionCooldown.reset();
                soundManager.playSound(Sound.MENU_MOVE);
            }
            if (inputManager.isKeyDown(KeyEvent.VK_DOWN)) {
                nextMenuItem();
                this.selectionCooldown.reset();
                soundManager.playSound(Sound.MENU_MOVE);
            }

            if (selectedOption == 0) {
                isUsernameActive = true;
                isPasswordActive = false;
            } else if (selectedOption == 1) {
                isUsernameActive = false;
                isPasswordActive = true;
            } else {
                isUsernameActive = false;
                isPasswordActive = false;
            }

            if (selectedOption != 2 && selectedOption != 3) {
                handleTextInput();
            }

            if (inputManager.isKeyDown(KeyEvent.VK_ENTER)) {
                handleEnterKey();
            }
        }
    }

    /**
     * Handles the text input for username and password.
     */
    private void handleTextInput() {
        for (int keyCode = KeyEvent.VK_A; keyCode <= KeyEvent.VK_Z; keyCode++) {
            if (inputManager.isKeyDown(keyCode)) {
                if (isUsernameActive && usernameInput.length() < 20) {
                    usernameInput += (char) keyCode;
                    soundManager.playSound(Sound.MENU_TYPING);
                } else if (isPasswordActive && passwordInput.length() < 20) {
                    passwordInput += (char) keyCode;
                    soundManager.playSound(Sound.MENU_TYPING);
                }
                this.selectionCooldown.reset();
            }
        }

        for (int keyCode = KeyEvent.VK_0; keyCode <= KeyEvent.VK_9; keyCode++) {
            if (inputManager.isKeyDown(keyCode)) {
                if (isUsernameActive && usernameInput.length() < 20) {
                    usernameInput += (char) (keyCode);
                    soundManager.playSound(Sound.MENU_TYPING);
                } else if (isPasswordActive && passwordInput.length() < 20) {
                    passwordInput += (char) (keyCode);
                    soundManager.playSound(Sound.MENU_TYPING);
                }
                this.selectionCooldown.reset();
            }
        }

        if (inputManager.isKeyDown(KeyEvent.VK_BACK_SPACE)) {
            if (isUsernameActive && !usernameInput.isEmpty()) {
                usernameInput = usernameInput.substring(0, usernameInput.length() - 1);
                soundManager.playSound(Sound.MENU_TYPING);
            } else if (isPasswordActive && !passwordInput.isEmpty()) {
                passwordInput = passwordInput.substring(0, passwordInput.length() - 1);
                soundManager.playSound(Sound.MENU_TYPING);
            }
            this.selectionCooldown.reset();
        }
    }

    /**
     * Handles the ENTER key functionality based on the selected option.
     */
    private void handleEnterKey() {
        if (selectedOption == 0) {
            isUsernameActive = true;
            isPasswordActive = false;
        } else if (selectedOption == 1) {
            isUsernameActive = false;
            isPasswordActive = true;
        } else if (selectedOption == 2) {
            if (validateLogin()) {
                soundManager.playSound(Sound.MENU_CLICK);
                if (soundManager.isSoundPlaying(Sound.BGM_LOGIN))
                    soundManager.stopSound(Sound.BGM_LOGIN);
                this.isRunning = false;

            } else {
                soundManager.playSound(Sound.COIN_INSUFFICIENT);
                alertCooldown.reset();
            }
        } else if (selectedOption == 3) {
            this.menu = Menu.SIGN_UP;
            soundManager.playSound(Sound.MENU_CLICK);
            this.isRunning = false;
        }
    }

    /**
     * Validates login credentials.
     *
     * @return True if login is successful, false otherwise.
     */
    private boolean validateLogin() {
        return usernameInput.equals("ADMIN") && passwordInput.equals("1234");
    }

    /**
     * Draws the login screen elements.
     */
    private void draw() {
        drawManager.initDrawing(this);

        drawManager.drawLoginScreen(this, usernameInput, passwordInput, isUsernameActive,
                isPasswordActive, selectedOption, !alertCooldown.checkFinished());

        drawManager.completeDrawing(this);
    }

    /**
     * Moves to the next menu item.
     */
    private void nextMenuItem() {
        if (this.selectedOption == 3) {
            this.selectedOption = 0;
        } else {
            this.selectedOption++;
        }
    }

    /**
     * Moves to the previous menu item.
     */
    private void previousMenuItem() {
        if (this.selectedOption == 0) {
            this.selectedOption = 3;
        } else {
            this.selectedOption--;
        }
    }
}
