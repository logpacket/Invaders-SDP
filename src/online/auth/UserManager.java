package online.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager {
    private final Map<String, String> users = new HashMap<>(); // username -> password
    private final Map<String, String> sessions = new HashMap<>(); // token -> username

    /**
     * Registers a new user with a unique username and password.
     *
     * @param username The username to register.
     * @param password The password for the user.
     * @return True if registration is successful, false if the username already exists.
     */
    public boolean register(String username, String password) {
        if (users.containsKey(username)) {
            return false;
        }
        users.put(username, password);
        return true;
    }

    /**
     * Validates login credentials and creates a session token if successful.
     *
     * @param username The username to authenticate.
     * @param password The password to authenticate.
     * @return A session token if login is successful, null otherwise.
     */
    public String login(String username, String password) {
        if (!users.containsKey(username) || !users.get(username).equals(password)) {
            return null;
        }
        String token = UUID.randomUUID().toString();
        sessions.put(token, username);
        return token;
    }

    /**
     * Validates if a given session token is currently active.
     *
     * @param token The session token to validate.
     * @return True if the token is valid, false otherwise.
     */
    public boolean validateSession(String token) {
        return sessions.containsKey(token);
    }

    /**
     * Ends a session by invalidating the given token.
     *
     * @param token The session token to invalidate.
     */
    public void endSession(String token) {
        sessions.remove(token);
    }
}
