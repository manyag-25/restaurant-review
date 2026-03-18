package application.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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
        if (isInvalidString(indexAsString)) {
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
        if (isInvalidString(scoreAsString)) {
            throw new MissingArgumentException("No score given!");
        }

        double score;

        try {
            score = Double.parseDouble(scoreAsString);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("Score provided is not a valid number!");
        }

        return score;
    }

    /**
     * Parses a date string into a LocalDate object.
     * @param dateAsString the date string to parse
     * @return a LocalDate object representing the date
     * @throws MissingArgumentException if the date string is empty or null
     * @throws InvalidArgumentException if the date string is not in the correct format
     */
    public static LocalDate toDate(String dateAsString) throws MissingArgumentException, InvalidArgumentException {
        if (isInvalidString(dateAsString)) {
            throw new MissingArgumentException("No date provided!"
                    + "Expected format: YYYY-MM-DD");
        }

        LocalDate date;

        try {
            date = LocalDate.parse(dateAsString);
        } catch (DateTimeParseException e) {
            throw new InvalidArgumentException("Invalid date format!"
                    + "Expected format: YYYY-MM-DD");
        }

        return date;
    }

    /**
     * Formats a LocalDate object into a specific String format.
     * @param date the LocalDate object to format
     * @return a formatted String representing the date
     */
    public static String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("EEE, dd LLL yyyy"));
    }
}
