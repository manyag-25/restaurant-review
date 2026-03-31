package application.command;

import application.auth.AuthManager;
import application.review.ReviewList;
import application.storage.Storage;

/**
 * Class representing a command to exit the program.
 */
public class ExitCommand extends Command {
    /**
     * Returns true, as this command should terminate the program.
     *
     * @return true
     */
    @Override
    public boolean isTerminatingCommand() {
        return true;
    }

    /**
     * Returns a message indicating that the program has exited.
     *
     * @param reviews the list of reviews
     * @param storage the storage object
     * @param manager the authentication manager
     * @return a string indicating that the program has exited
     */
    @Override
    public String execute(
            ReviewList reviews,
            Storage storage,
            AuthManager manager
    ) {
        return "Goodbye!";
    }
}
