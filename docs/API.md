# API Documentation

## `getCommand()`

### Description
Gets a `Command` object based on the user input string.

### Parameters / Inputs

| Parameter | Type | Description |
|---|---|---|
| `input` | `String` | User input from the Command Line Interface (CLI) |

### Return Value

| Type | Description |
|---|---|
| `Command` | A `Command` object containing information about its type and arguments |

### Example Usage

```java
CommandParser.getCommand("addreview restaurant was a little dirty but food was ok /food 4.0 /service 4.5 /clean 3.0");

CommandParser.getCommand("addtag 1 /tag isPositive food service");

CommandParser.getCommand("filter /tag isPositive service washroom");

CommandParser.getCommand("list");

CommandParser.getCommand("sort /by food /order ascending");