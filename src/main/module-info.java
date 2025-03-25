module org.example.demo2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.MPPGUI to javafx.fxml;
    exports org.example.MPPGUI;
}