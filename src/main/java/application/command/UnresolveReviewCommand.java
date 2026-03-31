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
 * Class representing a command to unresolve a review.
 */
public class UnresolveReviewCommand extends Command {
    public static final Set<String> DELIMITERS = Set.of("/default");
    private final int index;

    /**
     * Constructor for UnresolveReviewCommand class.
     *
     * @param commandArgs the arguments of the command
     * @throws InvalidArgumentException if the index is not a number
     * @throws MissingArgumentException if the index is missing
     */
    public UnresolveReviewCommand(Map<String, String> commandArgs)
            throws InvalidArgumentException, MissingArgumentException {
        String indexAsString = commandArgs.get("/default");
        this.index = ArgumentParser.toInt(indexAsString);
    }

    /**
     * Executes the command to unresolve a review in the list.
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
        Review review = reviews.markOutstanding(index);
        storage.saveReviews(reviews);

        return String.format("%s\nmarked as outstanding!", review);
    }
}
