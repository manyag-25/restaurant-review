package application.command;

import application.exception.InvalidArgumentException;
import application.exception.MissingArgumentException;
import application.parser.ArgumentParser;
import application.review.OperationCriterion;
import application.review.ReviewList;
import application.review.Tag;
import application.storage.Storage;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

//filter /hastag /notag /min food service /minscore /max /maxscore /r /o
public class FilterReviewsCommand extends Command {
    public static final Set<String> DELIMITERS = Set.of(
            "/hastag", //if a review contains the list of tags provided
            "/notag", //if a review does not contain the list of tags provided
            "/by", //numeric criterion: food, cleanliness, service, overall, tag count see @OperationCriterion
            "/resolved", //if the review is resolved
            "/minscore", //the minimum score to filter by
            "/maxscore" //the maximum score to filter by
    );
    private final Set<Tag> tagsToInclude;
    private final Set<Tag> tagsToExclude;
    private final Set<OperationCriterion> filterCriteria;
    private final Boolean isResolved;
    private final double minimumScore;
    private final double maximumScore;

    public FilterReviewsCommand(Map<String, String> commandArgs)
            throws InvalidArgumentException, MissingArgumentException {
        String tagsToIncludeAsString = commandArgs.get("/hastag");
        String tagsToExcludeAsString = commandArgs.get("/notag");
        String filterCriteriaAsString = commandArgs.get("/by");

        String isResolvedAsString = commandArgs.getOrDefault("/resolved", null);

        String minimumScoreAsString = commandArgs.getOrDefault("/minscore", "0.0");
        String maximumScoreAsString = commandArgs.getOrDefault("/maxscore", "5.0");

        //defaults to an empty set if not specified
        this.tagsToInclude = Tag.toTags(tagsToIncludeAsString);
        this.tagsToExclude = Tag.toTags(tagsToExcludeAsString);
        this.filterCriteria = OperationCriterion.getOperationCriteria(filterCriteriaAsString);

        //if the user does not specify, we will not filter by resolved status
        this.isResolved = isResolvedAsString == null ? null : Boolean.parseBoolean(isResolvedAsString);

        //these defaults will not affect the filtering if the user does not specify them
        //if unspecified, these should not throw an exception
        this.minimumScore = ArgumentParser.toDouble(minimumScoreAsString); //defaults to 0.0 if not specified
        this.maximumScore = ArgumentParser.toDouble(maximumScoreAsString); //defaults to 5.0 if not specified
    }

    /**
     * Executes the command to filter the list of reviews.
     * @param reviews the list of reviews to filter
     * @param storage the storage object
     * @return a string representation of the filtered list of reviews
     */
    @Override
    public String execute(ReviewList reviews, Storage storage) {
        ReviewList filteredReviews = reviews.filter(
                tagsToInclude,
                tagsToExclude,
                filterCriteria,
                isResolved,
                minimumScore,
                maximumScore
        );

        return String.format("""
                Filter criteria:
                Tags to include: %s
                Tags to exclude: %s
                Filter criteria: %s
                Resolved status: %s
                Minimum score: %s
                Maximum score: %s
                Filtered reviews:
                %s""",
                tagsToInclude.isEmpty() ? "None specified" : tagsToInclude,
                tagsToExclude.isEmpty() ? "None specified" : tagsToExclude,
                filterCriteria.isEmpty() ? "None specified" : filterCriteria,
                isResolved == null ? "Not specified" : isResolved,
                minimumScore == 0.0 ? "Not specified" : minimumScore,
                maximumScore == 5.0 ? "Not specified" : maximumScore,
                filteredReviews
        );
    }
}
