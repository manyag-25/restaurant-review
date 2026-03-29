package application;

import application.ui.Ui;

/**
 * Main class.
 */
public class Main {
    public static void main(String[] args) {
        try (Ui ui = new Ui()) {
            MealMeter mealMeter = new MealMeter();

            ui.showMessage(ui.getWelcomeMessage());
            if (mealMeter.hasStorageLoadFailure()) {
                ui.showMessage(ui.getStorageLoadWarningMessage());
            }
            for (String warning : mealMeter.getStartupStorageWarnings()) {
                ui.showMessage(ui.formatStorageWarningMessage(warning));
            }

            boolean shouldContinue = true;
            while (shouldContinue) {
                ui.showPrompt();
                String userInput = ui.readCommand();

                CommandResult commandResult = mealMeter.handleInput(userInput);
                ui.showMessage(commandResult.output());

                shouldContinue = !commandResult.shouldTerminate();
            }
        }
    }
}
