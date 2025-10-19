package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.FLAG_EMAIL_LAUNCH;
import static seedu.address.logic.parser.CliSyntax.FLAG_GITHUB_LAUNCH;
import static seedu.address.logic.parser.CliSyntax.FLAG_TELEGRAM_LAUNCH;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.LaunchCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.util.ApplicationLinkLauncher.ApplicationType;

/**
 * Parses input arguments and creates a new LaunchCommand object
 */
public class LaunchCommandParser implements Parser<LaunchCommand> {

    public static final String MESSAGE_USAGE = LaunchCommand.COMMAND_WORD + ": Launches the specified application "
            + "for the person identified by the index number used in the displayed person list.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + FLAG_EMAIL_LAUNCH + " | " + FLAG_TELEGRAM_LAUNCH + " | " + FLAG_GITHUB_LAUNCH + "]\n"
            + "Example: " + LaunchCommand.COMMAND_WORD + " 1 " + FLAG_EMAIL_LAUNCH;

    /**
     * Parses the given {@code String} of arguments in the context of the LaunchCommand
     * and returns a LaunchCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public LaunchCommand parse(String args) throws ParseException {
        // Implementation for parsing launch command arguments
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(
                        args, FLAG_EMAIL_LAUNCH, FLAG_TELEGRAM_LAUNCH, FLAG_GITHUB_LAUNCH
                );

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, LaunchCommand.MESSAGE_USAGE), pe);
        }

        boolean emailFlag = argMultimap.getValue(FLAG_EMAIL_LAUNCH).isPresent();
        boolean telegramFlag = argMultimap.getValue(FLAG_TELEGRAM_LAUNCH).isPresent();
        boolean githubFlag = argMultimap.getValue(FLAG_GITHUB_LAUNCH).isPresent();

        // Ensures that exactly one flag is provided
        if (emailFlag && !telegramFlag && !githubFlag) {
            return new LaunchCommand(index, ApplicationType.EMAIL);
        } else if (!emailFlag && telegramFlag && !githubFlag) {
            return new LaunchCommand(index, ApplicationType.TELEGRAM);
        } else if (!emailFlag && !telegramFlag && githubFlag) {
            return new LaunchCommand(index, ApplicationType.GITHUB);
        } else {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, LaunchCommand.MESSAGE_USAGE));
        }
    }
}
