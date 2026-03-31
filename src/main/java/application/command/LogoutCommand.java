package application.command;

import application.auth.AuthManager;
import application.review.ReviewList;
import application.storage.Storage;

/**
 * Class representing a command to log out.
 */
public class LogoutCommand extends Command{
    /**
     * Executes the logout command.
     * @param reviews the list of reviews
     * @param storage the storage object
     * @param manager the authentication manager
     * @return a string indicating the result of the logout command
     */
    @Override
    public String execute(
            ReviewList reviews,
            Storage storage,
            AuthManager manager
    ) {
        if (manager.isOwnerAuthenticated()) {
            manager.logout();
            return "Successfully logged out!";
        }

        return "You are not logged in!";
    }
}
