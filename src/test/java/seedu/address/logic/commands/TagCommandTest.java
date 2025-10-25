package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;


public class TagCommandTest {
    private Model model;
    private final Tag targetTag = new Tag("friends");
    private final Tag renamedTag = new Tag("buddies");

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_multiplePersonsUpdated_success() {
        // Give multiple people the same target tag
        Person first = model.getSortedAndFilteredPersonList().get(0);
        Person second = model.getSortedAndFilteredPersonList().get(1);
        Person third = model.getSortedAndFilteredPersonList().get(2);

        Person updatedFirst = new PersonBuilder(first).withTags("friends").build();
        Person updatedSecond = new PersonBuilder(second).withTags("friends").build();
        Person updatedThird = new PersonBuilder(third).withTags("friends").build();

        model.setPerson(first, updatedFirst);
        model.setPerson(second, updatedSecond);
        model.setPerson(third, updatedThird);
        Set<Tag> target = Set.of(targetTag);
        Set<Tag> renamed = Set.of(renamedTag);
        TagCommand command = new TagCommand(target, renamed);

        String expectedMessage = String.format(TagCommand.MESSAGE_RENAMED_SUCCESS,
                targetTag, renamedTag, 3);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(updatedFirst, new PersonBuilder(updatedFirst).withTags("buddies").build());
        expectedModel.setPerson(updatedSecond, new PersonBuilder(updatedSecond).withTags("buddies").build());
        expectedModel.setPerson(updatedThird, new PersonBuilder(third).withTags("buddies").build());

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noPersonsWithTag_throwsCommandException() {
        Set<Tag> target = Set.of(new Tag("missingTag"));
        Set<Tag> renamed = Set.of(new Tag("whatever"));
        TagCommand command = new TagCommand(target, renamed);

        assertCommandFailure(command, model,
                String.format(TagCommand.MESSAGE_TAG_FAILURE, target));
    }
}
