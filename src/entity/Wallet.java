package entity;

import engine.Core;
import service.ShopService;

import java.util.logging.*;

public class Wallet {
    private static final Logger logger = Core.getLogger();
    private int coin;
    private int bulletLevel;
    private int shootLevel;
    private int livesLevel;
    private int coinLevel;
    private final ShopService shopService;

    private Wallet() {
        this.shopService = new ShopService();
    }

    private static class WalletHolder {
        private static final Wallet INSTANCE = new Wallet();
    }

    public static Wallet getWallet() {
        return WalletHolder.INSTANCE;
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
        saveShopToServer();
        logger.info("Bullet Level upgraded to: " + bulletLevel);
    }

    public void setShootLevel(int shootLevel) {
        this.shootLevel = shootLevel;
        saveShopToServer();
        logger.info("Shoot Level upgraded to: " + shootLevel);
    }

    public void setLivesLevel(int livesLevel) {
        this.livesLevel = livesLevel;
        saveShopToServer();
        logger.info("Lives Level upgraded to: " + livesLevel);
    }

    public void setCoinLevel(int coinLevel) {
        this.coinLevel = coinLevel;
        saveShopToServer();
        logger.info("Coin Gain Level upgraded to: " + coinLevel);
    }

    public void deposit(int amount) {
        if (amount <= 0) return;
        coin += amount;
        saveShopToServer();
        logger.info("Deposit completed. Current coin: " + coin);
    }

    public boolean withdraw(int amount) {
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

    public void saveShopToServer() {
        logger.info("Sending shop data to server: coin=" + coin + ", bulletLevel=" + bulletLevel +
                ", shootLevel=" + shootLevel + ", livesLevel=" + livesLevel + ", coinLevel=" + coinLevel);
        shopService.saveShop(coin, bulletLevel, shootLevel, livesLevel, coinLevel,
                _ -> logger.info("Wallet data saved to server successfully."),
                _ -> logger.warning("Error saving shop data."));
    }

    public void initialize() {
        shopService.callShop(
                (event) -> {
                    if (event.body() instanceof message.Wallet wallet) {
                        this.coin = wallet.coin();
                        this.bulletLevel = wallet.bulletLevel();
                        this.shootLevel = wallet.shootLevel();
                        this.livesLevel = wallet.livesLevel();
                        this.coinLevel = wallet.coinLevel();

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
}
