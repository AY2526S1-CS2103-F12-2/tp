package seedu.address.logic.commands;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.parser.AddCommandParser;
import seedu.address.logic.parser.DeleteCommandParser;
import seedu.address.logic.parser.EditCommandParser;
import seedu.address.logic.parser.ExportCommandParser;
import seedu.address.logic.parser.FindCommandParser;
import seedu.address.logic.parser.HelpCommandParser;
import seedu.address.logic.parser.LaunchCommandParser;
import seedu.address.logic.parser.ListCommandParser;
import seedu.address.logic.parser.PinCommandParser;
import seedu.address.logic.parser.TagCommandParser;
import seedu.address.logic.parser.UnpinCommandParser;

/**
 * Maintains a registry of all available commands and their help information.
 * This class acts as a central repository where commands can register themselves
 * to provide consistent help documentation across the application.
 */
public class CommandRegistry {
    private static final Logger logger = LogsCenter.getLogger(CommandRegistry.class);
    private static final Map<String, CommandInfo> commands = new HashMap<>();
    private static final Map<String, CommandFactory> commandFactoryMap = new HashMap<>();

    // List of command class names to register
    private static final List<String> COMMAND_CLASS_NAMES = List.of(
            "AddCommand",
            "ClearCommand",
            "DeleteCommand",
            "EditCommand",
            "ExitCommand",
            "ExportCommand",
            "FindCommand",
            "HelpCommand",
            "ListCommand",
            "LaunchCommand",
            "PinCommand",
            "UnpinCommand",
            "TagCommand"
    );

    /**
     * Represents information about a command including its word, description, and usage details.
     */
    public static class CommandInfo {
        private final String commandWord;
        private final String description;
        private final String example;
        private final String detailedUsage;

        /**
         * Constructs a CommandInfo with the specified details.
         *
         * @param commandWord The command word used to invoke this command
         * @param description A brief description of what the command does
         * @param example     An example showing how to use the command
         */
        public CommandInfo(String commandWord, String description, String example) {
            this(commandWord, description, example, null);
        }

        /**
         * Constructs a CommandInfo with the specified details including detailed usage information.
         *
         * @param commandWord   The command word used to invoke this command
         * @param description   A brief description of what the command does
         * @param example       An example showing how to use the command
         * @param detailedUsage Detailed usage information including all parameters and flags
         */
        public CommandInfo(String commandWord, String description, String example, String detailedUsage) {
            this.commandWord = commandWord;
            this.description = description;
            this.example = example;
            this.detailedUsage = detailedUsage;
        }

        /**
         * Returns the command word for this command.
         *
         * @return The command word as a {@code String}
         */
        public String getCommandWord() {
            return commandWord;
        }

        /**
         * Returns the description of what this command does.
         *
         * @return The description as a {@code String}
         */
        public String getDescription() {
            return description;
        }

        /**
         * Returns an example showing how to use this command.
         *
         * @return The usage example as a {@code String}, or an empty string if no example is provided
         */
        public String getExample() {
            return example;
        }

        /**
         * Returns the detailed usage information for this command.
         *
         * @return The detailed usage as a {@code String}, or null if no detailed usage is provided
         */
        public String getDetailedUsage() {
            return detailedUsage;
        }

        /**
         * Returns whether this command has detailed usage information available.
         *
         * @return {@code true} if detailed usage information is available, {@code false} otherwise
         */
        public boolean hasDetailedUsage() {
            return detailedUsage != null && !detailedUsage.isEmpty();
        }
    }

    /**
     * Initializes the command registry by calling registerHelp() on all configured commands.
     */
    public static void initialize() {
        logger.info("Initializing command registry");

        for (String className : COMMAND_CLASS_NAMES) {
            try {
                Class<?> clazz = Class.forName("seedu.address.logic.commands." + className);
                Method method = clazz.getMethod("registerHelp");
                method.invoke(null);
            } catch (Exception e) {
                logger.warning("Failed to register " + className + ": " + e.getMessage());
            }
        }

        // Instantiate the command factory map
        commandFactoryMap.put(AddCommand.COMMAND_WORD, (args) -> new AddCommandParser().parse(args));
        commandFactoryMap.put(EditCommand.COMMAND_WORD, (args) -> new EditCommandParser().parse(args));
        commandFactoryMap.put(DeleteCommand.COMMAND_WORD, (args) -> new DeleteCommandParser().parse(args));
        commandFactoryMap.put(ClearCommand.COMMAND_WORD, (args) -> new ClearCommand());
        commandFactoryMap.put(FindCommand.COMMAND_WORD, (args) -> new FindCommandParser().parse(args));
        commandFactoryMap.put(ListCommand.COMMAND_WORD, (args) -> new ListCommandParser().parse(args));
        commandFactoryMap.put(ExitCommand.COMMAND_WORD, (args) -> new ExitCommand());
        commandFactoryMap.put(HelpCommand.COMMAND_WORD, (args) -> new HelpCommandParser().parse(args));
        commandFactoryMap.put(LaunchCommand.COMMAND_WORD, (args) -> new LaunchCommandParser().parse(args));
        commandFactoryMap.put(ExportCommand.COMMAND_WORD, (args) -> new ExportCommandParser().parse(args));
        commandFactoryMap.put(PinCommand.COMMAND_WORD, (args) -> new PinCommandParser().parse(args));
        commandFactoryMap.put(UnpinCommand.COMMAND_WORD, (args) -> new UnpinCommandParser().parse(args));
        commandFactoryMap.put(TagCommand.COMMAND_WORD, (args) -> new TagCommandParser().parse(args));

        logger.info("Command registry initialized with " + commands.size() + " commands");
    }

    /**
     * Registers a command with its help information in the registry.
     * If a command with the same command word already exists, it will be replaced.
     *
     * @param commandWord The command word used to invoke the command
     * @param description A brief description of what the command does
     * @param example     An example showing how to use the command, or an empty string if no example is needed
     */
    public static void register(String commandWord, String description, String example) {
        commands.put(commandWord, new CommandInfo(commandWord, description, example));
    }

    /**
     * Registers a command with its help information including detailed usage in the registry.
     * If a command with the same command word already exists, it will be replaced.
     *
     * @param commandWord   The command word used to invoke the command
     * @param description   A brief description of what the command does
     * @param example       An example showing how to use the command, or an empty string if no example is needed
     * @param detailedUsage Detailed usage information including all parameters and flags
     */
    public static void register(String commandWord, String description, String example, String detailedUsage) {
        commands.put(commandWord, new CommandInfo(commandWord, description, example, detailedUsage));
    }

    /**
     * Returns the help text for all registered commands.
     * Commands are sorted alphabetically by their command word.
     * Each command's help information is formatted consistently with proper spacing and indentation.
     *
     * @return A formatted {@code String} containing help information for all commands
     */
    public static String getAllCommandsHelp() {
        return "Available commands:\n\n" + commands.values().stream()
                .sorted((a, b) -> a.getCommandWord().compareTo(b.getCommandWord()))
                .map(CommandRegistry::formatCommandHelp)
                .collect(Collectors.joining("\n"));
    }

    /**
     * Returns the help text for a specific command.
     * If detailed usage is available, returns the detailed information.
     * If the command is not found in the registry, returns an error message.
     *
     * @param commandWord The command word to get help information for
     * @return A formatted {@code String} containing help information for the specified command,
     *      or an error message if the command is not registered
     */
    public static String getCommandHelp(String commandWord) {
        CommandInfo info = commands.get(commandWord);
        if (info == null) {
            return "Unknown command: " + commandWord
                    + "\n\nUse 'help' to see all available commands.";
        }
        return formatDetailedCommandHelp(info);
    }

    /**
     * Checks if a command is registered in the registry.
     *
     * @param commandWord The command word to check
     * @return {@code true} if the command is registered, {@code false} otherwise
     */
    public static boolean hasCommand(String commandWord) {
        return commands.containsKey(commandWord);
    }

    /**
     * Returns a set of all valid command words in the application.
     */
    public static Set<String> getCommandWords() {
        return commands.keySet();
    }

    /**
     * Formats the help information for a single command in the list view.
     * The format includes the command word aligned to 12 characters, followed by the description.
     * If an example is provided, it is displayed on a new line with proper indentation.
     *
     * @param info The {@code CommandInfo} containing the command's help information
     * @return A formatted {@code String} representing the command's help text
     */
    private static String formatCommandHelp(CommandInfo info) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-12s", info.getCommandWord()))
                .append(info.getDescription());
        if (info.getExample() != null && !info.getExample().isEmpty()) {
            sb.append("\n            ").append(info.getExample());
        }
        return sb.toString();
    }

    /**
     * Formats the detailed help information for a single command.
     * If detailed usage is available, returns the detailed format.
     * Otherwise, returns the standard format.
     *
     * @param info The {@code CommandInfo} containing the command's help information
     * @return A formatted {@code String} representing the command's detailed help text
     */
    private static String formatDetailedCommandHelp(CommandInfo info) {
        StringBuilder sb = new StringBuilder();

        sb.append("Command: ").append(info.getCommandWord()).append("\n\n");
        sb.append("Description: ").append(info.getDescription()).append("\n\n");

        if (info.hasDetailedUsage()) {
            sb.append(info.getDetailedUsage()).append("\n\n");
        }

        if (info.getExample() != null && !info.getExample().isEmpty()) {
            sb.append(info.getExample());
        }

        return sb.toString();
    }

    /**
     * Clears all registered commands from the registry.
     * This method is primarily useful for testing purposes to ensure a clean state between tests.
     */
    public static void clear() {
        commands.clear();
    }

    /**
     * Returns the CommandFactory associated with the given command word.
     * Returns null if the command word is not recognized.
     */
    public static CommandFactory getCommandFactory(String commandWord) {
        return commandFactoryMap.get(commandWord);
    }
}
