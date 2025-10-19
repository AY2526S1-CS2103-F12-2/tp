package seedu.address.logic.commands;

import static org.mockito.Mockito.mockStatic;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GITHUB_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TELEGRAM_AMY;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.LaunchCommand.MESSAGE_MISSING_EMAIL;
import static seedu.address.logic.commands.LaunchCommand.MESSAGE_MISSING_GITHUB;
import static seedu.address.logic.commands.LaunchCommand.MESSAGE_MISSING_TELEGRAM;
import static seedu.address.logic.commands.LaunchCommand.MESSAGE_UNRECOGNIZED_FLAG;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import seedu.address.logic.Messages;
import seedu.address.logic.util.ApplicationLinkLauncher;
import seedu.address.logic.util.ApplicationLinkLauncher.ApplicationType;
import seedu.address.logic.util.ApplicationLinkResult;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class LaunchCommandTest {

    private final Model model = new ModelManager();

    /**
     * Tests that executing a LaunchCommand for Email on a person with an email address
     * results in a successful launch.
     */
    @Test
    public void excute_launchCommand_emailsuccess() {
        Person person = new PersonBuilder().withEmail(VALID_EMAIL_AMY).build();
        model.addPerson(person);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        // Mock the static method ApplicationLinkLauncher.launchEmail
        try (MockedStatic<ApplicationLinkLauncher> mocked = mockStatic(ApplicationLinkLauncher.class)) {
            mocked.when(() -> ApplicationLinkLauncher.launchEmail(VALID_EMAIL_AMY))
                    .thenReturn(new ApplicationLinkResult(true,
                            String.format(ApplicationLinkLauncher.MESSAGE_SUCCESS, ApplicationType.EMAIL)));

            LaunchCommand command = new LaunchCommand(INDEX_FIRST_PERSON, ApplicationType.EMAIL);
            String expectedMessage = String.format(ApplicationLinkLauncher.MESSAGE_SUCCESS, ApplicationType.EMAIL);
            assertCommandSuccess(command, model, expectedMessage, expectedModel);
        }
    }

    /**
     * Tests that executing a LaunchCommand for Telegram on a person with a Telegram handle
     * results in a successful launch.
     */
    @Test
    public void execute_validTelegramLaunch_success() {
        Person person = new PersonBuilder().withTelegram(VALID_TELEGRAM_AMY).build();
        model.addPerson(person);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        try (MockedStatic<ApplicationLinkLauncher> mocked = mockStatic(ApplicationLinkLauncher.class)) {
            mocked.when(() -> ApplicationLinkLauncher.launchTelegram(VALID_TELEGRAM_AMY))
                    .thenReturn(new ApplicationLinkResult(true,
                            String.format(ApplicationLinkLauncher.MESSAGE_SUCCESS, ApplicationType.TELEGRAM)));

            LaunchCommand command = new LaunchCommand(INDEX_FIRST_PERSON, ApplicationType.TELEGRAM);
            String expectedMessage = String.format(ApplicationLinkLauncher.MESSAGE_SUCCESS, ApplicationType.TELEGRAM);
            assertCommandSuccess(command, model, expectedMessage, expectedModel);
        }
    }

    /**
     * Tests that executing a LaunchCommand for GitHub on a person with a GitHub profile
     * results in a successful launch.
     */
    @Test
    public void execute_validGithubLaunch_success() {
        Person person = new PersonBuilder().withGithub(VALID_GITHUB_AMY).build();
        model.addPerson(person);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        try (MockedStatic<ApplicationLinkLauncher> mocked = mockStatic(ApplicationLinkLauncher.class)) {
            mocked.when(() -> ApplicationLinkLauncher.launchGithub(VALID_GITHUB_AMY))
                    .thenReturn(new ApplicationLinkResult(true,
                            String.format(ApplicationLinkLauncher.MESSAGE_SUCCESS, ApplicationType.GITHUB)));

            LaunchCommand command = new LaunchCommand(INDEX_FIRST_PERSON, ApplicationType.GITHUB);
            String expectedMessage = String.format(ApplicationLinkLauncher.MESSAGE_SUCCESS, ApplicationType.GITHUB);
            assertCommandSuccess(command, model, expectedMessage, expectedModel);
        }
    }

    /**
     * Tests that executing a LaunchCommand for Email on a person without an email address
     * results in a CommandException being thrown.
     */
    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Person person = new PersonBuilder().build();
        model.addPerson(person);

        LaunchCommand command = new LaunchCommand(INDEX_SECOND_PERSON, ApplicationType.EMAIL);
        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Tests that executing a LaunchCommand for Email on a person without an email address
     * results in a CommandException being thrown.
     */
    @Test
    public void execute_missingEmail_throwsCommandException() {
        Person person = new PersonBuilder().build();
        model.addPerson(person);

        LaunchCommand command = new LaunchCommand(INDEX_FIRST_PERSON, ApplicationType.EMAIL);
        assertCommandFailure(command, model,
                String.format(MESSAGE_MISSING_EMAIL, person.getName().fullName));
    }

    /**
     * Tests that executing a LaunchCommand for Telegram on a person without a Telegram handle
     * results in a CommandException being thrown.
     */
    @Test
    public void execute_missingTelegram_throwsCommandException() {
        Person person = new PersonBuilder().build();
        model.addPerson(person);

        LaunchCommand command = new LaunchCommand(INDEX_FIRST_PERSON, ApplicationType.TELEGRAM);
        assertCommandFailure(command, model,
                String.format(MESSAGE_MISSING_TELEGRAM, person.getName().fullName));
    }

    /**
     * Tests that executing a LaunchCommand for GitHub on a person without a GitHub profile
     * results in a CommandException being thrown.
     */
    @Test
    public void execute_missingGithub_throwsCommandException() {
        Person person = new PersonBuilder().build();
        model.addPerson(person);

        LaunchCommand command = new LaunchCommand(INDEX_FIRST_PERSON, ApplicationType.GITHUB);
        assertCommandFailure(command, model,
                String.format(MESSAGE_MISSING_GITHUB, person.getName().fullName));
    }

    /**
     * Tests that executing a LaunchCommand with an unrecognized application type
     * results in a CommandException being thrown.
     */
    @Test
    public void execute_unknownApplicationType_throwsCommandException() {
        Person person = new PersonBuilder().withEmail(VALID_EMAIL_AMY).build();
        model.addPerson(person);

        LaunchCommand command = new LaunchCommand(INDEX_FIRST_PERSON, ApplicationType.UNKOWNN);
        assertCommandFailure(command, model, MESSAGE_UNRECOGNIZED_FLAG);
    }

    /* The following test cases checks if the equals methods works properly */
    @Test
    public void equals_sameObject_returnsTrue() {
        LaunchCommand launchCommand = new LaunchCommand(INDEX_FIRST_PERSON, ApplicationType.EMAIL);
        assert launchCommand.equals(launchCommand);
    }

    @Test
    public void equals_notInstanceOfLaunchCommand_returnsFalse() {
        LaunchCommand launchCommand = new LaunchCommand(INDEX_FIRST_PERSON, ApplicationType.EMAIL);
        assert !launchCommand.equals("Some String");
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        LaunchCommand launchCommand1 = new LaunchCommand(INDEX_FIRST_PERSON, ApplicationType.EMAIL);
        LaunchCommand launchCommand2 = new LaunchCommand(INDEX_FIRST_PERSON, ApplicationType.EMAIL);
        assert launchCommand1.equals(launchCommand2);
    }

    @Test
    public void equals_differentIndex_returnsFalse() {
        LaunchCommand launchCommand1 = new LaunchCommand(INDEX_FIRST_PERSON, ApplicationType.EMAIL);
        LaunchCommand launchCommand2 = new LaunchCommand(INDEX_SECOND_PERSON, ApplicationType.EMAIL);
        assert !launchCommand1.equals(launchCommand2);
    }

    @Test
    public void equals_differentApplicationType_returnsFalse() {
        LaunchCommand launchCommand1 = new LaunchCommand(INDEX_FIRST_PERSON, ApplicationType.EMAIL);
        LaunchCommand launchCommand2 = new LaunchCommand(INDEX_FIRST_PERSON, ApplicationType.TELEGRAM);
        assert !launchCommand1.equals(launchCommand2);
    }
}
