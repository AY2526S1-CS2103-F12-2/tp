---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* {list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well}

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams are in this document `docs/diagrams` folder. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2526S1-CS2103-F12-2/tp/blob/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2526S1-CS2103-F12-2/tp/blob/master/src/main/java/seedu/address/MainApp.java) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the five main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2526S1-CS2103-F12-2/tp/blob/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2526S1-CS2103-F12-2/tp/blob/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2526S1-CS2103-F12-2/tp/blob/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Logic` component, because launching communication mode application through `UI` relies `ApplicationLinkLauncher` to execute action.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.
* initializes with a list of commands from the `Logic` component to use for autocompletion.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2526S1-CS2103-F12-2/tp/blob/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser`, `DeleteCommand` and `ConfirmationPendingResult` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</div>

How the `Logic` component works:

When `Logic` is called upon to execute a command, it checks its `state` to see if there is an operation that is pending confirmation
* Case 1: There is no pending operation
  1. The command word is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
  1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
  1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
     Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
  1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.
     1. If the command requires a confirmation to be executed, a `ConfirmationPendingResult` object is created instead, which encapsulates the confirmation message to show to the user and the pending operation.
* Case 2: There is a pending operation
    1. The command word (e.g. `y`/`n`) is passed to an `AddressBookParser`, where it is parsed specifically as a `ConfirmCommand`
    2. A `ConfirmCommand` object is created. The `ConfirmCommand` executes the pending operation if the user inputs a valid confirmation.
    4. The `ConfirmCommand` executes a callback to manage State if the confirmation was successful, and the state needs to be cleared.
    5. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="700"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class queries `CommandRegistry` to find the appropriate `CommandFactory` instance that creates the corresponding `XYZCommandParser` class. (`XYZ`is a placeholder for the specific command name e.g., `AddCommandParser`) Once found, the `CommandFactory` instance creates the `XYZCommandParser` class which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.

* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

<img src="images/LogicUtilityClassDiagram.png" width="700"/>

How the utility classes work:
* When called upon by either `XYZCommandParser` classes or other external classes, ApplicationLinkLauncher tries to create an instance of `RealDesktopWrapper` (which implements `DesktopWrapper` interface) to launch the communication.
* If the `RealDesktopWrapper` instance is created successfully, it uses the real desktop environment to launch the communication application.
* If not, a `DummyDesktopWrapper` instance is created instead which does nothing when asked to launch the communication application.
* Upon failing to launch the application through either `RealDesktopWrapper` or `DummyDesktopWrapper`, `ApplicationLinkLauncher` will attempt a fallback mechanism through `DesktopApi` class.
* If all attempts to launch the application fail, an appropriate error message is returned to the user.

### Model component
**API** : [`Model.java`](https://github.com/AY2526S1-CS2103-F12-2/tp/blob/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered and sorted_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">:information_source: **Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>


### Storage component

**API** : [`Storage.java`](https://github.com/AY2526S1-CS2103-F12-2/tp/blob/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</div>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Logic.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</div>

Similarly, how an undo operation goes through the `Model` component is shown below:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Model.png)

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

<img src="images/CommitActivityDiagram.png" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**: NUS School of Computing Students
* He is a student in SOC
* He likes things to be fast and efficient
* Need to find connections
* Often needs to find students taking the same module for group work
* Often need to contact groupmates
* Loves using telegram for communication


**Value proposition**:
DevBooks provides fast digital access to students in NUS SOC, making it easier to
contact any student using their preferred mode of communication. Allow students to find project mates from the
same project group easily and view the development profile of their contact.

### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                                    | I want to …​                     | So that I can…​                                                        |
| -------- | ------------------------------------------ | ------------------------------ | ---------------------------------------------------------------------- |
| `* * *` | beginner user                 |	add a contact	        | i can retrieve it when I want	|
| `* * *` | beginner user                 |	add a phone number to a contact	        | I can easily message or call them when I need it	|
| `* * *` | beginner user                 |	tag my contacts	        | I can easily group my contacts	|
| `* * *` | beginner user                 |	list out all the contacts within my contact book	        | I can get an overview of everyone I've added so far	|
| `* * *` | beginner user                 |	delete a contact	        | I can declutter my contacts list if necessary	|
| `* * *` | beginner user                 |	look for list of available commands that I can use	        | I can know what commands I can use without memorizing	|
| `* * `  | beginner user                 |	add a telegram handle to a contact	        | I can easily access my contact's telegram when i need it	|
| `* * `  | beginner user                 |	update a contact	        | I can update the details of my contacts when they change	|
| `* * `  | student                       |	search for a contact by name	        | i can quickly find their contact details	|
| `* * `  | intermediate user             |	add all contact information all just with one line	        | I don't need to do so manually using mutiple updates	|
| `* * `  | beginner user                 |	delete a tag	        | I can remove outdated tags	|
| `* * `  | student                       |	quickly scroll through using arrow keys in an alphabetical list 	        | i find someone without typing.	|
| `* * `  | impatient user                |	load my contacts within 2 seconds	        | I do not need to wait too long to access my contacts	|
| `* * `  | beginner user                 |	see the recently accessed contacts	        | I can know who I recently contacted	|
| `* * `  | beginner user                 |	add an email address to a contact	        | I can easily email them when I need it	|
| `* * `  | beginner user                 |	set a preferred mode of communication to a contact	        | I can reach them at their preferred platform when |
| `* * `  | beginner user                 |	add a contact to my favourites list	        | I can easily access them	|
| `* * `  | beginner user                 |	see my favourites list	        | I can contact a favourite friend	|
| `* * `  | student                       |	be able to group contacts by teams	        | i can access all members in one place.	|
| `* * `  | advanced user                 |	be able to add multiple tags to a contact	        | i can find my groups quickly.	|
| `* * `  | intermediate user             |	access the github page of a contact	        | I can easily view their user activity and repos	|
| `* * `  | beginner user                 |	filter my contacts by tags and name	        | I can find my contact(s) easier	|
| `* * `  | beginner user                 |	get more details with each commands and flags	        | I am able to learn how to properly use each command/flag	|
| `* * `  | student                       |	back up my contacts to a CSV	        | i dont lose my contacts if the device fails.	|
| `* * `  | student                       |	import a list of contacts from a CSV or file	        | I can quickly add contacts to another device.	|
| `* * `  | advanced user                 |	create customizable shortcuts	        | I can increase the speed of using the application	|
| `* * `  | advanced user                 |	view a list of my shortcuts	        | I can see an overview of my customizations	|
| `* * `  | advanced user                 |	create my own shortcuts	        | I can quickly type out long commands instantly	|
| `* * `  | intermediate user             |	access my command history through arrow keys	        | I can execute repeated operations quickly	|
| `* * `  | intermediate user             |	press "Tab" to auto-complete the command that I am typing out	        | I can quickly finish the command that I am typing.	|
| `* * `  | intermediate user             |	see hint text of what command would be autocompleted if I press "tab"	        | I have visual feedback before I autocomplete a command	|
| `* * `  | intermediate user             |	revert the last command	        | I can undo any mistakes	|
| `* * `  | beginner user                 |	be able to rename my tags	        | I mass edit contacts with the same tag	|
| `* * `  | beginner user                 |	 add the github handle to a contact	        | I can easily access their github page in the future	|
| `* * `  | beginner user                 |	delete all contacts associated with a tag	        | I can remove irrelevant contacts from my list	|
| `*`     | beginner user                 |	go through a tutorial of the app	        | I can familiarize myself on how to use the app.|
| `*`     | beginner user                 |	read the documentation	        | I can get started on using the app|
| `*`     | beginner user                 |	import existing contacts from a .vcf file	        | I do not need to re-type all of my existing contacts|
| `*`     | student                       |	launch my telegram chat with the contact person through the app	        | I can start chatting with my contacts on telegram quickly|
| `*`     | student                       |	launch my email app quickly through the app	        | I can start writing email to my contacts quickly|
| `*`     | student                       |	delete contacts by date time query	        | my address book is clean.|
| `*`     | advanced user                 |	use DevBooks inside of my command console	        | I don't need to open the application to perform an operation|
| `*`     | aesthetic-minded individual   |	customize the theme of the application	        | it's more personal to me|

*{More to be added}*

### Use cases

(For all use cases below, the **System** is the `Devbooks` and the **Actor** is the `user`, unless specified otherwise)

**Use case: UC01 - Add Contact**

**MSS**

1.  Devbooks prompts for command
2.  User input add command with required contact information
3.  Devbooks saves contact and show success message
4.  Devbooks shows the updated contact list

    Use case ends.

**Extensions**

* 2a. User input add command with invalid contact information

    * 2a1. Devbooks shows an error message
    * 2a2. User input new add command with contact information

      Steps 2a1-2a2 are repeated until the add command and contact information entered are correct.

      Use case resumes from step 3.

* 2b. Duplicated contact information found

    * 2b1. Devbooks shows an error message
    * 2b2. User input new add command with contact information

      Steps 2b1-2b2 are repeated until the new contact information does not duplicate with existing contacts.

      Use case resumes from step 3.


**Use case: UC02 - Edit Contact**

**MSS**

1.  User edits contact in list
2.  Devbook detects correct data in the entered data
3.  Devbook updates the contact and displays the newly updated contact

    Use case ends.

**Extensions**

* 1a. Devbook detects an error in the entered data.

    * 1a1. Devbook prompts the user for the correct data.
    * 1a2. Beginner user enters new data.

      Steps 1a1-1a2 are repeated until the data entered are correct.

      Use case resumes from step 2.


**Use case: UC05 - Delete Contact**

**MSS**

1.  User inputs delete command with desired information to delete
2.  Devbooks shows a confirmation prompt
3.  User confirms intent to delete
4.  Devbooks deletes the contact
    Use case ends.

**Extensions**

* 1a. Devbooks does not find a corresponding user to delete

    * 1a1. Devbooks shows an error message

      Use case ends.

* 3a. User inputs an invalid confirmation prompt

    * 3a1. Devbooks shows an error message
    * 3a2. Devbooks re-prompts for confirmation

      Steps 3a1-3a2 are repeated until the data entered are correct.

      Use case resumes from step 4.


**Use case: UC09 – Find Contact by Name**

**MSS**

1.  User inputs a find command with keyword(s) to search
2.  Devbooks validates the input and searches for matching contacts
3.  Devbooks displays the matching contacts

    Use case ends.

**Extensions**

* 2a. User inputs an invalid search format

    * 2a1. Devbooks shows an error message
    * 2a2. Devbooks prompts for keywords
    * 2a3. User inputs find command with keyword(s) to search

    Steps 2a1–2a3 are repeated until valid input is provided.

    Use case resumes from step 3.


**Use case: UC06 – Show list of commands**

**MSS**

1.  User inputs a help command to look up all commands available
2.  Devbook lists out all the commands with its uses
3.  Devbook prompts the user to select an available command for more details
4.  User chooses specific help commands to look up details of one specific command.
5.  Devbooks shows the specific instructions and guide on how to use that command

    Use case ends.

**Extensions**

* 3a. User did not select any commands to view command details

  Use case ends.

* 4a. User inputs a commands that does not exist in list of commands

    * 4a1. Devbooks shows an error message
    * 4a2. Devbooks prompts user to select available command
    * 4a3. User selects a command from list of available command

  Steps 4a1–4a3 are repeated until available command is selected.

  Use case resumes from step 5.


### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
2.  Should be able to hold up to 1000 persons without a noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  Should only be used by a single user (i.e. not a multi-user product).
5.  Should store data locally and in a human editable text file.
6.  Should not use a DBMS to store data.
7.  Should work without requiring an installer.
8.  Should not depend on a specific remote server.
9.  Should be packaged into a single JAR file.
10. Should be able to load within 5 seconds.
*{More to be added}*

### Glossary

* **CLI (command line interface)**: A text-based interface where users type commands to interact with the system.
* **Development profile**: A user's GitHub profile used to store, manage, and showcase software development projects.
* **Digital access**: The ability to access DevBooks and retrieve information without needing any internet connection
* **Flag/ Parameter**: A prefix in a command (e.g. n/, p/, t/) used to specify values for different fields.
* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **NUS SOC**: National University of Singapore, School of Computing
* **Preferred Mode of communication**: Telegram, Email or Phone
* **Private contact detail**: A contact detail that is not meant to be shared with others
* **Project mates/ Project group**: A project group is a team of students from the same module who work together on an assigned project. A project mate is a member of that group.
* **Tag**: A Label assigned to contacts for easy grouping and searching

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_
