package service;

import engine.network.ErrorHandler;
import engine.network.EventHandler;
import message.User;

public class LoginService extends Service {
    public LoginService() {
        super("login");
    }

    public void login(String username, String password, EventHandler callback, ErrorHandler errorHandler) {
        request(new User(username, password), callback, errorHandler);
    }
}
