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
        // 실제로는 ShopService 등과의 연결을 통해 불러오거나 데이터를 초기화 후 반환해야 할 수 있습니다.
        Wallet wallet = new Wallet();
        wallet.fetchShopData(); // 서버에서 데이터를 로드하는 비동기 작업
        return wallet;
    }

    // Getter methods
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

    // Setter methods with server sync
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

    // Save data to the server (for sync)
    private void saveShopToServer() {
        shopService.saveShop(coin, bulletLevel, shootLevel, livesLevel, coinLevel,
                _ -> LOGGER.info("Wallet data saved to server successfully."),
                _ -> LOGGER.warning("Error saving shop data."));
    }

    // Fetch the shop data from server
    public void fetchShopData() {
        shopService.callShop(
                (event) -> {
                    if (event.body() instanceof message.Wallet wallet) {
                        this.coin = wallet.coin();
                        this.bulletLevel = wallet.bulletLevel();
                        this.shootLevel = wallet.shootLevel();
                        this.livesLevel = wallet.livesLevel();
                        this.coinLevel = wallet.coinLevel();

                        // 수정된 로그 메시지
                        LOGGER.info(String.format("Wallet data loaded from server: coin=%d, bulletLevel=%d, shootLevel=%d, livesLevel=%d, coinLevel=%d",
                                coin, bulletLevel, shootLevel, livesLevel, coinLevel));
                    } else {
                        LOGGER.warning("Unexpected response type: " + event.body().getClass().getName());
                        LOGGER.warning("Response body: " + event.body());  // 더 구체적인 응답 로깅
                    }
                },
                (error) -> {
                    LOGGER.warning("Error loading shop data: " + error.message());
                    LOGGER.warning("Error details: " + error);  // 에러 세부 사항 로깅
                }
        );
    }

}
