module com.example.partcprojectatp {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;


    opens view to javafx.fxml;
    exports view;
}
