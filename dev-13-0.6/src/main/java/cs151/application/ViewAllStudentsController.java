package cs151.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
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

    @FXML
    public void initialize() {
        setupTableColumns();
        setupSearchComboBox();
        loadProgrammingLanguages();
        loadCsv();
    }

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
        colComments.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getOrDefault("Comments", "")));
        colWhitelist.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getOrDefault("Whitelist", "")));
        colBlacklist.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getOrDefault("Blacklist", "")));
    }

    private void setupSearchComboBox() {
        List<String> searchTypes = Arrays.asList(
            "All Fields", "Full Name", "Academic Status", "Programming Language", 
            "Database", "Professional Role"
        );
        searchTypeCombo.setItems(FXCollections.observableArrayList(searchTypes));
        searchTypeCombo.setValue("All Fields");
    }

    private void loadProgrammingLanguages() {
        try (BufferedReader br = new BufferedReader(new FileReader(PROGRAMMING_LANGUAGES_CSV))) {
            String line;
            while ((line = br.readLine()) != null) {
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

    @FXML
    protected void onBackClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("home.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);

            Stage currentStage = (Stage) backBtn.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Student Tracker - Home");

        } catch (IOException e) {
            System.out.println("Error loading home page");
        }
    }
}