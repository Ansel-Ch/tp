package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.testutil.ModelStub;

public class UndoCommandTest {
    private CommandStack commandStack;
    private Model model;

    @BeforeEach
    public void setUp() {
        commandStack = CommandStack.getInstance();
        model = new ModelStub();
        commandStack.clear();
    }

    @Test
    public void execute_emptyStack() {
        assertEquals("There are no commands to undo",
                new UndoCommand().execute(model).getFeedbackToUser());
    }

    @Test
    public void execute_emptyStack_notUndoableCommand() {
        Command command = new NotUndoableCommandStub();
        CommandStack.pushCommand(command);
        assertEquals("There are no commands to undo",
                new UndoCommand().execute(model).getFeedbackToUser());
    }

    @Test
    public void execute_nonEmptyStack_undoableCommand() {
        Command command = new UndoableCommandStub();
        CommandStack.pushCommand(command);
        assertEquals(UndoCommand.MESSAGE_SUCCESS,
                new UndoCommand().execute(model).getFeedbackToUser());
    }

    @Test
    public void equals() {
        UndoCommand undoCommand = new UndoCommand();

        // same object -> returns true
        assertEquals(undoCommand, undoCommand);

        // different object -> returns false
        assertFalse(undoCommand.equals(1));

        // null -> returns false
        assertFalse(undoCommand.equals(null));
    }

    private class NotUndoableCommandStub extends Command {
        @Override
        public CommandResult execute(Model model) {
            return new CommandResult("Not Undoable command executed");
        }
    }

    private class UndoableCommandStub extends Command implements Undoable {
        @Override
        public CommandResult execute(Model model) {
            return new CommandResult("Undoable command executed");
        }

        @Override
        public boolean undo(Model model) {
            return true;
        }
    }
}
