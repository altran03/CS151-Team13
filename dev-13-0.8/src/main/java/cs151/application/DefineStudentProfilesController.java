package cs151.application;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DefineStudentProfilesController {

    @FXML
    private TextField fullNameField;

    @FXML
    private ComboBox<String> academicStatusCombo;

    @FXML
    private RadioButton employedRadio;

    @FXML
    private RadioButton notEmployedRadio;

    @FXML
    private ToggleGroup jobStatusGroup;

    @FXML
    private TextField jobDetailsField;

    @FXML
    private VBox programmingLanguagesContainer;

    @FXML
    private VBox databasesContainer;

    @FXML
    private ComboBox<String> professionalRoleCombo;

    @FXML
    private TextArea commentsField;

    @FXML
    private CheckBox whitelistCheckbox;

    @FXML
    private CheckBox blacklistCheckbox;

    @FXML
    private Button saveProfileBtn;

    @FXML
    private Button backToHomeBtn;

    @FXML
    private Label statusLabel;

    @FXML
    private Button viewAllBtn;


    private static final String STUDENT_PROFILES_CSV = "student_profiles.csv";
    private static final String PROGRAMMING_LANGUAGES_CSV = "programming_languages.csv";
    
    // Flag to prevent listeners from interfering during data load
    private boolean isLoadingData = false;

    @FXML
    public void initialize() {
        setupAcademicStatusCombo();
        setupProfessionalRoleCombo();
        setupJobStatusToggleGroup();
        loadProgrammingLanguages();
        setupDatabasesList();
        setupFutureServicesFlags();
        setupValidation();
    }

    @FXML
    protected void onViewAllClick() {
        try {
            var url = Main.class.getResource("view_all_students.fxml");
            System.out.println("view_all_students.fxml URL = " + url);
            if (url == null) {
                statusLabel.setTextFill(Color.RED);
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Scene scene = new Scene(loader.load(), 1200, 600);
            Stage stage = (Stage) backToHomeBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("All Student Profiles");
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText(e.getClass().getSimpleName() + " - " + e.getMessage());
            statusLabel.setTextFill(Color.RED);
        }
    }


    // Initialize academic status combo box
    private void setupAcademicStatusCombo() {
        ArrayList<String> academicStatuses = new ArrayList<>(Arrays.asList(
                "Freshman", "Sophomore", "Junior", "Senior", "Graduate"
        ));
        academicStatusCombo.setItems(FXCollections.observableArrayList(academicStatuses));
    }

    // Initialize professional role combo box
    private void setupProfessionalRoleCombo() {
        ArrayList<String> roles = new ArrayList<>(Arrays.asList(
                "Front-End", "Back-End", "Full-Stack", "Data", "Other"
        ));
        professionalRoleCombo.setItems(FXCollections.observableArrayList(roles));
    }

    /*
     Instantiate new toggle group and initialize job status radio buttons
     */
    private void setupJobStatusToggleGroup() {
        jobStatusGroup = new ToggleGroup();
        employedRadio.setToggleGroup(jobStatusGroup);
        notEmployedRadio.setToggleGroup(jobStatusGroup);
        notEmployedRadio.setSelected(true);

        // Enable/disable job details based on selection
        employedRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            jobDetailsField.setDisable(!newVal);
            // Don't clear fields during data loading
            if (isLoadingData) {
                return;
            }
            // Only clear when switching FROM employed TO not employed
            if (!newVal && Boolean.TRUE.equals(oldVal)) {
                jobDetailsField.clear();
            }
        });
        
        notEmployedRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                jobDetailsField.setDisable(true);
                // Don't clear fields during data loading
                if (isLoadingData) {
                    return;
                }
                // Only clear when switching TO not employed FROM employed
                if (Boolean.TRUE.equals(oldVal)) {
                    jobDetailsField.clear();
                }
            }
        });
    }

    // Loads programming languages from a CSV file
    private void loadProgrammingLanguages() {
        try {
            File file = new File(PROGRAMMING_LANGUAGES_CSV);
            if (!file.exists()) {
                statusLabel.setText("Warning: Programming languages file not found");
                statusLabel.setTextFill(Color.ORANGE);
                return;
            }

            ArrayList<String> languages = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                boolean firstLine = true;
                while ((line = reader.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false; // Skip header
                        continue;
                    }
                    if (!line.trim().isEmpty()) {
                        languages.add(line.trim());
                    }
                }
            }

            // Create checkboxes for each programming language
            programmingLanguagesContainer.getChildren().clear();
            for (String language : languages) {
                CheckBox checkbox = new CheckBox(language);
                checkbox.setUserData(language);
                programmingLanguagesContainer.getChildren().add(checkbox);
            }

        } catch (IOException e) {
            statusLabel.setText("Error loading programming languages");
            statusLabel.setTextFill(Color.RED);
        }
    }

    // Initialize list of databases (hard coded) and adds checkbox for database(s)
    private void setupDatabasesList() {
        // Create checkboxes for each database
        databasesContainer.getChildren().clear();
        ArrayList<String> databases = new ArrayList<>(Arrays.asList(
                "MySQL", "Postgres", "MongoDB"
        ));

        for (String database : databases) {
            CheckBox checkbox = new CheckBox(database);
            checkbox.setUserData(database);
            databasesContainer.getChildren().add(checkbox);
        }
    }

    // Setup mutual exclusivity for whitelist and blacklist checkboxes
    private void setupFutureServicesFlags() {
        // Ensure mutual exclusivity between whitelist and blacklist
        whitelistCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal && blacklistCheckbox.isSelected()) {
                blacklistCheckbox.setSelected(false);
            }
        });

        blacklistCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal && whitelistCheckbox.isSelected()) {
                whitelistCheckbox.setSelected(false);
            }
        });
    }

    private void setupValidation() {
        // Real-time validation could be added here if needed
    }

    // Read validated form, save, and clear
    @FXML
    protected void onSaveProfileClick() {
        if (!validateForm()) {
            return;
        }

        try {
            // check if in edit mode
            String orignalName = (String) fullNameField.getUserData();
            if (orignalName != null) {
                updateStudentProfile(orignalName);
                statusLabel.setText("Student profile updated successfully");
            } else {
                saveStudentProfile();
                statusLabel.setText("Student profile saved successfully!");
            }

            statusLabel.setTextFill(Color.GREEN);
            clearForm();
        } catch (IOException e) {
            statusLabel.setText("Error saving student profile");
            statusLabel.setTextFill(Color.RED);
        }
    }

    /**
     * Check if full name, academic status, employment status,
     * language, database, and professional role status is valid
     *
     * @return {@code false} if errors > 0, {@code true} if required fields is valid
     */
    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        // Validate required fields
        if (fullNameField.getText().trim().isEmpty()) {
            errors.append("Please enter the student's full name\n");
        }

        if (academicStatusCombo.getValue() == null) {
            errors.append("Please select an academic status\n");
        }

        if (employedRadio.isSelected() && jobDetailsField.getText().trim().isEmpty()) {
            errors.append("Please enter job details for employed students\n");
        }

        ArrayList<String> selectedLanguages = getSelectedProgrammingLanguages();
        if (selectedLanguages.isEmpty()) {
            errors.append("Please select at least one programming language\n");
        }

        ArrayList<String> selectedDatabases = getSelectedDatabases();
        if (selectedDatabases.isEmpty()) {
            errors.append("Please select at least one database\n");
        }

        if (professionalRoleCombo.getValue() == null) {
            errors.append("Please select a preferred professional role\n");
        }

        // Check for duplicate student based on trimmed full name
        // exclude current student if in edit mode
        String trimmedName = fullNameField.getText().trim();
        String orignalName = (String) fullNameField.getUserData(); // null for new students

        if (!trimmedName.isEmpty() && isDuplicateStudent(trimmedName, orignalName)) {
            errors.append("A student with this name already exists\n");
        }

        if (errors.length() > 0) {
            statusLabel.setText(errors.toString());
            statusLabel.setTextFill(Color.RED);
            return false;
        }

        return true;
    }

    /**
     * Check if a student with the given trimmed name already exists
     *
     * @param trimmedName the trimmed full name to check
     * @return true if duplicate exists, false otherwise
     */
    private boolean isDuplicateStudent(String trimmedName, String orignalName) {
        try {
            File file = new File(STUDENT_PROFILES_CSV);
            if (!file.exists()) {
                return false; // No file means no duplicates
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                boolean firstLine = true;
                while ((line = reader.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false; // Skip header
                        continue;
                    }
                    if (!line.trim().isEmpty()) {
                        String[] parts = line.split(",", -1);
                        if (parts.length > 0) {
                            String existingName = parts[0].trim();

                            // if in editing mode, skip the student
                            if (orignalName != null && existingName.equals(orignalName)) {
                                continue;
                            }
                            if (existingName.equalsIgnoreCase(trimmedName)) {
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            // If there's an error reading the file, allow the save to proceed
            // The error will be caught later in the save process
        }
        return false;
    }

    private void saveStudentProfile() throws IOException {
        // Create CSV file if it doesn't exist
        File file = new File(STUDENT_PROFILES_CSV);
        boolean fileExists = file.exists();

        try (FileWriter writer = new FileWriter(file, true)) {
            // Write header if file is new
            if (!fileExists) {
                writer.append("Full Name,Academic Status,Job Status,Job Details,Programming Languages,Databases,Professional Role,Comments,Whitelist,Blacklist\n");
            }

            // Prepare data
            String fullName = fullNameField.getText().trim().replace(",", ";");
            String academicStatus = academicStatusCombo.getValue();
            String jobStatus = employedRadio.isSelected() ? "Employed" : "Not Employed";
            String jobDetails = jobDetailsField.getText().trim().replace(",", ";");
            ArrayList<String> selectedLanguages = getSelectedProgrammingLanguages();
            ArrayList<String> selectedDatabases = getSelectedDatabases();
            String programmingLanguages = String.join(";", selectedLanguages);
            String databases = String.join(";", selectedDatabases);
            String professionalRole = professionalRoleCombo.getValue();
            String comments = commentsField.getText().trim().replace(",", ";").replace("\n", " ");
            String whitelist = whitelistCheckbox.isSelected() ? "Yes" : "No";
            String blacklist = blacklistCheckbox.isSelected() ? "Yes" : "No";

            // Write student profile data
            writer.append(fullName).append(",")
                    .append(academicStatus).append(",")
                    .append(jobStatus).append(",")
                    .append(jobDetails).append(",")
                    .append(programmingLanguages).append(",")
                    .append(databases).append(",")
                    .append(professionalRole).append(",")
                    .append(comments).append(",")
                    .append(whitelist).append(",")
                    .append(blacklist).append("\n");
        }
    }

    // Update an existing student profile in the CSV file
    private void updateStudentProfile(String originalName) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(STUDENT_PROFILES_CSV));
        List<String> updatedLines = new ArrayList<>();

        boolean headerProcessed = false;
        for (String line : lines) {
            if (!headerProcessed) {
                updatedLines.add(line);
                headerProcessed = true;
                continue;
            }

            if (line.trim().isEmpty()) {
                continue;
            }

            String[] parts = line.split(",", -1);
            if (parts.length > 0) {
                String currentName = parts[0].trim();

                if (currentName.equals(originalName)) {
                    // replace this line with updated data
                    String updatedLine = createStudentCSVLine();
                    updatedLines.add(updatedLine);
                } else {
                    // keep the original line
                    updatedLines.add(line);
                }
            }
        }

        // write all lines back to file
        Files.write(Paths.get(STUDENT_PROFILES_CSV), updatedLines);

    }

    // Set all fields to default values
    private void clearForm() {
        fullNameField.clear();
        academicStatusCombo.setValue(null);
        notEmployedRadio.setSelected(true);
        jobDetailsField.clear();
        clearProgrammingLanguagesSelection();
        clearDatabasesSelection();
        professionalRoleCombo.setValue(null);
        commentsField.clear();
        whitelistCheckbox.setSelected(false);
        blacklistCheckbox.setSelected(false);
        fullNameField.setUserData(null);
        saveProfileBtn.setText("Save Student Profile");

    }

    // prefill the form with existing student data for editing
    public void loadStudentDataForEditing(Map<String, String> student) {
        isLoadingData = true; // Prevent listeners from interfering
        
        fullNameField.setText(student.getOrDefault("Full Name", "").replace(";", ","));
        academicStatusCombo.setValue(student.getOrDefault("Academic Status", ""));

        // job status
        String jobStatus = student.getOrDefault("Job Status", "");
        String jobDetails = student.getOrDefault("Job Details", "");
        
        // Extract job details
        if (jobStatus.contains("(") && jobStatus.contains(")")) {
            int startIdx = jobStatus.indexOf("(") + 1;
            int endIdx = jobStatus.indexOf(")");
            if (startIdx < endIdx && jobDetails.isEmpty()) {
                jobDetails = jobStatus.substring(startIdx, endIdx);
            }
            // Extract base job status
            jobStatus = jobStatus.substring(0, jobStatus.indexOf("(")).trim();
        }
        
        // Set radio button 
        String jobStatusLower = jobStatus.toLowerCase();
        if (jobStatusLower.startsWith("employed")) {
            employedRadio.setSelected(true);
            jobDetailsField.setDisable(false);
            if (!jobDetails.isEmpty()) {
                jobDetailsField.setText(jobDetails.replace(";", ","));
            } else {
                jobDetailsField.setText("");
            }
        } else {
            // For "Not Employed", set radio button and clear/disable job details
            notEmployedRadio.setSelected(true);
            jobDetailsField.setDisable(true);
            jobDetailsField.setText("");
        }

        // programming languages
        clearProgrammingLanguagesSelection();
        String languages = student.getOrDefault("Programming Languages", "");
        for (String lang : languages.split(";")) {
            String trimmedLang = lang.trim();
            if (!trimmedLang.isEmpty()) {
                for (var node : programmingLanguagesContainer.getChildren()) {
                    if (node instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) node;
                        if (checkBox.getText().equalsIgnoreCase(trimmedLang)) {
                            checkBox.setSelected(true);
                            break;
                        }
                    }
                }
            }
        }

        // Database
        clearDatabasesSelection();
        String databases = student.getOrDefault("Databases", "");
        for (String db : databases.split(";")) {
            String trimmedDb = db.trim();
            if (!trimmedDb.isEmpty()) {
                for (var node : databasesContainer.getChildren()) {
                    if (node instanceof CheckBox) {
                        CheckBox checkbox = (CheckBox) node;
                        if (checkbox.getText().equalsIgnoreCase(trimmedDb)) {
                            checkbox.setSelected(true);
                            break;
                        }
                    }
                }
            }
        }

        // Professional role
        professionalRoleCombo.setValue(student.getOrDefault("Professional Role", ""));

        // Comments
        commentsField.setText(student.getOrDefault("Comments", "").replace(";", ","));

        // Whitelist and Blacklist
        String whitelist = student.getOrDefault("Whitelist", "No");
        String blacklist = student.getOrDefault("Blacklist", "No");
        whitelistCheckbox.setSelected("Yes".equalsIgnoreCase(whitelist));
        blacklistCheckbox.setSelected("Yes".equalsIgnoreCase(blacklist));

        // store the orignal name for update
        fullNameField.setUserData(student.get("Full Name"));

        // change the save btn to editing mode
        saveProfileBtn.setText("Update Profile");
        statusLabel.setText("Editing student: " + student.get("Full Name"));
        statusLabel.setTextFill(Color.BLUE);
        
        isLoadingData = false; // Re-enable listeners after data is loaded
    }



    @FXML
    protected void onBackToHomeClick() {
        try {
            // Load the Home page
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("home.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 500);

            // Get the current stage
            Stage currentStage = (Stage) backToHomeBtn.getScene().getWindow();

            // Set the new scene
            currentStage.setScene(scene);
            currentStage.setTitle("Student Tracker - Home");

        } catch (IOException e) {
            statusLabel.setText("Error loading home page");
            statusLabel.setTextFill(Color.RED);
        }
    }

    /**
     * Add selected languages in selected list
     * @return list of selected language(s)
     */
    private ArrayList<String> getSelectedProgrammingLanguages() {
        ArrayList<String> selected = new ArrayList<>();
        for (var node : programmingLanguagesContainer.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox checkbox = (CheckBox) node;
                if (checkbox.isSelected()) {
                    selected.add(checkbox.getText());
                }
            }
        }
        return selected;
    }

    /**
     * Add selected database in selected list
     * @return list of selected database(s)
     */
    private ArrayList<String> getSelectedDatabases() {
        ArrayList<String> selected = new ArrayList<>();
        for (var node : databasesContainer.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox checkbox = (CheckBox) node;
                if (checkbox.isSelected()) {
                    selected.add(checkbox.getText());
                }
            }
        }
        return selected;
    }

    // create CSV line from the current form data
    private String createStudentCSVLine() {
        String fullName = fullNameField.getText().trim().replace(",", ";");
        String academicStatus = academicStatusCombo.getValue();
        String jobStatus = employedRadio.isSelected() ? "Employed" : "Not Employed";
        String jobDetails = jobDetailsField.getText().trim().replace(",", ";");
        ArrayList<String> selectedLanguages = getSelectedProgrammingLanguages();
        ArrayList<String> selectedDatabases = getSelectedDatabases();
        String programmingLanguages = String.join(";", selectedLanguages);
        String databases = String.join(";", selectedDatabases);
        String professionalRole = professionalRoleCombo.getValue();
        String comments = commentsField.getText().trim().replace(",", ";").replace("\n", " ");
        String whitelist = whitelistCheckbox.isSelected() ? "Yes" : "No";
        String blacklist = blacklistCheckbox.isSelected() ? "Yes" : "No";

        return String.join(",", fullName, academicStatus, jobStatus, jobDetails,
                programmingLanguages, databases, professionalRole, comments, whitelist, blacklist);
    }

    // set programming language(s) to default values
    private void clearProgrammingLanguagesSelection() {
        for (var node : programmingLanguagesContainer.getChildren()) {
            if (node instanceof CheckBox) {
                ((CheckBox) node).setSelected(false);
            }
        }
    }

    // set database(s) to default values
    private void clearDatabasesSelection() {
        for (var node : databasesContainer.getChildren()) {
            if (node instanceof CheckBox) {
                ((CheckBox) node).setSelected(false);
            }
        }
    }
}

