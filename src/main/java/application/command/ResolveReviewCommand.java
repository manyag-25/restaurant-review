package application.command;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import application.auth.AuthManager;
import application.exception.InvalidArgumentException;
import application.exception.MissingArgumentException;
import application.parser.ArgumentParser;
import application.review.Review;
import application.review.ReviewList;
import application.storage.Storage;

/**
 * Class representing a command to resolve a review.
 */
public class ResolveReviewCommand extends Command {
    public static final Set<String> DELIMITERS = Set.of("/default");
    private final int index;

    /**
     * Constructor for ResolveReviewCommand class.
     *
     * @param commandArgs the arguments of the command
     * @throws InvalidArgumentException if the index is not a number
     * @throws MissingArgumentException if the index is missing
     */
    public ResolveReviewCommand(Map<String, String> commandArgs)
            throws InvalidArgumentException, MissingArgumentException {
        String indexAsString = commandArgs.get("/default");
        this.index = ArgumentParser.toInt(indexAsString);
    }

    /**
     * Executes the command to resolve a review in the list.
     *
     * @param reviews the list of reviews
     * @param storage the storage object
     * @param manager the authentication manager
     * @return a string representation of the command result
     * @throws InvalidArgumentException if the index is in the wrong format
     */
    @Override
    public String execute(
            ReviewList reviews,
            Storage storage,
            AuthManager manager
    ) throws InvalidArgumentException, IOException {
        Review review = reviews.markResolved(index);
        storage.saveReviews(reviews);

        return String.format("%s\nmarked as resolved!", review);
    }
}
