package application.review;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a single anonymous customer review.
 * <p>
 * Each review contains a structured rating, a written review body,
 * zero or more tags, and a resolved status for owner-side tracking.
 * </p>
 */
public class Review {
    private final String reviewBody;
    private final Rating rating;
    private final Set<Tag> tags;
    private boolean isResolved;

    /**
     * Constructs a {@code Review} with the specified review body, rating and tags.
     * <p>
     * The review is created with an unresolved status.
     * </p>
     *
     * @param reviewBody the written review content
     * @param rating the structured rating for the review
     * @param tags the set of tags associated with the review
     * @throws IllegalArgumentException if the review body is null or blank
     */
    public Review(String reviewBody, Rating rating, Set<Tag> tags) throws IllegalArgumentException {
        if (reviewBody == null || reviewBody.isBlank()) {
            throw new IllegalArgumentException("Review body cannot be null or blank.");
        }
        if (rating == null) {
            throw new IllegalArgumentException("Rating cannot be null.");
        }

        this.reviewBody = reviewBody.trim();
        this.rating = rating;
        this.tags = tags;
        this.isResolved = false;
    }

    /**
     * Constructs a {@code Review} with the specified review body and rating.
     * <p>
     * The review is created with no tags and an unresolved status.
     * </p>
     *
     * @param reviewBody the written review content
     * @param rating the structured rating for the review
     * @throws IllegalArgumentException if the review body is null or blank
     */
    public Review(String reviewBody, Rating rating) throws IllegalArgumentException {
        if (reviewBody == null || reviewBody.isBlank()) {
            throw new IllegalArgumentException("Review body cannot be null or blank.");
        }
        if (rating == null) {
            throw new IllegalArgumentException("Rating cannot be null.");
        }

        this.reviewBody = reviewBody.trim();
        this.rating = rating;
        this.tags = new HashSet<>();
        this.isResolved = false;
    }

    /**
     * Returns the written review body.
     *
     * @return the review body
     */
    public String getReviewBody() {
        return reviewBody;
    }

    /**
     * Returns the structured rating associated with this review.
     *
     * @return the rating
     */
    public Rating getRating() {
        return rating;
    }

    /**
     * Returns an unmodifiable view of the tags attached to this review.
     *
     * @return an unmodifiable set of tags
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns whether this review has been marked as resolved.
     *
     * @return {@code true} if the review is resolved, {@code false} otherwise
     */
    public boolean isResolved() {
        return isResolved;
    }

    /**
     * Adds the specified tag to this review.
     *
     * @param tag the tag to add
     * @throws IllegalArgumentException if the tag is null
     */
    public void addTag(Tag tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Tag cannot be null.");
        }
        tags.add(tag);
    }

    /**
     * Removes the specified tag from this review.
     *
     * @param tag the tag to remove
     * @throws IllegalArgumentException if the tag is null
     */
    public void removeTag(Tag tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Tag cannot be null.");
        }
        tags.remove(tag);
    }

    /**
     * Returns whether this review contains the specified tag.
     *
     * @param tag the tag to check for
     * @return {@code true} if the review contains the tag, {@code false} otherwise
     */
    public boolean containsTag(Tag tag) {
        if (tag == null) {
            return false;
        }
        return tags.contains(tag);
    }

    /**
     * Marks this review as resolved.
     */
    public void markResolved() {
        isResolved = true;
    }

    /**
     * Marks this review as outstanding.
     */
    public void markOutstanding() {
        isResolved = false;
    }

    /**
     * Returns a string representation of this review.
     *
     * @return a formatted string containing rating, resolution status, tags, and review body
     */
    @Override
    public String toString() {
        String resolvedStatus = isResolved ? "Resolved" : "Outstanding";
        String tagDisplay = tags.isEmpty() ? "No tags" : tags.toString();

        return String.format(
                "%s%nStatus: %s%nTags: %s%nReview: %s",
                rating,
                resolvedStatus,
                tagDisplay,
                reviewBody
        );
    }
}