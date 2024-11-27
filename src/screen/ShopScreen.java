package screen;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import engine.*;
import entity.TextEntity;
import entity.Wallet;

import javax.imageio.ImageIO;

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

    private BufferedImage imgAdditionalLife;
	private BufferedImage imgBulletSpeed;
	private BufferedImage imgCoin;
	private BufferedImage imgCoinGain;
	private BufferedImage imgShootInterval;

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

        try{
			imgAdditionalLife = ImageIO.read(new File("res/image/additional life.jpg"));
			imgBulletSpeed = ImageIO.read(new File("res/image/bullet speed.jpg"));
			imgCoin = ImageIO.read(new File("res/image/coin.jpg"));
			imgCoinGain = ImageIO.read(new File("res/image/coin gain.jpg"));
			imgShootInterval = ImageIO.read(new File("res/image/shot interval.jpg"));
		} catch (IOException e) {
			logger.info("Shop image loading failed");
		}

        soundManager.stopSound(Sound.BGM_MAIN);
        soundManager.loopSound(Sound.BGM_SHOP);
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected final void update() {
        super.update();
        createEntity();
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

        renderer.drawEntities(frontBufferEntities);

        renderer.completeDrawing(this);
    }

    protected void createEntity(){
        String shopString = "Shop";
        int shopStringY = Math.round(this.getHeight() * 0.15f);

        String coinString = ":  " + wallet.getCoin();
        String exitString = "PRESS \"ESC\" TO RETURN TO MAIN MENU";
        String[] costs = new String[] {"2000", "4000", "8000", "MAX LEVEL"};

        String[] itemString = new String[]{"BULLET SPEED", "SHOT INTERVAL", "ADDITIONAL LIFE","COIN GAIN"};
        int[] walletLevel = new int[]{wallet.getBulletLevel(), wallet.getShootLevel(), wallet.getLivesLevel(), wallet.getCoinLevel()};

        BufferedImage[] itemImages = new BufferedImage[]{imgBulletSpeed, imgShootInterval, imgAdditionalLife, imgCoinGain};

        int imgStartX = this.getWidth()/80*23;
        int imgStartY = this.getHeight()/80*27;
        int imgDis = this.getHeight()/80*12;
        int coinStartX = this.getWidth()/80*55;
        int coinStartY = this.getHeight()/160*66;
        int coinDis = this.getHeight()/80*12;
        int coinSize = 20;
        int coinTextStartX = this.getWidth()/80*60;
        int coinTextStartY = this.getHeight()/160*71;
        int coinTextDis = this.getHeight()/80*12;

        backBufferEntities.add(EntityFactory.createCenteredBigString(this, shopString, shopStringY, Color.GREEN));
        backBufferEntities.add(EntityFactory.createImageEntity(this.getWidth()/80*39-(coinString.length()-3)*this.getWidth()/80,
                this.getHeight()/80*18, Color.GREEN, coinSize, coinSize, imgCoin));
        backBufferEntities.add(EntityFactory.createTextEntity(this.getWidth()/80*44-(coinString.length()-3)*this.getWidth()/80,
                this.getHeight()/80*20, Color.WHITE, coinString, FontManager.getFontRegular()));

        for(int i = 0;i<4;i++) {
            backBufferEntities.add(EntityFactory.createCenteredRegularString(this, itemString[i],
                    this.getHeight() / 80 * (28 + 12 * i), Color.WHITE));
            for (int j = 0; j < 3; j++)
            {
                if (j + 2 <= walletLevel[i])
                {
                    backBufferEntities.add(EntityFactory.createRectEntity(this.getWidth() / 40 * (33 / 2) +
                            j * (this.getWidth() / 10), this.getHeight() / 80 * (30 + 12*i),
                            Color.GREEN,20, 20, true));
                } else
                {
                    backBufferEntities.add(EntityFactory.createRectEntity(this.getWidth() / 40 * (33 / 2) +
                            j * (this.getWidth() / 10), this.getHeight() / 80 * (30 + 12*i),
                            Color.WHITE,20, 20, true));
                }
            }
        }

        backBufferEntities.add(EntityFactory.createImageEntity(imgStartX,
                imgStartY + (imgDis*(selectedItem-1)),Color.WHITE,50,40,itemImages[selectedItem-1]));
        backBufferEntities.add(EntityFactory.createImageEntity(coinStartX,
                coinStartY + (coinDis*(selectedItem-1)),Color.WHITE,coinSize,coinSize,imgCoin));
        backBufferEntities.add(new TextEntity(coinTextStartX,
                coinTextStartY + (coinTextDis*(selectedItem-1)),Color.WHITE,"X "+costs[walletLevel[selectedItem-1]-1],FontManager.getFontRegular()));

        backBufferEntities.add(EntityFactory.createCenteredRegularString(this,
                exitString,this.getHeight()/80*80,Color.WHITE));

        if (!moneyAlertCooldown.checkFinished())
        {
            backBufferEntities.add(EntityFactory.createRectEntity((this.getWidth()-300)/2,
                    (this.getHeight()-100)/2,Color.red,300, 80, true));
            backBufferEntities.add(EntityFactory.createCenteredBigString(this,"Insufficient coin",
                    this.getHeight()/2, Color.black));
        }

        if(!maxAlertCooldown.checkFinished())
        {
            backBufferEntities.add(EntityFactory.createRectEntity((this.getWidth()-300)/2,
                    (this.getHeight()-100)/2,Color.red,300, 80, true));
            backBufferEntities.add(EntityFactory.createCenteredBigString(this,"Already max level",
                    this.getHeight()/2, Color.black));
        }

        swapBuffers();
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
