package cs151.application;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

/**
 * Controller for displaying student profile information with comments table
 */
public class StudentProfileViewController {

    // Profile fields
    @FXML private Label fullNameLabel;
    @FXML private Label academicStatusLabel;
    @FXML private Label jobStatusLabel;
    @FXML private Label languagesLabel;
    @FXML private Label databasesLabel;
    @FXML private Label roleLabel;
    @FXML private Label whitelistLabel;
    @FXML private Label blacklistLabel;

    // Comments table
    @FXML private TableView<Map<String, String>> commentsTable;
    @FXML private TableColumn<Map<String, String>, String> colDate;
    @FXML private TableColumn<Map<String, String>, String> colComment;

    @FXML private Button backBtn;
    @FXML private Label statusLabel;

    private Map<String, String> studentData;
    private List<Map<String, String>> commentsList = new ArrayList<>();
    private final MainController mainController = new MainController();

    /**
     * Set the student data and populate the view
     * @param student Map containing student profile data
     */
    public void setStudentData(Map<String, String> student) {
        this.studentData = student;
        populateProfile();
        loadComments();
        setupTableClickHandler();
    }

    /**
     * Populate profile fields with student data
     */
    private void populateProfile() {
        fullNameLabel.setText(studentData.getOrDefault("Full Name", "N/A"));
        academicStatusLabel.setText(studentData.getOrDefault("Academic Status", "N/A"));
        
        String jobStatus = studentData.getOrDefault("Job Status", "N/A");
        String jobDetails = studentData.getOrDefault("Job Details", "");
        if (!jobDetails.isEmpty() && !jobStatus.contains("(")) {
            jobStatusLabel.setText(jobStatus + " (" + jobDetails + ")");
        } else {
            jobStatusLabel.setText(jobStatus);
        }
        
        languagesLabel.setText(studentData.getOrDefault("Programming Languages", "N/A"));
        databasesLabel.setText(studentData.getOrDefault("Databases", "N/A"));
        roleLabel.setText(studentData.getOrDefault("Professional Role", "N/A"));
        whitelistLabel.setText(studentData.getOrDefault("Whitelist", "No"));
        blacklistLabel.setText(studentData.getOrDefault("Blacklist", "No"));
    }

    /**
     * Load and parse comments from student data
     */
    private void loadComments() {
        commentsList.clear();
        String commentsStr = studentData.getOrDefault("Comments", "").trim();
        
        if (commentsStr.isEmpty() || commentsStr.equalsIgnoreCase("N/A")) {
            commentsTable.setItems(FXCollections.emptyObservableList());
            statusLabel.setText("No comments available for this student");
            statusLabel.setTextFill(Color.BLUE);
            return;
        }

        // Parse comments in format: [date] text || [date] text
        String[] commentParts = commentsStr.split("\\|\\|");
        for (String part : commentParts) {
            part = part.trim();
            if (part.isEmpty()) continue;
            
            // Extract date in format [YYYY-MM-DD]
            if (part.startsWith("[") && part.contains("]")) {
                int endBracket = part.indexOf("]");
                String date = part.substring(1, endBracket);
                String text = part.substring(endBracket + 1).trim();

                Map<String, String> commentData = new HashMap<>();
                commentData.put("date", date);
                // Show excerpt if comment is too long
                if (text.length() > 100) {
                    commentData.put("comment", text.substring(0, 97) + "...");
                    commentData.put("fullComment", text);
                } else {
                    commentData.put("comment", text);
                    commentData.put("fullComment", text);
                }
                
                commentsList.add(commentData);
            }
        }

        // Make the entire comment as one entry
        if (commentsList.isEmpty()) {
            Map<String, String> commentData = new HashMap<>();
            commentData.put("date", "");
            commentData.put("comment", commentsStr);
            commentData.put("fullComment", commentsStr);
            commentsList.add(commentData);
        }

        // Setup table columns
        colDate.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getOrDefault("date", "")));
        colComment.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getOrDefault("comment", "")));

        // Display in table
        ObservableList<Map<String, String>> items = FXCollections.observableArrayList(commentsList);
        commentsTable.setItems(items);

        statusLabel.setText("Showing " + commentsList.size() + " comment(s). Click a row to view full comment.");
        statusLabel.setTextFill(Color.BLUE);
    }

    /**
     * Setup click handler for comment rows
     */
    private void setupTableClickHandler() {
        commentsTable.setRowFactory(tv -> {
            TableRow<Map<String, String>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY) {
                    Map<String, String> comment = row.getItem();
                    openCommentDetail(comment);
                }
            });
            return row;
        });
    }

    /**
     * Open detailed comment view in a new window
     * @param comment The comment data to display
     */
    private void openCommentDetail(Map<String, String> comment) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("comment_detail.fxml"));
            Scene scene = new Scene(loader.load(), mainController.gdWidth*0.5, mainController.gdHeight*0.6);

            CommentDetailController controller = loader.getController();
            controller.setCommentData(
                comment.get("date"),
                comment.get("fullComment"),
                studentData.get("Full Name")
            );

            // Open in a new window
            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setTitle("Comment Detail");
            newStage.show();

        } catch (IOException e) {
            statusLabel.setText("Error loading comment detail: " + e.getMessage());
            statusLabel.setTextFill(Color.RED);
        }
    }

    /**
     * Handle back button click - return to reports
     */
    @FXML
    protected void onBackClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("reports.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), mainController.gdWidth, mainController.gdHeight);

            Stage currentStage = (Stage) backBtn.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Student Tracker - Reports");

        } catch (IOException e) {
            statusLabel.setText("Error loading reports page: " + e.getMessage());
            statusLabel.setTextFill(Color.RED);
        }
    }
}

