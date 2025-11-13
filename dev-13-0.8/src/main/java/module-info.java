module cs151.application {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    requires com.dlsc.formsfx;

    opens cs151.application to javafx.fxml;
    exports cs151.application;
}