package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.FLAG_RENAME_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RENAMED_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TARGET_TAG;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Edits the Tag details of single or multiple contacts in the address book.
 */
public class TagCommand extends Command {

    public static final String COMMAND_WORD = "tag";

    public static final String MESSAGE_DESCRIPTION =
            "Edits the tags details for all persons identified in the displayed person list.";

    public static final String MESSAGE_FLAGS = "Flags: [" + FLAG_RENAME_TAG + "]";

    public static final String MESSAGE_PARAMETERS = "Parameters: "
            + PREFIX_TARGET_TAG + "(target) TAG "
            + "[" + PREFIX_RENAMED_TAG + "(renamed) TAG]";

    public static final String MESSAGE_EXAMPLE = "Example: "
            + COMMAND_WORD + " " + FLAG_RENAME_TAG + " "
            + PREFIX_TARGET_TAG + "CS1101" + " " + PREFIX_RENAMED_TAG + "CS2100";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": "
            + MESSAGE_DESCRIPTION + "\n"
            + MESSAGE_FLAGS + "\n"
            + MESSAGE_PARAMETERS + "\n"
            + MESSAGE_EXAMPLE;

    public static final String MESSAGE_RENAMED_SUCCESS = "Renamed tag %s to %s for %d person(s).";
    public static final String MESSAGE_TAG_FAILURE = "No persons found with tag: %s";
    public static final String MESSAGE_NO_FLAG_PROVIDED = "No/Invalid Flag provided\n" + MESSAGE_USAGE;
    public static final String MESSAGE_RENAMED_EXACTLY_ONE = "Must have exactly one target tag and one renamed tag";

    private final Set<Tag> targetTags;
    private final Set<Tag> renamedTags;

    /**
     * Creates a TagCommand to either rename or delete a specified tag from multiple people
     *
     * @param targetTag     set of target tags to delete/rename
     * @param renamedTags   name of tags to be renamed to
     */
    public TagCommand(Set<Tag> targetTag, Set<Tag> renamedTags) {
        this.targetTags = targetTag;
        this.renamedTags = renamedTags;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> lastShownList = model.getSortedAndFilteredPersonList();
        return executeRenameTags(model, lastShownList);
    }

    /**
     * Executes the TagCommand given the rename flag {@code -r} and returns the result message.
     *
     * @param model {@code Model} which the command should operate on.
     * @param lastShownList {@code lastShownList} which the command should iterate on.
     * @return feedback message of the rename tag result for display
     * @throws CommandException
     */
    private CommandResult executeRenameTags(Model model, List<Person> lastShownList) throws CommandException {
        assert targetTags.size() == 1;

        Tag targetTag = targetTags.iterator().next();
        Tag renamedTag = renamedTags.iterator().next();
        int updatedCount = 0;

        for (Person personToEdit : lastShownList) {
            // Skip people who don't have the target tag
            if (!personToEdit.getTags().contains(targetTag)) {
                continue;
            }
            Person updatedPerson = createPerson(personToEdit);
            model.setPerson(personToEdit, updatedPerson);
            updatedCount++;
        }

        if (updatedCount == 0) {
            throw new CommandException(String.format(MESSAGE_TAG_FAILURE, targetTags));
        }

        return new CommandResult(String.format(MESSAGE_RENAMED_SUCCESS, targetTag, renamedTag, updatedCount));
    }

    private Person createPerson(Person personToEdit) {
        Set<Tag> updatedTagSet = getUpdatedTagSet(personToEdit);

        return new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getTelegram(),
                personToEdit.getGithub(),
                personToEdit.getPreferredMode(),
                updatedTagSet,
                personToEdit.isPinned(),
                personToEdit.getPinnedAt().orElse(null)
        );
    }

    private Set<Tag> getUpdatedTagSet(Person person) {
        Set<Tag> baseTags = new HashSet<>(person.getTags());

        assert !baseTags.isEmpty() : "baseTags should contain at least the targetTag";
        assert renamedTags.size() == 1 : "renamedTag should contain exactly one tag to be renamed";

        baseTags.removeAll(targetTags);
        baseTags.addAll(renamedTags);

        return baseTags;
    }

    /**
     * @return <code>true</code> as TagCommand modifies the address book
     * @inheritDoc
     */
    @Override
    public boolean requiresWrite() {
        return true;
    }

    /**
     * Registers the launch command with the command registry, providing detailed help information
     * including usage syntax, parameters, and examples for user reference.
     * This method is called during application initialization to make the command
     * available in the help system.
     */
    public static void registerHelp() {
        CommandRegistry.register(
                COMMAND_WORD,
                MESSAGE_DESCRIPTION,
                MESSAGE_EXAMPLE,
                MESSAGE_PARAMETERS
        );
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof TagCommand)) {
            return false;
        }
        TagCommand otherCommand = (TagCommand) other;
        return targetTags.equals(otherCommand.targetTags) && renamedTags.equals(otherCommand.renamedTags);
    }
}
