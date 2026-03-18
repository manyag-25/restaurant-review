package application.review;

import application.parser.ArgumentParser;

import java.util.HashSet;
import java.util.Set;

/**
 * Enum representing the different criteria for ReviewList operations.
 */
public enum OperationCriterion {
    OVERALL_SCORE("overall scores"),
    FOOD_SCORE("food scores"),
    CLEANLINESS_SCORE("clean scores"),
    SERVICE_SCORE("service scores"),
    TAG_COUNT("tag count"),
    UNKNOWN("unknown");

    private final String operationCriterionString;

    /**
     * Constructor for OperationCriterion enum.
     * @param operationCriterionString the string representation of the criterion
     */
    OperationCriterion(String operationCriterionString) {
        this.operationCriterionString = operationCriterionString;
    }

    /**
     * Returns the sort criterion corresponding to the input string.
     * @param operationCriterionString the string representation of the criterion
     * @return the sort criterion corresponding to the input string
     */
    public static OperationCriterion getOperationCriterion(String operationCriterionString) {
        if (!ArgumentParser.isValidString(operationCriterionString)) {
            return UNKNOWN;
        }

        for (OperationCriterion criterion : OperationCriterion.values()) {
            if (criterion.operationCriterionString
                    .startsWith(operationCriterionString.toLowerCase())) {
                return criterion;
            }
        }
        return UNKNOWN;
    }

    /**
     * Returns a set of OperationCriteria given multiple criteria as a string.
     * @param OperationCriteriaAsString the string representation of the criteria
     * @return a set of OperationCriteria
     */
    public static Set<OperationCriterion> getOperationCriteria(
            String OperationCriteriaAsString
    ) {
        if (!ArgumentParser.isValidString(OperationCriteriaAsString)) {
            return new HashSet<>();
        }

        String[] criteria = OperationCriteriaAsString.split(" ");
        Set<OperationCriterion> criteriaSet = new HashSet<>();

        for (String criterion : criteria) {
            criteriaSet.add(getOperationCriterion(criterion));
        }

        return criteriaSet;
    }

    /**
     * Returns the string representation of the criterion.
     * @return the string representation of the criterion
     */
    @Override
    public String toString() {
        return operationCriterionString;
    }
}
