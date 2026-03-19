package application.condition;

import application.review.Criterion;
import application.review.Review;

/**
 * Class representing a condition that checks if a criterion is not equal to a value.
 */
public class NotEqualsToCondition extends Condition {
    /**
     * Constructor for a NotEqualsToCondition.
     * @param criterion the review criterion to compare
     * @param value the value to compare the criterion to
     */
    public NotEqualsToCondition(Criterion criterion, double value) {
        super(criterion, value);
    }

    /**
     * Checks if the criterion of the review is not equal to the value of this condition.
     * @param review the review to check
     * @return true if the criterion of the review is not equal to the value of this condition,
     */
    @Override
    public boolean isSatisfiedBy(Review review) {
        return criterion.getFunction().apply(review) != value;
    }

    /**
     * Returns a string representation of the condition.
     * @return a string representation of the condition
     */
    @Override
    public String toString() {
        return criterion + " " + ConditionType.NOT_EQUALS + " " + value;
    }
}
