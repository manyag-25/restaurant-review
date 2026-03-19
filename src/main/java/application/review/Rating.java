package application.review;

import application.exception.InvalidArgumentException;

/**
 * Represents the structured ratings for a dining experience.
 * <p>
 * Each review stores ratings for food, cleanliness, and service.
 * An overall rating can be derived from these category ratings.
 * </p>
 */
public class Rating {
    public static final double RATING_MIN = 1.0;
    public static final double RATING_MAX = 5.0;

    private final double foodScore;
    private final double cleanlinessScore;
    private final double serviceScore;

    /**
     * Constructs a {@code Rating} with scores for food, cleanliness, and service.
     *
     * @param foodScore the food score
     * @param cleanlinessScore the cleanliness rating
     * @param serviceScore the service rating
     * @throws InvalidArgumentException if any rating is invalid
     */
    public Rating(double foodScore,
                  double cleanlinessScore,
                  double serviceScore
    ) throws InvalidArgumentException {
        if (!isValidScore(foodScore)) {
            throw new InvalidArgumentException(
                    String.format("Food Score must be numbers between %.1f and %.1f.", RATING_MIN, RATING_MAX)
            );
        }

        if (!isValidScore(cleanlinessScore)) {
            throw new InvalidArgumentException(
                    String.format("Cleanliness Score must be numbers between %.1f and %.1f.", RATING_MIN, RATING_MAX)
            );
        }

        if (!isValidScore(serviceScore)) {
            throw new InvalidArgumentException(
                    String.format("Service Score must be numbers between %.1f and %.1f.", RATING_MIN, RATING_MAX)
            );
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
    public static boolean isValidScore(double score) {
        return score >= RATING_MIN && score <= RATING_MAX;
    }

    /**
     * Returns the food score.
     *
     * @return the food score
     */
    public double getFoodScore() {
        return foodScore;
    }

    /**
     * Returns the cleanliness score.
     *
     * @return the cleanliness score
     */
    public double getCleanlinessScore() {
        return cleanlinessScore;
    }

    /**
     * Returns the service score.
     *
     * @return the service score
     */
    public double getServiceScore() {
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
                "Food: %.1f | Cleanliness: %.1f | Service: %.1f | Overall: %.1f",
                getFoodScore(),
                getCleanlinessScore(),
                getServiceScore(),
                getOverallScore()
        );
    }
}