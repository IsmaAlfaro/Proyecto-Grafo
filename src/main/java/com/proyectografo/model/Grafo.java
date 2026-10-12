package com.proyectografo.model;

import java.util.ArrayList;
import java.util.List;

public class Grafo {

    private int[][] matriz;

    private final List<Runnable> observadores = new ArrayList<>();

    public Grafo() {
        matriz = new int[0][0];
    }

    public Grafo(int cantidadNodos) {
        if (cantidadNodos < 0) {
            throw new IllegalArgumentException(
                    "La cantidad de nodos no puede ser negativa."
            );
        }

        matriz = new int[cantidadNodos][cantidadNodos];
    }

    public Grafo(int[][] datos) {
        validarMatriz(datos);
        matriz = copiar(datos);
    }

    public int getCantidadNodos() {
        return matriz.length;
    }

    public List<Integer> getNodos() {
        List<Integer> nodos = new ArrayList<>();

        for (int i = 0; i < matriz.length; i++) {
            nodos.add(i);
        }

        return nodos;
    }

    public int agregarNodo() {
        int cantidadActual = matriz.length;

        int[][] nueva = new int[cantidadActual + 1][cantidadActual + 1];

        for (int i = 0; i < cantidadActual; i++) {
            System.arraycopy(
                    matriz[i], 0, nueva[i], 0, cantidadActual
            );
        }

        matriz = nueva;
        notificarCambio();

        return cantidadActual;
    }

    // Quita la fila y columna del nodo; los identificadores restantes se renumeran.
    public void eliminarNodo(int nodo) {
        validarNodo(nodo);

        int[][] nueva = new int[matriz.length - 1][matriz.length - 1];
        int nuevaFila = 0;

        for (int fila = 0; fila < matriz.length; fila++) {
            if (fila == nodo) {
                continue;
            }

            int nuevaColumna = 0;
            for (int columna = 0; columna < matriz.length; columna++) {
                if (columna == nodo) {
                    continue;
                }

                nueva[nuevaFila][nuevaColumna] = matriz[fila][columna];
                nuevaColumna++;
            }
            nuevaFila++;
        }

        matriz = nueva;
        notificarCambio();
    }

    public void crearConexion(int origen, int destino, int peso) {
        validarConexion(origen, destino, peso);

        matriz[origen][destino] = peso;
        notificarCambio();
    }

    public void modificarPeso(int origen, int destino, int peso) {
        validarConexion(origen, destino, peso);

        if (!existeConexion(origen, destino)) {
            throw new IllegalArgumentException(
                    "La conexión que desea modificar no existe."
            );
        }

        matriz[origen][destino] = peso;
        notificarCambio();
    }

    public void eliminarConexion(int origen, int destino) {
        validarNodo(origen);
        validarNodo(destino);

        matriz[origen][destino] = 0;
        notificarCambio();
    }

    // Permite usar el mismo método desde la matriz y el editor visual.
    // Un valor cero elimina la conexión.
    public void establecerConexion(int origen, int destino, int peso) {
        if (peso == 0) {
            eliminarConexion(origen, destino);
        } else {
            crearConexion(origen, destino, peso);
        }
    }

    public boolean existeConexion(int origen, int destino) {
        return getPeso(origen, destino) > 0;
    }

    public int getPeso(int origen, int destino) {
        validarNodo(origen);
        validarNodo(destino);

        return matriz[origen][destino];
    }

    public List<Integer> getVecinos(int nodo) {
        validarNodo(nodo);

        List<Integer> vecinos = new ArrayList<>();

        for (int destino = 0; destino < matriz.length; destino++) {
            if (matriz[nodo][destino] > 0) {
                vecinos.add(destino);
            }
        }

        return vecinos;
    }

    public int[][] getMatrizAdyacencia() {
        return copiar(matriz);
    }

    public void setMatrizAdyacencia(int[][] datos) {
        // Se valida todo antes de modificar el modelo.
        validarMatriz(datos);

        matriz = copiar(datos);
        notificarCambio();
    }

    public void limpiar() {
        matriz = new int[0][0];
        notificarCambio();
    }

    public void agregarObservador(Runnable observador) {
        if (observador == null) {
            throw new IllegalArgumentException(
                    "El observador no puede ser null."
            );
        }

        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void eliminarObservador(Runnable observador) {
        observadores.remove(observador);
    }

    private void notificarCambio() {
        // La copia permite registrar o quitar observadores durante el aviso.
        List<Runnable> copia = new ArrayList<>(observadores);

        for (Runnable observador : copia) {
            observador.run();
        }
    }

    private void validarNodo(int nodo) {
        if (nodo < 0 || nodo >= matriz.length) {
            throw new IllegalArgumentException(
                    "El nodo está fuera del rango."
            );
        }
    }

    private void validarConexion(int origen, int destino, int peso) {
        validarNodo(origen);
        validarNodo(destino);

        if (origen == destino) {
            throw new IllegalArgumentException(
                    "Este editor no admite conexiones al mismo nodo."
            );
        }

        if (peso <= 0) {
            throw new IllegalArgumentException(
                    "El peso debe ser un entero mayor que cero."
            );
        }
    }

    private static void validarMatriz(int[][] datos) {
        if (datos == null) {
            throw new IllegalArgumentException(
                    "La matriz no puede ser null."
            );
        }

        int cantidad = datos.length;

        for (int i = 0; i < cantidad; i++) {
            if (datos[i] == null || datos[i].length != cantidad) {
                throw new IllegalArgumentException(
                        "La matriz debe ser cuadrada."
                );
            }

            for (int j = 0; j < cantidad; j++) {
                if (datos[i][j] < 0) {
                    throw new IllegalArgumentException(
                            "Los valores no pueden ser negativos."
                    );
                }

                if (i == j && datos[i][j] != 0) {
                    throw new IllegalArgumentException(
                            "La diagonal de la matriz debe contener ceros."
                    );
                }
            }
        }
    }

    private static int[][] copiar(int[][] datos) {
        int[][] copia = new int[datos.length][datos.length];

        for (int i = 0; i < datos.length; i++) {
            System.arraycopy(
                    datos[i], 0, copia[i], 0, datos.length
            );
        }

        return copia;
    }
}
