package application.command;

import application.exception.InvalidArgumentException;
import application.exception.MissingArgumentException;
import application.review.ReviewList;
import application.storage.Storage;

import java.io.IOException;

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
     * @return message to be displayed to the user
     * @throws MissingArgumentException if commands do not receive their expected number of arguments
     * @throws InvalidArgumentException if commands do not receive their expected arguments in the correct format
     */
    public abstract String execute(ReviewList reviewList, Storage storage)
            throws MissingArgumentException, InvalidArgumentException, IOException;
}
