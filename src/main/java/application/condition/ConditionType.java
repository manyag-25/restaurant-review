package application.condition;

import application.parser.ArgumentParser;

/**
 * Enum representing the different types of conditions.
 */
public enum ConditionType {
    EQUALS("=="),
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUALS(">="),
    LESS_THAN("<"),
    LESS_THAN_OR_EQUALS("<="),
    NOT_EQUALS("!="),
    UNKNOWN("");

    private final String conditionString;

    /**
     * Constructor for ConditionType enum.
     * @param conditionString the string representation of the condition
     */
    ConditionType(String conditionString) {
        this.conditionString = conditionString;
    }

    /**
     * Returns the condition type corresponding to the input string.
     * @param conditionAsString the string representation of the condition
     * @return the condition type corresponding to the input string
     */
    public static ConditionType getConditionType(String conditionAsString) {
        if(!ArgumentParser.isValidString(conditionAsString)) {
            return UNKNOWN;
        }

        for(ConditionType type : ConditionType.values()) {
            if(type.conditionString.equals(conditionAsString)) {
                return type;
            }
        }

        return UNKNOWN;
    }

    /**
     * Returns the string representation of the condition.
     * @return the string representation of the condition
     */
    @Override public String toString() {
        return conditionString;
    }
}
