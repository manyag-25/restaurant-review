package application.review;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import application.exception.InvalidArgumentException;

/**
 * Represents a collection of reviews and provides collection-level operations
 * such as add, delete, filter, sort, and resolve status updates.
 */
public class ReviewList {
    private final List<Review> reviews;

    /**
     * Constructs a {@code ReviewList} with the specified list of reviews.
     *
     * @param reviews the initial list of reviews
     * @throws IllegalArgumentException if the provided list is null
     */
    public ReviewList(List<Review> reviews) {
        if (reviews == null) {
            throw new IllegalArgumentException("Review list cannot be null.");
        }
        this.reviews = new ArrayList<>(reviews);
    }

    /**
     * Constructs an empty {@code ReviewList}.
     */
    public ReviewList() {
        this.reviews = new ArrayList<>();
    }

    /**
     * Adds a review to the list.
     *
     * @param review the review to add
     * @throws IllegalArgumentException if the review is null
     */
    public void addReview(Review review) {
        if (review == null) {
            throw new IllegalArgumentException("Review cannot be null.");
        }
        reviews.add(review);
    }

    /**
     * Deletes the review at the specified 1-based index.
     *
     * @param index the 1-based index of the review to delete
     * @return the deleted review
     * @throws InvalidArgumentException if the index is invalid
     */
    public Review deleteReview(int index) throws InvalidArgumentException {
        validateIndex(index);
        return reviews.remove(index - 1);
    }

    /**
     * Returns the review at the specified 1-based index.
     *
     * @param index the 1-based index of the review to retrieve
     * @return the review at the specified index
     * @throws InvalidArgumentException if the index is invalid
     */
    public Review getReview(int index) throws InvalidArgumentException {
        validateIndex(index);
        return reviews.get(index - 1);
    }

    /**
     * Marks the review at the specified 1-based index as resolved.
     *
     * @param index the 1-based index of the review
     * @throws InvalidArgumentException if the index is invalid
     */
    public void markResolved(int index) throws InvalidArgumentException {
        validateIndex(index);
        reviews.get(index - 1).markResolved();
    }

    /**
     * Marks the review at the specified 1-based index as outstanding.
     *
     * @param index the 1-based index of the review
     * @throws InvalidArgumentException if the index is invalid
     */
    public void markOutstanding(int index) throws InvalidArgumentException {
        validateIndex(index);
        reviews.get(index - 1).markOutstanding();
    }

    /**
     * Returns whether the specified 1-based index is valid.
     *
     * @param index the index to check
     * @return {@code true} if the index is valid, {@code false} otherwise
     */
    public boolean isValidIndex(int index) {
        return index >= 1 && index <= reviews.size();
    }

    /**
     * Returns a defensive copy of all reviews in this list.
     *
     * @return a copy of the reviews
     */
    public List<Review> getAllReviews() {
        return new ArrayList<>(reviews);
    }

    /**
     * Filters the list of reviews based on the specified criteria.
     *
     * <p>
     * A review must match ALL criteria to be included in the filtered list.
     * 1. If tagsToInclude is not empty, the review must contain ALL tags in tagsToInclude.
     * 2. If tagsToExclude is not empty, the review must contain NONE of the tags in tagsToExclude.
     * 3. If isResolved is not null, the review must match the resolved status.
     * 4. If minimumScore is not 0.0, the review must have a score greater than or equal to minimumScore.
     * 5. If maximumScore is not 5.0, the review must have a score less than or equal to maximumScore.
     * </p>
     *
     * @param tagsToInclude 1 or more tags to include in the filter
     * @param tagsToExclude 1 or more tags to exclude from the filter
     * @param filterCriteria 1 or more OperationCriterion to filter by
     * @param isResolved whether to filter by resolved status
     * @param minimumScore minimum score to filter by
     * @param maximumScore maximum score to filter by
     * @return a filtered list of reviews that meet the specified criteria
     */
    public ReviewList filter(
            Set<Tag> tagsToInclude,
            Set<Tag> tagsToExclude,
            Set<OperationCriterion> filterCriteria,
            Boolean isResolved,
            double minimumScore,
            double maximumScore
    ) {
        Set<Function<Review, Double>> filterCriteriaFunctions = getOperationCriteriaFunctions(filterCriteria);

        //runs the list through all the filters available, default or not
        //default values should not affect results if not specified
        List<Review> filteredReviews = reviews.stream()
                //check if the review matches ALL required tags
                .filter(review -> review.containsAllMatchingTags(tagsToInclude))
                //check if the review matches NONE of the excluded tags
                .filter(review -> review.containsNoMatchingTags(tagsToExclude))
                //check if we should check by resolved status, if so, then check if the review matches resolved status
                .filter(review -> isResolved == null || review.isResolved() == isResolved)
                //for each HOF, apply to the review and ensure that all reviews are within range of min and max score
                .filter(review ->
                                filterCriteriaFunctions
                                        .stream()
                                        .allMatch(filterCriterionFunction ->
                                                filterCriterionFunction.apply(review) > minimumScore &&
                                                filterCriterionFunction.apply(review) < maximumScore
                                        )
                )
                .toList();

        return new ReviewList(filteredReviews);
    }

    /**
     * Returns a set of functions that extract the specified operation criteria from a review.
     *
     * <p>
     * UNKNOWN OperationCriterion will be discarded.
     * </p>
     *
     * @param operationCriteria the set of operation criteria to extract
     * @return a set of functions that extract the specified operation criteria from a review
     */
    private Set<Function<Review, Double>> getOperationCriteriaFunctions(Set<OperationCriterion> operationCriteria) {
        return operationCriteria
                .stream()
                .map(operationCriterion -> {
                            try {
                                return getOperationCriterionFunction(operationCriterion);
                            } catch (InvalidArgumentException ignored) {
                                return null; //set UNKNOWN criterion to null
                            }
                        }
                )
                .filter(Objects::nonNull) //discard the UNKNOWN criteria
                .collect(Collectors.toSet());
    }

    /**
     * Sorts the given review list based on the given criterion and sort order.
     *
     * @param sortCriterion the criterion to sort by
     * @param sortOrder the sort order (ascending or descending)
     * @param reviews the list of reviews to sort
     * @return a new sorted list of reviews
     * @throws InvalidArgumentException if the sort order is invalid
     */
    public ReviewList sort(
            OperationCriterion sortCriterion,
            SortOrder sortOrder,
            ReviewList reviews
    ) throws InvalidArgumentException {
        Function<Review, Double> sortCriterionFunction = getOperationCriterionFunction(sortCriterion);

        switch (sortOrder) {
        case ASCENDING:
            return reviews.sortByAscending(sortCriterionFunction);
        case DESCENDING:
            return reviews.sortByDescending(sortCriterionFunction);
        case UNKNOWN:
        default:
            throw new InvalidArgumentException("Invalid sort order!");
        }
    }

    /**
     * Returns a new list of reviews sorted by the specified criterion in descending order.
     *
     * @param sortCriterionFunction the criterion to sort by
     * @return a new list of reviews sorted by the specified criterion in descending order.
     */
    private ReviewList sortByDescending(Function<Review, Double> sortCriterionFunction) {
        List<Review> sortedList = reviews.stream()
                .sorted(Comparator.comparing(sortCriterionFunction).reversed())
                .toList();
        return new ReviewList(sortedList);
    }

    /**
     * Returns a new list of reviews sorted by the specified criterion in ascending order.
     *
     * @param sortCriterionFunction the criterion to sort by
     * @return a new list of reviews sorted by the specified criterion in ascending order.
     */
    private ReviewList sortByAscending(Function<Review, Double> sortCriterionFunction) {
        List<Review> sortedList = reviews.stream()
                .sorted(Comparator.comparing(sortCriterionFunction))
                .toList();
        return new ReviewList(sortedList);
    }

    /**
     * Returns a function that extracts the sort criterion value from a review.
     *
     * @param operationCriterion the operation criterion
     * @return a function that extracts the sort criterion value from a review
     */
    private Function<Review, Double> getOperationCriterionFunction(
            OperationCriterion operationCriterion
    ) throws InvalidArgumentException {
        Function<Review, Double> sortCriterionFunction;

        switch (operationCriterion) {
        case OVERALL_SCORE:
            sortCriterionFunction = review -> review.getRating().getOverallScore();
            break;
        case FOOD_SCORE:
            sortCriterionFunction = review -> review.getRating().getFoodScore();
            break;
        case SERVICE_SCORE:
            sortCriterionFunction = review -> review.getRating().getServiceScore();
            break;
        case CLEANLINESS_SCORE:
            sortCriterionFunction = review -> review.getRating().getCleanlinessScore();
            break;
        case TAG_COUNT:
            sortCriterionFunction = review -> (double) review.getTags().size();
            break;
        case UNKNOWN:
        default:
            throw new InvalidArgumentException("Invalid criterion specified!");
        }

        return sortCriterionFunction;
    }

    /**
     * Returns the number of reviews in the list.
     *
     * @return the number of reviews
     */
    public int size() {
        return reviews.size();
    }

    /**
     * Returns whether the list is empty.
     * @return true if the list is empty, false otherwise
     */
    public boolean isEmpty() {
        return reviews.isEmpty();
    }

    /**
     * Returns a string representation of the list of reviews.
     *
     * @return a formatted string representation of all reviews in the list
     */
    @Override
    public String toString() {
        if (reviews.isEmpty()) {
            return "Review list is empty.";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < reviews.size(); i++) {
            sb.append(i + 1)
                    .append(".")
                    .append(System.lineSeparator())
                    .append(reviews.get(i));

            if (i < reviews.size() - 1) {
                sb.append(System.lineSeparator()).append(System.lineSeparator());
            }
        }

        return sb.toString();
    }

    /**
     * Validates the specified 1-based index.
     *
     * @param index the index to validate
     * @throws InvalidArgumentException if the index is invalid
     */
    private void validateIndex(int index) throws InvalidArgumentException {
        if (!isValidIndex(index)) {
            throw new InvalidArgumentException("Invalid review index!");
        }
    }
}