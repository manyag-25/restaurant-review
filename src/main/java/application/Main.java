package application;

import java.io.IOException;
import java.util.Scanner;

import application.command.Command;
import application.exception.InvalidArgumentException;
import application.exception.MissingArgumentException;
import application.parser.CommandParser;
import application.review.ReviewList;
import application.storage.Storage;

/**
 * Main class.
 */
public class Main {
    public static void main(String[] args) {
        ReviewList reviewList = new ReviewList();
        Storage storage = new Storage();
        Scanner scanner = new Scanner(System.in);

        boolean shouldContinue = true;

        while (shouldContinue) {
            System.out.print("> ");
            String userInput = scanner.nextLine();
            try {
                Command command = CommandParser.getCommand(userInput);
                String output = command.execute(reviewList, storage);
                System.out.println(output);
                shouldContinue = !command.isTerminatingCommand();
            } catch (InvalidArgumentException | IOException | MissingArgumentException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }
    }
}
