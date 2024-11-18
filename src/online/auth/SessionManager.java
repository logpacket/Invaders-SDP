package online.auth;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private final Map<String, Instant> sessionExpiry = new HashMap<>();
    private static final long SESSION_DURATION = 3600 * 1000; // 1 hour in milliseconds

    /**
     * Creates a session with a specific token and sets an expiry time.
     *
     * @param token The session token to create.
     */
    public void createSession(String token) {
        Instant expiry = Instant.now().plusMillis(SESSION_DURATION);
        sessionExpiry.put(token, expiry);
    }

    /**
     * Checks if a session token is valid and not expired.
     *
     * @param token The session token to validate.
     * @return True if the token is valid, false otherwise.
     */
    public boolean isSessionValid(String token) {
        if (!sessionExpiry.containsKey(token)) {
            return false;
        }
        Instant expiry = sessionExpiry.get(token);
        if (Instant.now().isAfter(expiry)) {
            sessionExpiry.remove(token);
            return false;
        }
        return true;
    }

    /**
     * Renews a session by extending its expiry time.
     *
     * @param token The session token to renew.
     */
    public void renewSession(String token) {
        if (sessionExpiry.containsKey(token)) {
            Instant newExpiry = Instant.now().plusMillis(SESSION_DURATION);
            sessionExpiry.put(token, newExpiry);
        }
    }

    /**
     * Ends a session by removing the token.
     *
     * @param token The session token to terminate.
     */
    public void endSession(String token) {
        sessionExpiry.remove(token);
    }
}
