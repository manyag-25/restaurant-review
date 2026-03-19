package application.parser;

import application.condition.*;
import application.exception.InvalidArgumentException;
import application.exception.MissingArgumentException;
import application.review.Criterion;

import java.util.HashSet;
import java.util.Set;

/**
 * ConditionParser class containing methods for parsing conditions.
 */
public class ConditionParser {
    /**
     * Returns a set of conditions from the specified input string
     *
     * @param conditionsAsString the string representation of the conditions
     * @return the set of conditions corresponding to the input string
     * @throws InvalidArgumentException if the conditions are invalid
     * @throws MissingArgumentException if the conditions are missing an argument
     */
    public static Set<Condition> getConditions(String conditionsAsString)
            throws InvalidArgumentException, MissingArgumentException {
        if (!ArgumentParser.isValidString(conditionsAsString)) {
            return new HashSet<>();
        }

        String[] conditions = conditionsAsString.split(",");

        Set<Condition> conditionsSet = new HashSet<>();

        for (String conditionAsString : conditions) {
            Condition condition = getCondition(conditionAsString.strip());
            conditionsSet.add(condition);
        }

        return conditionsSet;
    }

    /**
     * Returns a condition from the specified input string
     *
     * @param conditionAsString the string representation of the condition
     * @return the condition corresponding to the input string
     * @throws InvalidArgumentException if any part of the condition is invalid
     * @throws MissingArgumentException if the condition is missing an argument
     */
    private static Condition getCondition(String conditionAsString)
            throws InvalidArgumentException, MissingArgumentException {
        if (!ArgumentParser.isValidString(conditionAsString)) {
            throw new MissingArgumentException("No condition given!");
        }

        String[] conditionParts = conditionAsString.split(" ");

        if (conditionParts.length != 3) {
            throw new InvalidArgumentException(
                    String.format("Did not recognise \"%s\" as a valid condition!", conditionAsString)
            );
        }

        //get the operator type
        ConditionType conditionType = ConditionType.getConditionType(conditionParts[1]);
        Criterion criterion = Criterion.getCriterion(conditionParts[0]);

        if (criterion == Criterion.UNKNOWN) {
            throw new InvalidArgumentException(String.format(
                    "Did not understand criterion specified: \"%s\"",
                    conditionParts[0])
            );
        }

        double value = ArgumentParser.toDouble(conditionParts[2]);

        Condition condition;

        switch (conditionType) {
        case EQUALS:
            condition = new EqualsToCondition(criterion, value);
            break;
        case NOT_EQUALS:
            condition = new NotEqualsToCondition(criterion, value);
            break;
        case GREATER_THAN:
            condition = new GreaterThanCondition(criterion, value);
            break;
        case LESS_THAN:
            condition = new LessThanCondition(criterion, value);
            break;
        case GREATER_THAN_OR_EQUALS:
            condition = new GreaterThanOrEqualsToCondition(criterion, value);
            break;
        case LESS_THAN_OR_EQUALS:
            condition = new LessThanOrEqualsToCondition(criterion, value);
            break;
        case UNKNOWN:
            //fallthrough
        default:
            throw new InvalidArgumentException(String.format(
                    "Did not understand operator: \"%s\"",
                    conditionParts[1])
            );
        }

        return condition;
    }
}
