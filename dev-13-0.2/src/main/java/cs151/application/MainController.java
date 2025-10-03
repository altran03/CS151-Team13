package cs151.application;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

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
        // Button does nothing for now
    }

    @FXML
    protected void onDefineStudentProfilesClick() {
        // Button does nothing for now
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