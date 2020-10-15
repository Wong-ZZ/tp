package seedu.medibook.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.medibook.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.medibook.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.medibook.logic.commands.CommandTestUtil.showPatientAtIndex;
import static seedu.medibook.testutil.TypicalIndexes.INDEX_FIRST_PATIENT;
import static seedu.medibook.testutil.TypicalIndexes.INDEX_SECOND_PATIENT;
import static seedu.medibook.testutil.TypicalPatients.getTypicalMediBook;

import org.junit.jupiter.api.Test;

import seedu.medibook.commons.core.Messages;
import seedu.medibook.commons.core.index.Index;
import seedu.medibook.model.Model;
import seedu.medibook.model.ModelManager;
import seedu.medibook.model.UserPrefs;
import seedu.medibook.model.patient.Patient;

public class AccessCommandTest {

    private Model model = new ModelManager(getTypicalMediBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Patient patientToAccess = model.getFilteredPatientList().get(INDEX_FIRST_PATIENT.getZeroBased());
        AccessCommand accessCommand = new AccessCommand(INDEX_FIRST_PATIENT);

        CommandResult expectedCommandResult =
                new CommandResult(String.format(AccessCommand.MESSAGE_ACCESS_PATIENT_SUCCESS, patientToAccess),
                        false, false, true);

        ModelManager expectedModel = new ModelManager(model.getMediBook(), new UserPrefs());
        expectedModel.accessPatient(patientToAccess);

        assertCommandSuccess(accessCommand, model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPatientList().size() + 1);
        AccessCommand accessCommand = new AccessCommand(outOfBoundIndex);

        assertCommandFailure(accessCommand, model, Messages.MESSAGE_INVALID_PATIENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPatientAtIndex(model, INDEX_FIRST_PATIENT);

        Patient patientToAccess = model.getFilteredPatientList().get(INDEX_FIRST_PATIENT.getZeroBased());
        AccessCommand accessCommand = new AccessCommand(INDEX_FIRST_PATIENT);

        CommandResult expectedCommandResult =
                new CommandResult(String.format(AccessCommand.MESSAGE_ACCESS_PATIENT_SUCCESS, patientToAccess),
                        false, false, true);

        Model expectedModel = new ModelManager(model.getMediBook(), new UserPrefs());
        expectedModel.deletePatient(patientToAccess);
        showNoPatient(expectedModel);

        assertCommandSuccess(accessCommand, model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPatientAtIndex(model, INDEX_FIRST_PATIENT);

        Index outOfBoundIndex = INDEX_SECOND_PATIENT;
        // ensures that outOfBoundIndex is still in bounds of medi book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getMediBook().getPatientList().size());

        AccessCommand accessCommand = new AccessCommand(outOfBoundIndex);

        assertCommandFailure(accessCommand, model, Messages.MESSAGE_INVALID_PATIENT_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        AccessCommand accessFirstCommand = new AccessCommand(INDEX_FIRST_PATIENT);
        AccessCommand accessSecondCommand = new AccessCommand(INDEX_SECOND_PATIENT);

        // same object -> returns true
        assertTrue(accessFirstCommand.equals(accessFirstCommand));

        // same values -> returns true
        AccessCommand accessFirstCommandCopy = new AccessCommand(INDEX_FIRST_PATIENT);
        assertTrue(accessFirstCommand.equals(accessFirstCommandCopy));

        // different types -> returns false
        assertFalse(accessFirstCommand.equals(1));

        // null -> returns false
        assertFalse(accessFirstCommand.equals(null));

        // different patient -> returns false
        assertFalse(accessFirstCommand.equals(accessSecondCommand));
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoPatient(Model model) {
        model.updateFilteredPatientList(p -> false);

        assertTrue(model.getFilteredPatientList().isEmpty());
    }
}
