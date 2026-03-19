package application.review;

import application.parser.ArgumentParser;

/**
 * Enum representing the different sort orders.
 */
public enum SortOrder {
    ASCENDING("ascending"),
    DESCENDING("descending"),
    UNKNOWN("unknown");

    private final String sortOrderString;

    /**
     * Constructor for SortOrder enum.
     * @param sortOrderString the string representation of the sort order
     */
    SortOrder(String sortOrderString) {
        this.sortOrderString = sortOrderString;
    }

    /**
     * Returns the sort order corresponding to the input string.
     * @param sortOrderString the string representation of the sort order
     * @return the sort order corresponding to the input string
     */
    public static SortOrder getSortOrder(String sortOrderString) {
        if (!ArgumentParser.isValidString(sortOrderString)) {
            return UNKNOWN;
        }

        for (SortOrder order : SortOrder.values()) {
            if (order.sortOrderString.startsWith(sortOrderString.toLowerCase())) {
                return order;
            }
        }

        return UNKNOWN;
    }

    /**
     * Returns the string representation of the sort order.
     * @return the string representation of the sort order
     */
    @Override
    public String toString() {
        return sortOrderString;
    }
}