package client;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    public static void main(String[] args) {
        launch();
    }

    @SuppressWarnings("exports")
    @Override
    public void start(Stage stage) throws Exception {
        
        FlowPane flow = new FlowPane(Orientation.VERTICAL);
        flow.setHgap(0);
        flow.setVgap(0);
        flow.setStyle("-fx-background-color: lightblue;");

        int headerHeight = 150;

        FlowPane header = new FlowPane();
        header.setPrefHeight(headerHeight);
        header.setPrefWidth(stage.getWidth()); 
        header.setStyle("-fx-background-color:rgb(255, 146, 146);");
        flow.getChildren().add(header);

        FlowPane body = new FlowPane();
        body.setPrefHeight(stage.getHeight()-headerHeight);
        body.setPrefWidth(stage.getWidth());
        body.setStyle("-fx-background-color:rgb(104, 98, 255);");
        flow.getChildren().add(body);

        Scene scene = new Scene(flow);

        stage.setScene(scene);

        stage.setTitle("CHAT JAVA!!");
        stage.setWidth(1280);
        stage.setHeight(720);
        stage.show();
    }
}