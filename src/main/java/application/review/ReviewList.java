package application.review;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
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
     * Returns all reviews that contain the specified tag.
     *
     * @param tag the tag to filter by
     * @return a list of reviews containing the tag
     * @throws IllegalArgumentException if the tag is null
     */
    public List<Review> filterByTag(Tag tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Tag cannot be null.");
        }

        return reviews.stream()
                .filter(review -> review.containsTag(tag))
                .collect(Collectors.toList());
    }

    /**
     * Returns all reviews with the specified resolved status.
     *
     * @param isResolved the resolved status to match
     * @return a list of reviews matching the resolved status
     */
    public List<Review> filterByResolvedStatus(boolean isResolved) {
        return reviews.stream()
                .filter(review -> review.isResolved() == isResolved)
                .collect(Collectors.toList());
    }

    /**
     * Returns all reviews whose overall score is at least the specified minimum.
     *
     * @param minimumScore the minimum overall score
     * @return a list of reviews meeting the score threshold
     */
    public List<Review> filterByMinimumOverallScore(double minimumScore) {
        return reviews.stream()
                .filter(review -> review.getRating().getOverallScore() >= minimumScore)
                .collect(Collectors.toList());
    }

    /**
     * Returns a new list of reviews sorted by the specified criterion in descending order.
     *
     * @param sortCriterion the criterion to sort by
     * @return a new list of reviews sorted by the specified criterion in descending order.
     */
    public ReviewList sortByDescending(Function<Review, Double> sortCriterion) {
        List<Review> sortedList = reviews.stream()
                .sorted(Comparator.comparing(sortCriterion).reversed())
                .toList();
        return new ReviewList(sortedList);
    }

    /**
     * Returns a new list of reviews sorted by the specified criterion in ascending order.
     *
     * @param sortCriterion the criterion to sort by
     * @return a new list of reviews sorted by the specified criterion in ascending order.
     */
    public ReviewList sortByAscending(Function<Review, Double> sortCriterion) {
        List<Review> sortedList = reviews.stream()
                .sorted(Comparator.comparing(sortCriterion))
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
     * Returns a string representation of the list of reviews.
     *
     * @return a formatted string representation of all reviews in the list
     */
    @Override
    public String toString() {
        if (reviews.isEmpty()) {
            return "No reviews yet!";
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