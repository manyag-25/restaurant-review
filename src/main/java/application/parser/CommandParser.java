package application.parser;

import application.command.AddReviewCommand;
import application.command.Command;
import application.command.CommandType;
import application.command.DeleteReviewCommand;
import application.command.ExitCommand;
import application.command.ListReviewsCommand;
import application.command.UnknownCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * CommandParser class for parsing user input into commands.
 */
public class CommandParser {

    /**
     * Returns a command from the specified input string
     *
     * @param input the user input string
     * @return a command containing its respective arguments
     */
    public static Command getCommand(String input) {
        String[] splitInput = Utility.splitIntoPair(input, " ");

        CommandType commandType = CommandType.getCommandType(splitInput[0].toLowerCase());

        Command command;
        Map<String, String> arguments;

        switch (commandType) {
        case EXIT:
            command = new ExitCommand();
            break;
        case ADD:
            arguments = parseArguments(AddReviewCommand.DELIMITERS, splitInput[1]);
            command = new AddReviewCommand(arguments);
            break;
        case DELETE:
            arguments = parseArguments(DeleteReviewCommand.DELIMITERS, splitInput[1]);
            command = new DeleteReviewCommand(arguments);
            break;
        case LIST:
            command = new ListReviewsCommand();
            break;
        case UNKNOWN:
        default:
            command = new UnknownCommand();
            break;
        }
        return command;
    }

    /**
     * Returns the specified input as a Map with specific delimiter-argument pairs, based on the provided delimiters.
     *
     * <p>
     * Input can have delimiters that are out of order. If multiple delimiters of the same type are in the input, the
     * latest argument for that delimiter will be captured.
     * </p>
     *
     * @param delimiters the delimiters the command expects
     * @param userInput the user input string without the command type
     * @return a map containing delimiter-argument pairs
     */
    private static Map<String, String> parseArguments(Set<String> delimiters, String userInput) {
        String[] argumentComponents = userInput.split(" ");

        Map<String, String> argumentsMap = new HashMap<>();
        StringBuilder currentArgument = new StringBuilder();

        String currentDelimiter = "/default";

        for (String argument : argumentComponents) {
            if (delimiters.contains(argument)) {
                argumentsMap.put(currentDelimiter, currentArgument.toString().strip().trim());
                currentDelimiter = argument;
                currentArgument = new StringBuilder();
            } else {
                currentArgument.append(argument).append(" ");
            }
        }

        argumentsMap.put(currentDelimiter, currentArgument.toString().strip().trim());

        return argumentsMap;
    }
}
