package application.command;

import application.parser.ArgumentParser;

/**
 * Enum representing the different types of commands.
 */
public enum CommandType {
    EXIT("exit"),
    ADD_REVIEW("review"),
    ADD_TAG("addtag"),
    DELETE_TAG("deletetag"),
    DELETE("delete"),
    FILTER("filter"),
    LIST("list"),
    LOGIN("login"),
    LOGOUT("logout"),
    RESOLVE("resolve"),
    UNRESOLVE("unresolve"),
    SORT("sort"),
    UNKNOWN("unknown");

    private final String commandString;

    /**
     * Constructor for CommandType enum.
     *
     * @param commandString the string representation of the command
     */
    CommandType(String commandString) {
        this.commandString = commandString;
    }

    /**
     * Returns the command type of the input.
     *
     * @param input the input string
     * @return the command type of the input
     */
    public static CommandType getCommandType(String input) {
        if (!ArgumentParser.isValidString(input)) {
            return UNKNOWN;
        }

        for (CommandType type : CommandType.values()) {
            if (type.commandString.equals(input)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
