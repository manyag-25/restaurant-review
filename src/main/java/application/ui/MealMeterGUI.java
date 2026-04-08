package application.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import application.CommandResult;
import application.MealMeter;
import application.condition.Condition;
import application.condition.GreaterThanOrEqualsToCondition;
import application.exception.InvalidArgumentException;
import application.parser.ConditionParser;
import application.review.Criterion;
import application.review.Review;
import application.review.ReviewList;
import application.review.SortOrder;
import application.review.Tag;

/**
 * Main GUI window for MealMeter. Coordinates between patron and owner panels,
 * forwarding all events to the backend via MealMeter.handleInput().
 *
 * <p>The GUI contains no business logic. All operations are expressed as
 * command strings passed to the backend (MVC controller layer).</p>
 */
// CHECKSTYLE.OFF: AbbreviationAsWordInName - "GUI" is an established acronym for this class
public class MealMeterGUI extends JFrame implements
        PatronPanel.PatronPanelListener, OwnerPanel.OwnerPanelListener {
    // CHECKSTYLE.ON: AbbreviationAsWordInName

    private static final int WINDOW_WIDTH = 1100;
    private static final int WINDOW_HEIGHT = 750;
    private static final int PATRON_TAB_INDEX = 0;
    private static final int OWNER_TAB_INDEX = 1;

    /** Single backend entry point — no Storage, AuthManager or ReviewList held directly. */
    private final MealMeter mealMeter;

    /** Current subset shown in the owner table (may differ from master list after filter/sort). */
    private ReviewList currentDisplayList;

    private final JTabbedPane tabbedPane;
    private final PatronPanel patronPanel;
    private final OwnerPanel ownerPanel;

    /**
     * Constructs and displays the MealMeterGUI window.
     */
    public MealMeterGUI() {
        this.mealMeter = new MealMeter();
        this.currentDisplayList = mealMeter.getReviewList();

        setTitle("MealMeter - Restaurant Feedback System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(true);

        this.tabbedPane = new JTabbedPane();
        tabbedPane.setUI(new CustomTabbedPaneUI());

        this.patronPanel = new PatronPanel(this);
        this.ownerPanel = new OwnerPanel(this);

        tabbedPane.addTab("Patron Feedback", patronPanel);
        tabbedPane.addTab("Owner Management", ownerPanel);

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == OWNER_TAB_INDEX
                    && !mealMeter.isOwnerAuthenticated()) {
                promptOwnerLogin();
            }
        });

        add(tabbedPane);

        // Show any storage warnings from startup
        for (String warning : mealMeter.getStartupStorageWarnings()) {
            JOptionPane.showMessageDialog(null, warning, "Storage Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
        if (mealMeter.hasStorageLoadFailure()) {
            JOptionPane.showMessageDialog(null,
                    "Could not load saved reviews. Starting with an empty list.",
                    "Storage Warning", JOptionPane.WARNING_MESSAGE);
        }

        setVisible(true);
    }

    // ── PatronPanelListener ─────────────────────────────────────────────────

    @Override
    public String onReviewSubmitted(String body, double food, double clean,
                                    double service, List<String> tags) {
        String cmd = String.format("review %s /food %.1f /clean %.1f /service %.1f",
                body, food, clean, service);
        if (!tags.isEmpty()) {
            cmd += " /tag " + String.join(",", tags);
        }

        CommandResult result = mealMeter.handleInput(cmd);

        // Auto-refresh owner table if logged in
        if (mealMeter.isOwnerAuthenticated()) {
            currentDisplayList = mealMeter.getReviewList();
            ownerPanel.refreshTable(currentDisplayList.getAllReviews());
        }

        return result.output();
    }

    // ── OwnerPanelListener ──────────────────────────────────────────────────

    @Override
    public void onFilterApplied(String includeTags, String excludeTags, String status,
                                double minRating, String conditions) {
        String cmd = buildFilterCommand(includeTags, excludeTags, status, minRating, conditions);
        CommandResult result = mealMeter.handleInput(cmd);

        JOptionPane.showMessageDialog(this, result.output(), "Filter Applied",
                JOptionPane.INFORMATION_MESSAGE);

        // Re-derive the display list with the same command so the table stays filtered
        refreshDisplayAfterFilter(includeTags, excludeTags, status, minRating, conditions);
    }

    @Override
    public void onSortApplied(String sortBy, String sortOrder) {
        String criterionArg = mapSortByToCriterionArg(sortBy);
        String orderArg = sortOrder.toLowerCase();
        CommandResult result = mealMeter.handleInput("sort " + orderArg + " /by " + criterionArg);

        JOptionPane.showMessageDialog(this, result.output(), "Sort Applied",
                JOptionPane.INFORMATION_MESSAGE);

        refreshDisplayAfterSort(sortBy, sortOrder);
    }

    @Override
    public void onResolveReview(int rowIndex) {
        int masterIdx = masterIndexOf(currentDisplayList, rowIndex);
        if (masterIdx < 0) {
            return;
        }
        CommandResult result = mealMeter.handleInput("resolve " + masterIdx);
        JOptionPane.showMessageDialog(this, result.output(), "Resolve", JOptionPane.INFORMATION_MESSAGE);
        ownerPanel.refreshTable(currentDisplayList.getAllReviews());
    }

    @Override
    public void onUnresolveReview(int rowIndex) {
        int masterIdx = masterIndexOf(currentDisplayList, rowIndex);
        if (masterIdx < 0) {
            return;
        }
        CommandResult result = mealMeter.handleInput("unresolve " + masterIdx);
        JOptionPane.showMessageDialog(this, result.output(), "Unresolve", JOptionPane.INFORMATION_MESSAGE);
        ownerPanel.refreshTable(currentDisplayList.getAllReviews());
    }

    @Override
    public void onTagReview(int rowIndex) {
        try {
            Review review = currentDisplayList.getReview(rowIndex);
            String currentTags = review.getTags().stream()
                    .map(t -> t.getTagName())
                    .sorted()
                    .collect(Collectors.joining(", "));

            String prompt = "Current tags: " + (currentTags.isEmpty() ? "none" : currentTags)
                    + "\n\nEnter a tag name to ADD it."
                    + "\nPrefix with '-' to REMOVE (e.g. -spicy).";
            String input = JOptionPane.showInputDialog(this, prompt, "Manage Tags",
                    JOptionPane.PLAIN_MESSAGE);

            if (input == null || input.trim().isEmpty()) {
                return;
            }

            int masterIdx = masterIndexOf(currentDisplayList, rowIndex);
            if (masterIdx < 0) {
                return;
            }

            String trimmed = input.trim();
            CommandResult result;
            if (trimmed.startsWith("-")) {
                String tagName = trimmed.substring(1).trim();
                result = mealMeter.handleInput("deletetag " + masterIdx + " /tag " + tagName);
            } else {
                result = mealMeter.handleInput("addtag " + masterIdx + " /tag " + trimmed);
            }

            JOptionPane.showMessageDialog(this, result.output(), "Tags", JOptionPane.INFORMATION_MESSAGE);
            ownerPanel.refreshTable(currentDisplayList.getAllReviews());
        } catch (InvalidArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onDeleteReview(int rowIndex) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Permanently delete this review?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int masterIdx = masterIndexOf(currentDisplayList, rowIndex);
        if (masterIdx < 0) {
            return;
        }

        try {
            Review toDelete = currentDisplayList.getReview(rowIndex);
            CommandResult result = mealMeter.handleInput("delete " + masterIdx);
            JOptionPane.showMessageDialog(this, result.output(), "Delete",
                    JOptionPane.INFORMATION_MESSAGE);

            // Remove from display list too so table stays consistent
            List<Review> remaining = currentDisplayList.getAllReviews().stream()
                    .filter(r -> r != toDelete)
                    .collect(Collectors.toList());
            currentDisplayList = new ReviewList(remaining);
            ownerPanel.refreshTable(currentDisplayList.getAllReviews());
        } catch (InvalidArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onRefresh() {
        currentDisplayList = mealMeter.getReviewList();
        ownerPanel.refreshTable(currentDisplayList.getAllReviews());
        JOptionPane.showMessageDialog(this, "Refreshed.", "Done", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void onLogout() {
        CommandResult result = mealMeter.handleInput("logout");
        JOptionPane.showMessageDialog(this, result.output(), "Logout",
                JOptionPane.INFORMATION_MESSAGE);
        tabbedPane.setSelectedIndex(PATRON_TAB_INDEX);
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private void promptOwnerLogin() {
        JPasswordField pwField = new JPasswordField(15);
        Object[] msg = {"Owner Password:", pwField};
        int option = JOptionPane.showConfirmDialog(this, msg, "Owner Login",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String entered = new String(pwField.getPassword());
            CommandResult result = mealMeter.handleInput("login " + entered);
            if (mealMeter.isOwnerAuthenticated()) {
                ownerPanel.refreshTable(currentDisplayList.getAllReviews());
                JOptionPane.showMessageDialog(this, result.output(), "Login Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, result.output(), "Access Denied",
                        JOptionPane.ERROR_MESSAGE);
                tabbedPane.setSelectedIndex(PATRON_TAB_INDEX);
            }
        } else {
            tabbedPane.setSelectedIndex(PATRON_TAB_INDEX);
        }
    }

    /**
     * Returns the 1-based master-list index of the review at rowIndex in the display list.
     * Uses reference equality since filter() returns the same Review objects.
     */
    private int masterIndexOf(ReviewList displayList, int rowIndex) {
        try {
            Review displayed = displayList.getReview(rowIndex);
            List<Review> all = mealMeter.getReviewList().getAllReviews();
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i) == displayed) {
                    return i + 1;
                }
            }
        } catch (InvalidArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return -1;
    }

    /**
     * Builds the filter command string from the panel's form inputs.
     */
    private String buildFilterCommand(String includeTags, String excludeTags, String status,
                                      double minRating, String conditions) {
        StringBuilder cmd = new StringBuilder("filter");

        if (!includeTags.isEmpty()) {
            cmd.append(" /hastag ").append(includeTags);
        }
        if (!excludeTags.isEmpty()) {
            cmd.append(" /notag ").append(excludeTags);
        }
        if ("Resolved".equals(status)) {
            cmd.append(" /resolved true");
        } else if ("Outstanding".equals(status)) {
            cmd.append(" /resolved false");
        }

        List<String> condParts = new ArrayList<>();
        if (minRating > 1.0) {
            condParts.add(String.format("overall >= %.1f", minRating));
        }
        if (!conditions.isEmpty()) {
            condParts.add(conditions);
        }
        if (!condParts.isEmpty()) {
            cmd.append(" /condition ").append(String.join(", ", condParts));
        }

        return cmd.toString();
    }

    /**
     * Re-runs the filter via MealMeter and updates the display list.
     * Called after a successful filter to keep the table showing filtered results.
     */
    private void refreshDisplayAfterFilter(String includeTags, String excludeTags, String status,
                                           double minRating, String conditions) {
        String cmd = buildFilterCommand(includeTags, excludeTags, status, minRating, conditions);
        CommandResult recheck = mealMeter.handleInput(cmd);

        // Only update the display if the command succeeded (no error output)
        if (!recheck.output().toLowerCase().contains("error")
                && !recheck.output().equals("Access denied. Please log in as the owner to use this command.")) {
            // Derive the filtered list using the same parameters through the backend
            currentDisplayList = deriveFilteredList(includeTags, excludeTags, status,
                    minRating, conditions);
        }
        ownerPanel.refreshTable(currentDisplayList.getAllReviews());
    }

    /**
     * Derives a filtered ReviewList from the master list using the form inputs.
     * Tag strings use comma-separation matching the backend's Tag.toTags() format.
     */
    private ReviewList deriveFilteredList(String includeTags, String excludeTags, String status,
                                          double minRating, String conditions) {
        try {
            Tag[] includeArr = parseTags(includeTags);
            Tag[] excludeArr = parseTags(excludeTags);

            Set<Tag> includeSet = new HashSet<>(Arrays.asList(includeArr));
            Set<Tag> excludeSet = new HashSet<>(Arrays.asList(excludeArr));

            Boolean isResolved = null;
            if ("Resolved".equals(status)) {
                isResolved = true;
            } else if ("Outstanding".equals(status)) {
                isResolved = false;
            }

            Set<Condition> conditionSet = new HashSet<>();
            if (minRating > 1.0) {
                conditionSet.add(new GreaterThanOrEqualsToCondition(
                        Criterion.OVERALL_SCORE, minRating));
            }
            if (!conditions.isEmpty()) {
                conditionSet.addAll(ConditionParser.getConditions(conditions));
            }

            return mealMeter.getReviewList().filter(includeSet, excludeSet, conditionSet, isResolved);
        } catch (Exception e) {
            return mealMeter.getReviewList();
        }
    }

    private Tag[] parseTags(String csv) {
        if (csv == null || csv.isBlank()) {
            return new Tag[0];
        }
        String[] parts = csv.split(",");
        Tag[] tags = new Tag[parts.length];
        for (int i = 0; i < parts.length; i++) {
            tags[i] = new Tag(parts[i].trim());
        }
        return tags;
    }

    /**
     * Re-runs the sort via MealMeter and updates the display list.
     */
    private void refreshDisplayAfterSort(String sortBy, String sortOrder) {
        try {
            Criterion criterion = mapSortByCriterion(sortBy);
            SortOrder order = "Descending".equals(sortOrder)
                    ? SortOrder.DESCENDING
                    : SortOrder.ASCENDING;
            currentDisplayList = mealMeter.getReviewList().sort(criterion, order,
                    currentDisplayList);
            ownerPanel.refreshTable(currentDisplayList.getAllReviews());
        } catch (InvalidArgumentException e) {
            JOptionPane.showMessageDialog(this, "Sort error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String mapSortByToCriterionArg(String sortBy) {
        switch (sortBy) {
        case "Food":
            return "food";
        case "Cleanliness":
            return "clean";
        case "Service":
            return "service";
        case "Tag Count":
            return "tag";
        default:
            return "overall";
        }
    }

    private Criterion mapSortByCriterion(String sortBy) {
        switch (sortBy) {
        case "Food":
            return Criterion.FOOD_SCORE;
        case "Cleanliness":
            return Criterion.CLEANLINESS_SCORE;
        case "Service":
            return Criterion.SERVICE_SCORE;
        case "Tag Count":
            return Criterion.TAG_COUNT;
        default:
            return Criterion.OVERALL_SCORE;
        }
    }

    /**
     * Custom tabbed pane UI that highlights the selected tab.
     */
    static class CustomTabbedPaneUI extends BasicTabbedPaneUI {
        @Override
        protected void paintTabBackground(java.awt.Graphics g, int tabPlacement, int tabIndex,
                int x, int y, int w, int h, boolean isSelected) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            if (isSelected) {
                g2.setColor(java.awt.Color.WHITE);
                g2.fillRoundRect(x, y, w, h + 3, 8, 8);
            }
        }
    }

    /**
     * Launches the MealMeter GUI.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MealMeterGUI::new);
    }
}
