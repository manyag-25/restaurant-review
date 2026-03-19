package application.parser;

import application.exception.InvalidArgumentException;
import application.exception.MissingArgumentException;

/**
 * ArgumentParser class containing generic methods for parsing inputs.
 */
public class ArgumentParser {
    /**
     * Returns the specified input as an array of length 2, after splitting with a specified string as the delimiter.
     *
     * @param input the input command string from the user
     * @return a String array of length 2
     */
    public static String[] splitIntoPair(String input, String delimiter) {
        if (input == null) {
            return new String[]{ "", "" };
        }

        String[] split = input.strip().split(delimiter, 2);

        if (split.length == 1) {
            return new String[]{ split[0], "" };
        }

        split[1] = split[1].strip();
        return split;
    }

    /**
     * Checks if the specified string is null or empty.
     *
     * @param string the string to check
     * @return true if the string is not null or empty, false otherwise
     */
    public static boolean isValidString(String string) {
        return string != null && !string.isBlank();
    }

    /**
     * Returns an index to a list as an integer after extracting the argument from the delimiter-argument pair.
     *
     * @param indexAsString string containing the index
     * @return an integer denoting the list index
     * @throws MissingArgumentException if the argument is an empty string or null
     * @throws InvalidArgumentException if the argument is not a number or multiple numbers are specified
     */
    public static int toInt(String indexAsString) throws MissingArgumentException, InvalidArgumentException {
        if (!isValidString(indexAsString)) {
            throw new MissingArgumentException("No index given!");
        }

        int index;

        try {
            index = Integer.parseInt(indexAsString);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("Index provided is not a single number!");
        }

        return index;
    }

    /**
     * Returns a score as a float after extracting the argument from the delimiter-argument pair.
     *
     * @param scoreAsString string containing the score
     * @return a float denoting the score
     * @throws MissingArgumentException if the argument is an empty string or null
     * @throws InvalidArgumentException if the argument is not a number or multiple numbers are specified
     */
    public static double toDouble(String scoreAsString) throws MissingArgumentException, InvalidArgumentException {
        if (!isValidString(scoreAsString)) {
            throw new MissingArgumentException("No number given!");
        }

        double score;

        try {
            score = Double.parseDouble(scoreAsString);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("Number provided is not a valid number!");
        }

        return score;
    }
}
