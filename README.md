# Proyecto-Grafo

Base de Java 26 y JavaFX para implementar grafos y matrices de adyacencia.

## Estructura

```text
src/
  main/
    java/com/proyectografo/
      App.java                 Punto de entrada JavaFX
      controller/              Controladores de las vistas
      model/                   Grafo, vertice, arista y matriz de adyacencia
      service/                 Algoritmos y logica de negocio
      util/                    Utilidades compartidas
    resources/com/proyectografo/
      view/                    Archivos FXML
      styles/                  Hojas de estilo CSS
      images/                  Imagenes de la aplicacion
  test/java/com/proyectografo/ Pruebas unitarias
```

## Abrir en IntelliJ IDEA

1. En `File > Project Structure > SDKs`, selecciona el JDK 26 ya instalado.
2. En `File > Project Structure > Project`, selecciona ese JDK como `Project SDK` y Java 26 como `Language level`.
3. Abre o recarga el archivo `pom.xml` como proyecto Maven. IntelliJ descargara JavaFX automaticamente.
4. Desde la ventana Maven ejecuta `Plugins > javafx > javafx:run`.

No es necesario descargar ni configurar manualmente el SDK de JavaFX: Maven lo resuelve mediante `pom.xml`.
