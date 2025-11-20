package cs151.application;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import java.util.Optional;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ViewAllStudentsController {

    @FXML private TableView<Map<String, String>> table;
    @FXML private TableColumn<Map<String, String>, String> colFullName;
    @FXML private TableColumn<Map<String, String>, String> colAcademic;
    @FXML private TableColumn<Map<String, String>, String> colJob;
    @FXML private TableColumn<Map<String, String>, String> colLangs;
    @FXML private TableColumn<Map<String, String>, String> colDBs;
    @FXML private TableColumn<Map<String, String>, String> colRole;
    @FXML private TableColumn<Map<String, String>, String> colComments;
    @FXML private TableColumn<Map<String, String>, String> colWhitelist;
    @FXML private TableColumn<Map<String, String>, String> colBlacklist;
    @FXML private TableColumn<Map<String, String>, String> colEdit;
    @FXML private TableColumn<Map<String, String>, String> colDelete;

    @FXML private Button backBtn;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchTypeCombo;
    @FXML private Button searchBtn;
    @FXML private Button clearSearchBtn;
    @FXML private Label statusLabel;

    private static final String FILE = "student_profiles.csv";
    private static final String PROGRAMMING_LANGUAGES_CSV = "programming_languages.csv";
    private List<Map<String, String>> allStudents = new ArrayList<>();
    private List<Map<String, String>> filteredStudents = new ArrayList<>();
    private List<String> availableLanguages = new ArrayList<>();
    private final MainController mainController = new MainController();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupSearchComboBox();
        setupActionColumns();
        loadProgrammingLanguages();
        loadCsv();
    }

    // Initialize and display default or get values for tableview
    private void setupTableColumns() {
        colFullName.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getOrDefault("Full Name", "")));
        colAcademic.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getOrDefault("Academic Status", "")));
        colJob.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getOrDefault("Job Status", "")));
        colLangs.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getOrDefault("Programming Languages", "")));
        colDBs.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getOrDefault("Databases", "")));
        colRole.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getOrDefault("Professional Role", "")));
        colWhitelist.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getOrDefault("Whitelist", "")));
        colBlacklist.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getOrDefault("Blacklist", "")));
    }

    // Initialize list for search parameters and default search values
    private void setupSearchComboBox() {
        List<String> searchTypes = Arrays.asList(
            "All Fields", "Full Name", "Academic Status", "Programming Language", 
            "Database", "Professional Role"
        );
        searchTypeCombo.setItems(FXCollections.observableArrayList(searchTypes));
        searchTypeCombo.setValue("All Fields");
    }

    // Open Scene that gets the columns' field and updates it
    private void setupActionColumns() {
        // Comments button for viewing/adding comments
        colComments.setCellFactory(column -> new TableCell<Map<String, String>, String>() {
            private final Button commentsBtn = new Button("Comments");
            {
                commentsBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-size: 10px;");
                commentsBtn.setOnAction(event -> {
                    Map<String, String> student = getTableView().getItems().get(getIndex());
                    onViewComments(student);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                }
                else {
                    setGraphic(commentsBtn);
                }
            }
        });

        // Edit student file with btn
        colEdit.setCellFactory(column -> new TableCell<Map<String, String>, String>() {
            private final Button editBtn = new Button("Edit");
            {
                editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 10px;");
                editBtn.setOnAction(event -> {
                    Map<String, String> student = getTableView().getItems().get(getIndex());
                    onEditStudent(student);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                }
                else {
                    setGraphic(editBtn);
                }
            }
        });

        // Delete student file with btn
        colDelete.setCellFactory(column -> new TableCell<Map<String, String>, String>() {
            private final Button deleteBtn = new Button("Delete");
            {
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                deleteBtn.setOnAction(event -> {
                    Map<String, String> student = getTableView().getItems().get(getIndex());
                    onDeleteStudent(student);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                }
                else {
                    setGraphic(deleteBtn);
                }
            }
        });

        // Call the edit, delete, and comments in initialize()
        colComments.setCellValueFactory(d -> new SimpleStringProperty(""));
        colEdit.setCellValueFactory(d -> new SimpleStringProperty(""));
        colDelete.setCellValueFactory(d -> new SimpleStringProperty(""));

    }

    private void loadProgrammingLanguages() {
        try (BufferedReader br = new BufferedReader(new FileReader(PROGRAMMING_LANGUAGES_CSV))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                if (!line.trim().isEmpty()) {
                    availableLanguages.add(line.trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading programming languages: " + e.getMessage());
            availableLanguages = Arrays.asList("Java", "Python", "C++", "JavaScript", "C#", "Go", "Rust");
        }
    }

    private void loadCsv() {
        allStudents.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String header = br.readLine();
            if (header == null) {
                table.setItems(FXCollections.observableArrayList());
                return;
            }

            String[] headers = header.split(",", -1);
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                Map<String, String> map = new HashMap<>();
                for (int i = 0; i < Math.min(headers.length, parts.length); i++) {
                    map.put(headers[i].trim(), parts[i].trim());
                }

                String jobStatus = map.getOrDefault("Job Status", "").trim();
                String jobDetails = map.getOrDefault("Job Details", "").trim();
                if (!jobDetails.isEmpty()) {
                    map.put("Job Status", jobStatus + " (" + jobDetails + ")");
                }

                allStudents.add(map);
            }

            // Sort by name
            allStudents.sort(Comparator.comparing(
                    r -> r.getOrDefault("Full Name", ""),
                    String.CASE_INSENSITIVE_ORDER
            ));

            filteredStudents = new ArrayList<>(allStudents);
            updateTable();
        } catch (IOException e) {
            table.setItems(FXCollections.observableArrayList());
            statusLabel.setText("Error loading student data");
            statusLabel.setTextFill(Color.RED);
        }
    }

    private void updateTable() {
        ObservableList<Map<String, String>> items = FXCollections.observableArrayList(filteredStudents);
        table.setItems(items);
    }

    //Receives user input for search field and updates table with matching search field
    @FXML
    protected void onSearchClick() {
        String searchTerm = searchField.getText().trim();
        String searchType = searchTypeCombo.getValue();

        if (searchTerm.isEmpty()) {
            filteredStudents = new ArrayList<>(allStudents);
        } else {
            filteredStudents = allStudents.stream()
                .filter(student -> matchesSearch(student, searchTerm, searchType))
                .collect(Collectors.toList());
        }

        updateTable();
        statusLabel.setText("Found " + filteredStudents.size() + " student(s)");
        statusLabel.setTextFill(Color.BLUE);
    }

    @FXML
    protected void onClearSearchClick() {
        searchField.clear();
        searchTypeCombo.setValue("All Fields");
        filteredStudents = new ArrayList<>(allStudents);
        updateTable();
        statusLabel.setText("Search cleared");
        statusLabel.setTextFill(Color.BLUE);
    }

    /**
     * Takes in users text field and selected field, and compare with table values
     * @param student profile data contained in the map
     * @param searchTerm the inputted field for finding a value
     * @param searchType the classification used for finding a value
     * @return boolean {@code true} if value is found, else {@code false}
     * */
    private boolean matchesSearch(Map<String, String> student, String searchTerm, String searchType) {
        String term = searchTerm.toLowerCase();
        
        switch (searchType) {
            case "Full Name":
                return student.getOrDefault("Full Name", "").toLowerCase().contains(term);
            case "Academic Status":
                return student.getOrDefault("Academic Status", "").toLowerCase().contains(term);
            case "Programming Language":
                return student.getOrDefault("Programming Languages", "").toLowerCase().contains(term);
            case "Database":
                return student.getOrDefault("Databases", "").toLowerCase().contains(term);
            case "Professional Role":
                return student.getOrDefault("Professional Role", "").toLowerCase().contains(term);
            case "All Fields":
            default:
                return student.values().stream()
                    .anyMatch(value -> value.toLowerCase().contains(term));
        }
    }

    /**
     * Opens the comments page for viewing and adding comments
     * @param student corresponding with the column
     */
    private void onViewComments(Map<String, String> student) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("student_comments.fxml"));
            Scene scene = new Scene(loader.load(), 700, 650);

            StudentCommentsController controller = loader.getController();
            controller.setStudentData(student);

            Stage currentStage = (Stage) table.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Student Comments - " + student.get("Full Name"));

        } catch (IOException e) {
            statusLabel.setText("Error loading comments page: " + e.getMessage());
            statusLabel.setTextFill(Color.RED);
        }
    }

    /**
     * Loads edit scene and allows for student to be updated
     * @param student corresponding with the column
     */
    private void onEditStudent(Map<String, String> student) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("define_student_profiles.fxml"));
            Scene scene = new Scene(loader.load(), mainController.gdWidth*0.65, mainController.gdHeight);

            DefineStudentProfilesController controller = loader.getController();
            controller.loadStudentDataForEditing(student); //go to DSPController later

            Stage currentStage = (Stage) table.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Edit Student Profile - " + student.get("Full Name"));

        } catch (IOException e) {
            statusLabel.setText("Error loading edit student form: " + e.getMessage());
            statusLabel.setTextFill(Color.RED);
        }
    }

    /**
     * Deletes student from student profile and tableview
     * @param student profile from corresponding column
     */
    private void onDeleteStudent(Map<String, String> student) {
        String studentName = student.get("Full Name");

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Student");
        alert.setHeaderText("Delete Student Profile");
        alert.setContentText("Are you sure you want to delete " + studentName + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteStudentFromCSV(student);
            loadCsv();
            statusLabel.setText("Student: " + studentName + " has been deleted");
            statusLabel.setTextFill(Color.GREEN);
        }
    }

    /**
     * Deletes student data from CSV file
     * @param studentToDelete a map containing to be deleted student data
     */
    private void deleteStudentFromCSV(Map<String, String> studentToDelete) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE));
            List<String> updateLines = new ArrayList<>();

            if (!lines.isEmpty()) {
                updateLines.add(lines.get(0));

                for (int i = 1; i < lines.size(); i++) {
                    String line = lines.get(i);
                    String[] parts = line.split(",", -1);
                    if (parts.length > 0) {
                        String currentName = parts[0].trim();
                        String targetName = studentToDelete.get("Full Name").trim();
                        if (!currentName.equals(targetName)) {
                            updateLines.add(line);
                        }
                    }
                }
            }
            // write back to file
            Files.write(Paths.get(FILE), updateLines);
        } catch (IOException e) {
            statusLabel.setText("Error deleting student: " + e.getMessage());
            statusLabel.setTextFill(Color.RED);
        }
    }


    @FXML
    protected void onBackClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("home.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), mainController.gdWidth, mainController.gdHeight);

            Stage currentStage = (Stage) backBtn.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Student Tracker - Home");

        } catch (IOException e) {
            System.out.println("Error loading home page");
        }
    }
}