package application.condition;

import application.review.Review;

/**
 * Class representing an unknown condition.
 */
public class UnknownCondition extends Condition {
    /**
     * Constructor for an unknown condition.
     */
    public UnknownCondition() {
        super();
    }

    /**
     * Always returns false.
     * @return false
     */
    @Override
    public boolean shouldDisplay() {
        return false;
    }

    /**
     * Always returns false.
     * @param review the review to check
     * @return false
     */
    @Override
    public boolean isSatisfiedBy(Review review) {
        return false;
    }

    /**
     * Returns a string representation of the unknown condition.
     * @return a string representation of the unknown condition
     */
    @Override
    public String toString() {
        return "Unknown Condition";
    }
}
