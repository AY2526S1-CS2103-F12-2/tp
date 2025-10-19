package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.FLAG_EMAIL_LAUNCH;
import static seedu.address.logic.parser.CliSyntax.FLAG_GITHUB_LAUNCH;
import static seedu.address.logic.parser.CliSyntax.FLAG_TELEGRAM_LAUNCH;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.util.ApplicationLinkLauncher;
import seedu.address.logic.util.ApplicationLinkLauncher.ApplicationType;
import seedu.address.logic.util.ApplicationLinkResult;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Launches the specified application for a person identified by the index number
 * used in the displayed person list.
 */
public class LaunchCommand extends Command {

    public static final String COMMAND_WORD = "launch";

    protected static final String MESSAGE_MISSING_EMAIL = "%s This person does not have an email address.";
    protected static final String MESSAGE_MISSING_TELEGRAM = "%s This person does not have a Telegram handle.";
    protected static final String MESSAGE_MISSING_GITHUB = "%s This person does not have a GitHub profile.";

    private static final String MESSAGE_DESCRIPTION =
            "Launches the specified application for a person in the address book";
    private static final String MESSAGE_PARAMETERS = "Parameters: INDEX (must be a positive integer)"
            + "[" + FLAG_EMAIL_LAUNCH + " (email) | "
            + FLAG_TELEGRAM_LAUNCH + " (telegram) | "
            + FLAG_GITHUB_LAUNCH + " (github)]"
            + "\n\nNotes:\n"
            + "  - INDEX must be a positive integer corresponding to a person in the displayed list.\n"
            + "  - Exactly one application flag must be specified.\n"
            + "  - Email launch application/browser must be configured on your system.\n"
            + "  - Telegram and GitHub links will be launched in your default web browser.\n";
    private static final String MESSAGE_EXAMPLE = "Example: " + COMMAND_WORD + " 1 " + FLAG_EMAIL_LAUNCH
            + " (Launches the email application/browser for the person at index 1 in the address book.)";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": " + MESSAGE_DESCRIPTION + "\n\n"
            + MESSAGE_PARAMETERS + "\n" + MESSAGE_EXAMPLE;
    public static final String MESSAGE_UNRECOGNIZED_FLAG = "Unrecognized application flag provided.\n" + MESSAGE_USAGE;

    private final Index index;
    private final ApplicationType type;

    /**
     * Creates a LaunchCommand to launch the specified application for the person at the given index.
     *
     * @param index The index of the person in the displayed person list.
     * @param type  The type of application to launch.
     */
    public LaunchCommand(Index index, ApplicationType type) {
        requireNonNull(index);
        requireNonNull(type);

        this.index = index;
        this.type = type;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getSortedAndFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToLaunch = lastShownList.get(index.getZeroBased());
        ApplicationLinkResult result = launchApplicationLink(personToLaunch, type);

        return new CommandResult(result.getMessage());
    }

    /**
     * Launches the specified application link for the given person.
     *
     * @param person    The person whose application link is to be launched.
     * @param type      The type of application to launch.
     * @return The result of the application link launch.
     * @throws CommandException If the person does not have the required information
     *                          for the specified application type.
     */
    private ApplicationLinkResult launchApplicationLink(Person person, ApplicationType type) throws CommandException {
        requireNonNull(person);
        requireNonNull(type);

        switch (type) {
        case EMAIL: {
            if (person.getEmail().isEmpty()) {
                throw new CommandException(String.format(MESSAGE_MISSING_EMAIL, person.getName().fullName));
            }
            return ApplicationLinkLauncher.launchEmail(person.getEmail().value);
        }
        case TELEGRAM: {
            if (person.getTelegram().isEmpty()) {
                throw new CommandException(String.format(MESSAGE_MISSING_TELEGRAM, person.getName().fullName));
            }
            return ApplicationLinkLauncher.launchTelegram(person.getTelegram().value);
        }
        case GITHUB:
            if (person.getGithub().isEmpty()) {
                throw new CommandException(String.format(MESSAGE_MISSING_GITHUB, person.getName().fullName));
            }
            return ApplicationLinkLauncher.launchGithub(person.getGithub().value);
        default: {
            throw new CommandException(MESSAGE_UNRECOGNIZED_FLAG);
        }
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof LaunchCommand)) {
            return false;
        }
        LaunchCommand otherCommand = (LaunchCommand) other;
        return index.equals(otherCommand.index) && type == otherCommand.type;
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
}
