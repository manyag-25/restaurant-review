package application.review;

import application.parser.ArgumentParser;

/**
 * Enum representing the different criteria for sorting reviews.
 */
public enum SortCriterion {
    OVERALL_SCORE("overall scores"),
    FOOD_SCORE("food scores"),
    CLEANLINESS_SCORE("clean scores"),
    SERVICE_SCORE("service scores"),
    TAG_COUNT("tag count");

    private final String sortCriterionString;

    /**
     * Constructor for SortCriterion enum.
     * @param sortCriterionString the string representation of the criterion
     */
    SortCriterion(String sortCriterionString) {
        this.sortCriterionString = sortCriterionString;
    }

    /**
     * Returns the sort criterion corresponding to the input string.
     * @param sortCriterionString the string representation of the criterion
     * @return the sort criterion corresponding to the input string
     */
    public static SortCriterion getSortCriterion(String sortCriterionString) {
        if (!ArgumentParser.isValidString(sortCriterionString)) {
            return OVERALL_SCORE;
        }

        for (SortCriterion criterion : SortCriterion.values()) {
            if (criterion.sortCriterionString.startsWith(sortCriterionString.toLowerCase())) {
                return criterion;
            }
        }
        return OVERALL_SCORE;
    }

    /**
     * Returns the string representation of the criterion.
     * @return the string representation of the criterion
     */
    @Override
    public String toString() {
        return sortCriterionString;
    }
}
