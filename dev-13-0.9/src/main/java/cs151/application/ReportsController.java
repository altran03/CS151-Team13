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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Controller for generating reports on blacklisted/whitelisted students
 */
public class ReportsController {

    @FXML private RadioButton blacklistRadio;
    @FXML private RadioButton whitelistRadio;
    @FXML private ToggleGroup reportTypeGroup;
    @FXML private Button generateReportBtn;
    @FXML private TableView<Map<String, String>> table;
    @FXML private TableColumn<Map<String, String>, String> colFullName;
    @FXML private TableColumn<Map<String, String>, String> colAcademic;
    @FXML private TableColumn<Map<String, String>, String> colJob;
    @FXML private TableColumn<Map<String, String>, String> colLangs;
    @FXML private TableColumn<Map<String, String>, String> colDBs;
    @FXML private TableColumn<Map<String, String>, String> colRole;
    @FXML private TableColumn<Map<String, String>, String> colComments;
    @FXML private Button backBtn;
    @FXML private Label statusLabel;

    private static final String STUDENT_PROFILES_CSV = "student_profiles.csv";
    private final MainController mainController = new MainController();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupRadioButtons();
        setupTableDoubleClick();
    }

    /**
     * Setup table columns with data binding
     */
    private void setupTableColumns() {
        colFullName.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getOrDefault("Full Name", "")));
        colAcademic.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getOrDefault("Academic Status", "")));
        colJob.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getOrDefault("Job Status", "")));
        colLangs.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getOrDefault("Programming Languages", "")));
        colDBs.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getOrDefault("Databases", "")));
        colRole.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getOrDefault("Professional Role", "")));
        colComments.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getOrDefault("Comments", "")));
        ;
    }

    /**
     * Setup radio buttons for report type selection
     */
    private void setupRadioButtons() {
        reportTypeGroup = new ToggleGroup();
        blacklistRadio.setToggleGroup(reportTypeGroup);
        whitelistRadio.setToggleGroup(reportTypeGroup);
        blacklistRadio.setSelected(true);
    }

    /**
     * Setup double-click handler for table rows
     */
    private void setupTableDoubleClick() {
        table.setRowFactory(tv -> {
            TableRow<Map<String, String>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY 
                    && event.getClickCount() == 2) {
                    Map<String, String> student = row.getItem();
                    openStudentProfileView(student);
                }
            });
            return row;
        });
    }

    /**
     * Generate report based on selected filter (blacklist/whitelist)
     */
    @FXML
    protected void onGenerateReportClick() {
        List<Map<String, String>> filteredStudents = new ArrayList<>();
        String filterType = blacklistRadio.isSelected() ? "Blacklist" : "Whitelist";

        try (BufferedReader br = new BufferedReader(new FileReader(STUDENT_PROFILES_CSV))) {
            String header = br.readLine();
            if (header == null) {
                statusLabel.setText("No student data found");
                statusLabel.setTextFill(Color.RED);
                return;
            }

            String[] headers = header.split(",", -1);
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                Map<String, String> student = new HashMap<>();
                
                for (int i = 0; i < Math.min(headers.length, parts.length); i++) {
                    student.put(headers[i].trim(), parts[i].trim());
                }

                // Combine job status and details for display
                String jobStatus = student.getOrDefault("Job Status", "").trim();
                String jobDetails = student.getOrDefault("Job Details", "").trim();
                if (!jobDetails.isEmpty()) {
                    student.put("Job Status", jobStatus + " (" + jobDetails + ")");
                }

                // Filter by blacklist or whitelist
                String flagValue = student.getOrDefault(filterType, "No");
                if ("Yes".equalsIgnoreCase(flagValue)) {
                    filteredStudents.add(student);
                }
            }

            // Sort by name
            filteredStudents.sort(Comparator.comparing(
                    r -> r.getOrDefault("Full Name", ""),
                    String.CASE_INSENSITIVE_ORDER
            ));

            // Display results
            ObservableList<Map<String, String>> items = FXCollections.observableArrayList(filteredStudents);
            table.setItems(items);

            statusLabel.setText("Found " + filteredStudents.size() + " " + 
                              filterType.toLowerCase() + "ed student(s)");
            statusLabel.setTextFill(Color.BLUE);

        } catch (IOException e) {
            statusLabel.setText("Error loading student data: " + e.getMessage());
            statusLabel.setTextFill(Color.RED);
        }
    }

    /**
     * Open detailed profile view when student row is double-clicked
     * @param student The student data to display
     */
    private void openStudentProfileView(Map<String, String> student) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("student_profile_view.fxml"));
            Scene scene = new Scene(loader.load(), mainController.gdWidth, mainController.gdHeight);

            StudentProfileViewController controller = loader.getController();
            controller.setStudentData(student);

            Stage currentStage = (Stage) table.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Student Profile - " + student.get("Full Name"));

        } catch (IOException e) {
            statusLabel.setText("Error loading student profile: " + e.getMessage());
            statusLabel.setTextFill(Color.RED);
        }
    }

    /**
     * Handle back button click - return to home
     */
    @FXML
    protected void onBackClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("home.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 500, 600);

            Stage currentStage = (Stage) backBtn.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Student Tracker - Home");

        } catch (IOException e) {
            statusLabel.setText("Error loading home page: " + e.getMessage());
            statusLabel.setTextFill(Color.RED);
        }
    }
}

