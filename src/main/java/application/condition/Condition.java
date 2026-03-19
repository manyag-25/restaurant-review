package application.condition;

import application.review.Criterion;
import application.review.Review;

/**
 * Abstract class representing a boolean condition.
 */
public abstract class Condition {
    protected Criterion criterion;
    protected double value;

    /**
     * Constructor for a condition.
     * @param criterion the criterion to compare the review against
     * @param value the value to compare the criterion to
     */
    public Condition(Criterion criterion, double value) {
        this.criterion = criterion;
        this.value = value;
    }

    /**
     * Default constructor for a condition.
     */
    public Condition() { }

    /**
     * Returns whether the condition should be displayed.
     * @return true if the condition should be displayed, false otherwise
     */
    public boolean shouldDisplay() {
        return true;
    }

    /**
     * Abstract method to determine if the condition is satisfied by a review.
     * @param review the review to check
     * @return true if the condition is satisfied, false otherwise
     */
    public abstract boolean isSatisfiedBy(Review review);

    @Override
    public abstract String toString();
}
