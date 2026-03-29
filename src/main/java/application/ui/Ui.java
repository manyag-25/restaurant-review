package application.ui;

import java.util.Scanner;

/**
 * Ui class containing methods for interacting with the user.
 */
public class Ui implements AutoCloseable {
    private final Scanner scanner;

    /**
     * Constructs a Ui object.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Returns the welcome message shown at application startup.
     *
     * @return the welcome message
     */
    public String getWelcomeMessage() {
        return "Welcome to MealMeter.\n"
                + "Enter a command to manage customer reviews.\n"
                + "Type 'exit' to leave the application.";
    }

    /**
     * Returns the startup warning for storage loading failures.
     *
     * @return startup warning message
     */
    public String getStorageLoadWarningMessage() {
        return "Warning: Failed to load reviews from storage. Starting with an empty list.";
    }

    /**
     * Returns a formatted warning message for non-fatal storage loading issues.
     *
     * @param warning the warning details
     * @return a display-ready warning message
     */
    public String formatStorageWarningMessage(String warning) {
        return "Warning: " + warning;
    }

    /**
     * Displays a message to the user.
     *
     * @param message the message to display
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Displays the command prompt.
     */
    public void showPrompt() {
        System.out.print("> ");
    }

    /**
     * Reads the next command from the user.
     *
     * @return the user's input
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Closes UI resources.
     */
    @Override
    public void close() {
        scanner.close();
    }
}
