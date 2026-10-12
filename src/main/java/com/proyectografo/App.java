package com.proyectografo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                App.class.getResource("/com/proyectografo/view/main-view.fxml")
        );

        Scene scene = new Scene(loader.load());

        scene.getStylesheets().add(
                java.util.Objects.requireNonNull(
                        App.class.getResource("/com/proyectografo/styles/app.css")
                ).toExternalForm()
        );

        stage.setTitle("Proyecto Grafo");
        stage.setMinWidth(850);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }
}