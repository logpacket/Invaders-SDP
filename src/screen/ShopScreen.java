package screen;

import engine.Cooldown;
import engine.Core;
import engine.Sound;
import engine.SoundManager;
import entity.Wallet;

import java.awt.event.KeyEvent;

public class ShopScreen extends Screen {

    /** Milliseconds between changes in user selection. */
    private static final int SELECTION_TIME = 200;
    private static final int ALERT_TIME = 1500;

    /** Singleton instance of SoundManager */
    private final SoundManager soundManager = SoundManager.getInstance();

    /** Time between changes in user selection. */
    private final Cooldown selectionCooldown;

    /** Time until not enough coin alert disappear */
    private final Cooldown moneyAlertCooldown;

    /** Time until maxLevel alert disappear */
    private final Cooldown maxAlertCooldown;

    /** Player's wallet */
    private final Wallet wallet;

    /** 1-bullet speed 2-shoot frequency 3-additional lives 4-gain coin upgrade */
    private int selectedItem;

    /** price per upgrade level */
    private final int[] upgradeCost = {2000, 4000, 8000};

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
    public ShopScreen(final int width, final int height, final int fps) {
        super(width, height, fps);

        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.selectionCooldown.reset();
        this.moneyAlertCooldown = Core.getCooldown(ALERT_TIME);
        this.maxAlertCooldown = Core.getCooldown(ALERT_TIME);
        this.wallet = Wallet.getWallet();
        selectedItem = 1;

        soundManager.stopSound(Sound.BGM_MAIN);
        soundManager.loopSound(Sound.BGM_SHOP);
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected final void update() {
        super.update();

        draw();
        if (this.selectionCooldown.checkFinished()
                && this.inputDelay.checkFinished()
                && this.moneyAlertCooldown.checkFinished()
                && this.maxAlertCooldown.checkFinished()) {

            if (inputManager.isKeyDown(KeyEvent.VK_UP)
                    || inputManager.isKeyDown(KeyEvent.VK_W)) {
                previousMenuItem();
                this.selectionCooldown.reset();
                soundManager.playSound(Sound.MENU_MOVE);
            }
            if (inputManager.isKeyDown(KeyEvent.VK_DOWN)
                    || inputManager.isKeyDown(KeyEvent.VK_S)) {
                nextMenuItem();
                this.selectionCooldown.reset();
                soundManager.playSound(Sound.MENU_MOVE);
            }
            if (inputManager.isKeyDown(KeyEvent.VK_SPACE))
            {
                switch (selectedItem) {
                    case 1:
                        if (upgrade(wallet.getBulletLevel())) {
                            wallet.setBulletLevel(wallet.getBulletLevel() + 1);
                            this.selectionCooldown.reset();
                        }
                        break;
                    case 2:
                        if (upgrade(wallet.getShootLevel())) {
                            wallet.setShootLevel(wallet.getShootLevel() + 1);
                            this.selectionCooldown.reset();
                        }
                        break;
                    case 3:
                        if (upgrade(wallet.getLivesLevel())) {
                            wallet.setLivesLevel(wallet.getLivesLevel() + 1);
                            this.selectionCooldown.reset();
                        }
                        break;
                    case 4:
                        if (upgrade(wallet.getCoinLevel())) {
                            wallet.setCoinLevel(wallet.getCoinLevel() + 1);
                            this.selectionCooldown.reset();
                        }
                        break;
                    default:
                        break;
                }
            }

            if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
                this.isRunning = false;
                soundManager.playSound(Sound.MENU_BACK);
                soundManager.stopSound(Sound.BGM_SHOP);
            }
        }
    }

    /**
     * Shifts the focus to the next shop item.
     */
    private void nextMenuItem() {
        if (this.selectedItem == 4)
            this.selectedItem = 1;
        else
            this.selectedItem++;
    }

    /**
     * Shifts the focus to the previous shop item.
     */
    private void previousMenuItem() {
        if (this.selectedItem == 1)
            this.selectedItem = 4;
        else
            this.selectedItem--;
    }

    private void draw() {
        renderer.initDrawing(this);

        renderer.drawShop(this, selectedItem, wallet, moneyAlertCooldown, maxAlertCooldown);

        renderer.completeDrawing(this);
    }

    public boolean upgrade(int level)
    {
        if (level >= 4) {
            soundManager.playSound(Sound.COIN_INSUFFICIENT);
            moneyAlertCooldown.reset();
            return false;
        }

        if (wallet.withdraw(upgradeCost[level-  1])) {
            soundManager.playSound(Sound.COIN_USE);
            return true;
        }
        else  {
            soundManager.playSound(Sound.COIN_INSUFFICIENT);
            moneyAlertCooldown.reset();
            return false;
        }
    }
}
