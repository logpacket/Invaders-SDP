package entity;

import engine.Core;
import service.ShopService;

import java.util.logging.*;

public class Wallet {
    private static Logger logger = Core.getLogger();
    private int coin;
    private int bulletLevel;
    private int shootLevel;
    private int livesLevel;
    private int coinLevel;
    private final ShopService shopService;

    private boolean initialized = false;
    private final Object initializationLock = new Object();

    private Wallet() {
        this.shopService = new ShopService();
        initialize();
    }

    private static class WalletHolder {
        private static final Wallet INSTANCE = new Wallet();
    }

    public static Wallet getWallet() {
        return WalletHolder.INSTANCE;
    }

    private void initialize() {
        new Thread(this::fetchShopData).start();
    }

    public boolean isInitialized() {
        return initialized;
    }

    public int getCoin() {
        waitForInitialization();
        return coin;
    }

    public int getBulletLevel() {
        waitForInitialization();
        return bulletLevel;
    }

    public int getShootLevel() {
        waitForInitialization();
        return shootLevel;
    }

    public int getLivesLevel() {
        waitForInitialization();
        return livesLevel;
    }

    public int getCoinLevel() {
        waitForInitialization();
        return coinLevel;
    }

    public void setBulletLevel(int bulletLevel) {
        waitForInitialization();
        this.bulletLevel = bulletLevel;
        saveShopToServer();
        logger.info("Bullet Level upgraded to: " + bulletLevel);
    }

    public void setShootLevel(int shootLevel) {
        waitForInitialization();
        this.shootLevel = shootLevel;
        saveShopToServer();
        logger.info("Shoot Level upgraded to: " + shootLevel);
    }

    public void setLivesLevel(int livesLevel) {
        waitForInitialization();
        this.livesLevel = livesLevel;
        saveShopToServer();
        logger.info("Lives Level upgraded to: " + livesLevel);
    }

    public void setCoinLevel(int coinLevel) {
        waitForInitialization();
        this.coinLevel = coinLevel;
        saveShopToServer();
        logger.info("Coin Gain Level upgraded to: " + coinLevel);
    }

    public void deposit(int amount) {
        waitForInitialization();
        if (amount <= 0) return;
        coin += amount;
        saveShopToServer();
        logger.info("Deposit completed. Current coin: " + coin);
    }

    public boolean withdraw(int amount) {
        waitForInitialization();
        if (amount <= 0) return false;
        if (coin < amount) {
            logger.warning("Insufficient coin balance.");
            return false;
        }
        coin -= amount;
        saveShopToServer();
        logger.info("Withdraw completed. Remaining coin: " + coin);
        return true;
    }

    private void saveShopToServer() {
        shopService.saveShop(coin, bulletLevel, shootLevel, livesLevel, coinLevel,
                _ -> logger.info("Wallet data saved to server successfully."),
                _ -> logger.warning("Error saving shop data."));
    }

    private void fetchShopData() {
        shopService.callShop(
                (event) -> {
                    if (event.body() instanceof message.Wallet wallet) {
                        synchronized (initializationLock) {
                            this.coin = wallet.coin();
                            this.bulletLevel = wallet.bulletLevel();
                            this.shootLevel = wallet.shootLevel();
                            this.livesLevel = wallet.livesLevel();
                            this.coinLevel = wallet.coinLevel();
                            this.initialized = true;
                            initializationLock.notifyAll();
                        }

                        logger.info(String.format("Wallet data loaded from server: coin=%d, bulletLevel=%d, shootLevel=%d, livesLevel=%d, coinLevel=%d",
                                coin, bulletLevel, shootLevel, livesLevel, coinLevel));
                    } else {
                        logger.warning("Unexpected response type: " + event.body().getClass().getName());
                    }
                },
                (error) -> {
                    logger.warning("Error loading shop data: " + error.message());
                }
        );
    }

    private void waitForInitialization() {
        synchronized (initializationLock) {
            while (!initialized) {
                try {
                    initializationLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.warning("Thread interrupted while waiting for Wallet initialization.");
                }
            }
        }
    }
}
