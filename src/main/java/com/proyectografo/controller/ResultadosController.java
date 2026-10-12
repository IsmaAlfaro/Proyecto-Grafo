package com.proyectografo.controller;

import com.proyectografo.model.Grafo;
import com.proyectografo.service.RecorridosService;
import javafx.fxml.FXML; //permite que JavaFX conecte los controles de la vista FXML con los campos y métodos del controlador
import javafx.scene.control.ComboBox; //importa el menú desplegable donde se elige el nodo inicial del recorrido
import javafx.scene.control.Label; //importa la etiqueta donde se muestra el resultado de BFS o DFS y los mensajes
import javafx.scene.control.TextArea; //importa el área de texto que presenta la lista de adyacencia
import java.util.List; //interfaz List, utilizada para guardar los nodos vecinos y el orden del recorrido
import javafx.util.StringConverter;

public class ResultadosController {

//selector del nodo desde el que iniciará BFS o DFS
@FXML
private ComboBox<Integer> nodoInicialCombo;

//area donde se muestra la lista de adyacencia con los pesos
@FXML
private TextArea listaAdyacenciaArea;

//etiqueta donde se presenta el resultado del recorrido
@FXML
private Label recorridoLabel;

//grafo compartido con las vistas de matriz y dibujo
private Grafo grafo;

//servicio que implementa los recorridos BFS y DFS
private final RecorridosService recorridosService =
        new RecorridosService();

@FXML
private void initialize() {
        nodoInicialCombo.setConverter(new StringConverter<Integer>() {
            @Override
            public String toString(Integer nodo) {
                return nodo == null ? "" : String.valueOf(nodo + 1);
            }

            @Override
            public Integer fromString(String texto) {
                return Integer.parseInt(texto.trim()) - 1;
            }
        });
    }

//recibe el grafo que debe mostrar esta vista
public void setGrafo(Grafo grafo) {
    this.grafo = grafo;
    actualizarResultados();
}

//refresca la lista de adyacencia, el selector de nodo inicial y el texto del recorrido
public void actualizarResultados() {
    //evita acceder al grafo si todavía no fue asignado.
    if (grafo == null) {
        listaAdyacenciaArea.clear();
        nodoInicialCombo.getItems().clear();
        recorridoLabel.setText("No hay un grafo cargado.");
        return;
    }
    mostrarListaAdyacencia();
    actualizarNodosIniciales();
    recorridoLabel.setText("Recorrido: —");
}

//construye y muestra la lista de adyacencia, solo se muestran los vecinos salientes de cada nodo
public void mostrarListaAdyacencia() {
    StringBuilder texto = new StringBuilder();
    //recorre los nodos en el orden que entrega el modelo.
    for (int nodo : grafo.getNodos()) {
        texto.append(nodo + 1).append(" → ");
        List<Integer> vecinos = grafo.getVecinos(nodo);
        if (vecinos.isEmpty()) {
            texto.append("(sin conexiones)");
        } else {
            //muestra cada vecino y el peso de la conexión dirigida
            for (int vecino : vecinos) {
                int peso = grafo.getPeso(nodo, vecino);
                texto.append(vecino + 1).append(" (peso ").append(peso).append("), ");
            }
            //quita la coma y el espacio que quedaron al final
            texto.setLength(texto.length() - 2);
        }
        texto.append("\n");
    }
    //presenta el texto construido en la interfaz
    listaAdyacenciaArea.setText(texto.toString());
}
//llena el selector con los identificadores de nodo disponibles
public void actualizarNodosIniciales() {
    nodoInicialCombo.getItems().setAll(grafo.getNodos());
    //selecciona el primer nodo si el grafo contiene alguno
    if (!nodoInicialCombo.getItems().isEmpty()) {
        nodoInicialCombo.getSelectionModel().selectFirst();
    }
}

//ejecuta el recorrido en anchura desde el nodo seleccionado
@FXML
private void ejecutarBFS() {
    Integer inicio = nodoInicialCombo.getValue();
    if (inicio == null) {
        recorridoLabel.setText("Selecciona un nodo inicial.");
        return;
    }
    List<Integer> orden = recorridosService.bfs(grafo, inicio);
    //muestra en orden los nodos alcanzables desde el nodo inicial
    recorridoLabel.setText("BFS: " + formatearRecorrido(orden));
}

//ejecuta el recorrido en profundidad desde el nodo seleccionado
@FXML
private void ejecutarDFS() {
    Integer inicio = nodoInicialCombo.getValue();
    if (inicio == null) {
        recorridoLabel.setText("Selecciona un nodo inicial.");
        return;
    }
    List<Integer> orden = recorridosService.dfs(grafo, inicio);
    //muestra en orden los nodos alcanzables desde el nodo inicial
    recorridoLabel.setText("DFS: " + formatearRecorrido(orden));
}

//convierte la lista de nodos en una cadena con flechas para que el orden del recorrido sea más fácil de leer
private String formatearRecorrido(List<Integer> orden) {
    StringBuilder texto = new StringBuilder();
    for (int i = 0; i < orden.size(); i++) {
        texto.append(orden.get(i) + 1);
        //agrega la flecha entre nodos, pero no después del último
        if (i < orden.size() - 1) {
            texto.append(" → ");
        }
    }
    return texto.toString();
}
}