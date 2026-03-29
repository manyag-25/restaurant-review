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
    private final Storage storage;
    private final ReviewList reviewList;
    private final boolean hasStorageLoadFailure;
    private final List<String> startupStorageWarnings;
    private final AuthManager authManager = new AuthManager("hello");

    /**
     * Constructs a MealMeter application and loads stored reviews.
     */
    public MealMeter() {
        this.storage = new Storage();

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
        try {
            Command command = CommandParser.getCommand(userInput);
            String output = command.execute(reviewList, storage, authManager);
            return new CommandResult(output, command.isTerminatingCommand());
        } catch (InvalidArgumentException | MissingArgumentException | IOException e) {
            return new CommandResult(e.getMessage(), false);
        } catch (Exception e) {
            return new CommandResult("An unexpected error occurred: " + e.getMessage(), false);
        }
    }
}
