package application.command;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
     * @param storage the storage object
     * @return a string representation of the command result
     * @throws MissingArgumentException if any argument is missing
     * @throws InvalidArgumentException if any argument is in the wrong format
     */
    @Override
    public String execute(ReviewList reviewList, Storage storage)
            throws MissingArgumentException, InvalidArgumentException {
        //get all the arguments
        String reviewBody = commandArgs.get("/default");
        String foodScoreAsString = commandArgs.get("/food");
        String cleanlinessScoreAsString = commandArgs.get("/clean");
        String serviceScoreAsString = commandArgs.get("/service");
        String tagsAsString = commandArgs.get("/tag");

        //convert scores to doubles
        double foodScore = ArgumentParser.toDouble(foodScoreAsString);
        double cleanlinessScore = ArgumentParser.toDouble(cleanlinessScoreAsString);
        double serviceScore = ArgumentParser.toDouble(serviceScoreAsString);

        //create new Rating object with scores
        Rating rating = new Rating(foodScore, cleanlinessScore, serviceScore);

        if (ArgumentParser.isValidString(tagsAsString)) {}
        //separate tags in string format
        String[] listOfTagsAsString = tagsAsString.trim().split(" ");

        //map each tag to a Tag object
        Set<Tag> tags = Arrays.stream(listOfTagsAsString)
                .map(Tag::new)
                .collect(Collectors.toSet());

        //create new Review object
        Review review = new Review(reviewBody, rating, tags);

        reviewList.addReview(review);

        return String.format("Added review to list:\n%s", review);
    }
}
