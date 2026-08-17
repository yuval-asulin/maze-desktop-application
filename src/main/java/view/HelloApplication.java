package view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/view/MyView.fxml"));
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double windowWidth = Math.min(820, visualBounds.getWidth() - 80);
        double windowHeight = Math.min(680, visualBounds.getHeight() - 80);
        Scene scene = new Scene(fxmlLoader.load(), windowWidth, windowHeight);
        MyViewController controller = fxmlLoader.getController();
        stage.setTitle("Maze Game");
        stage.setMinWidth(700);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            controller.shutdown();
            Platform.exit();
        });
        stage.show();
    }
}
