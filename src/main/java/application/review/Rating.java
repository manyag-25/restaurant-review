package application.review;

/**
 * Represents the structured ratings for a dining experience.
 *
 * Each review stores ratings for food, cleanliness, and service.
 * An overall rating can be derived from these category ratings.
 */
public class Rating {
    public static final int RATING_MIN = 1;
    public static final int RATING_MAX = 5;

    private final int foodScore;
    private final int cleanlinessScore;
    private final int serviceScore;

    /**
     * Constructs a {@code Rating} with scores for food, cleanliness, and service.
     *
     * @param foodScore the food score
     * @param cleanlinessScore the cleanliness rating
     * @param serviceScore the service rating
     * @throws IllegalArgumentException if any rating is invalid
     */
    public Rating(int foodScore, int cleanlinessScore, int serviceScore) throws IllegalArgumentException {
        if (!isValidScore(foodScore)
                || !isValidScore(cleanlinessScore)
                || !isValidScore(serviceScore)) {
            throw new IllegalArgumentException(
                    "All ratings must be integers between " + RATING_MIN + " and " + RATING_MAX + ".");
        }

        this.foodScore = foodScore;
        this.cleanlinessScore = cleanlinessScore;
        this.serviceScore = serviceScore;
    }

    /**
     * Returns whether the given score is valid.
     *
     * @param score the score to validate
     * @return {@code true} if the rating is between 1 and 5 inclusive,
     *         {@code false} otherwise
     */
    public static boolean isValidScore(int score) {
        return score >= RATING_MIN && score <= RATING_MAX;
    }

    /**
     * Returns the food score.
     *
     * @return the food score
     */
    public int getFoodScore() {
        return foodScore;
    }

    /**
     * Returns the cleanliness score.
     *
     * @return the cleanliness score
     */
    public int getCleanlinessScore() {
        return cleanlinessScore;
    }

    /**
     * Returns the service score.
     *
     * @return the service score
     */
    public int getServiceScore() {
        return serviceScore;
    }

    /**
     * Returns the derived overall score.
     *
     * @return the average of the three category ratings
     */
    public double getOverallScore() {
        return (foodScore + cleanlinessScore + serviceScore) / 3.0;
    }

    /**
     * Returns a string representation of the rating.
     *
     * @return a formatted string containing all category ratings and the overall rating
     */
    @Override
    public String toString() {
        return String.format(
                "Food: %d | Cleanliness: %d | Service: %d | Overall: %.1f",
                getFoodScore(),
                getCleanlinessScore(),
                getServiceScore(),
                getOverallScore()
        );
    }
}