package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Finds and lists all persons in address book whose name or tag contains any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all persons whose names or tags contain any of "
            + "the specified keywords (case-insensitive) and displays them as a list with index numbers.\n"
            + "Parameters: n/KEYWORD [MORE_KEYWORDS]... (to search by name)\n"
            + "            t/KEYWORD [MORE_KEYWORDS]... (to search by tag)\n"
            + "Note: Only one prefix (n/ or t/) can be used at a time\n"
            + "Example: " + COMMAND_WORD + " n/alice bob charlie\n"
            + COMMAND_WORD + " t/family";

    public static final String MESSAGE_MULTIPLE_PREFIXES_NOT_ALLOWED =
            "WARNING: Both prefixes provided. Only the first prefix is used.\n";

    private final Predicate<Person> predicate;
    private final boolean showWarning;

    /**
     * Constructs a FindCommand with the specified search criteria and warning flag
     *
     * @param predicate A condition to check which person to find
     * @param showWarning whether to show warning if no person match
     */
    public FindCommand(Predicate<Person> predicate, Boolean showWarning) {
        this.predicate = predicate;
        this.showWarning = showWarning;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        String combinedMessage = showWarning
                ? MESSAGE_MULTIPLE_PREFIXES_NOT_ALLOWED + Messages.MESSAGE_PERSONS_LISTED_OVERVIEW
                : Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;

        return new CommandResult(
                String.format(combinedMessage, model.getSortedAndFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand)) {
            return false;
        }

        FindCommand otherFindCommand = (FindCommand) other;
        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }

    /**
     * Registers the find command with the command registry, providing detailed help information
     * including usage syntax, parameters, and examples for user reference.
     * This method is called during application initialization to make the command
     * available in the help system.
     */
    public static void registerHelp() {
        CommandRegistry.register(
                COMMAND_WORD,
                "Finds contacts whose names or tags contain any of the given keywords",
                "Example: find n/alice bob charlie\n"
                        + "            Example: find t/family friends",
                "Usage:\n  find n/KEYWORD [MORE_KEYWORDS]...\n  find t/KEYWORD [MORE_KEYWORDS]...\n\n"
                        + "Finds all contacts whose names or tags contain any of the specified keywords "
                        + "(case-insensitive).\n\n"
                        + "Parameters:\n"
                        + "  PREFIX - Either 'n/' to search by name or 't/' to search by tag\n"
                        + "  KEYWORD - One or more keywords to search for (required)\n\n"
                        + "Notes:\n"
                        + "  - Keywords must match the start of the word (e.g. 'De' matches 'Derek')\n"
                        + "  - Only one prefix can be used at a time\n"
                        + "  - The search is case-insensitive (e.g., 'alice' matches 'Alice')\n"
                        + "  - Only the name field or tag field is searched\n"
                        + "  - Contacts matching at least one keyword will be displayed\n"
                        + "  - If both prefixes are provided. Only the first prefix is used."

        );
    }
}
