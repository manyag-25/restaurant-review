package application.storage;

import java.util.List;

import application.review.ReviewList;

/**
 * Represents the result of loading reviews from storage.
 * Used in MealMeter to encapsulate both the loaded reviews and any
 * warnings that occurred during loading.
 *
 * @param reviewList the reviews loaded from storage
 * @param warnings non-fatal warnings encountered while loading
 */
public record StorageLoadResult(ReviewList reviewList, List<String> warnings) {
}
