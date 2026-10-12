package com.proyectografo.view;

import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.input.MouseButton;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import java.util.HashMap;
import java.util.Map;
import com.proyectografo.model.Grafo;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;



public class GrafoPane extends Pane {

    private static final double RADIO = 22;
    private int siguienteId = 0;
    private Circle circulosSeleccionado;
    private Integer nodoSeleccionado;
    private final Map<Integer, Group> nodosVisuales = new HashMap<>();
    private final Map<String, Group> conexionesVisuales = new HashMap<>();
    private BiConsumer <Integer, Integer> alEliminarConexion;
    private IntConsumer alEliminarNodo;

    @FunctionalInterface
    public interface AlCrearConexion{
        void ejecutar (int origen, int destino, int peso);
    }

    private AlCrearConexion alCrearConexion;

    public void setAlEliminarConexion(BiConsumer <Integer, Integer> accion) {
        alEliminarConexion = accion;
    }

    public void setAlCrearConexion(AlCrearConexion accion) {
        alCrearConexion = accion;
    }

    public void setAlEliminarNodo(IntConsumer accion) {
        alEliminarNodo = accion;
    }

    public GrafoPane() {
        setPrefSize(900, 520);
        setStyle(
                "-fx-background-color: #121418;"
                        + "-fx-background-radius: 8;"
                        + "-fx-border-color: #38352e;"
                        + "-fx-border-radius: 8;"
        );
    }

    public int agregarNodo(int id, double x, double y) {
        siguienteId = Math.max(siguienteId, id + 1);

        // Mantener el nodo dentro del área de dibujo.
        double anchoDisponible = Math.max(getWidth(), getMinWidth());
        double altoDisponible = Math.max(getHeight(), getMinHeight());

        double posicionX = Math.max(
                RADIO,
                Math.min(x, anchoDisponible - RADIO)
        );

        double posicionY = Math.max(
                RADIO,
                Math.min(y, altoDisponible - RADIO)
        );

        Circle circulo = new Circle(RADIO);
        circulo.setFill(Color.web("#343943"));
        circulo.setStroke(Color.web("#d4af60"));
        circulo.setStrokeWidth(2);

        // Internamente, usamos 0, 1, 2...
        // En pantalla mostramos 1, 2, 3...
        Text etiqueta = new Text(String.valueOf(id + 1));
        etiqueta.setFill(Color.WHITE);
        etiqueta.setFont(Font.font(15));
        etiqueta.setTextOrigin(VPos.CENTER);
        etiqueta.setX(-etiqueta.getLayoutBounds().getWidth() / 2);

        Group nodo = new Group(circulo, etiqueta);
        nodo.setLayoutX(posicionX);
        nodo.setLayoutY(posicionY);

        MenuItem eliminarNodo = new MenuItem("Eliminar nodo");
        ContextMenu menuNodo = new ContextMenu(eliminarNodo);

        eliminarNodo.setOnAction(event -> {
            menuNodo.hide();
            if (alEliminarNodo != null) {
                alEliminarNodo.accept(id);
            }
        });

        nodo.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                seleccionarNodo(id, circulo);
            } else if (event.getButton() == MouseButton.SECONDARY) {
                menuNodo.show(nodo, event.getScreenX(), event.getScreenY());
            }
            event.consume();
        });

        nodosVisuales.put(id, nodo);
        getChildren().add(nodo);

        return id;
    }


    private void seleccionarNodo(int id, Circle circulo) {
        // El primer clic selecciona el origen.
        if (nodoSeleccionado == null) {
            nodoSeleccionado = id;
            circulosSeleccionado = circulo;
            circulosSeleccionado.setFill(Color.web("#806025"));
            return;
        }

        // Pulsar otra vez el origen cancela la selección.
        if (nodoSeleccionado.equals(id)) {
            limpiarSeleccion();
            return;
        }

        // El segundo nodo es el destino.
        int origen = nodoSeleccionado;
        int destino = id;

        Integer peso = solicitarPeso(origen, destino);

        if (peso != null && alCrearConexion != null) {
            alCrearConexion.ejecutar(origen, destino, peso);
        }

        limpiarSeleccion();
    }

    private void limpiarSeleccion() {
        if (circulosSeleccionado != null) {
            circulosSeleccionado.setFill(Color.web("#343943"));
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
        conexionesVisuales.clear();
        nodosVisuales.clear();
        siguienteId = 0;
    }

    private Integer solicitarPeso(int origen, int destino) {
        TextInputDialog dialogo = new TextInputDialog();

        dialogo.setTitle("Peso de la conexión");
        dialogo.setHeaderText(
                "Conectar " + (origen + 1) + " → " + (destino + 1)
        );
        dialogo.setContentText("Peso entero mayor que cero:");

        while (true) {
            Optional<String> respuesta = dialogo.showAndWait();

            // Cancelar o cerrar la ventana no crea una conexión.
            if (respuesta.isEmpty()) {
                return null;
            }

            try {
                int peso = Integer.parseInt(respuesta.get().trim());

                if (peso > 0) {
                    return peso;
                }
            } catch (NumberFormatException e) {
                // El texto no representa un entero dentro del rango de int.
            }

            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Peso inválido");
            error.setHeaderText("Ingresa un número entero mayor que cero");
            error.setContentText(
                    "No se permiten cero, negativos, decimales ni letras."
                            + "\nEl valor máximo es " + Integer.MAX_VALUE + "."
            );
            error.showAndWait();
        }
    }

    public void dibujarConexion(int origen, int destino, int peso) {
        Group nodoOrigen = nodosVisuales.get(origen);
        Group nodoDestino = nodosVisuales.get(destino);

        double x1 = nodoOrigen.getLayoutX();
        double y1 = nodoOrigen.getLayoutY();
        double x2 = nodoDestino.getLayoutX();
        double y2 = nodoDestino.getLayoutY();

        double dx = x2 - x1;
        double dy = y2 - y1;
        double distancia = Math.hypot(dx, dy);

        // Necesitamos espacio entre los nodos para mostrar la flecha.
        if (distancia <= RADIO * 2 + 15) {
            Alert aviso = new Alert(Alert.AlertType.WARNING);
            aviso.setTitle("Nodos demasiado cerca");
            aviso.setHeaderText("No hay espacio para dibujar la flecha");
            aviso.setContentText(
                    "Prueba con dos nodos más separados."
            );
            aviso.showAndWait();
            return;
        }

        // Vector de longitud 1 que apunta hacia el destino.
        double ux = dx / distancia;
        double uy = dy / distancia;

        // Vector perpendicular a la dirección de la conexión.
        double perpendicularX = -uy;
        double perpendicularY = ux;

        // Separamos las líneas para distinguir A → B de B → A.
        double desplazamiento = 7;
        double recorte = Math.sqrt(
                RADIO * RADIO - desplazamiento * desplazamiento
        );

        double inicioX = x1 + ux * recorte
                + perpendicularX * desplazamiento;
        double inicioY = y1 + uy * recorte
                + perpendicularY * desplazamiento;

        double finX = x2 - ux * recorte
                + perpendicularX * desplazamiento;
        double finY = y2 - uy * recorte
                + perpendicularY * desplazamiento;

        Line linea = new Line(inicioX, inicioY, finX, finY);
        linea.setStroke(Color.web("#d4af60"));
        linea.setStrokeWidth(2);

        // Triángulo cuya punta termina en el nodo destino.
        double largoFlecha = 12;
        double medioAnchoFlecha = 5;

        Polygon flecha = new Polygon(
                finX, finY,
                finX - ux * largoFlecha
                        + perpendicularX * medioAnchoFlecha,
                finY - uy * largoFlecha
                        + perpendicularY * medioAnchoFlecha,
                finX - ux * largoFlecha
                        - perpendicularX * medioAnchoFlecha,
                finY - uy * largoFlecha
                        - perpendicularY * medioAnchoFlecha
        );
        flecha.setFill(Color.web("#d4af60"));

        Text etiquetaPeso = new Text(String.valueOf(peso));
        etiquetaPeso.setFont(Font.font(14));
        etiquetaPeso.setFill(Color.web("#f0d99e"));
        etiquetaPeso.setTextOrigin(VPos.CENTER);

        double centroX = (inicioX + finX) / 2;
        double centroY = (inicioY + finY) / 2;

        etiquetaPeso.setX(
                centroX + perpendicularX * 14
                        - etiquetaPeso.getLayoutBounds().getWidth() / 2
        );
        etiquetaPeso.setY(centroY + perpendicularY * 14);

        Group conexion = new Group(linea, flecha, etiquetaPeso);

        // Facilita pulsar la línea sin cambiar su apariencia.
        Line zonaClic = new Line(inicioX, inicioY, finX, finY);
        zonaClic.setStroke(Color.TRANSPARENT);
        zonaClic.setStrokeWidth(14);
        conexion.getChildren().add(0, zonaClic);

        MenuItem modificar = new MenuItem("Modificar peso");
        MenuItem eliminar = new MenuItem("Eliminar conexión");

        ContextMenu menu = new ContextMenu(modificar, eliminar);

        modificar.setOnAction(event -> {
            menu.hide();
            limpiarSeleccion();

            Integer nuevoPeso = solicitarPeso(origen, destino);

            if (nuevoPeso != null && alCrearConexion != null) {
                // El controlador actualiza el modelo y redibuja la conexión.
                alCrearConexion.ejecutar(origen, destino, nuevoPeso);
            }
        });

        eliminar.setOnAction(event -> {
            menu.hide();
            limpiarSeleccion();

            if (alEliminarConexion != null) {
                alEliminarConexion.accept(origen, destino);
            }
        });

        conexion.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                menu.show(
                        conexion,
                        event.getScreenX(),
                        event.getScreenY()
                );
            }

            event.consume();
        });

        String clave = origen + "->" + destino;

        Group conexionAnterior = conexionesVisuales.put(clave, conexion);

        if (conexionAnterior != null) {
            getChildren().remove(conexionAnterior);
        }

        getChildren().add(0, conexion);
    }

    public boolean hayEspacioParaConexion(int origen, int destino) {
        Group nodoOrigen = nodosVisuales.get(origen);
        Group nodoDestino = nodosVisuales.get(destino);

        if (nodoOrigen == null || nodoDestino == null) {
            return false;
        }

        double dx = nodoDestino.getLayoutX() - nodoOrigen.getLayoutX();
        double dy = nodoDestino.getLayoutY() - nodoOrigen.getLayoutY();

        return Math.hypot(dx, dy) > RADIO * 2 + 15;
    }

    private void aplicarTema(javafx.scene.control.Dialog<?> dialogo) {
        String css = java.util.Objects.requireNonNull(
                getClass().getResource("/com/proyectografo/styles/app.css")
        ).toExternalForm();

        dialogo.getDialogPane().getStylesheets().add(css);
    }

    public void mostrarGrafo(Grafo grafo) {
        // Limpiar solo la representación visual.
        limpiar();

        int cantidad = grafo.getCantidadNodos();

        if (cantidad == 0) {
            setMinSize(300, 250);
            setPrefSize(900, 520);
            return;
        }

        // Distribución circular con espacio suficiente entre nodos.
        double radioDistribucion = cantidad <= 1
                ? 0
                : Math.max(150, 50 / Math.sin(Math.PI / cantidad));

        double margen = 70;
        double lado = Math.max(400, 2 * (radioDistribucion + margen));

        setMinSize(lado, lado);
        setPrefSize(lado, lado);

        double centro = lado / 2;

        for (int id : grafo.getNodos()) {
            double angulo = -Math.PI / 2
                    + (2 * Math.PI * id / cantidad);

            double x = centro + radioDistribucion * Math.cos(angulo);
            double y = centro + radioDistribucion * Math.sin(angulo);

            agregarNodo(id, x, y);
        }

        // Primero deben existir todos los nodos visuales.
        for (int origen : grafo.getNodos()) {
            for (int destino : grafo.getVecinos(origen)) {
                dibujarConexion(
                        origen,
                        destino,
                        grafo.getPeso(origen, destino)
                );
            }
        }
    }

    public void eliminarConexionVisual(int origen, int destino) {
        String clave = origen + "->" + destino;

        Group conexion = conexionesVisuales.remove(clave);

        if (conexion != null) {
            getChildren().remove(conexion);
        }

        limpiarSeleccion();
    }

}
