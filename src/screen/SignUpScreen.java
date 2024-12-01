package screen;

import engine.*;
import service.SignUpService;

import java.awt.event.KeyEvent;

public class SignUpScreen extends Screen {

    private static final int SELECTION_TIME = 200;
    private static final int ALERT_TIME = 1500;

    /** Singleton instance of SoundManager */
    private final SoundManager soundManager = SoundManager.getInstance();


    private String usernameInput;
    private String passwordInput;
    private String confirmPasswordInput;
    private boolean isUsernameActive;
    private boolean isPasswordActive;
    private boolean isConfirmPasswordActive;
    private final SignUpService signUpService = new SignUpService();

    private final Cooldown selectionCooldown;
    private final Cooldown alertCooldown;

    public SignUpScreen(final int width, final int height, final int fps) {
        super(width, height, fps);

        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.alertCooldown = Core.getCooldown(ALERT_TIME);

        this.usernameInput = "";
        this.passwordInput = "";
        this.confirmPasswordInput = "";
        this.isUsernameActive = true;
        this.isPasswordActive = false;
        this.isConfirmPasswordActive = false;
        this.menu = Menu.SIGN_UP;
    }

    @Override
    protected final void update() {
        super.update();
        draw();
        handleInput();
    }

    private void signUp() {
        signUpService.signUp(usernameInput, passwordInput, _ -> {
            this.menu = Menu.LOGIN;
            isRunning = false;
        },
        _ -> {
            soundManager.playSound(Sound.COIN_INSUFFICIENT);
            alertCooldown.reset();
        });
    }

    private void handleInput() {
        if (this.selectionCooldown.checkFinished() && this.inputDelay.checkFinished()) {
            if (inputManager.isKeyDown(KeyEvent.VK_UP) || inputManager.isKeyDown(KeyEvent.VK_DOWN)) {
                if (isUsernameActive) {
                    isUsernameActive = false;
                    isPasswordActive = true;
                    soundManager.playSound(Sound.MENU_MOVE);
                } else if (isPasswordActive) {
                    isPasswordActive = false;
                    isConfirmPasswordActive = true;
                    soundManager.playSound(Sound.MENU_MOVE);
                } else if (isConfirmPasswordActive) {
                    isConfirmPasswordActive = false;
                    isUsernameActive = true;
                    soundManager.playSound(Sound.MENU_MOVE);
                }
                this.selectionCooldown.reset();
            }

            for (int keyCode = KeyEvent.VK_0; keyCode <= KeyEvent.VK_Z; keyCode++) {
                if (inputManager.isKeyDown(keyCode)) {
                    if (isUsernameActive && usernameInput.length() < 20) {
                        usernameInput += (char) keyCode;
                        soundManager.playSound(Sound.MENU_TYPING);
                    } else if (isPasswordActive && passwordInput.length() < 20) {
                        passwordInput += (char) keyCode;
                        soundManager.playSound(Sound.MENU_TYPING);
                    } else if (isConfirmPasswordActive && confirmPasswordInput.length() < 20) {
                        confirmPasswordInput += (char) keyCode;
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
                } else if (isConfirmPasswordActive && !confirmPasswordInput.isEmpty()) {
                    confirmPasswordInput = confirmPasswordInput.substring(0, confirmPasswordInput.length() - 1);
                    soundManager.playSound(Sound.MENU_TYPING);
                }
                this.selectionCooldown.reset();
            }

            if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
                if (validateInput()) {
                    signUp();
                    soundManager.playSound(Sound.MENU_CLICK);
                } else {
                    soundManager.playSound(Sound.COIN_INSUFFICIENT);
                    alertCooldown.reset();
                }
            }

            if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
                soundManager.playSound(Sound.MENU_CLICK);
                this.menu = Menu.LOGIN;
            }
        }
    }

    private boolean validateInput() {

        if (usernameInput.isEmpty() || passwordInput.isEmpty() || confirmPasswordInput.isEmpty())
            return false;
        return passwordInput.equals(confirmPasswordInput);
    }

    private void draw() {
        drawManager.initDrawing(this);

        drawManager.drawSignUpScreen(this, usernameInput, passwordInput, confirmPasswordInput,
                isUsernameActive, isPasswordActive, isConfirmPasswordActive, !alertCooldown.checkFinished());

        drawManager.completeDrawing(this);
    }
}
