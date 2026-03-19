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
        Set<Tag> existingTags = review.getMatchingTags(tagsToDelete);

        //get the new tags that are not in the review
        Set<Tag> nonExistentTags = review.getNonMatchingTags(tagsToDelete);

        //delete the existing tags from the review
        existingTags.forEach(review::removeTag);

        return String.format("""
                Tags that do not exist in review: %s
                Tags deleted: %s
                Updated review:
                %s""", nonExistentTags, existingTags, review);
    }
}
