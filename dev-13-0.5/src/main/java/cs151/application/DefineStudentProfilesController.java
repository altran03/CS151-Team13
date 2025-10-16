package cs151.application;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private Button saveProfileBtn;
    
    @FXML
    private Button backToHomeBtn;
    
    @FXML
    private Label statusLabel;
    
    private static final String STUDENT_PROFILES_CSV = "student_profiles.csv";
    private static final String PROGRAMMING_LANGUAGES_CSV = "programming_languages.csv";
    
    @FXML
    public void initialize() {
        setupAcademicStatusCombo();
        setupProfessionalRoleCombo();
        setupJobStatusToggleGroup();
        loadProgrammingLanguages();
        setupDatabasesList();
        setupValidation();
    }
    
    private void setupAcademicStatusCombo() {
        ArrayList<String> academicStatuses = new ArrayList<>(Arrays.asList(
            "Freshman", "Sophomore", "Junior", "Senior", "Graduate"
        ));
        academicStatusCombo.setItems(FXCollections.observableArrayList(academicStatuses));
    }
    
    private void setupProfessionalRoleCombo() {
        ArrayList<String> roles = new ArrayList<>(Arrays.asList(
            "Front-End", "Back-End", "Full-Stack", "Data", "Other"
        ));
        professionalRoleCombo.setItems(FXCollections.observableArrayList(roles));
    }
    
    private void setupJobStatusToggleGroup() {
        jobStatusGroup = new ToggleGroup();
        employedRadio.setToggleGroup(jobStatusGroup);
        notEmployedRadio.setToggleGroup(jobStatusGroup);
        notEmployedRadio.setSelected(true);
        
        // Enable/disable job details based on selection
        employedRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            jobDetailsField.setDisable(!newVal);
            if (!newVal) {
                jobDetailsField.clear();
            }
        });
    }
    
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
    
    private void setupValidation() {
        // Real-time validation could be added here if needed
    }
    
    @FXML
    protected void onSaveProfileClick() {
        if (!validateForm()) {
            return;
        }
        
        try {
            saveStudentProfile();
            statusLabel.setText("Student profile saved successfully!");
            statusLabel.setTextFill(Color.GREEN);
            clearForm();
        } catch (IOException e) {
            statusLabel.setText("Error saving student profile");
            statusLabel.setTextFill(Color.RED);
        }
    }
    
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
        
        if (errors.length() > 0) {
            statusLabel.setText(errors.toString());
            statusLabel.setTextFill(Color.RED);
            return false;
        }
        
        return true;
    }
    
    private void saveStudentProfile() throws IOException {
        // Create CSV file if it doesn't exist
        File file = new File(STUDENT_PROFILES_CSV);
        boolean fileExists = file.exists();
        
        try (FileWriter writer = new FileWriter(file, true)) {
            // Write header if file is new
            if (!fileExists) {
                writer.append("Full Name,Academic Status,Job Status,Job Details,Programming Languages,Databases,Professional Role,Comments\n");
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
            
            // Write student profile data
            writer.append(fullName).append(",")
                  .append(academicStatus).append(",")
                  .append(jobStatus).append(",")
                  .append(jobDetails).append(",")
                  .append(programmingLanguages).append(",")
                  .append(databases).append(",")
                  .append(professionalRole).append(",")
                  .append(comments).append("\n");
        }
    }
    
    private void clearForm() {
        fullNameField.clear();
        academicStatusCombo.setValue(null);
        notEmployedRadio.setSelected(true);
        jobDetailsField.clear();
        clearProgrammingLanguagesSelection();
        clearDatabasesSelection();
        professionalRoleCombo.setValue(null);
        commentsField.clear();
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
    
    private void clearProgrammingLanguagesSelection() {
        for (var node : programmingLanguagesContainer.getChildren()) {
            if (node instanceof CheckBox) {
                ((CheckBox) node).setSelected(false);
            }
        }
    }
    
    private void clearDatabasesSelection() {
        for (var node : databasesContainer.getChildren()) {
            if (node instanceof CheckBox) {
                ((CheckBox) node).setSelected(false);
            }
        }
    }
}
