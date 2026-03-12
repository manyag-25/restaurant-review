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

`CommandParser.getCommand("addreview restaurant was a little dirty but food was ok /food 4.0 /service 4.5 /clean 3.0");`

`CommandParser.getCommand("addtag 1 /tag isPositive food service");`

`CommandParser.getCommand("filter /tag isPositive service washroom");`

`CommandParser.getCommand("list");`

`CommandParser.getCommand("sort /by food /order ascending");`

## `execute()`

### Description
Executes the `Command` based on the `Command` object

### Parameters / Inputs

| Parameter | Type         | Description |
|-----------|--------------|---|
| `reviews` | `ReviewList` | A list of reviews |
| `storage` | `Storage`    | Handles saving reviews to disk |

### Return Value

| Type | Description |
|---|---|
| `String` | A description of the result after command execution |

### Example Usage

`command.execute(reviewsList, storage);`

## `loadReviewsFromFile()`

### Description
Loads reviews from a text file on disk into a `ReviewList` on app startup.

### Parameters / Inputs

| Parameter | Type         | Description |
|-----------|--------------|---|
| `reviewsListFilePath` | `Path` | The file path to the text file hosting reviews |

### Return Value

| Type | Description |
|---|---|
| `ReviewList` | A list of reviews |

### Example Usage

`storage.loadReviewsFromFile();`

## `saveReviewsToFile()`

### Description
Saves reviews from a `ReviewList` to the disk when the list is updated.

### Parameters / Inputs

| Parameter | Type         | Description |
|-----------|--------------|---|
| `reviews` | `ReviewList` | A list of reviews |
| `reviewsListFilePath` | `Path` | The file path to the text file hosting reviews |

### Example Usage

`storage.saveReviewsToFile();`
