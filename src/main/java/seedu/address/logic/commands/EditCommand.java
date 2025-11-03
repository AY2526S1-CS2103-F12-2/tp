package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GITHUB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PREFERRED_MODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMOVE_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TELEGRAM;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.model.person.PreferredCommunicationMode.MESSAGE_INVALID_PREFERRED_MODE;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.util.Pair;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Email;
import seedu.address.model.person.Github;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.PreferredCommunicationMode;
import seedu.address.model.person.Telegram;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String EXAMPLE_MESSAGE = "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by the index number used in the displayed person list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_TELEGRAM + "TELEGRAM] "
            + "[" + PREFIX_GITHUB + "GITHUB] "
            + "[" + PREFIX_PREFERRED_MODE + "PREFERRED_MODE]"
            + "[" + PREFIX_TAG + "(add) TAG] "
            + "[" + PREFIX_REMOVE_TAG + "(remove) TAG] \n"
            + EXAMPLE_MESSAGE;

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param index                of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        assert editPersonDescriptor.isAnyFieldEdited() : "No fields to edit. Expected at least 1 field being edited";

        List<Person> lastShownList = model.getSortedAndFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(
                    Messages.getMessageInvalidPersonDisplayedIndex(index.getOneBased(), lastShownList.size()));
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());

        Pair<Person, TagUpdateResult> editResult = createEditedPersonAndResult(personToEdit, editPersonDescriptor);
        Person editedPerson = editResult.getKey();
        TagUpdateResult tagUpdateResult = editResult.getValue();

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        String resultMessage = String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));
        if (tagUpdateResult.hasInvalidRemovals()) {
            resultMessage += "\n" + tagUpdateResult.getInvalidRemovalMessage();
        }

        return new CommandResult(resultMessage);
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Pair<Person, TagUpdateResult> createEditedPersonAndResult(
            Person personToEdit, EditPersonDescriptor editPersonDescriptor) throws CommandException {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Telegram updatedTelegram = editPersonDescriptor.getTelegram().orElse(personToEdit.getTelegram());
        Github updatedGithub = editPersonDescriptor.getGithub().orElse(personToEdit.getGithub());
        TagUpdateResult tagUpdateResult = getUpdatedTags(
                personToEdit.getTags(),
                editPersonDescriptor.getTags().orElse(Collections.emptySet()),
                editPersonDescriptor.getRemovedTags().orElse(Collections.emptySet())
        );
        Instant pinnedAt = personToEdit.getPinnedAt().orElse(null);
        PreferredCommunicationMode updatedPreferredMode =
                editPersonDescriptor.getPreferredMode().orElse(personToEdit.getPreferredMode());

        Person editedPerson = new Person(
                updatedName,
                updatedPhone,
                updatedEmail,
                updatedTelegram,
                updatedGithub,
                updatedPreferredMode,
                tagUpdateResult.getUpdatedTags(),
                pinnedAt
        );

        editedPerson = getUpdatedPreferredModePerson(editedPerson, editPersonDescriptor);

        return new Pair<>(editedPerson, tagUpdateResult);
    }

    /**
     * Validates the preferred communication mode of the edited person against their available contact options.
     * Returns a person with preferred mode set to NONE if it is no longer valid.
     * Throws a CommandException if the preferred mode is invalid.
     */
    private static Person getUpdatedPreferredModePerson(Person editedPerson, EditPersonDescriptor editPersonDescriptor)
            throws CommandException {
        // Ensure current preferred mode is valid
        editedPerson = resetInvalidPreferredMode(editedPerson);
        Set<PreferredCommunicationMode> availableModes = editedPerson.getAvailableModes();

        // Extract preferred mode name as string, or null if not set
        String modeString = editPersonDescriptor.getPreferredMode()
                .map(PreferredCommunicationMode::name)
                .orElse(null);

        boolean allowNone = true;
        boolean isInvalidPreferredMode = !PreferredCommunicationMode.isValidMode(modeString, availableModes, allowNone);

        if (isInvalidPreferredMode) {
            throw new CommandException(String.format(MESSAGE_INVALID_PREFERRED_MODE, modeString));
        }

        return editedPerson;
    }

    /**
     * Returns a new Person with preferred mode set to NONE if current mode is no longer available.
     */
    private static Person resetInvalidPreferredMode(Person person) {
        Set<PreferredCommunicationMode> availableModes = person.getAvailableModes();

        PreferredCommunicationMode currentMode = person.getPreferredMode();
        boolean shouldClearPreferredMode = currentMode != null && !availableModes.contains(currentMode);

        // If the preferred mode is no longer valid, create a new Person with NONE preferred mode
        if (shouldClearPreferredMode) {
            person = new Person(person.getName(), person.getPhone(), person.getEmail(),
                    person.getTelegram(), person.getGithub(), PreferredCommunicationMode.NONE,
                    person.getTags(), person.getPinnedAt().orElse(null)
            );
        }
        return person;
    }

    /**
     * Returns the updated set of tags after applying the additions and removals from the
     * {@code editPersonDescriptor}.
     */
    private static TagUpdateResult getUpdatedTags(Set<Tag> baseTags, Set<Tag> tagsToAdd, Set<Tag> tagsToRemove) {
        requireNonNull(baseTags);
        requireNonNull(tagsToAdd);
        requireNonNull(tagsToRemove);

        Set<Tag> updatedTags = new HashSet<>(baseTags);
        Set<Tag> invalidRemovals = new HashSet<>(tagsToRemove);

        updatedTags.addAll(tagsToAdd);
        updatedTags.removeAll(tagsToRemove);
        invalidRemovals.removeAll(baseTags);
        invalidRemovals.removeAll(tagsToAdd);

        return new TagUpdateResult(updatedTags, invalidRemovals);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        EditCommand otherEditCommand = (EditCommand) other;
        return index.equals(otherEditCommand.index)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    /**
     * @return <code>true</code> as EditCommand modifies the address book
     * @inheritDoc
     */
    @Override
    public boolean requiresWrite() {
        return true;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Telegram telegram;
        private Github github;
        private PreferredCommunicationMode preferredMode;
        private Set<Tag> tags;
        private Set<Tag> removedTags;

        public EditPersonDescriptor() {
        }

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setTelegram(toCopy.telegram);
            setGithub(toCopy.github);
            setPreferredMode(toCopy.preferredMode);
            setTags(toCopy.tags);
            setRemovedTags(toCopy.removedTags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, telegram, github, preferredMode, tags, removedTags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setTelegram(Telegram telegram) {
            this.telegram = telegram;
        }

        public Optional<Telegram> getTelegram() {
            return Optional.ofNullable(telegram);
        }

        public void setGithub(Github github) {
            this.github = github;
        }

        public Optional<Github> getGithub() {
            return Optional.ofNullable(github);
        }

        public void setPreferredMode(PreferredCommunicationMode preferredMode) {
            this.preferredMode = preferredMode;
        }

        public Optional<PreferredCommunicationMode> getPreferredMode() {
            return Optional.ofNullable(preferredMode);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        public void setRemovedTags(Set<Tag> removedTags) {
            this.removedTags = (removedTags != null) ? new HashSet<>(removedTags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        public Optional<Set<Tag>> getRemovedTags() {
            return (removedTags != null) ? Optional.of(Collections.unmodifiableSet(removedTags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor)) {
                return false;
            }

            EditPersonDescriptor otherEditPersonDescriptor = (EditPersonDescriptor) other;
            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && Objects.equals(telegram, otherEditPersonDescriptor.telegram)
                    && Objects.equals(github, otherEditPersonDescriptor.github)
                    && Objects.equals(preferredMode, otherEditPersonDescriptor.preferredMode)
                    && Objects.equals(tags == null
                            ? Collections.emptySet()
                            : tags,
                    otherEditPersonDescriptor.tags == null
                            ? Collections.emptySet()
                            : otherEditPersonDescriptor.tags)

                    && Objects.equals(removedTags == null ? Collections.emptySet() : removedTags,
                    otherEditPersonDescriptor.removedTags == null
                            ? Collections.emptySet()
                            : otherEditPersonDescriptor.removedTags);
        }


        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("telegram", telegram)
                    .add("github", github)
                    .add("tags", tags)
                    .toString();
        }
    }

    /**
     * Helper result for getUpdatedTags.
     */
    private static class TagUpdateResult {
        private final Set<Tag> updatedTags;
        private final Set<Tag> invalidRemovals;

        TagUpdateResult(Set<Tag> updatedTags, Set<Tag> invalidRemovals) {
            this.updatedTags = updatedTags;
            this.invalidRemovals = invalidRemovals;
        }

        public Set<Tag> getUpdatedTags() {
            return updatedTags;
        }

        public boolean hasInvalidRemovals() {
            return !invalidRemovals.isEmpty();
        }

        public String getInvalidRemovalMessage() {
            assert hasInvalidRemovals();

            String tagsString = invalidRemovals.stream()
                    .map(Tag::toString)
                    .collect(Collectors.joining(" "));

            return "The following tags could not be removed as they do not exist: "
                    + tagsString;
        }
    }

    /**
     * Registers the edit command with the command registry, providing detailed help information
     * including usage syntax, parameters, and examples for user reference.
     * This method is called during application initialization to make the command
     * available in the help system.
     */
    public static void registerHelp() {
        String detailedUsage = String.format(
                "Usage: edit INDEX [%sNAME] [%sPHONE] [%sEMAIL] [%sTELEGRAM] [%sGITHUB] "
                        + "[%sPREFERRED_MODE] [%sTAG]... [%sTAG]...\n"
                        + "\n"
                        + "Edits the contact at the specified index. At least one optional field must be provided.\n"
                        + "\n"
                        + "Parameters:\n"
                        + "  INDEX              - The index number shown in the displayed contact list (required)\n"
                        + "  %sNAME             - New name for the contact (optional)\n"
                        + "  %sPHONE            - New phone number (optional)\n"
                        + "  %sEMAIL            - New email address (optional)\n"
                        + "  %sTELEGRAM         - New Telegram username (optional)\n"
                        + "  %sGITHUB           - New GitHub username (optional)\n"
                        + "  %sPREFERRED_MODE   - New Preferred communication mode (optional)\n"
                        + "  %sTAG              - Add tag(s) to the contact (optional, can have multiple)\n"
                        + "  %sTAG              - Remove tag(s) from the contact (optional, can have multiple)\n"
                        + "\n"
                        + "Notes:\n"
                        + "  - The index must be a positive integer (1, 2, 3, ...)\n"
                        + "  - Existing values will be overwritten by the new input values\n"
                        + "  - Use %s to add tags and %s to remove tags",
                PREFIX_NAME,
                PREFIX_PHONE,
                PREFIX_EMAIL,
                PREFIX_TELEGRAM,
                PREFIX_GITHUB,
                PREFIX_PREFERRED_MODE,
                PREFIX_TAG,
                PREFIX_REMOVE_TAG,
                PREFIX_NAME,
                PREFIX_PHONE,
                PREFIX_EMAIL,
                PREFIX_TELEGRAM,
                PREFIX_GITHUB,
                PREFIX_PREFERRED_MODE,
                PREFIX_TAG,
                PREFIX_REMOVE_TAG,
                PREFIX_TAG,
                PREFIX_REMOVE_TAG
        );

        CommandRegistry.register(
                COMMAND_WORD,
                "Edits the details of an existing contact by index number",
                EXAMPLE_MESSAGE,
                detailedUsage
        );
    }
}
