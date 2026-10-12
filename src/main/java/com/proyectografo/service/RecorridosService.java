package com.proyectografo.service;

import com.proyectografo.model.Grafo;

import java.util.ArrayDeque; //funciona como una cola para BFS, los nodos pendientes se agregan al final y se procesan desde el principio
import java.util.ArrayList; //lista donde se guarda el orden de los nodos visitados
import java.util.HashSet; //implementación de conjunto que guarda los nodos sin repetirlos, registra cuáles ya se visitaron
import java.util.List; //permite declarar variables y tipos de retorno que contienen nodos ordenados
import java.util.Queue; //la interfaz de cola que usa BFS, ArrayDeque es la implementación concreta
import java.util.Set; //se usa para declarar el grupo de nodos visitados

public class RecorridosService {
    //recorre primero los vecinos cercanos al nodo inicial
    public List<Integer> bfs(Grafo grafo, int inicio) {
        validarInicio(grafo, inicio);
        List<Integer> recorrido = new ArrayList<>();
        Set<Integer> visitados = new HashSet<>();
        Queue<Integer> cola = new ArrayDeque<>();

        //agrega el nodo inicial para comenzar el recorrido
        visitados.add(inicio);
        cola.add(inicio);

        while (!cola.isEmpty()) {
            int actual = cola.remove();
            recorrido.add(actual);
            //agrega a la cola los vecinos que falten por visitar
            for (int vecino : grafo.getVecinos(actual)) {
                if (visitados.add(vecino)) {
                    cola.add(vecino);
                }
            }
        }
        return recorrido;
    }

    //explora cada camino antes de regresar a buscar otros vecinos
    public List<Integer> dfs(Grafo grafo, int inicio) {
        validarInicio(grafo, inicio);
        List<Integer> recorrido = new ArrayList<>();
        Set<Integer> visitados = new HashSet<>();
        dfsRecursivo(grafo, inicio, visitados, recorrido);
        return recorrido;
    }

    //visita el nodo y luego explora recursivamente sus vecinos
    private void dfsRecursivo(Grafo grafo, int nodo, Set<Integer> visitados, List<Integer> recorrido) {
        visitados.add(nodo);
        recorrido.add(nodo);
        for (int vecino : grafo.getVecinos(nodo)) {
            if (!visitados.contains(vecino)) {
                dfsRecursivo(grafo, vecino, visitados, recorrido);
            }
        }
    }

    //comprueba que el nodo inicial exista antes de iniciar el recorrido
    private void validarInicio(Grafo grafo, int inicio) {
        if (grafo == null) {
            throw new IllegalArgumentException("El grafo no puede ser null.");
        }
        if (!grafo.getNodos().contains(inicio)) {
            throw new IllegalArgumentException("El nodo inicial no existe en el grafo.");
        }
    }
}
