package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_LAUNCH_APP_FLAG;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.parser.CliSyntax.FLAG_EMAIL_LAUNCH;
import static seedu.address.logic.parser.CliSyntax.FLAG_GITHUB_LAUNCH;
import static seedu.address.logic.parser.CliSyntax.FLAG_TELEGRAM_LAUNCH;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.LaunchCommand;

public class LaunchCommandParserTest {
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, LaunchCommand.MESSAGE_USAGE);
    private LaunchCommandParser parser = new LaunchCommandParser();

    /**
     * Tests that parsing fails when parts of the command are missing.
     */
    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, VALID_NAME_AMY, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, "1", MESSAGE_INVALID_FORMAT);

        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    /**
     * Tests that parsing fails when an invalid preamble is provided.
     */
    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5" + FLAG_EMAIL_LAUNCH, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0" + FLAG_EMAIL_LAUNCH, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);
    }

    /**
     * Tests that parsing fails when an invalid launch application flag is provided.
     */
    @Test
    public void parse_invalidFlag_failure() {
        // invalid launch application flag
        assertParseFailure(parser, "1" + INVALID_LAUNCH_APP_FLAG, MESSAGE_INVALID_FORMAT);
    }

    /**
     * Tests that parsing is successful when a valid Email launch flag is provided.
     */
    @Test
    public void parse_validEmail_success() {
        // valid email launch flag
        LaunchCommand expectedCommand = new LaunchCommand(INDEX_FIRST_PERSON,
                seedu.address.logic.util.ApplicationLinkLauncher.ApplicationType.EMAIL);
        assertParseSuccess(parser, "1 " + FLAG_EMAIL_LAUNCH, expectedCommand);
    }

    /**
     * Tests that parsing is successful when a valid Telegram launch flag is provided.
     */
    @Test
    public void parse_validTelegram_success() {
        // valid telegram launch flag
        LaunchCommand expectedCommand = new LaunchCommand(INDEX_FIRST_PERSON,
                seedu.address.logic.util.ApplicationLinkLauncher.ApplicationType.TELEGRAM);
        assertParseSuccess(parser, "1 " + FLAG_TELEGRAM_LAUNCH, expectedCommand);
    }

    /**
     * Tests that parsing is successful when a valid GitHub launch flag is provided.
     */
    @Test
    public void parse_validGithub_success() {
        // valid github launch flag
        LaunchCommand expectedCommand = new LaunchCommand(INDEX_FIRST_PERSON,
                seedu.address.logic.util.ApplicationLinkLauncher.ApplicationType.GITHUB);
        assertParseSuccess(parser, "1 " + FLAG_GITHUB_LAUNCH, expectedCommand);
    }

    /**
     * Tests that parsing fails when multiple launch application flags are provided.
     */
    @Test
    public void parse_multipleFlags_failure() {
        // email and telegram
        assertParseFailure(
                parser, "1 " + FLAG_EMAIL_LAUNCH + " " + FLAG_TELEGRAM_LAUNCH, MESSAGE_INVALID_FORMAT);

        // telegram and github
        assertParseFailure(
                parser, "1 " + FLAG_EMAIL_LAUNCH + " " + FLAG_GITHUB_LAUNCH, MESSAGE_INVALID_FORMAT);

        // email and github
        assertParseFailure(
                parser, "1 " + FLAG_TELEGRAM_LAUNCH + " " + FLAG_GITHUB_LAUNCH, MESSAGE_INVALID_FORMAT);

        // all three flags
        assertParseFailure(
                parser, "1 " + FLAG_EMAIL_LAUNCH + " " + FLAG_TELEGRAM_LAUNCH + " " + FLAG_GITHUB_LAUNCH,
                MESSAGE_INVALID_FORMAT);
    }
}
