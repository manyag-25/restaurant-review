package application.command;

import application.auth.AuthManager;
import application.exception.MissingArgumentException;
import application.parser.ArgumentParser;
import application.review.ReviewList;
import application.storage.Storage;

import java.util.Map;
import java.util.Set;

/**
 * Represents a login command.
 */
public class LoginCommand extends Command{
    public static final Set<String> DELIMITERS = Set.of("/default");
    private final String password;

    /**
     * Constructor for LoginCommand class.
     * @param commandArgs the arguments of the command
     * @throws MissingArgumentException if the password is missing
     */
    public LoginCommand(Map<String, String> commandArgs) throws MissingArgumentException {
        String rawPassword = commandArgs.get("/default");
        if (!ArgumentParser.isValidString(rawPassword)) {
            throw new MissingArgumentException("Please enter a valid password.");
        }
        this.password = rawPassword.trim();
    }

    /**
     * Executes the login command.
     * @param reviews the list of reviews
     * @param storage the storage object
     * @param manager the authentication manager
     * @return a string indicating the result of the login command
     */
    @Override
    public String execute(
            ReviewList reviews,
            Storage storage,
            AuthManager manager
    ) {
        if (manager.isOwnerAuthenticated()) {
            return "You are already logged in!";
        }

        boolean isLoggedIn = manager.authenticateOwner(password);
        return isLoggedIn ? "Successfully logged in!" : "Incorrect password!";
    }
}
