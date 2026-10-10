package com.proyectografo.view;

import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.input.MouseButton;

public class GrafoPane extends Pane {

    private static final double RADIO = 22;
    private int siguienteId = 0;
    private Circle circulosSeleccionado;
    private Integer nodoSeleccionado;

    public GrafoPane() {
        setPrefSize(900, 520);
        setStyle("-fx-background-color: #f8fafc;");
    }

    public int agregarNodo(double x, double y) {
        int id = siguienteId++;

        // Mantener el nodo dentro del área de dibujo.
        double posicionX = Math.max(
                RADIO,
                Math.min(x, getWidth() - RADIO)
        );

        double posicionY = Math.max(
                RADIO,
                Math.min(y, getHeight() - RADIO)
        );

        Circle circulo = new Circle(RADIO);
        circulo.setFill(Color.web("#2563eb"));
        circulo.setStroke(Color.web("#1e3a8a"));
        circulo.setStrokeWidth(2);

        // Internamente usamos 0, 1, 2...
        // En pantalla mostramos 1, 2, 3...
        Text etiqueta = new Text(String.valueOf(id + 1));
        etiqueta.setFill(Color.WHITE);
        etiqueta.setFont(Font.font(15));
        etiqueta.setTextOrigin(VPos.CENTER);
        etiqueta.setX(-etiqueta.getLayoutBounds().getWidth() / 2);

        Group nodo = new Group(circulo, etiqueta);
        nodo.setLayoutX(posicionX);
        nodo.setLayoutY(posicionY);

        nodo.setOnMouseClicked (event-> {
            if (event.getButton()==MouseButton.PRIMARY) {
                seleccionarNodo(id, circulo);
            event.consume();
        }
        });

        getChildren().add(nodo);

        return id;
    }


    private void seleccionarNodo(int id, Circle circulo) {
        boolean esElMismoNodo = Integer.valueOf(id).equals(nodoSeleccionado);

        limpiarSeleccion();

        //Si pulsamos otra vez el nodo seleccionado, queda desmarcado.
        if(esElMismoNodo) {
            return;
        }

        nodoSeleccionado = id;
        circulosSeleccionado = circulo;
        circulosSeleccionado.setFill(Color.web("#f97316"));

    }

    private void limpiarSeleccion() {
        if (circulosSeleccionado != null) {
            circulosSeleccionado.setFill(Color.web("#2563eb"));
        }

        circulosSeleccionado = null;
        nodoSeleccionado = null;
    }


    public int getCantidadNodos() {
        return siguienteId;
    }

    public void limpiar() {
        limpiarSeleccion();
        getChildren().clear();
        siguienteId = 0;
    }
}