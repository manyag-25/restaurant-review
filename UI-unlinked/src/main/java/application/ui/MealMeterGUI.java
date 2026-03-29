package application.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

/**
 * Beautiful, modern GUI for MealMeter with gradient backgrounds and minimalist design.
 * Patron section: Forest theme (Green→Teal→Lime)
 * Owner section: Ocean theme (Deep Blue→Cyan→Teal)
 */
public class MealMeterGUI extends JFrame {
    private static final int WINDOW_WIDTH = 1100;
    private static final int WINDOW_HEIGHT = 750;

    // Forest Colors (Patron)
    private static final Color FOREST_DARK = new Color(34, 139, 91);
    private static final Color FOREST_MID = new Color(0, 188, 145);
    private static final Color FOREST_LIGHT = new Color(144, 238, 144);

    // Ocean Colors (Owner)
    private static final Color OCEAN_DARK = new Color(13, 71, 161);
    private static final Color OCEAN_MID = new Color(3, 155, 229);
    private static final Color OCEAN_LIGHT = new Color(0, 188, 212);

    // Neutral
    private static final Color BG_WHITE = new Color(248, 249, 250);
    private static final Color TEXT_DARK = new Color(33, 33, 33);
    private static final Color TEXT_LIGHT = new Color(117, 117, 117);
    private static final Color BORDER_LIGHT = new Color(224, 224, 224);

    private JTabbedPane tabbedPane;

    public MealMeterGUI() {
        setTitle("MealMeter - Restaurant Feedback System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(true);
        setBackground(BG_WHITE);

        // Create custom tabbed pane with gradient tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setUI(new CustomTabbedPaneUI());
        tabbedPane.setBackground(BG_WHITE);
        tabbedPane.setForeground(TEXT_DARK);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabbedPane.addTab("Patron Feedback", createPatronPanel());
        tabbedPane.addTab("Owner Management", createOwnerPanel());

        add(tabbedPane);
        setVisible(true);
    }

    /**
     * Creates the patron feedback panel with Forest theme.
     */
    private JPanel createPatronPanel() {
        JPanel mainPanel = new GradientPanel(FOREST_DARK, FOREST_MID, FOREST_LIGHT);
        mainPanel.setLayout(new BorderLayout(0, 0));
        mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Content panel with whitespace
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Title
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

        // Card sections
        JPanel ratingPanel = createModernCard(createRatingInputPanel(), "Rate Your Experience");
        contentPanel.add(ratingPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        JPanel reviewPanel = createModernCard(createReviewInputPanel(), "Your Review");
        contentPanel.add(reviewPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        JPanel tagsPanel = createModernCard(createTagsInputPanel(), "Tags (Optional)");
        contentPanel.add(tagsPanel);
        contentPanel.add(Box.createVerticalStrut(30));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        JButton submitButton = createGradientButton("Submit Review", FOREST_MID, FOREST_LIGHT, Color.WHITE);
        submitButton.setPreferredSize(new Dimension(160, 45));
        submitButton.addActionListener(e -> handleReviewSubmit());

        JButton cancelButton = createGradientButton("Clear Form", new Color(255, 152, 0), new Color(255, 193, 7), Color.WHITE);
        cancelButton.setPreferredSize(new Dimension(160, 45));
        cancelButton.addActionListener(e -> clearPatronForm());

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        contentPanel.add(buttonPanel);

        contentPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    /**
     * Creates the owner management panel with Ocean theme.
     */
    private JPanel createOwnerPanel() {
        JPanel mainPanel = new GradientPanel(OCEAN_DARK, OCEAN_MID, OCEAN_LIGHT);
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Review Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(BG_WHITE);
        contentPanel.setBorder(new LineBorder(BORDER_LIGHT, 1));

        // Control Panel
        JPanel controlPanel = createOwnerControlPanel();
        contentPanel.add(controlPanel, BorderLayout.NORTH);

        // Reviews List
        JPanel reviewsPanel = createReviewsListPanel();
        contentPanel.add(reviewsPanel, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    /**
     * Creates a modern card with rounded corners and shadow.
     */
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

    /**
     * Creates rating input section.
     */
    private JPanel createRatingInputPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 0));
        panel.setOpaque(false);

        String[] labels = {"Food Quality", "Cleanliness", "Service"};
        for (String label : labels) {
            JPanel ratingItem = new JPanel(new BorderLayout(10, 5));
            ratingItem.setOpaque(false);

            JLabel ratingLabel = new JLabel(label);
            ratingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            ratingLabel.setForeground(TEXT_DARK);

            JSpinner spinner = createModernRatingSpinner();

            ratingItem.add(ratingLabel, BorderLayout.WEST);
            ratingItem.add(spinner, BorderLayout.CENTER);
            panel.add(ratingItem);
        }

        return panel;
    }

    /**
     * Creates a modern rating spinner.
     */
    private JSpinner createModernRatingSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(3.0, 1.0, 5.0, 0.5));
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner, "0.0");
        spinner.setEditor(editor);
        spinner.setOpaque(false);
        return spinner;
    }

    /**
     * Creates review text input section.
     */
    private JPanel createReviewInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JTextArea reviewTextArea = new JTextArea(4, 40);
        reviewTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reviewTextArea.setLineWrap(true);
        reviewTextArea.setWrapStyleWord(true);
        reviewTextArea.setForeground(TEXT_DARK);
        reviewTextArea.setBackground(new Color(248, 249, 250));
        reviewTextArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane scrollPane = new JScrollPane(reviewTextArea);
        scrollPane.setBorder(new LineBorder(BORDER_LIGHT, 1));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates tags input section.
     */
    private JPanel createTagsInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);

        JTextField tagInput = new JTextField(15);
        tagInput.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tagInput.setBorder(new EmptyBorder(8, 10, 8, 10));
        tagInput.setBackground(new Color(248, 249, 250));

        JButton addTagButton = createGradientButton("Add", FOREST_MID, FOREST_LIGHT, Color.WHITE);
        addTagButton.setPreferredSize(new Dimension(80, 35));

        panel.add(tagInput, BorderLayout.CENTER);
        panel.add(addTagButton, BorderLayout.EAST);

        return panel;
    }

    /**
     * Creates owner control panel with filters and sorting.
     */
    private JPanel createOwnerControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Filter Section
        JPanel filterPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        filterPanel.setOpaque(false);

        filterPanel.add(createLabeledField("Include Tags:", new JTextField(15)));
        filterPanel.add(createLabeledCombo("Status:", new String[]{"All", "Resolved", "Outstanding"}));
        filterPanel.add(createLabeledField("Min Rating:", new JSpinner(new SpinnerNumberModel(1.0, 1.0, 5.0, 0.5))));

        JButton applyFilterButton = createGradientButton("Apply Filter", OCEAN_MID, OCEAN_LIGHT, Color.WHITE);
        applyFilterButton.setMaximumSize(new Dimension(150, 35));
        applyFilterButton.addActionListener(e -> handleOwnerFilter());
        filterPanel.add(applyFilterButton);

        panel.add(filterPanel);
        panel.add(Box.createVerticalStrut(10));

        // Sort Section
        JPanel sortPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        sortPanel.setOpaque(false);

        sortPanel.add(createLabeledCombo("Sort By:", new String[]{"Overall", "Food", "Cleanliness", "Service", "Date"}));
        sortPanel.add(createLabeledCombo("Order:", new String[]{"Ascending", "Descending"}));

        JButton applySortButton = createGradientButton("Apply Sort", OCEAN_MID, OCEAN_LIGHT, Color.WHITE);
        applySortButton.addActionListener(e -> handleOwnerSort());
        sortPanel.add(applySortButton);

        panel.add(sortPanel);

        return panel;
    }

    /**
     * Helper to create labeled field.
     */
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

    /**
     * Helper to create labeled combo.
     */
    private JPanel createLabeledCombo(String label, String[] items) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXT_DARK);
        lbl.setPreferredSize(new Dimension(80, 20));
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panel.add(lbl, BorderLayout.WEST);
        panel.add(combo, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates reviews list panel.
     */
    private JPanel createReviewsListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"#", "Overall", "Food", "Clean", "Service", "Status", "Tags"};
        Object[][] reviewData = {
                {1, 4.0, 4.5, 3.5, 4.0, "Outstanding", "slow-service"},
                {2, 4.5, 4.5, 4.5, 4.5, "Resolved", "excellent"},
                {3, 2.5, 2.0, 3.0, 2.5, "Outstanding", "cold-food"}
        };

        JTable reviewsTable = new JTable(reviewData, columnNames);
        reviewsTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        reviewsTable.setRowHeight(28);
        reviewsTable.setGridColor(BORDER_LIGHT);
        reviewsTable.setSelectionBackground(new Color(200, 220, 255));

        JScrollPane scrollPane = new JScrollPane(reviewsTable);
        scrollPane.setBorder(new LineBorder(BORDER_LIGHT, 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setOpaque(false);

        JButton resolveButton = createGradientButton("✓ Resolved", FOREST_MID, FOREST_LIGHT, Color.WHITE);
        resolveButton.addActionListener(e -> handleResolveReview(reviewsTable));

        JButton unresolveButton = createGradientButton("⊘ Outstanding", new Color(255, 152, 0), new Color(255, 193, 7), Color.WHITE);
        unresolveButton.addActionListener(e -> handleUnresolveReview(reviewsTable));

        JButton tagButton = createGradientButton("🏷 Tags", OCEAN_MID, OCEAN_LIGHT, Color.WHITE);
        tagButton.addActionListener(e -> handleTagReview(reviewsTable));

        JButton deleteButton = createGradientButton("🗑 Delete", new Color(229, 57, 53), new Color(244, 67, 54), Color.WHITE);
        deleteButton.addActionListener(e -> handleDeleteReview(reviewsTable));

        actionPanel.add(resolveButton);
        actionPanel.add(unresolveButton);
        actionPanel.add(tagButton);
        actionPanel.add(deleteButton);

        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates a gradient button with rounded corners.
     */
    private JButton createGradientButton(String text, Color color1, Color color2, Color textColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw gradient background
                GradientPaint gradient = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                // Draw text
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

    // Event handlers
    private void handleReviewSubmit() {
        JOptionPane.showMessageDialog(this, "✓ Review submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearPatronForm() {
        JOptionPane.showMessageDialog(this, "Form cleared.", "Reset", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleOwnerFilter() {
        JOptionPane.showMessageDialog(this, "Filter applied to reviews.", "Done", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleOwnerSort() {
        JOptionPane.showMessageDialog(this, "Reviews sorted successfully.", "Done", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleResolveReview(JTable table) {
        if (table.getSelectedRow() >= 0) {
            JOptionPane.showMessageDialog(this, "✓ Marked as resolved.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a review.", "Info", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleUnresolveReview(JTable table) {
        if (table.getSelectedRow() >= 0) {
            JOptionPane.showMessageDialog(this, "⊘ Marked as outstanding.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a review.", "Info", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleTagReview(JTable table) {
        if (table.getSelectedRow() >= 0) {
            String tag = JOptionPane.showInputDialog(this, "Enter tag:");
            if (tag != null && !tag.isEmpty()) {
                JOptionPane.showMessageDialog(this, "✓ Tag added.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a review.", "Info", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleDeleteReview(JTable table) {
        if (table.getSelectedRow() >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this review?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "✓ Review deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a review.", "Info", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Custom gradient panel for background.
     */
    static class GradientPanel extends JPanel {
        private final Color color1, color2, color3;

        GradientPanel(Color c1, Color c2, Color c3) {
            this.color1 = c1;
            this.color2 = c2;
            this.color3 = c3;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gradient = new GradientPaint(0, 0, color1, getWidth() / 2, getHeight() / 2, color2);
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            gradient = new GradientPaint(getWidth() / 2, getHeight() / 2, color2, getWidth(), getHeight(), color3);
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * Custom tabbed pane UI.
     */
    static class CustomTabbedPaneUI extends BasicTabbedPaneUI {
        @Override
        protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isSelected) {
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(x, y, w, h + 3, 8, 8);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MealMeterGUI::new);
    }
}
