package cs151.application;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ViewAllStudentsController {

    @FXML private TableView<Map<String, String>> table;
    @FXML private TableColumn<Map<String, String>, String> colFullName;
    @FXML private TableColumn<Map<String, String>, String> colAcademic;
    @FXML private TableColumn<Map<String, String>, String> colJob;
    @FXML private TableColumn<Map<String, String>, String> colLangs;
    @FXML private TableColumn<Map<String, String>, String> colDBs;
    @FXML private TableColumn<Map<String, String>, String> colRole;
    @FXML private TableColumn<Map<String, String>, String> colComments;

    @FXML private Button backBtn;

    private static final String FILE = "student_profiles.csv";

    @FXML
    public void initialize() {
        // 绑定列
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

        loadCsv();
    }

    private void loadCsv() {
        List<Map<String, String>> rows = new ArrayList<>();
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

                rows.add(map);
            }

            // 排序
            rows.sort(Comparator.comparing(
                    r -> r.getOrDefault("Full Name", ""),
                    String.CASE_INSENSITIVE_ORDER
            ));

            table.setItems(FXCollections.observableArrayList(rows));
        } catch (IOException e) {
            table.setItems(FXCollections.observableArrayList());
        }
    }


    @FXML
    protected void onBackClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("home.fxml"));
        Scene scene = new Scene(loader.load(), 600, 500);
        Stage stage = (Stage) backBtn.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Student Tracker - Home");
    }
}
