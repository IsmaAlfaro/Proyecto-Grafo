package com.proyectografo.controller;

import com.proyectografo.model.Grafo;
import com.proyectografo.util.Tema;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import javafx.scene.control.Label;

public class MainController {
    //contenedor donde se muestra la vista seleccionada
    @FXML
    private StackPane contenido;

    //modelo de grafo compartido por vistas
    private Grafo grafo;

    //se conserva para actualizar la vista de resultados cuando cambia el grafo
    private ResultadosController resultadosController;

    private MatrizController matrizController;
    // Se conserva el editor para mantener las posiciones al cambiar de pantalla.
    private GrafoController grafoController;
    private Parent vistaGrafo;

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

        Button botonDibujo = new Button("Dibujo → matriz");
        botonDibujo.setOnAction(event -> {
            event.consume();
            mostrarOpcionDibujo();
        });

        Label titulo = new Label("¿Cómo deseas trabajar?");
        titulo.getStyleClass().add("titulo-seccion");

        Label descripcion = new Label(
                "Crea un grafo desde una matriz o dibújalo para obtener su matriz."
        );
        descripcion.getStyleClass().add("descripcion");
        descripcion.setWrapText(true);

        botonMatriz.getStyleClass().add("boton-opcion");
        botonDibujo.getStyleClass().add("boton-opcion");

        VBox opciones = new VBox(
                titulo,
                descripcion,
                botonMatriz,
                botonDibujo
        );

        opciones.getStyleClass().add("menu-inicio");

        cambiarVista(opciones);
    }

    //carga la vista de matriz y le entrega el grafo compartido
    @FXML
    private void mostrarOpcionMatriz() {
        MatrizController controladorMatriz = null;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/proyectografo/view/matriz-view.fxml")
            );
            Parent vista = loader.load();
            controladorMatriz = loader.getController();
            controladorMatriz.setGrafo(grafo);

            //permite que la matriz solicite abrir el editor visual
            controladorMatriz.setAlSolicitarDibujo(this::mostrarOpcionDibujo);
            cambiarVista(vista);
            matrizController = controladorMatriz;
        } catch (IOException | RuntimeException e) {
            if (controladorMatriz != null) {
                controladorMatriz.liberar();
            }
            mostrarError("No se pudo cargar la vista de matriz.", e);
        }
    }

    //recibe el modelo enviado por MatrizController y abre el editor visual
    private void mostrarOpcionDibujo(Grafo grafoRecibido) {
        if (grafoRecibido != null && grafoRecibido != grafo) {
            grafo.eliminarObservador(observadorGrafo);
            grafo = grafoRecibido;
            grafo.agregarObservador(observadorGrafo);
        }
        mostrarOpcionDibujo();
    }

    //carga la vista del editor visual
    @FXML
    private void mostrarOpcionDibujo() {
        try {
            if (vistaGrafo == null) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/proyectografo/view/grafo-view.fxml")
                );
                vistaGrafo = loader.load();
                grafoController = loader.getController();
                grafoController.setAlSolicitarMatriz(this::mostrarOpcionMatriz);
            }

            // Actualiza los datos conservando las posiciones del mismo modelo.
            grafoController.setGrafo(grafo);
            cambiarVista(vistaGrafo);

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
            ResultadosController controlador = loader.getController();
            controlador.setGrafo(grafo);
            cambiarVista(vista);
            resultadosController = controlador;
        } catch (IOException | RuntimeException e) {
            mostrarError("No se pudo cargar la vista de resultados.", e);
        }
    }

    private void cambiarVista(Parent vista) {
        if (matrizController != null) {
            matrizController.liberar();
            matrizController = null;
        }
        resultadosController = null;
        contenido.getChildren().setAll(vista);
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
        Tema.aplicar(alerta);
        alerta.setTitle("Error");
        alerta.setHeaderText(mensaje);
        alerta.setContentText(error.getMessage());
        alerta.showAndWait();
    }

    @FXML
    private void volverAlMenu() {
        mostrarOpcionesInicio();
    }
}
