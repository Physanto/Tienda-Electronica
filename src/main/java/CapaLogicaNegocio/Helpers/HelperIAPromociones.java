package CapaLogicaNegocio.Helpers;

import CapaLogicaNegocio.Logica_Negocio.Promocion;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.ManhattanDistance;
import java.util.ArrayList;

/**
 * Clase que desarrolla un modelo no supervisado para un sistema de promociones
 * esta hace uso de la libreria Weka para toda la matematica asociada ya que se usa
 * Kmeans para la solucion
 *
 * @author Manuel Figueroa (Physanto)
 */
public class HelperIAPromociones {

    private static SimpleKMeans kmeans;

    private static Instances convertirDatosAWeka(ArrayList<Promocion> listaPromocion) {
        if (listaPromocion == null || listaPromocion.isEmpty()) {
            return null;
        }

        ArrayList<Attribute> columnas = new ArrayList<>();
        columnas.add(new Attribute("stock"));
        columnas.add(new Attribute("diasSinVender"));
        columnas.add(new Attribute("ventas"));

        Instances datasetWeka = new Instances("DatasetPromociones", columnas, listaPromocion.size());

        for (Promocion p : listaPromocion) {
            double[] valoresFila = new double[3];
            valoresFila[0] = p.getStockActual();
            valoresFila[1] = p.getDiasSinVender();
            valoresFila[2] = p.getTotalVendido();

            datasetWeka.add(new DenseInstance(1.0, valoresFila));
        }
        return datasetWeka;
    }

    public static ArrayList<Promocion> agruparProductos(ArrayList<Promocion> lista) {
        try {
            Instances dataset = convertirDatosAWeka(lista);

            int numeroClusters = 3;

            kmeans = new SimpleKMeans();
            kmeans.setNumClusters(numeroClusters);

            ManhattanDistance manhattan = new ManhattanDistance();
            kmeans.setDistanceFunction(manhattan);
            kmeans.setPreserveInstancesOrder(true);

            kmeans.buildClusterer(dataset);

            int[] asignaciones = kmeans.getAssignments();

            for (int i = 0; i < lista.size(); i++) {
                lista.get(i).setCluster(asignaciones[i]);
                System.out.println(lista.get(i).toString());
            }
        }
        catch (Exception e) {
            System.out.println("Error ejecutando K-Means: " + e.getMessage());
        }
        return lista;
    }

    public static ArrayList<Promocion> clasificarInventario() {
        ArrayList<Promocion> listaPerfiles = new ArrayList<>();

        if (kmeans == null) {
            System.out.println("Error: Ejecuta el K-Means primero.");
            return listaPerfiles;
        }

        try {
            Instances centroides = kmeans.getClusterCentroids();
            int totalClusters = kmeans.getNumClusters();

            for (int i = 0; i < totalClusters; i++) {
                double promStock = centroides.instance(i).value(0);
                double promDias = centroides.instance(i).value(1);
                double promVentas = centroides.instance(i).value(2);

                listaPerfiles.add(new Promocion(i, promStock, promDias, promVentas));
            }

            listaPerfiles.sort((p1, p2) -> Double.compare(p2.getPuntajeRiesgo(), p1.getPuntajeRiesgo()));

            // 3. Asignamos la clasificación pura
            System.out.println("\n--- CLASIFICACIÓN DE CLÚSTERES ---");
            for (int pos = 0; pos < listaPerfiles.size(); pos++) {
                Promocion perfil = listaPerfiles.get(pos);

                if (pos == 0) {
                    perfil.setClasificacion("ESTANCADO");
                } else if (pos == listaPerfiles.size() - 1) {
                    perfil.setClasificacion("ESTRELLA");
                } else {
                    perfil.setClasificacion("REGULAR");
                }

                System.out.printf("El Clúster ID [%d] de Weka es: %s (Stock Prom: %.0f | Días Inactivo Prom: %.0f)%n",
                        perfil.getCluster(), perfil.getClasificacion(), perfil.getStockActual(), perfil.getDiasSinVender());
            }
        } catch (Exception e) {
            System.out.println("Error en la clasificación: " + e.getMessage());
        }
        return listaPerfiles;
    }

    public static void analizarMetodoDelCodo(ArrayList<Promocion> lista, int maxClustersAProbar) {
        try {
            Instances dataset = convertirDatosAWeka(lista);

            for (int k = 1; k <= maxClustersAProbar; k++) {
                SimpleKMeans kmCodo = new SimpleKMeans();
                kmCodo.setNumClusters(k);

                // IMPORTANTE: Misma distancia que en el modelo principal
                ManhattanDistance manhattan = new ManhattanDistance();
                kmCodo.setDistanceFunction(manhattan);

                kmCodo.buildClusterer(dataset);
                double error = kmCodo.getSquaredError();
                System.out.printf("Para K = %d | SSE (Error): %.4f%n", k, error);
            }
        } catch (Exception e) {
            System.out.println("Error en el análisis del codo: " + e.getMessage());
        }
    }

    public static void main(String[] args){

        ArrayList<Promocion> lista = new ArrayList<>();

        lista.add(new Promocion("p1", 120.0, 3.0, 450.0, -1));
        lista.add(new Promocion("p2", 15.0, 45.0, 80.0, -1));
        lista.add(new Promocion("p3", 300.0, 0.0, 1200.0, -1));
        lista.add(new Promocion("p4", 8.0, 60.0, 40.0, -1));
        lista.add(new Promocion("p5", 75.0, 12.0, 320.0, -1));
        lista.add(new Promocion("p6", 200.0, 5.0, 950.0, -1));
        lista.add(new Promocion("p7", 5.0, 90.0, 15.0, -1));
        lista.add(new Promocion("p8", 180.0, 8.0, 780.0, -1));
        lista.add(new Promocion("p9", 25.0, 30.0, 140.0, -1));
        lista.add(new Promocion("p10", 400.0, 1.0, 2100.0, -1));
        lista.add(new Promocion("p11", 60.0, 18.0, 270.0, -1));
        lista.add(new Promocion("p12", 12.0, 75.0, 55.0, -1));
        lista.add(new Promocion("p13", 95.0, 10.0, 430.0, -1));
        lista.add(new Promocion("p14", 250.0, 2.0, 1350.0, -1));
        lista.add(new Promocion("p15", 18.0, 50.0, 90.0, -1));
        lista.add(new Promocion("p16", 500.0, 0.0, 3500.0, -1));
        lista.add(new Promocion("p17", 40.0, 22.0, 180.0, -1));
        lista.add(new Promocion("p18", 7.0, 110.0, 20.0, -1));
        lista.add(new Promocion("p19", 130.0, 6.0, 610.0, -1));
        lista.add(new Promocion("p20", 35.0, 28.0, 160.0, -1));
        lista.add(new Promocion("p21", 220.0, 4.0, 980.0, -1));
        lista.add(new Promocion("p22", 10.0, 95.0, 30.0, -1));
        lista.add(new Promocion("p23", 85.0, 14.0, 390.0, -1));
        lista.add(new Promocion("p24", 160.0, 7.0, 720.0, -1));
        lista.add(new Promocion("p25", 20.0, 40.0, 110.0, -1));
        lista.add(new Promocion("p26", 350.0, 1.0, 1850.0, -1));
        lista.add(new Promocion("p27", 55.0, 20.0, 250.0, -1));
        lista.add(new Promocion("p28", 6.0, 130.0, 10.0, -1));
        lista.add(new Promocion("p29", 145.0, 9.0, 680.0, -1));
        lista.add(new Promocion("p30", 280.0, 3.0, 1420.0, -1));

        agruparProductos(lista);
    }
}