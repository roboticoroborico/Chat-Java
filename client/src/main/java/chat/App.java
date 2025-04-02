package chat;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
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

        int headerHeight = 150;
    // ------- setup root
        
        BorderPane root = new BorderPane();
        
    // ------- setup header
        FlowPane header = new FlowPane();
        header.setPrefHeight(headerHeight);
        header.prefWidthProperty().bind(root.widthProperty());
        header.setStyle("-fx-background-color:rgb(255, 146, 146);");
        header.setAlignment(javafx.geometry.Pos.CENTER);
        root.setTop(header);
        
        Button button = new Button("prova");
        header.getChildren().add(button);

    // ------- setup body
        
        // Usa BorderPane anche per il body per un migliore controllo del layout
        BorderPane body = new BorderPane();
        body.prefHeightProperty().bind(root.heightProperty().subtract(headerHeight));
        body.setStyle("-fx-background-color:rgb(78, 78, 78);");
        // Aggiungi body nella parte centrale del BorderPane
        root.setCenter(body);

        int userPanelWidth = 400;
        FlowPane userPanel = new FlowPane();
        userPanel.setMaxWidth(userPanelWidth);
        userPanel.prefHeightProperty().bind(body.heightProperty());
        userPanel.setStyle("-fx-background-color:rgb(104, 98, 255);");
        userPanel.setAlignment(javafx.geometry.Pos.CENTER);
        body.setLeft(userPanel);

        FlowPane chatList = new FlowPane();
        chatList.setMinWidth(20);
        chatList.prefWidthProperty().bind(body.widthProperty().subtract(userPanelWidth));
        chatList.prefHeightProperty().bind(body.heightProperty());
        chatList.setStyle("-fx-background-color:rgb(98, 255, 237);");
        chatList.setAlignment(javafx.geometry.Pos.CENTER);
        body.setCenter(chatList);

        Button button1 = new Button("prova");
        userPanel.getChildren().addAll(button1);

        Button button2 = new Button("prova");
        chatList.getChildren().addAll(button2);

    // ------- setup scene

        Scene scene = new Scene(root);
        
        stage.setScene(scene);

    // ------- setup stage

        stage.setTitle("CHAT JAVA!!");
        stage.setMinWidth(600);
        stage.setWidth(1280);

        stage.setMinHeight(400);
        stage.setHeight(720);
        stage.show();
    }
}