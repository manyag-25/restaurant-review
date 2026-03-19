package application.command;

import application.condition.Condition;
import application.exception.InvalidArgumentException;
import application.exception.MissingArgumentException;
import application.parser.ConditionParser;
import application.review.ReviewList;
import application.review.Tag;
import application.storage.Storage;

import java.util.Map;
import java.util.Set;

//filter /hastag /notag /min food service /minscore /max /maxscore /r /o
public class FilterReviewsCommand extends Command {
    public static final Set<String> DELIMITERS = Set.of(
            "/hastag", //if a review contains the list of tags provided
            "/notag", //if a review does not contain the list of tags provided
            "/resolved", //if the review is resolved
            "/condition" //a wrapper class indicating some boolean comparison (numeric)
    );
    private final Set<Tag> tagsToInclude;
    private final Set<Tag> tagsToExclude;
    private final Set<Condition> filterConditions;
    private final Boolean isResolved;

    public FilterReviewsCommand(Map<String, String> commandArgs)
            throws InvalidArgumentException, MissingArgumentException {
        String tagsToIncludeAsString = commandArgs.get("/hastag");
        String tagsToExcludeAsString = commandArgs.get("/notag");
        String filterConditionsAsString = commandArgs.get("/condition");
        String isResolvedAsString = commandArgs.getOrDefault("/resolved", null);

        //defaults to an empty set if not specified
        this.tagsToInclude = Tag.toTags(tagsToIncludeAsString);
        this.tagsToExclude = Tag.toTags(tagsToExcludeAsString);
        this.filterConditions = ConditionParser.getConditions(filterConditionsAsString);

        //if the user does not specify, we will not filter by resolved status
        this.isResolved = isResolvedAsString == null ? null : Boolean.parseBoolean(isResolvedAsString);
    }

    /**
     * Executes the command to filter the list of reviews.
     * @param reviews the list of reviews to filter
     * @param storage the storage object
     * @return a string representation of the filtered list of reviews
     */
    @Override
    public String execute(ReviewList reviews, Storage storage) throws InvalidArgumentException {
        ReviewList filteredReviews = reviews.filter(
                tagsToInclude,
                tagsToExclude,
                filterConditions,
                isResolved
        );

        return String.format("""
                Filter criteria:
                Tags to include: %s
                Tags to exclude: %s
                Filter criteria: %s
                Resolved status: %s
                Filtered reviews:
                %s""",
                tagsToInclude.isEmpty() ? "None specified" : tagsToInclude,
                tagsToExclude.isEmpty() ? "None specified" : tagsToExclude,
                filterConditions.isEmpty() ? "None specified" : filterConditions.stream().filter(Condition::shouldDisplay).toList(),
                isResolved == null ? "Not specified" : isResolved,
                filteredReviews
        );
    }
}
