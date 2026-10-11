package com.proyectografo.controller;

import com.proyectografo.view.GrafoPane;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;

public class GrafoController {

    @FXML
    private GrafoPane grafoPane;

    @FXML
    private Label cantidadNodosLabel;

    @FXML
    private void initialize() {
        actualizarCantidadNodos();

        grafoPane.setOnMouseClicked(event -> {
            boolean clicIzquierdo =
                    event.getButton() == MouseButton.PRIMARY;

            boolean clicEnFondo =
                    event.getTarget() == grafoPane;

            if (clicIzquierdo && clicEnFondo) {
                grafoPane.agregarNodo(
                        event.getX(),
                        event.getY()
                );

                actualizarCantidadNodos();
            }
        });
    }

    @FXML
    private void limpiarGrafo() {
        grafoPane.limpiar();
        actualizarCantidadNodos();
    }

    private void actualizarCantidadNodos() {
        cantidadNodosLabel.setText(
                "Nodos: " + grafoPane.getCantidadNodos()
        );
    }
}