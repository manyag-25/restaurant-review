package application.review;

import application.parser.ArgumentParser;

import java.util.function.Function;

/**
 * Enum representing the different criteria for ReviewList operations.
 */
public enum Criterion {
    OVERALL_SCORE("overall scores", review -> review.getRating().getOverallScore()),
    FOOD_SCORE("food scores", review -> review.getRating().getFoodScore()),
    CLEANLINESS_SCORE("clean scores", review -> review.getRating().getCleanlinessScore()),
    SERVICE_SCORE("service scores", review -> review.getRating().getServiceScore()),
    TAG_COUNT("tag count", review -> (double) review.getTags().size()),
    UNKNOWN("unknown", review -> 0.0);

    private final String criterionString;
    private final Function<Review, Double> criterionFunction;

    /**
     * Constructor for Criterion enum.
     * @param criterionString the string representation of the criterion
     * @param criterionFunction the function to extract the criterion value from a review
     */
    Criterion(String criterionString, Function<Review, Double> criterionFunction) {
        this.criterionString = criterionString;
        this.criterionFunction = criterionFunction;
    }

    public Function<Review, Double> getFunction() {
        return criterionFunction;
    }

    /**
     * Returns the criterion corresponding to the input string.
     * @param criterionString the string representation of the criterion
     * @return the criterion corresponding to the input string
     */
    public static Criterion getCriterion(String criterionString) {
        if (!ArgumentParser.isValidString(criterionString)) {
            return UNKNOWN;
        }

        for (Criterion criterion : Criterion.values()) {
            if (criterion.criterionString
                    .startsWith(criterionString.toLowerCase())) {
                return criterion;
            }
        }
        return UNKNOWN;
    }

    /**
     * Returns the string representation of the criterion.
     * @return the string representation of the criterion
     */
    @Override
    public String toString() {
        return criterionString;
    }
}
