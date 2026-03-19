package application.command;

import application.exception.InvalidArgumentException;
import application.exception.MissingArgumentException;
import application.parser.ArgumentParser;
import application.review.Review;
import application.review.ReviewList;
import application.review.Tag;
import application.storage.Storage;

import java.util.Map;
import java.util.Set;

/**
 * Class representing a command to add tags to a review.
 */
public class AddTagsCommand extends Command {
    public static final Set<String> DELIMITERS = Set.of("/default", "/tag");
    private final int index;
    private final Set<Tag> tagsToAdd;

    /**
     * Constructor for AddTagCommand class.
     *
     * @param commandArgs the arguments of the command
     * @throws InvalidArgumentException if the index is not a number
     * @throws MissingArgumentException if the index is missing
     */
    public AddTagsCommand(Map<String, String> commandArgs)
            throws InvalidArgumentException, MissingArgumentException {
        String indexAsString = commandArgs.get("/default");
        String tagsAsString = commandArgs.get("/tag");

        this.index = ArgumentParser.toInt(indexAsString);
        this.tagsToAdd = Tag.toTags(tagsAsString);

        if (tagsToAdd.isEmpty()) {
            throw new InvalidArgumentException("No tags provided!");
        }
    }

    /**
     * Executes the command to add tags to a review.
     *
     * <p>
     * Tags that already exist in the review are not added again.
     * </p>
     *
     * @param reviews the list of reviews
     * @param storage the storage object
     * @return a string representation of the command result
     * @throws InvalidArgumentException if any argument is in the wrong format
     */
    @Override
    public String execute(
            ReviewList reviews,
            Storage storage
    ) throws InvalidArgumentException {
        //get the review object and its tags
        Review review = reviews.getReview(index);

        //get the new tags that are already in the review
        Set<Tag> existingTags = review.getMatchingTags(tagsToAdd);

        //get the new tags that are not in the review
        Set<Tag> nonExistentTags = review.getNonMatchingTags(tagsToAdd);

        //add the non-existent tags to the review
        nonExistentTags.forEach(review::addTag);

        return String.format("""
                Existing tags not added: %s
                New tags added: %s
                Updated review:
                %s""", existingTags, nonExistentTags, review);
    }
}
