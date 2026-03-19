package application.condition;

import application.review.Criterion;
import application.review.Review;

/**
 * Class representing a condition that checks if a criterion is less than a value.
 */
public class LessThanCondition extends Condition {
    /**
     * Constructor for a LessThanCondition.
     * @param criterion the review criterion to compare
     * @param value the value to compare the criterion to
     */
    public LessThanCondition(Criterion criterion, double value) {
        super(criterion, value);
    }

    /**
     * Checks if the criterion of the review is less than the value of this condition.
     * @param review the review to check
     * @return true if the criterion of the review is less than the value of this condition,
     * false otherwise
     */
    @Override
    public boolean isSatisfiedBy(Review review) {
        return criterion.getFunction().apply(review) < value;
    }

    /**
     * Returns a string representation of the condition.
     * @return a string representation of the condition
     */
    @Override
    public String toString() {
        return criterion + " " + ConditionType.LESS_THAN + " " + value;
    }
}
