package application.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableModel;

import application.auth.AuthManager;
import application.condition.GreaterThanOrEqualsToCondition;
import application.exception.InvalidArgumentException;
import application.review.Criterion;
import application.review.Rating;
import application.review.Review;
import application.review.ReviewList;
import application.review.SortOrder;
import application.review.Tag;
import application.storage.Storage;

/**
 * Integrated GUI for MealMeter. Patron and Owner panels are wired to the backend
 * ReviewList and Storage. All edits stay inside UI-unlinked; backend is untouched.
 *
 * Patron section: Forest theme (Green to Teal to Lime)
 * Owner section:  Ocean theme (Deep Blue to Cyan to Teal)
 */
public class MealMeterGUI extends JFrame {

    private static final int WINDOW_WIDTH = 1100;
    private static final int WINDOW_HEIGHT = 750;
    private static final String OWNER_PASSWORD = "password";

    // Forest colors (Patron tab)
    private static final Color FOREST_DARK = new Color(34, 139, 91);
    private static final Color FOREST_MID = new Color(0, 188, 145);
    private static final Color FOREST_LIGHT = new Color(144, 238, 144);

    // Ocean colors (Owner tab)
    private static final Color OCEAN_DARK = new Color(13, 71, 161);
    private static final Color OCEAN_MID = new Color(3, 155, 229);
    private static final Color OCEAN_LIGHT = new Color(0, 188, 212);

    // Neutral
    private static final Color BG_WHITE = new Color(248, 249, 250);
    private static final Color TEXT_DARK = new Color(33, 33, 33);
    private static final Color BORDER_LIGHT = new Color(224, 224, 224);

    // ── Backend state ────────────────────────────────────────────────────────
    private final Storage storage;
    private final AuthManager authManager;
    private final ReviewList reviewList;
    /** The currently displayed (possibly filtered/sorted) view. */
    private ReviewList currentDisplayList;

    // ── Patron tab components ─────────────────────────────────────────────────
    private JSpinner foodSpinner;
    private JSpinner cleanSpinner;
    private JSpinner serviceSpinner;
    private JTextArea reviewTextArea;
    private JTextField tagInputField;
    private JLabel patronTagsLabel;
    private final List<String> pendingTags = new ArrayList<>();

    // ── Owner tab components ──────────────────────────────────────────────────
    private JTextField includeTagsField;
    private JComboBox<String> statusCombo;
    private JSpinner minRatingSpinner;
    private JComboBox<String> sortByCombo;
    private JComboBox<String> sortOrderCombo;
    private DefaultTableModel tableModel;
    private JTable reviewsTable;

    // ── Main frame ────────────────────────────────────────────────────────────
    private javax.swing.JTabbedPane tabbedPane;

    // ─────────────────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Builds the GUI and initialises the backend (Storage + ReviewList).
     */
    public MealMeterGUI() {
        // Initialise backend
        this.storage = new Storage();
        this.authManager = new AuthManager(OWNER_PASSWORD);

        ReviewList loaded = new ReviewList();
        try {
            loaded = storage.loadReviews();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Could not load saved reviews: " + e.getMessage()
                    + "\nStarting with an empty list.",
                    "Storage Warning", JOptionPane.WARNING_MESSAGE);
        }
        this.reviewList = loaded;
        this.currentDisplayList = this.reviewList;

        // Build window
        setTitle("MealMeter - Restaurant Feedback System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(true);
        setBackground(BG_WHITE);

        tabbedPane = new javax.swing.JTabbedPane();
        tabbedPane.setUI(new CustomTabbedPaneUI());
        tabbedPane.setBackground(BG_WHITE);
        tabbedPane.setForeground(TEXT_DARK);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabbedPane.addTab("Patron Feedback", createPatronPanel());
        tabbedPane.addTab("Owner Management", createOwnerPanel());

        // Prompt owner login when switching to Owner tab
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1 && !authManager.isOwnerAuthenticated()) {
                promptOwnerLogin();
            }
        });

        add(tabbedPane);
        setVisible(true);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Patron panel
    // ─────────────────────────────────────────────────────────────────────────

    private JPanel createPatronPanel() {
        JPanel mainPanel = new GradientPanel(FOREST_DARK, FOREST_MID, FOREST_LIGHT);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Submit Your Feedback");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));

        JLabel subtitleLabel = new JLabel("Help us improve your dining experience");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 255, 220));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(30));

        contentPanel.add(createModernCard(createRatingInputPanel(), "Rate Your Experience"));
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createModernCard(createReviewInputPanel(), "Your Review"));
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createModernCard(createTagsInputPanel(), "Tags (Optional)"));
        contentPanel.add(Box.createVerticalStrut(30));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        JButton submitButton = createGradientButton("Submit Review", FOREST_MID, FOREST_LIGHT, Color.WHITE);
        submitButton.setPreferredSize(new Dimension(160, 45));
        submitButton.addActionListener(e -> handleReviewSubmit());

        JButton clearButton = createGradientButton("Clear Form",
                new Color(255, 152, 0), new Color(255, 193, 7), Color.WHITE);
        clearButton.setPreferredSize(new Dimension(160, 45));
        clearButton.addActionListener(e -> clearPatronForm());

        buttonPanel.add(submitButton);
        buttonPanel.add(clearButton);
        contentPanel.add(buttonPanel);
        contentPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createRatingInputPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 0));
        panel.setOpaque(false);

        foodSpinner = createModernRatingSpinner();
        cleanSpinner = createModernRatingSpinner();
        serviceSpinner = createModernRatingSpinner();

        String[] labels = {"Food Quality", "Cleanliness", "Service"};
        JSpinner[] spinners = {foodSpinner, cleanSpinner, serviceSpinner};

        for (int i = 0; i < labels.length; i++) {
            JPanel item = new JPanel(new BorderLayout(10, 5));
            item.setOpaque(false);
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lbl.setForeground(TEXT_DARK);
            item.add(lbl, BorderLayout.WEST);
            item.add(spinners[i], BorderLayout.CENTER);
            panel.add(item);
        }

        return panel;
    }

    private JSpinner createModernRatingSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(3.0, 1.0, 5.0, 0.5));
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spinner.setEditor(new JSpinner.NumberEditor(spinner, "0.0"));
        spinner.setOpaque(false);
        return spinner;
    }

    private JPanel createReviewInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        reviewTextArea = new JTextArea(4, 40);
        reviewTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reviewTextArea.setLineWrap(true);
        reviewTextArea.setWrapStyleWord(true);
        reviewTextArea.setForeground(TEXT_DARK);
        reviewTextArea.setBackground(new Color(248, 249, 250));
        reviewTextArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane sp = new JScrollPane(reviewTextArea);
        sp.setBorder(new LineBorder(BORDER_LIGHT, 1));
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTagsInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setOpaque(false);

        tagInputField = new JTextField(15);
        tagInputField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tagInputField.setBorder(new EmptyBorder(8, 10, 8, 10));
        tagInputField.setBackground(new Color(248, 249, 250));

        JButton addTagButton = createGradientButton("Add", FOREST_MID, FOREST_LIGHT, Color.WHITE);
        addTagButton.setPreferredSize(new Dimension(80, 35));
        addTagButton.addActionListener(e -> {
            String tag = tagInputField.getText().trim();
            if (!tag.isEmpty()) {
                pendingTags.add(tag);
                tagInputField.setText("");
                updatePatronTagsLabel();
            }
        });

        patronTagsLabel = new JLabel("No tags added yet");
        patronTagsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        patronTagsLabel.setForeground(TEXT_DARK);

        panel.add(tagInputField, BorderLayout.CENTER);
        panel.add(addTagButton, BorderLayout.EAST);
        panel.add(patronTagsLabel, BorderLayout.SOUTH);
        return panel;
    }

    private void updatePatronTagsLabel() {
        if (pendingTags.isEmpty()) {
            patronTagsLabel.setText("No tags added yet");
        } else {
            patronTagsLabel.setText("Tags: " + String.join(", ", pendingTags));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Owner panel
    // ─────────────────────────────────────────────────────────────────────────

    private JPanel createOwnerPanel() {
        JPanel mainPanel = new GradientPanel(OCEAN_DARK, OCEAN_MID, OCEAN_LIGHT);
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Review Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(BG_WHITE);
        contentPanel.setBorder(new LineBorder(BORDER_LIGHT, 1));
        contentPanel.add(createOwnerControlPanel(), BorderLayout.NORTH);
        contentPanel.add(createReviewsListPanel(), BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createOwnerControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Filter row
        JPanel filterPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        filterPanel.setOpaque(false);

        includeTagsField = new JTextField(15);
        filterPanel.add(createLabeledField("Include Tags:", includeTagsField));

        statusCombo = new JComboBox<>(new String[]{"All", "Resolved", "Outstanding"});
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        filterPanel.add(createLabeledCombo("Status:", statusCombo));

        minRatingSpinner = new JSpinner(new SpinnerNumberModel(1.0, 1.0, 5.0, 0.5));
        filterPanel.add(createLabeledField("Min Rating:", minRatingSpinner));

        JButton applyFilterButton = createGradientButton("Apply Filter", OCEAN_MID, OCEAN_LIGHT, Color.WHITE);
        applyFilterButton.setMaximumSize(new Dimension(150, 35));
        applyFilterButton.addActionListener(e -> handleOwnerFilter());
        filterPanel.add(applyFilterButton);

        panel.add(filterPanel);
        panel.add(Box.createVerticalStrut(10));

        // Sort row
        JPanel sortPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        sortPanel.setOpaque(false);

        sortByCombo = new JComboBox<>(new String[]{"Overall", "Food", "Cleanliness", "Service", "Tag Count"});
        sortByCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sortPanel.add(createLabeledCombo("Sort By:", sortByCombo));

        sortOrderCombo = new JComboBox<>(new String[]{"Ascending", "Descending"});
        sortOrderCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sortPanel.add(createLabeledCombo("Order:", sortOrderCombo));

        JButton applySortButton = createGradientButton("Apply Sort", OCEAN_MID, OCEAN_LIGHT, Color.WHITE);
        applySortButton.addActionListener(e -> handleOwnerSort());
        sortPanel.add(applySortButton);

        panel.add(sortPanel);
        return panel;
    }

    private JPanel createReviewsListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] columns = {"#", "Overall", "Food", "Clean", "Service", "Status", "Tags", "Review"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        reviewsTable = new JTable(tableModel);
        reviewsTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        reviewsTable.setRowHeight(28);
        reviewsTable.setGridColor(BORDER_LIGHT);
        reviewsTable.setSelectionBackground(new Color(200, 220, 255));
        reviewsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));

        // Populate with real data from reviewList on startup
        refreshTable(currentDisplayList);

        JScrollPane scrollPane = new JScrollPane(reviewsTable);
        scrollPane.setBorder(new LineBorder(BORDER_LIGHT, 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setOpaque(false);

        JButton resolveButton = createGradientButton("Resolved", FOREST_MID, FOREST_LIGHT, Color.WHITE);
        resolveButton.addActionListener(e -> handleResolveReview(reviewsTable));

        JButton unresolveButton = createGradientButton("Outstanding",
                new Color(255, 152, 0), new Color(255, 193, 7), Color.WHITE);
        unresolveButton.addActionListener(e -> handleUnresolveReview(reviewsTable));

        JButton tagButton = createGradientButton("Tags", OCEAN_MID, OCEAN_LIGHT, Color.WHITE);
        tagButton.addActionListener(e -> handleTagReview(reviewsTable));

        JButton deleteButton = createGradientButton("Delete",
                new Color(229, 57, 53), new Color(244, 67, 54), Color.WHITE);
        deleteButton.addActionListener(e -> handleDeleteReview(reviewsTable));

        JButton refreshButton = createGradientButton("Refresh", OCEAN_DARK, OCEAN_MID, Color.WHITE);
        refreshButton.addActionListener(e -> {
            currentDisplayList = reviewList;
            refreshTable(currentDisplayList);
        });

        actionPanel.add(resolveButton);
        actionPanel.add(unresolveButton);
        actionPanel.add(tagButton);
        actionPanel.add(deleteButton);
        actionPanel.add(Box.createHorizontalStrut(20));
        actionPanel.add(refreshButton);

        panel.add(actionPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Layout helpers
    // ─────────────────────────────────────────────────────────────────────────

    private JPanel createModernCard(JPanel content, String title) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(BORDER_LIGHT, 1));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(FOREST_DARK);
        titleLabel.setBorder(new EmptyBorder(15, 15, 10, 15));

        card.add(titleLabel, BorderLayout.NORTH);
        content.setBorder(new EmptyBorder(0, 15, 15, 15));
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createLabeledField(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXT_DARK);
        lbl.setPreferredSize(new Dimension(100, 20));
        panel.add(lbl, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLabeledCombo(String label, JComboBox<String> combo) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXT_DARK);
        lbl.setPreferredSize(new Dimension(80, 20));
        panel.add(lbl, BorderLayout.WEST);
        panel.add(combo, BorderLayout.CENTER);
        return panel;
    }

    private JButton createGradientButton(String text, Color c1, Color c2, Color textColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(textColor);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Backend helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Repopulates the owner table from the given ReviewList.
     */
    private void refreshTable(ReviewList list) {
        tableModel.setRowCount(0);
        List<Review> reviews = list.getAllReviews();
        for (int i = 0; i < reviews.size(); i++) {
            Review r = reviews.get(i);
            Rating rating = r.getRating();
            String tags = r.getTags().stream()
                    .map(Tag::getTagName)
                    .sorted()
                    .collect(Collectors.joining(", "));
            tableModel.addRow(new Object[]{
                i + 1,
                String.format("%.1f", rating.getOverallScore()),
                String.format("%.1f", rating.getFoodScore()),
                String.format("%.1f", rating.getCleanlinessScore()),
                String.format("%.1f", rating.getServiceScore()),
                r.isResolved() ? "Resolved" : "Outstanding",
                tags,
                r.getReviewBody()
            });
        }
    }

    /**
     * Saves reviewList to storage and shows an error dialog on failure.
     */
    private void saveOrWarn() {
        try {
            storage.saveReviews(reviewList);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not save reviews: " + e.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Returns the actual index (1-based) of the displayed review in the master reviewList.
     * Since filter() shares the same Review object references, we use identity comparison.
     */
    private int masterIndexOf(Review displayed) {
        List<Review> all = reviewList.getAllReviews();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i) == displayed) {
                return i + 1; // 1-based
            }
        }
        return -1;
    }

    /**
     * Shows a login dialog for the Owner tab.
     * Switches back to Patron tab if login is cancelled or fails.
     */
    private void promptOwnerLogin() {
        JPasswordField pwField = new JPasswordField(15);
        Object[] msg = {"Owner Password:", pwField};
        int result = JOptionPane.showConfirmDialog(this, msg, "Owner Login",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String entered = new String(pwField.getPassword());
            if (authManager.authenticateOwner(entered)) {
                refreshTable(currentDisplayList);
                JOptionPane.showMessageDialog(this, "Welcome, Owner!", "Login Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect password.", "Access Denied",
                        JOptionPane.ERROR_MESSAGE);
                tabbedPane.setSelectedIndex(0); // back to Patron tab
            }
        } else {
            tabbedPane.setSelectedIndex(0); // cancelled
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Patron event handlers
    // ─────────────────────────────────────────────────────────────────────────

    private void handleReviewSubmit() {
        double food = ((Number) foodSpinner.getValue()).doubleValue();
        double clean = ((Number) cleanSpinner.getValue()).doubleValue();
        double service = ((Number) serviceSpinner.getValue()).doubleValue();
        String body = reviewTextArea.getText().trim();

        if (body.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a review before submitting.",
                    "Missing Review", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Rating rating = new Rating(food, clean, service);
            String tagsCsv = String.join(",", pendingTags);
            Set<Tag> tags = tagsCsv.isEmpty() ? new HashSet<>() : Tag.toTags(tagsCsv);
            Review review = new Review(body, rating, tags);

            reviewList.addReview(review);
            saveOrWarn();

            JOptionPane.showMessageDialog(this,
                    "Review submitted successfully!\nOverall score: "
                    + String.format("%.1f", rating.getOverallScore()),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            clearPatronForm();
        } catch (InvalidArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not create review: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearPatronForm() {
        foodSpinner.setValue(3.0);
        cleanSpinner.setValue(3.0);
        serviceSpinner.setValue(3.0);
        reviewTextArea.setText("");
        tagInputField.setText("");
        pendingTags.clear();
        updatePatronTagsLabel();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Owner event handlers
    // ─────────────────────────────────────────────────────────────────────────

    private void handleOwnerFilter() {
        // Tags to include
        String tagText = includeTagsField.getText().trim();
        Set<Tag> includeTags = tagText.isEmpty() ? new HashSet<>() : Tag.toTags(tagText);

        // Resolution status
        String statusStr = (String) statusCombo.getSelectedItem();
        Boolean isResolved = null;
        if ("Resolved".equals(statusStr)) {
            isResolved = true;
        } else if ("Outstanding".equals(statusStr)) {
            isResolved = false;
        }

        // Min rating condition (only apply if > 1.0 so default shows all)
        double minRating = ((Number) minRatingSpinner.getValue()).doubleValue();
        Set<application.condition.Condition> conditions = new HashSet<>();
        if (minRating > 1.0) {
            conditions.add(new GreaterThanOrEqualsToCondition(Criterion.OVERALL_SCORE, minRating));
        }

        currentDisplayList = reviewList.filter(includeTags, new HashSet<>(), conditions, isResolved);
        refreshTable(currentDisplayList);

        JOptionPane.showMessageDialog(this,
                "Filter applied. Showing " + currentDisplayList.size() + " review(s).",
                "Filter", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleOwnerSort() {
        String sortByStr = (String) sortByCombo.getSelectedItem();
        Criterion criterion;
        switch (sortByStr) {
        case "Food":
            criterion = Criterion.FOOD_SCORE;
            break;
        case "Cleanliness":
            criterion = Criterion.CLEANLINESS_SCORE;
            break;
        case "Service":
            criterion = Criterion.SERVICE_SCORE;
            break;
        case "Tag Count":
            criterion = Criterion.TAG_COUNT;
            break;
        default:
            criterion = Criterion.OVERALL_SCORE;
            break;
        }

        SortOrder order = "Descending".equals(sortOrderCombo.getSelectedItem())
                ? SortOrder.DESCENDING : SortOrder.ASCENDING;

        try {
            currentDisplayList = reviewList.sort(criterion, order, currentDisplayList);
            refreshTable(currentDisplayList);
        } catch (InvalidArgumentException e) {
            JOptionPane.showMessageDialog(this, "Sort error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleResolveReview(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a review first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            // currentDisplayList shares Review references with reviewList
            Review review = currentDisplayList.getReview(row + 1);
            review.markResolved();
            saveOrWarn();
            refreshTable(currentDisplayList);
            JOptionPane.showMessageDialog(this, "Marked as Resolved.", "Done",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (InvalidArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUnresolveReview(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a review first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Review review = currentDisplayList.getReview(row + 1);
            review.markOutstanding();
            saveOrWarn();
            refreshTable(currentDisplayList);
            JOptionPane.showMessageDialog(this, "Marked as Outstanding.", "Done",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (InvalidArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleTagReview(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a review first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Review review = currentDisplayList.getReview(row + 1);
            String currentTags = review.getTags().stream()
                    .map(Tag::getTagName)
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

            String trimmed = input.trim();
            if (trimmed.startsWith("-")) {
                String tagName = trimmed.substring(1).trim();
                review.removeTag(new Tag(tagName));
                JOptionPane.showMessageDialog(this, "Tag removed.", "Done",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                review.addTag(new Tag(trimmed));
                JOptionPane.showMessageDialog(this, "Tag added.", "Done",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            saveOrWarn();
            refreshTable(currentDisplayList);
        } catch (InvalidArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteReview(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a review first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Permanently delete this review?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Review toDelete = currentDisplayList.getReview(row + 1);
            int masterIdx = masterIndexOf(toDelete);
            if (masterIdx == -1) {
                JOptionPane.showMessageDialog(this, "Review not found in master list.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            reviewList.deleteReview(masterIdx);

            // Rebuild display list excluding the deleted review
            List<Review> remaining = currentDisplayList.getAllReviews().stream()
                    .filter(r -> r != toDelete)
                    .collect(Collectors.toList());
            currentDisplayList = new ReviewList(remaining);

            saveOrWarn();
            refreshTable(currentDisplayList);
            JOptionPane.showMessageDialog(this, "Review deleted.", "Done",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (InvalidArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Inner classes
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Panel that paints a two-pass gradient background.
     */
    static class GradientPanel extends JPanel {
        private final Color c1;
        private final Color c2;
        private final Color c3;

        GradientPanel(Color c1, Color c2, Color c3) {
            this.c1 = c1;
            this.c2 = c2;
            this.c3 = c3;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp1 = new GradientPaint(0, 0, c1, getWidth() / 2f, getHeight() / 2f, c2);
            g2.setPaint(gp1);
            g2.fillRect(0, 0, getWidth(), getHeight());
            GradientPaint gp2 = new GradientPaint(getWidth() / 2f, getHeight() / 2f, c2, getWidth(), getHeight(), c3);
            g2.setPaint(gp2);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * Minimal custom tab UI: highlights selected tab in white.
     */
    static class CustomTabbedPaneUI extends BasicTabbedPaneUI {
        @Override
        protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (isSelected) {
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(x, y, w, h + 3, 8, 8);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Entry point
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Launches the MealMeter GUI on the Swing event dispatch thread.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MealMeterGUI::new);
    }
}
