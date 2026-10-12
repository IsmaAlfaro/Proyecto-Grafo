package com.proyectografo.controller;

import com.proyectografo.model.Grafo;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.function.Consumer;

public class MatrizController {

    @FXML
    private TextField cantidadNodosField;

    @FXML
    private GridPane matrizGrid;

    @FXML
    private Label estadoLabel;

    private Grafo grafo = new Grafo();

    private TextField[][] celdas;

    private boolean actualizandoVista;
    private boolean vistaPreparada;

    private Consumer<Grafo> alSolicitarDibujo;

    private final Runnable observador = this::modeloModificado;

    @FXML
    private void initialize() {
        vistaPreparada = true;

        grafo.agregarObservador(observador);
        mostrarMatriz();
    }

    // El integrante que integra la aplicación entrega aquí
    // el MISMO objeto Grafo que utiliza el editor visual.
    public void setGrafo(Grafo nuevoGrafo) {
        if (nuevoGrafo == null) {
            throw new IllegalArgumentException(
                    "El grafo no puede ser null."
            );
        }

        grafo.eliminarObservador(observador);
        grafo = nuevoGrafo;

        if (vistaPreparada) {
            grafo.agregarObservador(observador);
            mostrarMatriz();
        }
    }

    public Grafo getGrafo() {
        return grafo;
    }

    // El integrante 2 o 3 conecta aquí su método de dibujo.
    public void setAlSolicitarDibujo(Consumer<Grafo> accion) {
        alSolicitarDibujo = accion;
    }

    // Puede llamarse cuando se cierra definitivamente esta vista.
    public void liberar() {
        grafo.eliminarObservador(observador);
    }

    @FXML
    private void generarMatriz() {
        try {
            int cantidad = Integer.parseInt(
                    cantidadNodosField.getText().trim()
            );

            if (cantidad <= 0) {
                throw new IllegalArgumentException(
                        "La cantidad de nodos debe ser mayor que cero."
                );
            }

            // Sustituye los datos, pero conserva el mismo objeto compartido.
            grafo.setMatrizAdyacencia(
                    new int[cantidad][cantidad]
            );

            estadoLabel.setText(
                    "Matriz generada. Edite los pesos: 0 = sin conexión."
            );

        } catch (NumberFormatException e) {
            mostrarError(
                    "Digite una cantidad de nodos válida y entera."
            );

        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    private void mostrarMatriz() {
        if (!vistaPreparada) {
            return;
        }

        actualizandoVista = true;

        try {
            int[][] datos = grafo.getMatrizAdyacencia();
            int cantidad = datos.length;

            cantidadNodosField.setText(
                    String.valueOf(cantidad)
            );

            matrizGrid.getChildren().clear();
            celdas = new TextField[cantidad][cantidad];

            matrizGrid.add(new Label("De / A"), 0, 0);

            for (int i = 0; i < cantidad; i++) {
                matrizGrid.add(
                        new Label(String.valueOf(i + 1)),
                        i + 1,
                        0
                );

                matrizGrid.add(
                        new Label(String.valueOf(i + 1)),
                        0,
                        i + 1
                );

                for (int j = 0; j < cantidad; j++) {
                    final int fila = i;
                    final int columna = j;

                    TextField celda = new TextField(
                            String.valueOf(datos[i][j])
                    );

                    celda.setPrefWidth(65);
                    celda.setMinWidth(65);

                    if (i == j) {
                        celda.setEditable(false);
                    }

                    celdas[i][j] = celda;

                    celda.textProperty().addListener(
                            (observable, anterior, nuevo) ->
                                    actualizarCelda(
                                            fila,
                                            columna,
                                            celda,
                                            nuevo
                                    )
                    );

                    matrizGrid.add(celda, j + 1, i + 1);
                }
            }

            estadoLabel.setText(
                    cantidad == 0
                            ? "Digite la cantidad de nodos para comenzar."
                            : "Matriz actualizada desde el modelo."
            );

        } finally {
            actualizandoVista = false;
        }
    }

    private void actualizarCelda(
            int fila,
            int columna,
            TextField celda,
            String texto
    ) {
        if (actualizandoVista) {
            return;
        }

        int peso;

        try {
            peso = Integer.parseInt(texto.trim());

            if (peso < 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {
            celda.setStyle(
                    "-fx-border-color: red; -fx-border-width: 2;"
            );

            estadoLabel.setText(
                    "Celda [" + (fila + 1) + ", " + (columna + 1)
                            + "] inválida. Digite un entero no negativo."
            );

            // El texto inválido no modifica el modelo.
            return;
        }

        actualizandoVista = true;

        try {
            grafo.establecerConexion(
                    fila, columna, peso
            );

            celda.setStyle("");

            estadoLabel.setText(
                    "Modelo actualizado. Las celdas deben contener "
                            + "enteros no negativos."
            );

        } catch (IllegalArgumentException e) {
            celda.setStyle(
                    "-fx-border-color: red; -fx-border-width: 2;"
            );

            estadoLabel.setText(e.getMessage());

        } finally {
            actualizandoVista = false;
        }
    }

    @FXML
    private void convertirAGrafo() {
        if (celdas == null || celdas.length == 0) {
            mostrarError("Primero genere una matriz.");
            return;
        }

        int cantidad = celdas.length;
        int[][] datos = new int[cantidad][cantidad];

        for (int i = 0; i < cantidad; i++) {
            for (int j = 0; j < cantidad; j++) {
                try {
                    datos[i][j] = Integer.parseInt(
                            celdas[i][j].getText().trim()
                    );

                    if (datos[i][j] < 0) {
                        throw new NumberFormatException();
                    }

                } catch (NumberFormatException e) {
                    mostrarError(
                            "Revise la celda [" + (i + 1)
                                    + ", " + (j + 1)
                                    + "]. Debe contener un entero "
                                    + "mayor o igual que cero."
                    );
                    return;
                }
            }
        }

        try {
            grafo.setMatrizAdyacencia(datos);

            if (alSolicitarDibujo != null) {
                alSolicitarDibujo.accept(grafo);

                estadoLabel.setText(
                        "Matriz convertida y enviada al editor visual."
                );

            } else {
                estadoLabel.setText(
                        "Modelo actualizado. Falta conectar "
                                + "la vista de dibujo."
                );
            }

        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    private void modeloModificado() {
        if (!vistaPreparada || actualizandoVista) {
            return;
        }

        if (Platform.isFxApplicationThread()) {
            mostrarMatriz();

        } else {
            Platform.runLater(() -> {
                if (!actualizandoVista) {
                    mostrarMatriz();
                }
            });
        }
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);

        alerta.setTitle("Datos inválidos");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}