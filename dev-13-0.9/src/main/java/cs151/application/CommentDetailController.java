package cs151.application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for displaying full comment details
 */
public class CommentDetailController {

    @FXML private Label dateLabel;
    @FXML private Label studentNameLabel;
    @FXML private TextArea commentTextArea;
    @FXML private Button backBtn;
    @FXML private Label statusLabel;

    private String commentDate;
    private String commentText;
    private String studentName;

    /**
     * Set the comment data to display
     * @param date The date of the comment
     * @param text The full text of the comment
     * @param name The student's name
     */
    public void setCommentData(String date, String text, String name) {
        this.commentDate = date;
        this.commentText = text;
        this.studentName = name;
        displayComment();
    }

    /**
     * Display the comment in the UI
     */
    private void displayComment() {
        studentNameLabel.setText("Comment for: " + studentName);
        dateLabel.setText("Date: " + commentDate);
        commentTextArea.setText(commentText);
        commentTextArea.setWrapText(true);
        commentTextArea.setEditable(false);
    }

    /**
     * Handle back button click - close the detail window
     */
    @FXML
    protected void onBackClick() {
        Stage stage = (Stage) backBtn.getScene().getWindow();
        stage.close();
    }
}

