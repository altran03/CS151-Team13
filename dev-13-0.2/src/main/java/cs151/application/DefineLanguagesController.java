package cs151.application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private ListView<String> languagesListView;

    // Temporary storage for languages (will be replaced with file persistence later (only allowed flat files or SQLite database))
    private List<String> programmingLanguages = new ArrayList<>();

    private void showStatus(String message, String color) {
        statusMessage.setText(message);
        statusMessage.setStyle("-fx-text-fill: white; -fx-background-color: " + color + "; -fx-padding: 10px; -fx-background-radius: 5px; -fx-alignment: center; -fx-font-weight: bold;");
        statusMessage.setVisible(true);
    }

    private void hideStatus() {
        statusMessage.setVisible(false);
    }



    @FXML
    public void initialize() {
        // Initialize the list view
        updateLanguagesList();

        // Hide status message on startup
        hideStatus();

        // Update the languages shown on screen
        updateLanguagesList();
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
        updateLanguagesList();

        // Clear text field and show success message
        languageField.clear();
        showStatus("Success: '" + languageName + "' has been added!", "#2ecc71"); // Success in green

        // For now, just print to console (will be replaced with file saving for later use)
        System.out.println("Saved language: " + languageName);
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

    private void updateLanguagesList() {
        languagesListView.getItems().clear();
        languagesListView.getItems().addAll(programmingLanguages);
    }
}