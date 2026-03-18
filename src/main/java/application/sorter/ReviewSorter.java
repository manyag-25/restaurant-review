package application.sorter;

import application.review.Review;
import application.review.ReviewList;

import java.util.function.Function;

public class ReviewSorter {
    public static ReviewList sort(String sortCriterionAsString, String sortOrderAsString, ReviewList reviewList) {
        SortCriterion sortCriterion = SortCriterion.getSortCriterion(sortCriterionAsString);
        SortOrder sortOrder = SortOrder.getSortOrder(sortOrderAsString);

        Function<Review, Double> sortCriterionFunction = getSortCriterionFunction(sortCriterion);

        if (sortOrder == SortOrder.ASCENDING) {
            return reviewList.sortByAscending(sortCriterionFunction);
        } else {
            //default to descending
            return reviewList.sortByDescending(sortCriterionFunction);
        }
    }

    private static Function<Review, Double> getSortCriterionFunction(SortCriterion sortCriterion) {
        Function<Review, Double> sortCriterionFunction;

        switch (sortCriterion) {
        case OVERALL_SCORE:
            sortCriterionFunction = review -> review.getRating().getOverallScore();
            break;
        case FOOD_SCORE:
            sortCriterionFunction = review -> review.getRating().getFoodScore();
            break;
        case SERVICE_SCORE:
            sortCriterionFunction = review -> review.getRating().getServiceScore();
            break;
        case CLEANLINESS_SCORE:
            sortCriterionFunction = review -> review.getRating().getCleanlinessScore();
            break;
        case TAG_COUNT:
            sortCriterionFunction = review -> (double) review.getTags().size();
            break;
        default:
            //default to overall score
            sortCriterionFunction = review -> review.getRating().getOverallScore();
        }

        return sortCriterionFunction;
    }
}
