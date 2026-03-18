package application.sorter;

enum SortCriterion {
    OVERALL_SCORE("overall"),
    FOOD_SCORE("food"),
    CLEANLINESS_SCORE("clean"),
    SERVICE_SCORE("service"),
    TAG_COUNT("tag");

    private final String criterionString;

    SortCriterion(String criterionString) {
        this.criterionString = criterionString;
    }

    public static SortCriterion getSortCriterion(String criterionString) {
        for (SortCriterion criterion : SortCriterion.values()) {
            if (criterion.criterionString.startsWith(criterionString)) {
                return criterion;
            }
        }
        return OVERALL_SCORE;
    }
}
