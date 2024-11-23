package screen;

import engine.*;

import java.awt.event.KeyEvent;

public class SettingScreen extends Screen {

    /** Width of Volume bar */
    private static final int VOLUME_BAR_WIDTH = 200;
    /** Volume adjustment units */
    private static final int VOLUME_ADJUST_STEP = 10;
    /** Between menu items */
    private static final int MENU_ITEM_GAP = 120;
    /** Spacing between soundbar and text (closer to sound) */
    private static final int VOLUME_BAR_GAP = 20;
    /** Spacing between soundbar and volume numbers */
    private static final int VOLUME_PERCENTAGE_GAP = 40;
    /** Milliseconds between changes in user selection. */
    private static final int COOLDOWN_TIME = 200;

    /** Menu item list */
    private final String[] menuItems = {"Sound", "Ending Credit"};
    /** Default selected menu item */
    private int selectedItem = 0;
    /** Default volume value */
    private int volumeLevel;
    /** Time between changes in user selection. */
    private final Cooldown selectionCooldown;
    /** Singleton instance of SoundManager */
    private final SoundManager soundManager = SoundManager.getInstance();

    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param width
     *            Screen width.
     * @param height
     *            Screen height.
     * @param fps
     *            Frames per second, frame rate at which the game is run.
     */
    public SettingScreen(int width, int height, int fps) {
        super(width, height, fps);
        this.volumeLevel = soundManager.getVolume()*10;
        this.selectionCooldown = Core.getCooldown(COOLDOWN_TIME);
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected void update() {
        super.update();

        if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
            this.isRunning = false;
            soundManager.playSound(Sound.MENU_BACK);
            return;
        }

        if (this.selectionCooldown.checkFinished()) {

            if (selectedItem == 0) {
                if (inputManager.isKeyDown(KeyEvent.VK_LEFT)) {
                    volumeLevel = Math.max(0, volumeLevel - VOLUME_ADJUST_STEP);
                    this.selectionCooldown.reset();
                    soundManager.volumeDown();
                    soundManager.playSound(Sound.MENU_MOVE);
                } else if (inputManager.isKeyDown(KeyEvent.VK_RIGHT)) {
                    volumeLevel = Math.min(100, volumeLevel + VOLUME_ADJUST_STEP);
                    this.selectionCooldown.reset();
                    soundManager.volumeUp();
                    soundManager.playSound(Sound.MENU_MOVE);
                }
            }

            if (inputManager.isKeyDown(KeyEvent.VK_UP)) {
                selectedItem = (selectedItem - 1 + menuItems.length) % menuItems.length;
                this.selectionCooldown.reset();
                soundManager.playSound(Sound.MENU_MOVE);
            } else if (inputManager.isKeyDown(KeyEvent.VK_DOWN)) {
                selectedItem = (selectedItem + 1) % menuItems.length;
                this.selectionCooldown.reset();
                soundManager.playSound(Sound.MENU_MOVE);
            }

            if (inputManager.isKeyDown(KeyEvent.VK_SPACE) && selectedItem == 1) {
                this.menu = Menu.CREDIT;
                this.isRunning = false;
                soundManager.playSound(Sound.MENU_CLICK);
            }
        }

        draw();
    }

    /**
     * Draws the elements associated with the screen.
     */
    private void draw() {
        renderer.initDrawing(this);

        renderer.drawSettingsScreen(this);

        for (int i = 0; i < menuItems.length; i++) {
            boolean isSelected = (i == selectedItem);
            renderer.drawCenteredRegularString(this, menuItems[i], this.getHeight() / 3 + i * MENU_ITEM_GAP, isSelected);
        }

        int filledWidth = (volumeLevel * VOLUME_BAR_WIDTH) / 100;
        boolean isVolumeSelected = (selectedItem == 0);

        renderer.drawVolumeBar(this, this.getWidth() / 2 - VOLUME_BAR_WIDTH / 2, this.getHeight() / 3 + VOLUME_BAR_GAP, VOLUME_BAR_WIDTH, filledWidth, isVolumeSelected);

        renderer.drawVolumePercentage(this, this.getHeight() / 3 + VOLUME_BAR_GAP + VOLUME_PERCENTAGE_GAP, volumeLevel, isVolumeSelected);

        renderer.completeDrawing(this);
    }

    protected void createEntity(){


        swapBuffers();
    }
}
