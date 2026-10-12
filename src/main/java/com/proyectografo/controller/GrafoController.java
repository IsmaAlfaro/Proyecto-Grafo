package com.proyectografo.controller;

import com.proyectografo.model.Grafo;
import com.proyectografo.util.Tema;
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

    // MainController entrega el mismo modelo que utilizan matriz y resultados.
    private Grafo grafo = new Grafo();

    private Runnable alSolicitarMatriz;



    @FXML
    private void initialize() {
        actualizarCantidadNodos();

        grafoPane.setAlCrearConexion(this::crearConexion);
        grafoPane.setAlEliminarConexion(this::eliminarConexion);
        grafoPane.setAlEliminarNodo(this::eliminarNodo);

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
            Tema.aplicar(aviso);
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
            Tema.aplicar(error);
            error.setTitle("No se pudo crear la conexión");
            error.setHeaderText(null);
            error.setContentText(e.getMessage());
            error.showAndWait();
        }
    }

    public void setGrafo(Grafo grafoCompartido) {
        if (grafoCompartido == null) {
            throw new IllegalArgumentException(
                    "El grafo compartido no puede ser null."
            );
        }

        if (grafo != grafoCompartido) {
            grafoPane.limpiar();
        }
        grafo = grafoCompartido;
        grafoPane.mostrarGrafo(grafo);
        actualizarCantidadNodos();
    }

    public void setAlSolicitarMatriz(Runnable accion) {
        alSolicitarMatriz = accion;
    }

    @FXML
    private void mostrarMatriz() {
        if (alSolicitarMatriz != null) {
            alSolicitarMatriz.run();
        }
    }

    private void eliminarNodo(int nodo) {
        try {
            grafo.eliminarNodo(nodo);

            // Renumera los nodos conservando las posiciones de los restantes.
            grafoPane.mostrarTrasEliminarNodo(grafo, nodo);
            actualizarCantidadNodos();

        } catch (IllegalArgumentException e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            Tema.aplicar(error);
            error.setTitle("No se pudo eliminar el nodo");
            error.setHeaderText(null);
            error.setContentText(e.getMessage());
            error.showAndWait();
        }
    }

    private void eliminarConexion(int origen, int destino) {
        try {
            // Primero modificar los datos.
            grafo.eliminarConexion(origen, destino);

            // Después quitar la representación visual.
            grafoPane.eliminarConexionVisual(origen, destino);

        } catch (IllegalArgumentException e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            Tema.aplicar(error);
            error.setTitle("No se pudo eliminar la conexión");
            error.setHeaderText(null);
            error.setContentText(e.getMessage());
            error.showAndWait();
        }
    }
}
