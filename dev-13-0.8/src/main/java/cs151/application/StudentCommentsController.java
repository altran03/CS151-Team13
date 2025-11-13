package cs151.application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Controller for managing student comments.
 * Displays all stored comments for a selected student and allows adding new comments.
 */
public class StudentCommentsController {

    @FXML private Label studentNameLabel;
    @FXML private VBox commentsContainer;
    @FXML private TextArea newCommentField;
    @FXML private Button addCommentBtn;
    @FXML private Button backBtn;
    @FXML private Label statusLabel;
    @FXML private ScrollPane commentsScrollPane;

    private static final String STUDENT_PROFILES_CSV = "student_profiles.csv";
    private Map<String, String> studentData;
    private List<Comment> comments = new ArrayList<>();

    /**
     * Represents a single comment with date and text
     */
    private static class Comment {
        String date;
        String text;

        Comment(String date, String text) {
            this.date = date;
            this.text = text;
        }
    }

    /**
     * Initialize the controller with student data
     * @param student Map containing student profile data
     */
    public void setStudentData(Map<String, String> student) {
        this.studentData = student;
        String studentName = student.getOrDefault("Full Name", "Unknown Student");
        studentNameLabel.setText("Comments for " + studentName);
        loadComments();
        displayComments();
    }

    /**
     * Load and parse comments from the student's profile
     */
    private void loadComments() {
        comments.clear();
        String commentsStr = studentData.getOrDefault("Comments", "").trim();
        
        if (commentsStr.isEmpty() || commentsStr.equalsIgnoreCase("N/A")) {
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
                comments.add(new Comment(date, text));
            } else {
                // If no date format found, add as-is with today's date
                String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
                comments.add(new Comment(today, part));
            }
        }
    }

    /**
     * Display all comments in the UI
     */
    private void displayComments() {
        commentsContainer.getChildren().clear();
        
        if (comments.isEmpty()) {
            Label noCommentsLabel = new Label("No comments available for this student.");
            noCommentsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
            commentsContainer.getChildren().add(noCommentsLabel);
            return;
        }

        for (Comment comment : comments) {
            VBox commentBox = createCommentDisplay(comment);
            commentsContainer.getChildren().add(commentBox);
        }
    }

    /**
     * Create a visual display for a single comment
     * @param comment The comment to display
     * @return VBox containing the formatted comment
     */
    private VBox createCommentDisplay(Comment comment) {
        VBox box = new VBox(5);
        box.setStyle("-fx-background-color: white; -fx-padding: 10px; -fx-background-radius: 5px;");
        box.setPadding(new Insets(10));
        VBox.setMargin(box, new Insets(0, 0, 10, 0));

        // Date label
        Label dateLabel = new Label(comment.date);
        dateLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        // Comment text
        Label textLabel = new Label(comment.text);
        textLabel.setWrapText(true);
        textLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");

        box.getChildren().addAll(dateLabel, textLabel);
        return box;
    }

    /**
     * Handle adding a new comment
     */
    @FXML
    protected void onAddCommentClick() {
        String newCommentText = newCommentField.getText().trim();
        
        if (newCommentText.isEmpty()) {
            statusLabel.setText("Please enter a comment before adding.");
            statusLabel.setTextFill(Color.RED);
            return;
        }

        // Get today's date
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        
        // Create new comment
        Comment newComment = new Comment(today, newCommentText);
        comments.add(newComment);
        
        // Save to CSV
        try {
            saveCommentsToCSV();
            statusLabel.setText("Comment added successfully!");
            statusLabel.setTextFill(Color.GREEN);
            
            // Clear the text field
            newCommentField.clear();
            
            // Refresh display
            displayComments();
        } catch (IOException e) {
            statusLabel.setText("Error saving comment: " + e.getMessage());
            statusLabel.setTextFill(Color.RED);
        }
    }

    /**
     * Save updated comments back to the CSV file
     * @throws IOException if file operations fail
     */
    private void saveCommentsToCSV() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(STUDENT_PROFILES_CSV));
        List<String> updatedLines = new ArrayList<>();

        String targetName = studentData.get("Full Name");
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

                if (currentName.equals(targetName)) {
                    // Update this student's comments
                    String updatedLine = buildUpdatedCSVLine(parts);
                    updatedLines.add(updatedLine);
                } else {
                    // Keep the original line
                    updatedLines.add(line);
                }
            }
        }

        // Write all lines back to file
        Files.write(Paths.get(STUDENT_PROFILES_CSV), updatedLines);
    }

    /**
     * Build an updated CSV line with new comments
     * @param originalParts Original CSV line parts
     * @return Updated CSV line as string
     */
    private String buildUpdatedCSVLine(String[] originalParts) {
        // CSV format: Full Name,Academic Status,Job Status,Job Details,Programming Languages,
        //             Databases,Professional Role,Comments,Whitelist,Blacklist
        
        // Build comments string in format: [date] text || [date] text
        StringBuilder commentsStr = new StringBuilder();
        for (int i = 0; i < comments.size(); i++) {
            Comment c = comments.get(i);
            commentsStr.append("[").append(c.date).append("] ").append(c.text);
            if (i < comments.size() - 1) {
                commentsStr.append(" || ");
            }
        }

        // Build the complete line
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < originalParts.length; i++) {
            if (i == 7) { // Comments column index
                line.append(commentsStr.toString());
            } else {
                line.append(originalParts[i]);
            }
            
            if (i < originalParts.length - 1) {
                line.append(",");
            }
        }

        return line.toString();
    }

    /**
     * Handle back button click - return to student list
     */
    @FXML
    protected void onBackClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view_all_students.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 600);

            Stage currentStage = (Stage) backBtn.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Student Tracker - Search Students");

        } catch (IOException e) {
            statusLabel.setText("Error loading student list: " + e.getMessage());
            statusLabel.setTextFill(Color.RED);
        }
    }
}

