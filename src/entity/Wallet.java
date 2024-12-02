package entity;

import engine.Core;
import engine.Session;
import message.Shop;
import handler.ShopHandler;

import java.util.logging.Logger;

public class Wallet {
    private static final Logger logger = Core.getLogger();
    private final String username;
    private int coin;
    private int bulletLevel;
    private int shootLevel;
    private int livesLevel;
    private int coinLevel;

    public Wallet(String username) {
        this.username = username;
        this.coin = 0;
        this.bulletLevel = 1;
        this.shootLevel = 1;
        this.livesLevel = 1;
        this.coinLevel = 1;
        saveWalletToServer();
    }

    public Wallet(String username, int coin, int bulletLevel, int shootLevel, int livesLevel, int coinLevel) {
        this.username = username;
        this.coin = coin;
        this.bulletLevel = bulletLevel;
        this.shootLevel = shootLevel;
        this.livesLevel = livesLevel;
        this.coinLevel = coinLevel;
    }

    public String getUsername() {
        return username;
    }

    public int getCoin() {
        return coin;
    }

    public int getBulletLevel() {
        return bulletLevel;
    }

    public int getShootLevel() {
        return shootLevel;
    }

    public int getLivesLevel() {
        return livesLevel;
    }

    public int getCoinLevel() {
        return coinLevel;
    }

    public void setBulletLevel(int bulletLevel) {
        this.bulletLevel = bulletLevel;
        saveWalletToServer();
        logger.info("Bullet Level upgraded to: " + bulletLevel);
    }

    public void setShootLevel(int shootLevel) {
        this.shootLevel = shootLevel;
        saveWalletToServer();
        logger.info("Shoot Level upgraded to: " + shootLevel);
    }

    public void setLivesLevel(int livesLevel) {
        this.livesLevel = livesLevel;
        saveWalletToServer();
        logger.info("Lives Level upgraded to: " + livesLevel);
    }

    public void setCoinLevel(int coinLevel) {
        this.coinLevel = coinLevel;
        saveWalletToServer();
        logger.info("Coin Gain Level upgraded to: " + coinLevel);
    }

    public void deposit(int amount) {
        if (amount <= 0) return;
        coin += amount;
        saveWalletToServer();
        logger.info("Deposit completed. Current coin: " + coin);
    }

    public boolean withdraw(int amount) {
        if (amount <= 0) return false;
        if (coin < amount) {
            logger.warning("Insufficient coin balance.");
            return false;
        }
        coin -= amount;
        saveWalletToServer();
        logger.info("Withdraw completed. Remaining coin: " + coin);
        return true;
    }

    private void saveWalletToServer() {
        try {
            Shop shopMessage = new Shop(username, coin, bulletLevel, shootLevel, livesLevel, coinLevel);

            Session session = Session.getInstance();
            ShopHandler shopHandler = new ShopHandler();
            shopHandler.saveShopToServer(session, shopMessage);
        } catch (Exception e) {
            logger.warning("Failed to save wallet to server: " + e.getMessage());
        }
    }

    public static Wallet loadWalletFromServer(Session session, String username) {
        try {
            ShopHandler shopHandler = new ShopHandler();
            Shop shopMessage = shopHandler.loadShop(session, username);

            if (shopMessage != null) {
                return new Wallet(
                        username,
                        shopMessage.coin(),
                        shopMessage.bulletLevel(),
                        shopMessage.shootLevel(),
                        shopMessage.livesLevel(),
                        shopMessage.coinLevel()
                );
            } else {
                logger.info("No wallet data found for username: " + username + ". Initializing default values.");
                return new Wallet(username);
            }
        } catch (Exception e) {
            logger.warning("Failed to load wallet from server for username " + username + ": " + e.getMessage());
            return new Wallet(username);
        }
    }
}
