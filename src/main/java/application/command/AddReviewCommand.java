package application.command;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import application.auth.AuthManager;
import application.exception.InvalidArgumentException;
import application.exception.MissingArgumentException;
import application.parser.ArgumentParser;
import application.review.Rating;
import application.review.Review;
import application.review.ReviewList;
import application.review.Tag;
import application.storage.Storage;

/**
 * Class representing a command to add a review.
 */
public class AddReviewCommand extends Command {
    public static final Set<String> DELIMITERS = Set.of("/default", "/food", "/clean", "/service", "/tag");
    private final String reviewBody;
    private final Double foodScore;
    private final Double cleanlinessScore;
    private final Double serviceScore;
    private final Set<Tag> tagsToAdd;

    /**
     * Constructor for AddReviewCommand class.
     *
     * @param commandArgs the arguments of the command
     * @throws InvalidArgumentException if any argument is in the wrong format
     * @throws MissingArgumentException if any argument is missing
     */
    public AddReviewCommand(Map<String, String> commandArgs)
            throws InvalidArgumentException, MissingArgumentException {
        String reviewBody = commandArgs.get("/default");
        String foodScoreAsString = commandArgs.get("/food");
        String cleanlinessScoreAsString = commandArgs.get("/clean");
        String serviceScoreAsString = commandArgs.get("/service");
        String tagsToAddAsString = commandArgs.get("/tag");

        this.reviewBody = reviewBody;
        this.foodScore = ArgumentParser.toDouble(foodScoreAsString);
        this.cleanlinessScore = ArgumentParser.toDouble(cleanlinessScoreAsString);
        this.serviceScore = ArgumentParser.toDouble(serviceScoreAsString);
        this.tagsToAdd = Tag.toTags(tagsToAddAsString);
    }

    /**
     * Executes the command to add a review to the list.
     *
     * @param reviews the list of reviews
     * @param storage the storage object
     * @param manager the authentication manager
     * @return a string representation of the command result
     * @throws InvalidArgumentException if any argument is in the wrong format
     */
    @Override
    public String execute(
            ReviewList reviews,
            Storage storage,
            AuthManager manager
    ) throws InvalidArgumentException, IOException {
        Rating rating = new Rating(foodScore, cleanlinessScore, serviceScore);
        Review review = new Review(reviewBody, rating, tagsToAdd);

        reviews.addReview(review);
        storage.saveReviews(reviews);

        return String.format("Added review to list:\n%s", review);
    }
}
