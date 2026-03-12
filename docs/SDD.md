# Software Design Document (SDD)

## System Overview
MealMeter is a desktop-based restaurant feedback system intended for deployment at restaurant point-of-sale terminals or customer kiosks.

The system allows patrons to provide feedback through a simple interface consisting of:

- Rating inputs
- Text input for written reviews
- A submission action

Submitted feedback is processed by the application and stored locally for later viewing, sorting, filtering, and management by restaurant staff.

---

## Architecture Design
The system follows a **3-tier architecture** consisting of:

- **User Interface Layer**
- **Logic Layer**
- **Storage Layer**

This architecture separates user interaction, application logic, and data persistence so that each part of the system remains easier to maintain, extend, and test.

### Architecture Diagram
![Architecture Diagram](docs/images/architecture-diagram.jpg)

---

## Major System Components

### 1. User Interface Layer

#### Responsibilities
- Displays the review submission form
- Displays the list of reviews
- Allows users to enter commands or interact through GUI controls
- Shows system messages and feedback to the user

#### Key Classes
- `Ui`

---

### 2. Logic Layer

#### Responsibilities
- Determines how user input should be handled
- Parses user commands and executes the appropriate actions
- Validates user input
- Handles sorting and filtering of reviews
- Manages review objects and review collections
- Coordinates saving and loading through the storage layer

#### Key Classes
- `CommandParser`
- `Command`
- `Review`
- `ReviewList`
- `Rating`
- `Tag`
- `ArgumentParser`
- `FileParser`

---

### 3. Storage Layer

#### Responsibilities
- Loads reviews from a text file when the application starts
- Saves reviews to a text file when the review list is updated
- Handles file creation if the data file does not already exist

#### Key Classes
- `Storage`

---

## UML Diagrams

### Class Diagram
The class diagram below shows the main classes in the system and the relationships between them.

![Class Diagram](docs/images/class-diagram.jpg)

### Sequence Diagrams
*Placeholder: Insert sequence diagrams here.*

### Use Case Diagram
*Placeholder: Insert use case diagram here.*

---

## Design Details

### `Ui`
The `Ui` class handles interaction with the user. It is responsible for collecting user input and displaying output messages.

**Main responsibilities:**
- Read user input
- Display system messages

### `CommandParser`
The `CommandParser` class interprets raw user input and converts it into a `Command` object that can be executed by the system.

**Main responsibilities:**
- Parse command keywords
- Extract command arguments
- Construct the appropriate `Command`

### `Command`
The `Command` class represents an executable user instruction. Each command stores the information needed for execution.

**Main responsibilities:**
- Represent a user action
- Execute logic on the review list
- Interact with storage when needed

### `Review`
The `Review` class represents a single customer review.

**Main responsibilities:**
- Store review body text
- Track resolution status
- Manage associated tags
- Maintain its associated rating

### `ReviewList`
The `ReviewList` class stores and manages multiple `Review` objects.

**Main responsibilities:**
- Add reviews
- Delete reviews
- Resolve or unresolve reviews
- Filter reviews
- Sort reviews
- Validate indices

### `Rating`
The `Rating` class stores structured rating information for a review.

**Rating categories:**
- Food
- Cleanliness
- Service

It also supports computing an overall rating value.

### `Tag`
The `Tag` class represents a label attached to a review for categorisation and filtering.

### `Storage`
The `Storage` class is responsible for persistent file handling.

**Main responsibilities:**
- Save reviews to file
- Load reviews from file
- Create the storage file if necessary

---

## Key Design Decisions

### Structured Rating Categories
The system uses structured rating categories:

- Food
- Cleanliness
- Service

This allows restaurants to analyse feedback more effectively than relying only on free-text reviews.

### Immediate Feedback Collection
Feedback is collected directly at the point of sale. This improves response rates and helps restaurants capture impressions while the dining experience is still fresh in the patron’s mind.

### Layered Architecture
A 3-tier architecture was chosen to separate presentation, logic, and storage concerns. This improves modularity and makes future enhancements easier to implement.

### Local File Storage
The system uses local file-based storage instead of a server-based backend. This keeps deployment simple and aligns with the intended lightweight desktop application design.

---

## Planned Diagrams to Include
The following supporting diagrams should be added to complete this document:

- Architecture diagram
- Class diagram
- Sequence diagram for application initialisation
- Sequence diagram for adding a review
- Sequence diagram for deleting a review
- Sequence diagram for tagging a review
- Sequence diagram for sorting reviews
- Use case diagram