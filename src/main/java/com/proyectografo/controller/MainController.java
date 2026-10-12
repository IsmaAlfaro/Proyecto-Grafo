package com.proyectografo.controller;

import com.proyectografo.model.Grafo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class MainController {
    //contenedor donde se muestra la vista seleccionada
    @FXML
    private StackPane contenido;

    //modelo de grafo compartido por vistas
    private Grafo grafo;

    //se conserva para actualizar la vista de resultados cuando cambia el grafo
    private ResultadosController resultadosController;

    //observador que actualiza los resultados al cambiar el modelo
    private final Runnable observadorGrafo = this::sincronizarVistas;

    //inicializa el modelo, registra el observador y muestra el menú
    @FXML
    private void initialize() {
        grafo = new Grafo();
        grafo.agregarObservador(observadorGrafo);
        mostrarOpcionesInicio();
    }

    //crea los botones para acceder a las distintas vistas
    private void mostrarOpcionesInicio() {
        Button botonMatriz = new Button("Matriz → grafo");
        botonMatriz.setOnAction(event -> {
            event.consume();
            mostrarOpcionMatriz();
        });
        Button botonDibujo = new Button("Editor visual del grafo");
        botonDibujo.setOnAction(event -> {
            event.consume();
            mostrarOpcionDibujo();
        });
        Button botonResultados = new Button("Lista y recorridos");
        botonResultados.setOnAction(event -> {
            event.consume();
            mostrarResultados();
        });
        VBox opciones = new VBox(
                12,
                botonMatriz,
                botonDibujo,
                botonResultados
        );
        opciones.setStyle("-fx-padding: 20;");
        contenido.getChildren().setAll(opciones);
    }

    //carga la vista de matriz y le entrega el grafo compartido
    @FXML
    private void mostrarOpcionMatriz() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/proyectografo/view/matriz-view.fxml")
            );
            Parent vista = loader.load();
            MatrizController controladorMatriz = loader.getController();
            controladorMatriz.setGrafo(grafo);

            //permite que la matriz solicite abrir el editor visual
            controladorMatriz.setAlSolicitarDibujo(this::mostrarOpcionDibujo);
            contenido.getChildren().setAll(vista);
        } catch (IOException | RuntimeException e) {
            mostrarError("No se pudo cargar la vista de matriz.", e);
        }
    }

    //recibe el modelo enviado por MatrizController y abre el editor visual
    private void mostrarOpcionDibujo(Grafo grafoRecibido) {
        if (grafoRecibido != null) {
            grafo = grafoRecibido;
        }
        mostrarOpcionDibujo();
    }

    //carga la vista del editor visual
    @FXML
    private void mostrarOpcionDibujo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/proyectografo/view/grafo-view.fxml"));
            Parent vista = loader.load();
            contenido.getChildren().setAll(vista);
        } catch (IOException | RuntimeException e) {
            mostrarError("No se pudo cargar la vista del grafo.", e);
        }
    }

    //carga resultados y les entrega el grafo compartido
    @FXML
    private void mostrarResultados() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/proyectografo/view/resultados-view.fxml"));
            Parent vista = loader.load();
            resultadosController = loader.getController();
            resultadosController.setGrafo(grafo);
            contenido.getChildren().setAll(vista);
        } catch (IOException | RuntimeException e) {
            mostrarError("No se pudo cargar la vista de resultados.", e);
        }
    }

    //refresca resultados cuando el modelo notifica un cambio
    private void sincronizarVistas() {
        if (resultadosController != null) {
            resultadosController.setGrafo(grafo);
        }
    }

    //muestra un mensaje si no se pudo cargar una vista
    private void mostrarError(String mensaje, Exception error) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(mensaje);
        alerta.setContentText(error.getMessage());
        alerta.showAndWait();
    }
}