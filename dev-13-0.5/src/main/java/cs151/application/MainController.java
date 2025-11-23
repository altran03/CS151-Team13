package cs151.application;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainController {
    @FXML
    private Label appTitle;
    
    @FXML
    private Label subtitle;
    
    @FXML
    private Label welcomeText;
    
    @FXML
    private Button manageLanguagesBtn;

    @FXML
    private Button defineStudentProfilesBtn;

    @FXML
    private Button manageStudentBtn;

    @FXML
    private Button generateReportsBtn;
    
    @FXML
    private Button exitBtn;

    @FXML
    protected void onManageLanguagesClick() {
        try {
            // Load the Define Programming Languages page
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("define_languages.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 500, 600);

            // Get the current stage
            Stage currentStage = (Stage) manageLanguagesBtn.getScene().getWindow();

            // Set the new scene
            currentStage.setScene(scene);
            currentStage.setTitle("Student Tracker - Define Programming Languages");

        } catch (IOException e) {
            System.out.println("Error loading Define Programming Languages page");
        }
    }

    @FXML
    protected void onDefineStudentProfilesClick() {
        try {
            // Load the Define Student Profiles page
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("define_student_profiles.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 700, 800);

            // Get the current stage
            Stage currentStage = (Stage) defineStudentProfilesBtn.getScene().getWindow();

            // Set the new scene
            currentStage.setScene(scene);
            currentStage.setTitle("Student Tracker - Define Student Profiles");

        } catch (IOException e) {
            System.out.println("Error loading Define Student Profiles page");
        }
    }

    @FXML
    protected void onManageStudentClick() {
        // Button does nothing for now
    }

    @FXML
    protected void onGenerateReportsClick() {
        // Button does nothing for now
    }
    
    @FXML
    protected void onExitClick() {
        Platform.exit();
    }
}