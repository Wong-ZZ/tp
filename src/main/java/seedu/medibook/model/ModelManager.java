package seedu.medibook.model;

import static java.util.Objects.requireNonNull;
import static seedu.medibook.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.medibook.commons.core.GuiSettings;
import seedu.medibook.commons.core.LogsCenter;
import seedu.medibook.model.patient.Patient;

/**
 * Represents the in-memory model of the medi book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final MediBook mediBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Patient> filteredPatients;
    private Optional<Patient> accessedPatient;

    /**
     * Initializes a ModelManager with the given mediBook and userPrefs.
     */
    public ModelManager(ReadOnlyMediBook mediBook, ReadOnlyUserPrefs userPrefs) {
        super();
        requireAllNonNull(mediBook, userPrefs);

        logger.fine("Initializing with MediBook: " + mediBook + " and user prefs " + userPrefs);

        this.mediBook = new MediBook(mediBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPatients = new FilteredList<>(this.mediBook.getPatientList());
    }

    public ModelManager() {
        this(new MediBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getMediBookFilePath() {
        return userPrefs.getMediBookFilePath();
    }

    @Override
    public void setMediBookFilePath(Path mediBookFilePath) {
        requireNonNull(mediBookFilePath);
        userPrefs.setMediBookFilePath(mediBookFilePath);
    }

    //=========== MediBook ================================================================================

    @Override
    public void setMediBook(ReadOnlyMediBook mediBook) {
        this.mediBook.resetData(mediBook);
    }

    @Override
    public ReadOnlyMediBook getMediBook() {
        return mediBook;
    }

    @Override
    public boolean hasPatient(Patient patient) {
        requireNonNull(patient);
        return mediBook.hasPatient(patient);
    }

    @Override
    public void deletePatient(Patient target) {
        mediBook.removePatient(target);
    }

    @Override
    public void addPatient(Patient patient) {
        mediBook.addPatient(patient);
        updateFilteredPatientList(PREDICATE_SHOW_ALL_PATIENTS);
    }

    @Override
    public void accessPatient(Patient patient) {
        this.accessedPatient = Optional.of(patient);
    }

    @Override
    public Patient getPatientToAccess() {
        return this.accessedPatient.orElse(null);
    }

    @Override
    public void setPatient(Patient target, Patient editedPatient) {
        requireAllNonNull(target, editedPatient);

        mediBook.setPatient(target, editedPatient);
    }

    //=========== Filtered Patient List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Patient} backed by the internal list of
     * {@code versionedMediBook}
     */
    @Override
    public ObservableList<Patient> getFilteredPatientList() {
        return filteredPatients;
    }

    @Override
    public void updateFilteredPatientList(Predicate<Patient> predicate) {
        requireNonNull(predicate);
        filteredPatients.setPredicate(predicate);
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return mediBook.equals(other.mediBook)
                && userPrefs.equals(other.userPrefs)
                && filteredPatients.equals(other.filteredPatients)
                && accessedPatient.equals(other.accessedPatient);
    }

}
