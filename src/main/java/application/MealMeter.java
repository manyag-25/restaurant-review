package application;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import application.auth.AuthManager;
import application.command.Command;
import application.exception.InvalidArgumentException;
import application.exception.MissingArgumentException;
import application.parser.CommandParser;
import application.review.ReviewList;
import application.storage.Storage;
import application.storage.StorageLoadResult;

/**
 * Represents the core MealMeter application logic.
 *
 * <p>This class acts as the bridge between the user interface
 * and the command-processing logic.</p>
 */
public class MealMeter {
    private static final String DEFAULT_OWNER_PASSWORD = "password";
    private static final String LOGIN_KEYWORD = "login";

    private static final String LOGIN_SUCCESS_MESSAGE = "Owner login successful.";
    private static final String LOGIN_INVALID_PASSWORD_MESSAGE = "Invalid owner password.";
    private static final String LOGIN_ALREADY_AUTHENTICATED_MESSAGE = "Owner already logged in.";
    private static final String LOGIN_INVALID_FORMAT_MESSAGE = "Invalid login format. Usage: login PASSWORD";
    private static final String ACCESS_DENIED_MESSAGE =
            "Access denied. Please log in as the owner to use this command.";

    private final Storage storage;
    private final AuthManager authManager;
    private final ReviewList reviewList;
    private final boolean hasStorageLoadFailure;
    private final List<String> startupStorageWarnings;

    /**
     * Constructs a MealMeter application and loads stored reviews.
     */
    public MealMeter() {
        this(DEFAULT_OWNER_PASSWORD);
    }

    /**
     * Constructs a MealMeter application with a custom owner password
     * and loads stored reviews.
     *
     * @param ownerPassword the owner password for session login
     */
    public MealMeter(String ownerPassword) {
        this(new Storage(), new AuthManager(ownerPassword));
    }

    MealMeter(Storage storage, AuthManager authManager) {
        this.storage = storage;
        this.authManager = authManager;

        ReviewList loadedReviews;
        boolean loadFailure;
        List<String> storageWarnings;

        try {
            StorageLoadResult loadResult = storage.loadReviewsWithWarnings();
            loadedReviews = loadResult.reviewList();
            storageWarnings = loadResult.warnings();
            loadFailure = false;
        } catch (IOException e) {
            loadedReviews = new ReviewList();
            storageWarnings = Collections.emptyList();
            loadFailure = true;
        }

        this.reviewList = loadedReviews;
        this.hasStorageLoadFailure = loadFailure;
        this.startupStorageWarnings = storageWarnings;
    }

    /**
     * Returns whether loading reviews from storage failed at startup.
     *
     * @return true if storage loading failed, false otherwise
     */
    public boolean hasStorageLoadFailure() {
        return hasStorageLoadFailure;
    }

    /**
     * Returns non-fatal storage warnings encountered during startup loading.
     *
     * @return immutable list of startup storage warnings
     */
    public List<String> getStartupStorageWarnings() {
        return startupStorageWarnings;
    }

    /**
     * Handles one user input command and returns the command result.
     *
     * @param userInput the raw user input
     * @return the result containing output and termination status
     */
    public CommandResult handleInput(String userInput) {
        if (isLoginInput(userInput)) {
            return handleLogin(userInput);
        }

        try {
            Command command = CommandParser.getCommand(userInput);

            if (command.requiresOwnerAuthentication() && !authManager.isOwnerAuthenticated()) {
                return new CommandResult(ACCESS_DENIED_MESSAGE, false);
            }

            String output = command.execute(reviewList, storage);
            return new CommandResult(output, command.isTerminatingCommand());
        } catch (InvalidArgumentException | MissingArgumentException | IOException e) {
            return new CommandResult(e.getMessage(), false);
        } catch (Exception e) {
            return new CommandResult("An unexpected error occurred: " + e.getMessage(), false);
        }
    }

    private boolean isLoginInput(String userInput) {
        if (userInput == null || userInput.isBlank()) {
            return false;
        }

        String[] commandParts = userInput.trim().split("\\s+", 2);
        return LOGIN_KEYWORD.equalsIgnoreCase(commandParts[0]);
    }

    private CommandResult handleLogin(String userInput) {
        String[] commandParts = userInput.trim().split("\\s+", 2);

        if (commandParts.length != 2 || commandParts[1].isBlank() || commandParts[1].contains(" ")) {
            return new CommandResult(LOGIN_INVALID_FORMAT_MESSAGE, false);
        }

        if (authManager.isOwnerAuthenticated()) {
            return new CommandResult(LOGIN_ALREADY_AUTHENTICATED_MESSAGE, false);
        }

        String password = commandParts[1].trim();

        if (authManager.authenticateOwner(password)) {
            return new CommandResult(LOGIN_SUCCESS_MESSAGE, false);
        }

        return new CommandResult(LOGIN_INVALID_PASSWORD_MESSAGE, false);
    }
}
