package service;

import engine.network.ErrorHandler;
import engine.network.EventHandler;
import message.User;

public class SignUpService extends Service {
    public SignUpService() {
        super("signup");
    }

    public void signUp(String username, String password, EventHandler callback, ErrorHandler errorHandler) {
        request(new User(username, password), callback, errorHandler);
    }
}
