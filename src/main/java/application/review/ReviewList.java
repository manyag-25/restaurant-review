package application.review;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import application.condition.Condition;
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
     * 3. If filterConditions is not empty, the review must satisfy ALL conditions.
     * 4. If isResolved is not null, the review must match the resolved status.
     * </p>
     *
     * @param tagsToInclude 1 or more tags to include in the filter
     * @param tagsToExclude 1 or more tags to exclude from the filter
     * @param filterConditions 1 or more Criterion to filter by
     * @param isResolved whether to filter by resolved status
     * @return a filtered list of reviews that meet the specified criteria
     */
    public ReviewList filter(
            Set<Tag> tagsToInclude,
            Set<Tag> tagsToExclude,
            Set<Condition> filterConditions,
            Boolean isResolved
    ) {
        //runs the list through all the filters available, default or not
        //default values should not affect results if not specified
        List<Review> filteredReviews = reviews.stream()
                //check if the review matches ALL required tags
                .filter(review -> review.containsAllMatchingTags(tagsToInclude))
                //check if the review matches NONE of the excluded tags
                .filter(review -> review.containsNoMatchingTags(tagsToExclude))
                //check if we should check by resolved status, if so, then check if the review matches resolved status
                .filter(review -> isResolved == null || review.isResolved() == isResolved)
                //for each HOF, apply to the review and ensure that all reviews satisfy all conditions
                .filter(review ->
                        filterConditions.stream()
                                .allMatch(filterCriterionFunction ->
                                        filterCriterionFunction.isSatisfiedBy(review)
                                )
                )
                .toList();

        return new ReviewList(filteredReviews);
    }

    /**
     * Sorts the given review list based on the given criterion and sort order.
     *
     * @param sortCriterion the criterion function to sort by
     * @param sortOrder the sort order (ascending or descending)
     * @param reviews the list of reviews to sort
     * @return a new sorted list of reviews
     * @throws InvalidArgumentException if the sort order is invalid
     */
    public ReviewList sort(
            Criterion sortCriterion,
            SortOrder sortOrder,
            ReviewList reviews
    ) throws InvalidArgumentException {
        switch (sortOrder) {
        case ASCENDING:
            return reviews.sortByAscending(sortCriterion);
        case DESCENDING:
            return reviews.sortByDescending(sortCriterion);
        case UNKNOWN:
        default:
            throw new InvalidArgumentException("Invalid sort order!");
        }
    }

    /**
     * Returns a new list of reviews sorted by the specified criterion in descending order.
     *
     * @param sortCriterion the criterion to sort by
     * @return a new list of reviews sorted by the specified criterion in descending order.
     */
    private ReviewList sortByDescending(Criterion sortCriterion) {
        List<Review> sortedList = reviews.stream()
                .sorted(Comparator.comparing(sortCriterion.getFunction())
                        .reversed())
                .toList();
        return new ReviewList(sortedList);
    }

    /**
     * Returns a new list of reviews sorted by the specified criterion in ascending order.
     *
     * @param sortCriterion the criterion to sort by
     * @return a new list of reviews sorted by the specified criterion in ascending order.
     */
    private ReviewList sortByAscending(Criterion sortCriterion) {
        List<Review> sortedList = reviews.stream()
                .sorted(Comparator.comparing(sortCriterion.getFunction()))
                .toList();
        return new ReviewList(sortedList);
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