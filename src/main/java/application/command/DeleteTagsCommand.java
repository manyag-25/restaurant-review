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
import application.review.Tag;
import application.storage.Storage;

/**
 * Class representing a command to delete tags from a review.
 */
public class DeleteTagsCommand extends Command {
    public static final Set<String> DELIMITERS = Set.of("/default", "/tag");
    private final int index;
    private final Set<Tag> tagsToDelete;

    /**
     * Constructor for DeleteTagCommand class.
     *
     * @param commandArgs the arguments of the command
     * @throws InvalidArgumentException if the index is not a number
     * @throws MissingArgumentException if the index is missing
     */
    public DeleteTagsCommand(Map<String, String> commandArgs)
            throws InvalidArgumentException, MissingArgumentException {
        String indexAsString = commandArgs.get("/default");
        String tagsAsString = commandArgs.get("/tag");

        this.index = ArgumentParser.toInt(indexAsString);
        this.tagsToDelete = Tag.toTags(tagsAsString);

        if (tagsToDelete.isEmpty()) {
            throw new InvalidArgumentException("No tags provided!");
        }
    }


    /**
     * Executes the command to delete tags from a review.
     *
     * <p>
     * Tags that do not exist in the review are not deleted.
     * </p>
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
        Review review = reviews.getReview(index);
        Set<Tag> existingTags = review.getMatchingTags(tagsToDelete);
        Set<Tag> nonExistentTags = review.getNonMatchingTags(tagsToDelete);

        existingTags.forEach(review::removeTag);
        storage.saveReviews(reviews);

        return String.format("""
                Tags that do not exist in review: %s
                Tags deleted: %s
                Updated review:
                %s""", nonExistentTags, existingTags, review);
    }
}
