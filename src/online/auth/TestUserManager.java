package online.auth;

/**
 * TestUserManager demonstrates the usage of UserManager and SessionManager.
 * It simulates user registration, login, session validation, session renewal, and session termination.
 */
public class TestUserManager {
    public static void main(String[] args) {
        UserManager userManager = new UserManager();
        SessionManager sessionManager = new SessionManager();

        /**
         * Step 1: Register a new user.
         * @output Displays whether the registration is successful.
         */
        System.out.println("Register User: " + userManager.register("player1", "password123"));

        /**
         * Step 2: Log in with the registered user credentials.
         * @output Displays the generated session token or null if login fails.
         */
        String token = userManager.login("player1", "password123");
        System.out.println("Login Token: " + token);

        /**
         * Step 3: Create a session for the user using the token.
         * @output Checks if the session is valid.
         */
        if (token != null) {
            sessionManager.createSession(token);
            System.out.println("Session Valid: " + sessionManager.isSessionValid(token));
        }

        /**
         * Step 4: Renew the session to extend its expiry time.
         * @output Displays the session validity after renewal.
         */
        sessionManager.renewSession(token);
        System.out.println("Session Renewed. Valid: " + sessionManager.isSessionValid(token));

        /**
         * Step 5: End the session and verify if it is invalidated.
         * @output Displays the session validity after termination.
         */
        sessionManager.endSession(token);
        System.out.println("Session Valid After End: " + sessionManager.isSessionValid(token));
    }
}
