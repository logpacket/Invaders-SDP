package service;

import engine.network.ErrorHandler;
import engine.network.EventHandler;
import message.Wallet;

public class ShopService extends Service {
    public ShopService() {
        super("shop");
    }

    public void saveShop(int coin, int bulletLevel, int shootLevel, int livesLevel, int coinLevel, EventHandler callback, ErrorHandler errorHandler) {
        request(new Wallet(coin, bulletLevel, shootLevel, livesLevel, coinLevel), callback, errorHandler);
    }

    public void callShop(EventHandler callback, ErrorHandler errorHandler) {
        request(null, callback, errorHandler);
    }
}
