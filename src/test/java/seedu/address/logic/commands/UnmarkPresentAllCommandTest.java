package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.attendance.AttendanceRecord;
import seedu.address.model.student.Student;
import seedu.address.model.student.TutorialGroup;
import seedu.address.testutil.StudentBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code UnmarkPresentAllCommand}.
 */
public class UnmarkPresentAllCommandTest {

    private final TutorialGroup validTutorialGroup = new TutorialGroup("A01");
    private final TutorialGroup emptyTutorialGroup = new TutorialGroup("B01");

    private final LocalDate validDate = LocalDate.of(2024, 10, 23);

    private final Student student1 = new StudentBuilder().withName("John Doe")
            .withStudentNumber("A1234567M")
            .withTutorialGroup("A01").build();
    private final Student student2 = new StudentBuilder().withName("Jane Doe")
            .withStudentNumber("A7654321M")
            .withTutorialGroup("A01").build();

    @Test
    public void equals() {
        TutorialGroup tutorialGroup1 = new TutorialGroup("A01");
        TutorialGroup tutorialGroup2 = new TutorialGroup("A02");

        LocalDate date1 = LocalDate.of(2024, 10, 23);
        LocalDate date2 = LocalDate.of(2024, 10, 24);

        UnmarkPresentAllCommand standardUnmarkPresentAllCommand = new UnmarkPresentAllCommand(tutorialGroup1, date1);

        // same object -> returns true
        assertTrue(standardUnmarkPresentAllCommand.equals(standardUnmarkPresentAllCommand));

        // same values -> returns true
        UnmarkPresentAllCommand standardUnmarkPresentAllCommandCopy = new UnmarkPresentAllCommand(
                tutorialGroup1, date1);
        assertTrue(standardUnmarkPresentAllCommand.equals(standardUnmarkPresentAllCommandCopy));

        // different types -> returns false
        assertFalse(standardUnmarkPresentAllCommand.equals(1));

        // null -> returns false
        assertFalse(standardUnmarkPresentAllCommand.equals(null));

        // different tutorial group -> returns false
        assertFalse(standardUnmarkPresentAllCommand.equals(new UnmarkPresentAllCommand(tutorialGroup2, date1)));

        // different date -> returns false
        assertFalse(standardUnmarkPresentAllCommand.equals(new UnmarkPresentAllCommand(tutorialGroup1, date2)));
    }

    @Test
    void execute_validTutorialGroup_success() {
        Model model = new ModelManager();
        model.addStudent(student1);
        model.addStudent(student2);
        UnmarkPresentAllCommand command = new UnmarkPresentAllCommand(validTutorialGroup, validDate);

        String expectedMessage = String.format(UnmarkPresentAllCommand.MESSAGE_SUCCESS, validTutorialGroup,
                DateTimeFormatter.ofPattern("MMM d yyyy").format(validDate));
        Student unmarkedStudent1 = new StudentBuilder().withName("John Doe")
                .withStudentNumber("A1234567M")
                .withTutorialGroup("A01").build();
        unmarkedStudent1.markAttendance(validDate, "a");
        Student unmarkedStudent2 = new StudentBuilder().withName("Jane Doe")
                .withStudentNumber("A7654321M")
                .withTutorialGroup("A01").build();
        unmarkedStudent2.markAttendance(validDate, "a");
        Model expectedModel = new ModelManager();
        expectedModel.addStudent(unmarkedStudent1);
        expectedModel.addStudent(unmarkedStudent2);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    void execute_emptyTutorialGroup_throwsCommandException() {
        Model model = new ModelManager();
        model.addStudent(student1);
        model.addStudent(student2);
        UnmarkPresentAllCommand command = new UnmarkPresentAllCommand(emptyTutorialGroup, validDate);

        String expectedMessage = UnmarkPresentAllCommand.MESSAGE_EMPTY_TG;

        assertCommandFailure(command, model, expectedMessage);
    }

    @Test
    public void toStringMethod() {
        TutorialGroup tutorialGroup = new TutorialGroup("A01");
        LocalDate date = LocalDate.of(2024, 10, 23);
        UnmarkPresentAllCommand unmarkPresentAllCommand = new UnmarkPresentAllCommand(tutorialGroup, date);
        String expectedString = UnmarkPresentAllCommand.class.getCanonicalName() + "{tutorialGroup=" + tutorialGroup
                + ", date=" + date + "}";
        assertEquals(expectedString, unmarkPresentAllCommand.toString());
    }

    @Test
    public void execute_undoUnmarkPresentAllCommand_success() throws Exception {
        Model model = new ModelManager();
        model.addStudent(student1);
        model.addStudent(student2);
        UnmarkPresentAllCommand markCommand = new UnmarkPresentAllCommand(validTutorialGroup, validDate);
        markCommand.execute(model);

        CommandStack commandStack = CommandStack.getInstance();
        commandStack.push(markCommand);

        UndoCommand undoCommand = new UndoCommand();
        CommandResult result = undoCommand.execute(model);

        assertEquals(UndoCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertTrue(model.getStudentByName(student1.getName()).getAttendanceRecord().isEmpty());
        assertTrue(model.getStudentByName(student2.getName()).getAttendanceRecord().isEmpty());
    }

    @Test
    public void execute_undoUnmarkPresentAllCommandTwoAttendance_success() throws Exception {
        Model model = new ModelManager();
        model.addStudent(student1);
        model.addStudent(student2);
        MarkPresentAllCommand markCommand = new MarkPresentAllCommand(validTutorialGroup, validDate);
        UnmarkPresentAllCommand unmarkCommand = new UnmarkPresentAllCommand(validTutorialGroup, validDate);
        Attendance attendance = new Attendance("p");
        markCommand.execute(model);
        unmarkCommand.execute(model);
        unmarkCommand.undo(model);

        AttendanceRecord ar = student1.getAttendanceRecord().get(0);
        assertTrue(ar.getAttendance().equals(attendance));
        AttendanceRecord ar2 = student2.getAttendanceRecord().get(0);
        assertTrue(ar2.getAttendance().equals(attendance));
    }


    @Test
    public void execute_noCommandToUndo_throwsCommandException() {
        Model model = new ModelManager();
        UndoCommand undoCommand = new UndoCommand();
        CommandResult result = undoCommand.execute(model);

        assertEquals("There are no commands to undo", result.getFeedbackToUser());
    }
}



