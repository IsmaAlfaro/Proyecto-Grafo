package com.proyectografo.controller;

import com.proyectografo.model.Grafo;
import com.proyectografo.view.GrafoPane;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.control.Alert;

public class GrafoController {

    @FXML
    private GrafoPane grafoPane;

    @FXML
    private Label cantidadNodosLabel;

    // Modelo utilizado por el editor.
    // En la integración recibiremos el compartido por MainController.
    private Grafo grafo = new Grafo();

    @FXML
    private void initialize() {
        actualizarCantidadNodos();

        grafoPane.setAlCrearConexion(this::crearConexion);

        grafoPane.setOnMouseClicked(event -> {
            boolean clicIzquierdo =
                    event.getButton() == MouseButton.PRIMARY;

            boolean clicEnFondo =
                    event.getTarget() == grafoPane;

            if (clicIzquierdo && clicEnFondo) {
                agregarNodo(event.getX(), event.getY());
            }
        });
    }

    private void agregarNodo(double x, double y) {
        // Primero se registra el nodo en el modelo.
        int id = grafo.agregarNodo();

        // Después se dibuja usando el mismo identificador.
        grafoPane.agregarNodo(id, x, y);

        actualizarCantidadNodos();
    }

    @FXML
    private void limpiarGrafo() {
        // Limpiar tanto los datos como el dibujo.
        grafo.limpiar();
        grafoPane.limpiar();

        actualizarCantidadNodos();
    }

    private void actualizarCantidadNodos() {
        cantidadNodosLabel.setText(
                "Nodos: " + grafo.getCantidadNodos()
        );
    }

    public Grafo getGrafo() {
        return grafo;
    }

    private void crearConexion(int origen, int destino, int peso) {
        if (!grafoPane.hayEspacioParaConexion(origen, destino)) {
            Alert aviso = new Alert(Alert.AlertType.WARNING);
            aviso.setTitle("Nodos demasiado cerca");
            aviso.setHeaderText("No hay espacio para dibujar la flecha");
            aviso.setContentText("Prueba con dos nodos más separados.");
            aviso.showAndWait();
            return;
        }

        try {
            // Primero guardar la conexión dirigida y su peso.
            grafo.crearConexion(origen, destino, peso);

            // Después representarla en el dibujo.
            grafoPane.dibujarConexion(origen, destino, peso);

        } catch (IllegalArgumentException e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("No se pudo crear la conexión");
            error.setHeaderText(null);
            error.setContentText(e.getMessage());
            error.showAndWait();
        }
    }
}