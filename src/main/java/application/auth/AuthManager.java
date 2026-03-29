package application.auth;

/**
 * Manages owner authentication for the application.
 *
 * This class is responsible for verifying the owner password and tracking
 * whether owner-only access has been granted for the current application session.
 */
public class AuthManager {
    private final String ownerPassword;
    private boolean isOwnerAuthenticated;

    /**
     * Constructs an {@code AuthManager} with the specified owner password.
     *
     * @param ownerPassword the password required for owner access
     * @throws IllegalArgumentException if the password is null or blank
     */
    public AuthManager(String ownerPassword) {
        if (ownerPassword == null || ownerPassword.isBlank()) {
            throw new IllegalArgumentException("Owner password cannot be null or blank.");
        }
        this.ownerPassword = ownerPassword;
        this.isOwnerAuthenticated = false;
    }

    /**
     * Attempts to authenticate the owner using the provided password.
     *
     * If the password matches the stored owner password, owner access is granted.
     *
     * @param password the password entered by the user
     * @return {@code true} if authentication succeeds, {@code false} otherwise
     */
    public boolean authenticateOwner(String password) {
        if (password == null) {
            return false;
        }

        if (ownerPassword.equals(password)) {
            isOwnerAuthenticated = true;
            return true;
        }

        return false;
    }

    /**
     * Returns whether the owner is currently authenticated.
     *
     * @return {@code true} if owner access is currently granted, {@code false} otherwise
     */
    public boolean isOwnerAuthenticated() {
        return isOwnerAuthenticated;
    }

}
