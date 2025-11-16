package cs151.application;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
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

    private int screenWidth;
    private int screenHeight;

    public void setScreenSize(int gdWidth, int gdHeight) {
        screenWidth = gdWidth;
        screenHeight = gdHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }


    @FXML
    protected void onManageLanguagesClick() {
        try {
            // Load the Define Programming Languages page
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("define_languages.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            DefineLanguagesController defineLanguagesController =
                    (DefineLanguagesController) fxmlLoader.getController();
            Scene scene = new Scene(root, screenWidth / 2, screenHeight);
            setScreenSize(screenWidth, screenHeight);

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
            Parent root = (Parent) fxmlLoader.load();
            DefineStudentProfilesController defineStudentProfilesController =
                    (DefineStudentProfilesController) fxmlLoader.getController();
            defineStudentProfilesController.setScreenSize(screenWidth, screenHeight);
            Scene scene = new Scene(root, (screenWidth*0.65), screenHeight);

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
        try {
            // Load the View All Students page
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view_all_students.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            ViewAllStudentsController viewAllStudentsController =
                    (ViewAllStudentsController) fxmlLoader.getController();
            viewAllStudentsController.setScreenSize(screenWidth, screenHeight);
            Scene scene = new Scene(root, screenWidth, screenHeight);

            // Get the current stage
            Stage currentStage = (Stage) manageStudentBtn.getScene().getWindow();

            // Set the new scene
            currentStage.setScene(scene);
            currentStage.setTitle("Student Tracker - Search Students");

        } catch (IOException e) {
            System.out.println("Error loading Search & Manage Students page");
        }
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