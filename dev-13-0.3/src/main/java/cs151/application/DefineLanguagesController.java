package cs151.application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class DefineLanguagesController {

    @FXML
    private TextField languageField;

    @FXML
    private Button saveButton;

    @FXML
    private Button backButton;

    @FXML
    private Label statusMessage;

    @FXML
    private Button deleteButton;

    @FXML
    private TableView<String> languagesTableView;

    @FXML
    private TableColumn<String, String> languagesColumn;

    // Storage for languages with CSV file persistence
    private ObservableList<String> programmingLanguages = FXCollections.observableArrayList();
    private static final String CSV_FILE_PATH = "programming_languages.csv";

    private void showStatus(String message, String color) {
        statusMessage.setText(message);
        statusMessage.setStyle("-fx-text-fill: white; -fx-background-color: " + color + "; -fx-padding: 10px; -fx-background-radius: 5px; -fx-alignment: center; -fx-font-weight: bold;");
        statusMessage.setVisible(true);
    }

    private void hideStatus() {
        statusMessage.setVisible(false);
    }

    //Saves the programming languages list to a CSV file
    private void saveLanguagesToCSV() {
        try {
            Path filePath = Paths.get(CSV_FILE_PATH);
            
            // Create the file if it doesn't exist
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(filePath.toFile()))) {
                // Write header
                writer.println("Programming Language");
                
                // Write each language
                for (String language : programmingLanguages) {
                    writer.println(language);
                }
            }
            
            System.out.println("Languages saved to CSV file: " + CSV_FILE_PATH);
            
        } catch (IOException e) {
            System.err.println("Error saving languages to CSV: " + e.getMessage());
        }
    }

   //Loads programming languages from a CSV file
    private void loadLanguagesFromCSV() {
        try {
            Path filePath = Paths.get(CSV_FILE_PATH);
            
            if (!Files.exists(filePath)) {
                System.out.println("CSV file does not exist yet. Starting with empty list.");
                return;
            }

            // Use intermediate list to sort added languages all at once
            List<String> loadedLanguages = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
                String line;
                boolean isFirstLine = true;
                
                while ((line = reader.readLine()) != null) {
                    // Skip header line
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }
                    
                    String language = line.trim();
                    if (!language.isEmpty() && !loadedLanguages.contains(language)) {
                        loadedLanguages.add(language);
                    }
                }
            }

            // Sort the loaded languages alphabetically (case-insensitive)
            Collections.sort(loadedLanguages, String.CASE_INSENSITIVE_ORDER);

            // Add the sorted languages to observable list
            programmingLanguages.setAll(loadedLanguages);
            
            System.out.println("Languages loaded from CSV file: " + CSV_FILE_PATH);
            System.out.println("Loaded languages: " + programmingLanguages);
            
        } catch (IOException e) {
            System.err.println("Error loading languages from CSV: " + e.getMessage());
        }
    }


    @FXML
    public void initialize() {
        // Set TableView
        languagesColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue()));

        // Set case-insensitive comparator for sorting
        languagesColumn.setComparator((String s1, String s2) -> {
            return s1.compareToIgnoreCase(s2);
        });

        // Load existing languages from CSV file
        loadLanguagesFromCSV();

        // Set sorted data to TableView
        languagesTableView.setItems(programmingLanguages);

        // Sort the TableView
        languagesTableView.getSortOrder().add(languagesColumn);
        languagesColumn.setSortable(true);

        // Hide status message on startup
        hideStatus();
    }

    @FXML
    protected void onSaveLanguageClick() {
        String languageName = languageField.getText().trim();

        // Validation
        if (languageName.isEmpty()) {
            showStatus("Error: Please enter a programming language name", "#e74c3c"); // Errors in red
            return;
        }

        if (programmingLanguages.contains(languageName)) {
            showStatus("Error: This language already exists", "#e74c3c"); // Errors in red
            return;
        }

        // Save the language
        programmingLanguages.add(languageName);

        // Sort in alphabetical order (case-insensitive)
        FXCollections.sort(programmingLanguages, String.CASE_INSENSITIVE_ORDER);

        // Save to CSV file for permanent storage
        saveLanguagesToCSV();

        // Clear text field and show success message
        languageField.clear();
        showStatus("Success: '" + languageName + "' has been added and saved!", "#2ecc71"); // Success in green

        System.out.println("Saved language: " + languageName);
        System.out.println("All languages: " + programmingLanguages);
    }

    @FXML
    protected void onDeleteLanguageClick() {
        String selectedLanguage = languagesTableView.getSelectionModel().getSelectedItem();

        // Validation
        if (selectedLanguage == null) {
            showStatus("Error: Please select a language to delete", "#e74c3c"); // Errors in red
            return;
        }

        // Remove the language
        programmingLanguages.remove(selectedLanguage);
        //updateLanguagesList();

        // Save to CSV file to persist changes
        saveLanguagesToCSV();

        // Show success message
        showStatus("Success: '" + selectedLanguage + "' has been deleted!", "#2ecc71"); // Success in green

        System.out.println("Deleted language: " + selectedLanguage);
        System.out.println("All languages: " + programmingLanguages);
    }

    @FXML
    protected void onBackToHomeClick() {
        try {
            // Load the home page
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("home.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 500, 600);

            // Get the current stage
            Stage currentStage = (Stage) backButton.getScene().getWindow();

            // Set the new scene
            currentStage.setScene(scene);
            currentStage.setTitle("Student Knowledgebase - Faculty Management System");

        } catch (IOException e) {
            e.printStackTrace();
            statusMessage.setText("Error: Cannot navigate back to home page");
        }
    }
}