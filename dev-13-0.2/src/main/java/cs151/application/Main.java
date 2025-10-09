package cs151.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import cs151.application.models.ConnectDB;

import javax.xml.transform.Result;
import java.sql.*;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 600);
        stage.setTitle("Student Knowledgebase - Faculty Management System");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        ConnectDB db = new ConnectDB();
        Connection connection = db.getConnection();

        if(connection != null){
                try{
                Statement statement = connection.createStatement();
                String createTable="CREATE TABLE IF NOT EXISTS defineLanguage (id INTEGER PRIMARY KEY, language TEXT NOT NULL UNIQUE)";
                statement.executeUpdate(createTable);

                statement.executeUpdate("Insert into defineLanguage (language) values ('Lua')");
                System.out.println("Language Added Successfully");

                ResultSet resultSet = statement.executeQuery("Select * from defineLanguage");
                System.out.println("ID\tLanguage");
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String language = resultSet.getString("langauge");
                    System.out.println(id + "\t" + language);

                }

                } catch (SQLException e){
                    e.printStackTrace();
                } finally {
                    db.closeConnection();
                }
        } else {
            System.out.println("Connection is null");
        }

        launch();

    }
}