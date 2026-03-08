package application.command;

import application.exception.InvalidArgumentException;
import application.exception.MissingArgumentException;
import application.parser.Utility;
import application.review.Rating;
import application.review.Review;
import application.review.ReviewList;
import application.storage.Storage;

import java.util.Map;
import java.util.Set;

/**
 * Class representing a command to add a review.
 */
public class AddReviewCommand extends Command {
    public static final Set<String> DELIMITERS = Set.of("/default", "/rating", "/review");
    private final Map<String, String> commandArgs;

    /**
     * Constructor for AddReviewCommand class.
     *
     * @param commandArgs the arguments of the command
     */
    public AddReviewCommand(Map<String, String> commandArgs) {
        this.commandArgs = commandArgs;
    }

    /**
     * Executes the command to add a review to the list.
     *
     * @param reviewList the list of reviews
     * @param storage    the storage object
     * @return a string representation of the command result
     * @throws MissingArgumentException if any argument is missing
     * @throws InvalidArgumentException if any argument is in the wrong format
     */
    @Override
    public String execute(ReviewList reviewList, Storage storage)
            throws MissingArgumentException, InvalidArgumentException {
        String user = commandArgs.get("/default");
        String reviewBody = commandArgs.get("/review");
        String ratingAsString = commandArgs.get("/rating");

        if (Utility.isInvalidString(user)) {
            throw new MissingArgumentException("No user provided!");
        }

        if (Utility.isInvalidString(reviewBody)) {
            throw new MissingArgumentException("No review provided!");
        }

        Rating rating = Utility.toRating(ratingAsString);

        Review review = new Review(user, reviewBody, rating);
        reviewList.addReview(review);

        return String.format("Added review to list:\n%s", review);
    }
}
