package application.command;

import application.exception.InvalidArgumentException;
import application.exception.MissingArgumentException;
import application.review.ReviewList;
import application.sorter.ReviewSorter;
import application.storage.Storage;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class SortReviewsCommand extends Command {
    public static final Set<String> DELIMITERS = Set.of("/default", "/by");
    private final Map<String, String> commandArgs;

    public SortReviewsCommand(Map<String, String> commandArgs) {
        this.commandArgs = commandArgs;
    }

    @Override
    public String execute(
            ReviewList reviewList,
            Storage storage
    ) throws MissingArgumentException, InvalidArgumentException, IOException {
        String sortOrderString = commandArgs.get("/default");
        String sortCriterionString = commandArgs.get("/by");

        ReviewList sortedReviewList = ReviewSorter.sort(sortCriterionString, sortOrderString, reviewList);

        return String.format("""
                Sorted by %s in %s order:
                %s
                """,
                sortCriterionString,
                sortOrderString,
                sortedReviewList
        );
    }
}