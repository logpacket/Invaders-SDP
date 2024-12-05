package entity;

import engine.Core;
import service.ShopService;

import java.util.logging.*;

public class Wallet {
    private static final Logger LOGGER = Logger.getLogger(Core.class.getSimpleName());
    private int coin;
    private int bulletLevel;
    private int shootLevel;
    private int livesLevel;
    private int coinLevel;
    private final ShopService shopService;

    public Wallet() {
        this.shopService = new ShopService();
    }

    public static Wallet getWallet() {
        Wallet wallet = new Wallet();
        wallet.fetchShopData();
        return wallet;
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
        LOGGER.info("Bullet Level upgraded to: " + bulletLevel);
    }

    public void setShootLevel(int shootLevel) {
        this.shootLevel = shootLevel;
        saveShopToServer();
        LOGGER.info("Shoot Level upgraded to: " + shootLevel);
    }

    public void setLivesLevel(int livesLevel) {
        this.livesLevel = livesLevel;
        saveShopToServer();
        LOGGER.info("Lives Level upgraded to: " + livesLevel);
    }

    public void setCoinLevel(int coinLevel) {
        this.coinLevel = coinLevel;
        saveShopToServer();
        LOGGER.info("Coin Gain Level upgraded to: " + coinLevel);
    }

    public void deposit(int amount) {
        if (amount <= 0) return;
        coin += amount;
        saveShopToServer();
        LOGGER.info("Deposit completed. Current coin: " + coin);
    }

    public boolean withdraw(int amount) {
        if (amount <= 0) return false;
        if (coin < amount) {
            LOGGER.warning("Insufficient coin balance.");
            return false;
        }
        coin -= amount;
        saveShopToServer();
        LOGGER.info("Withdraw completed. Remaining coin: " + coin);
        return true;
    }

    private void saveShopToServer() {
        shopService.saveShop(coin, bulletLevel, shootLevel, livesLevel, coinLevel,
                _ -> LOGGER.info("Wallet data saved to server successfully."),
                _ -> LOGGER.warning("Error saving shop data."));
    }

    public void fetchShopData() {
        shopService.callShop(
                (event) -> {
                    if (event.body() instanceof message.Wallet wallet) {
                        this.coin = wallet.coin();
                        this.bulletLevel = wallet.bulletLevel();
                        this.shootLevel = wallet.shootLevel();
                        this.livesLevel = wallet.livesLevel();
                        this.coinLevel = wallet.coinLevel();

                        LOGGER.info(String.format("Wallet data loaded from server: coin=%d, bulletLevel=%d, shootLevel=%d, livesLevel=%d, coinLevel=%d",
                                coin, bulletLevel, shootLevel, livesLevel, coinLevel));
                    } else {
                        LOGGER.warning("Unexpected response type: " + event.body().getClass().getName());
                        LOGGER.warning("Response body: " + event.body());
                    }
                },
                (error) -> {
                    LOGGER.warning("Error loading shop data: " + error.message());
                    LOGGER.warning("Error details: " + error);
                }
        );
    }

}
