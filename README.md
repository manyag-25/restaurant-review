## CS2103DE tP

> [!NOTE]
> **Notes about the command format:**
>- Words in `UPPER_CASE` are the parameters to be supplied by the user.\
> e.g. in `review REVIEW_BODY`, `REVIEW_BODY` is a parameter which can be used as `review Good Food`.
> - Aside from the first argument (if it exists), parameters can be in any order.\
> e.g. if the command specifies `/tag TAG1 /food SCORE`, `/food SCORE /tag TAG1` is also acceptable.
> - Aside from the first argument (if it exists), if a parameter is expected only once in the command but multiple parameters are provided, the last parameter will be taken.\
> e.g. `/food SCORE1 /food SCORE2` is taken as `/food SCORE2`.
> - Extraneous parameters for commands that do not take in parameters (such as `list` and `exit`) will be
> ignored.\
> e.g. if the command specifies `list 123`, it will be interpreted as `list`.
> - Duchess supports partial matching of some arguments for advanced users.\
> e.g. `a`, `asc` and `as` will all match the `SORT_ORDER` `ascending` command.
> - Arguments wrapped in `[SQUARE_BRACKETS]` are optional and can be omitted.
> - Arguments with trailing ellipsis `...` can include multiple arguments.\
> e.g. `TAG1, TAG2, ...` indicates that multiple tags can be provided.
> - `SCORE` must be a positive integer between 1.0 and 5.0.

## Adding a Review
`review`: Adds a review to the database.\
Format: `review [REVIEW_BODY] /food SCORE /clean SCORE /service SCORE [/tag TAG1[, TAG2...]]`

## Deleting a Review
`delete`: Deletes a review from the database.\
Format: `delete INDEX`

## Listing all reviews
`list`: Lists all reviews in the database.\
Format: `list`

## Add Tags to a Review
`addtag`: Adds tags to a review in the database.\
Format: `addtag INDEX /tag TAG1[,TAG2...]`

## Delete Tags from a Review
`deletetag`: Deletes tags from a review in the database.\
Format: `deletetag INDEX /tag TAG1[,TAG2...]`

## Resolving a Review
`resolve`: Resolves a review in the database.\
Format: `resolve INDEX`

## Unresolving a Review
`unresolve`: Unresolves a review in the database.\
Format: `unresolve INDEX`

## Sorting Reviews
`sort`: Sorts reviews in the database.\
Format: `sort SORT_ORDER /by [CRITERION]`

> [!NOTE]
> **Notes about the `sort` command:**
> - `SORT_ORDER` can be `ascending` or `descending`.
> - `CRITERION` can be `food scores`, `cleanliness scores`, `service scores`, `overall scores` or `tag count`.
> - If `CRITERION` is not specified, the sorted list will be the original unsorted list.

## Filtering Reviews
`filter`: Filters reviews in the database.\
Format: `filter [/hastag TAG1[,TAG2...]] [/notag TAG1[,TAG2...]] [/resolved [TRUE/FALSE]] [/condition CONDIITION1[,CONDITION2...]]`
All arguments are optional. If any argument is specified, the filtered reviews must match ALL conditions.

> [!NOTE]
> **Notes about the condition format:**\
> `CONDITION` is in the format `CRITERION COMPARATOR VALUE`.
> - `CRITERION` can be `food scores`, `cleanliness scores`, `service scores`, `overall scores` or `tag count`.
> - `COMPARATOR` can be `>`, `>=`, `==`, `!=`, `<` or `<=`.
> - `VALUE` can be any number.
> - Order of components matters.
> - A space must separate all components.
> 
> Example of valid conditions:
> - `food scores > 3.5`
> - `cleanliness scores >= 4`
> - `service scores == 5`
> - `overall scores < 3`
> - `tag count == 2`
> 
> Example of invalid conditions:
> - `food scores > 3.5 4` (not a number)
> - `cleanliness scores >=4` (no space between `>=` and `4`)
> - `invalid == 5` (invalid `CRITERION`)
> - `overall scores = 3` (invalid `COMPARATOR`)

> [!NOTE]
> **Notes about comma-separated values:**\
> Separate `CONDITION`s or `TAG`s with commas to specify multiple values.
> Spaces between commas are optional.
>
> Example of valid comma-separated values:
> - `CONDITION`: `food scores > 3.5, cleanliness scores >= 4`
> - `TAG`: `isGood, good good, helloTag3`

## Exiting the program
`exit`: Exits the program.\
Format: `exit`
