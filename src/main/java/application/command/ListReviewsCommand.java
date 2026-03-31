package application.command;

import application.auth.AuthManager;
import application.review.ReviewList;
import application.storage.Storage;

/**
 * Class representing a command to list all reviews.
 */
public class ListReviewsCommand extends Command {
    /**
     * Returns a string representation of the review list.
     *
     * @param reviews the list of reviews
     * @param storage the storage object
     * @param manager the authentication manager
     * @return a string representation of the review list
     */
    @Override
    public String execute(
            ReviewList reviews,
            Storage storage,
            AuthManager manager
    ) {
        return reviews.toString();
    }
}
