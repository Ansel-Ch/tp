package seedu.address.logic.commands;

import seedu.address.model.Model;

/**
 * A command used to undo previous undoable commands.
 */
public class UndoCommand extends Command {
    public static final String COMMAND_WORD = "undo";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Undoes the previous undoable command.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Undo successful";
    public static final String MESSAGE_FAILURE = "The previous command cannot be undone";

    @Override
    public CommandResult execute(Model model) {
        Undoable command = CommandStack.popCommand();
        if (command == null) {
            return new CommandResult("There are no commands to undo");
        }

        return command.undo(model)
                ? new CommandResult(String.format(MESSAGE_SUCCESS))
                : new CommandResult(MESSAGE_FAILURE);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof UndoCommand;
    }
}
