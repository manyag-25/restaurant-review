package application.command;

import java.io.IOException;

import application.auth.AuthManager;
import application.exception.InvalidArgumentException;
import application.review.ReviewList;
import application.storage.Storage;

/**
 * Class representing a generic user command.
 */
public abstract class Command {
    /**
     * Returns true if the command should end the main program, else false.
     *
     * @return boolean representing if the command should terminate the main program
     */
    public boolean isTerminatingCommand() {
        return false;
    }

    /**
     * Abstract generic execute method for all commands to complete their specified actions.
     *
     * @param reviews the list of reviews
     * @param storage the storage object
     * @param manager the authentication manager
     * @return message to be displayed to the user
     * @throws InvalidArgumentException if commands do not receive their expected arguments in the correct format
     * @throws IOException if there is an error reading or writing to the file
     */
    public abstract String execute(
            ReviewList reviews,
            Storage storage,
            AuthManager manager
    ) throws InvalidArgumentException, IOException;
}
