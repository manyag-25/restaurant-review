package application.sorter;

public enum SortOrder {
    ASCENDING("ascending"),
    DESCENDING("descending"),
    UNKNOWN("unknown");

    private final String sortOrderString;

    SortOrder(String sortOrderString) {
        this.sortOrderString = sortOrderString;
    }

    public static SortOrder getSortOrder(String sortOrderString) {
        for (SortOrder order : SortOrder.values()) {
            if (order.sortOrderString.startsWith(sortOrderString)) {
                return order;
            }
        }
        return DESCENDING;
    }
}