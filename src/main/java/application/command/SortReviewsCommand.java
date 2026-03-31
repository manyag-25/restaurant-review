package application.command;

import java.util.Map;
import java.util.Set;

import application.auth.AuthManager;
import application.exception.InvalidArgumentException;
import application.review.Criterion;
import application.review.ReviewList;
import application.review.SortOrder;
import application.storage.Storage;

/**
 * Class representing a command to sort reviews.
 */
public class SortReviewsCommand extends Command {
    public static final Set<String> DELIMITERS = Set.of("/default", "/by");
    private final SortOrder sortOrder;
    private final Criterion sortCriterion;


    /**
     * Constructor for SortReviewsCommand class.
     *
     * @param commandArgs the arguments of the command
     */
    public SortReviewsCommand(Map<String, String> commandArgs) {
        String sortOrderAsString = commandArgs.get("/default");
        String sortCriterionAsString = commandArgs.get("/by");

        this.sortOrder = SortOrder.getSortOrder(sortOrderAsString);
        this.sortCriterion = Criterion.getCriterion(sortCriterionAsString);
    }

    /**
     * Executes the command to sort reviews.
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
    ) throws InvalidArgumentException {
        if (reviews.isEmpty()) {
            return "No reviews to sort!";
        }

        ReviewList sortedReviewList = reviews.sort(sortCriterion, sortOrder, reviews);

        return String.format("""
                Sorted by %s in %s order:
                %s
                """,
                sortCriterion,
                sortOrder,
                sortedReviewList
        );
    }
}
