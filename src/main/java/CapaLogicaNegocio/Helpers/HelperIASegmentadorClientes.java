package CapaLogicaNegocio.Helpers;

import CapaLogicaNegocio.DTOS.AnalisisCliente;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.ManhattanDistance;
import java.util.ArrayList;

/**
 * Clase que desarrolla un modelo no supervisado para hacer un analisis de
 * clientes
 * dependiendo su comportamiento en el sistema para brindar promociones
 * personalizas
 * esta hace uso de la libreria Weka para toda la matematica asociada ya que se
 * usa
 * Kmeans para la solucion
 *
 * @author Manuel Figueroa (Physanto)
 */
public class HelperIASegmentadorClientes {

    private static SimpleKMeans kmeans;

    private static Instances convertirDatosAWeka(ArrayList<AnalisisCliente> listaClientes) {

        if (listaClientes == null || listaClientes.isEmpty()) {
            return null;
        }

        ArrayList<Attribute> columnas = new ArrayList<>();
        columnas.add(new Attribute("ultDiaCompra"));
        columnas.add(new Attribute("cantCompras"));
        columnas.add(new Attribute("dineroGastado"));

        Instances datasetWeka = new Instances("DatasetClientesRFM", columnas, listaClientes.size());

        for (AnalisisCliente p : listaClientes) {
            double[] valoresFila = new double[3];
            valoresFila[0] = p.getUltDiaCompra();
            valoresFila[1] = p.getCantCompras();
            valoresFila[2] = p.getDineroGastado();

            datasetWeka.add(new DenseInstance(1.0, valoresFila));
        }
        return datasetWeka;
    }

    public static ArrayList<AnalisisCliente> agruparClientes(ArrayList<AnalisisCliente> lista) {
        try {
            Instances dataset = convertirDatosAWeka(lista);

            // se asignan 3 cluster por reglas del negocio
            int numeroClusters = 3;

            kmeans = new SimpleKMeans();
            kmeans.setNumClusters(numeroClusters);

            // Nota: SimpleKMeans de Weka normaliza los datos automáticamente por defecto,
            // lo cual es bueno para que el dinero gastado no rompa el modelo.
            ManhattanDistance manhattan = new ManhattanDistance();
            kmeans.setDistanceFunction(manhattan);
            kmeans.setPreserveInstancesOrder(true);

            kmeans.buildClusterer(dataset);

            int[] asignaciones = kmeans.getAssignments();

            for (int i = 0; i < lista.size(); i++) {
                lista.get(i).setCluster(asignaciones[i]);
            }

            // calcularCentroides();
            analizarYEtiquetarClusters();

        } catch (Exception e) {
            System.out.println("Error ejecutando K-Means: " + e.getMessage());
        }
        return lista;
    }

    public static ArrayList<AnalisisCliente> analizarYEtiquetarClusters() {
        ArrayList<AnalisisCliente> listaPerfiles = new ArrayList<>();

        if (kmeans == null) {
            System.out.println("Error: K-Means no ha sido ejecutado.");
            return listaPerfiles;
        }
        try {
            Instances centroides = kmeans.getClusterCentroids();
            int totalClusters = kmeans.getNumClusters();

            // Extraer los datos de los centroides de Weka
            for (int i = 0; i < totalClusters; i++) {
                double promRecencia = centroides.instance(i).value(0);
                double promFrecuencia = centroides.instance(i).value(1);
                double promMonetario = centroides.instance(i).value(2);

                listaPerfiles.add(new AnalisisCliente(i, promRecencia, promFrecuencia, promMonetario));
            }

            // Ordena los clústers por Gasto Promedio (Monetario) de MAYOR a MENOR
            listaPerfiles.sort((c1, c2) -> Double.compare(c2.getDineroGastado(), c1.getDineroGastado()));

            for (int posicion = 0; posicion < listaPerfiles.size(); posicion++) {
                AnalisisCliente cluster = listaPerfiles.get(posicion);

                if (posicion == 0) {
                    if (cluster.getUltDiaCompra() > 45) {
                        cluster.setEtiquetaNegocio("VIP-CRITICO");
                        cluster.setDescuentoRecomendado("30");
                    } else {
                        cluster.setEtiquetaNegocio("VIP-FIEL");
                        cluster.setDescuentoRecomendado("0");
                    }
                } else if (posicion == 1) {
                    if (cluster.getUltDiaCompra() > 45) {
                        cluster.setEtiquetaNegocio("REGULAR-CRITICO");
                        cluster.setDescuentoRecomendado("15");
                    } else {
                        cluster.setEtiquetaNegocio("REGULAR-ACTIVO");
                        cluster.setDescuentoRecomendado("10");
                    }
                } else {
                    cluster.setEtiquetaNegocio("NUEVO");
                    cluster.setDescuentoRecomendado("5");
                }
                System.out.printf("Weka ID [%d] -> %s | Gasto Prom.: $%.2f | Inactividad: %.1f días -> Acción: %s%n",
                        cluster.getCluster(), cluster.getEtiquetaNegocio(), cluster.getDineroGastado(),
                        cluster.getUltDiaCompra(), cluster.getDescuentoRecomendado());
            }

        } catch (Exception e) {
            System.out.println("Error en el mapeo dinámico: " + e.getMessage());
        }
        return listaPerfiles;
    }

    public static void analizarMetodoDelCodo(ArrayList<AnalisisCliente> lista, int maxClustersAProbar) {
        try {
            Instances dataset = convertirDatosAWeka(lista);

            for (int k = 1; k <= maxClustersAProbar; k++) {
                SimpleKMeans kmCodo = new SimpleKMeans();
                kmCodo.setNumClusters(k);

                ManhattanDistance manhattan = new ManhattanDistance();
                kmCodo.setDistanceFunction(manhattan);

                kmCodo.buildClusterer(dataset);

                double error = kmCodo.getSquaredError();
                System.out.printf("Para K = %d | SSE (Suma de Errores Cuadráticos): %.4f%n", k, error);
            }
        } catch (Exception e) {
            System.out.println("Error en el análisis del codo: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        ArrayList<AnalisisCliente> lista = new ArrayList<>();

        lista.add(new AnalisisCliente("c1", 8.0, 14.0, 210.0, -1, "", ""));
        lista.add(new AnalisisCliente("c2", 175.0, 85.0, 4300.0, -1, "", ""));
        lista.add(new AnalisisCliente("c3", 4.0, 2.0, 35.0, -1, "", ""));
        lista.add(new AnalisisCliente("c4", 62.0, 47.0, 890.0, -1, "", ""));
        lista.add(new AnalisisCliente("c5", 19.0, 29.0, 540.0, -1, "", ""));
        lista.add(new AnalisisCliente("c6", 240.0, 160.0, 7200.0, -1, "", ""));
        lista.add(new AnalisisCliente("c7", 11.0, 7.0, 120.0, -1, "", ""));
        lista.add(new AnalisisCliente("c8", 38.0, 91.0, 1950.0, -1, "", ""));
        lista.add(new AnalisisCliente("c9", 83.0, 53.0, 1120.0, -1, "", ""));
        lista.add(new AnalisisCliente("c10", 14.0, 18.0, 280.0, -1, "", ""));
        lista.add(new AnalisisCliente("c11", 320.0, 240.0, 9800.0, -1, "", ""));
        lista.add(new AnalisisCliente("c12", 27.0, 36.0, 610.0, -1, "", ""));
        lista.add(new AnalisisCliente("c13", 51.0, 22.0, 470.0, -1, "", ""));
        lista.add(new AnalisisCliente("c14", 6.0, 11.0, 145.0, -1, "", ""));
        lista.add(new AnalisisCliente("c15", 97.0, 74.0, 1650.0, -1, "", ""));
        lista.add(new AnalisisCliente("c16", 132.0, 108.0, 3850.0, -1, "", ""));
        lista.add(new AnalisisCliente("c17", 2.0, 4.0, 60.0, -1, "", ""));
        lista.add(new AnalisisCliente("c18", 43.0, 31.0, 730.0, -1, "", ""));
        lista.add(new AnalisisCliente("c19", 71.0, 66.0, 1280.0, -1, "", ""));
        lista.add(new AnalisisCliente("c20", 23.0, 19.0, 340.0, -1, "", ""));
        lista.add(new AnalisisCliente("c21", 188.0, 142.0, 5600.0, -1, "", ""));
        lista.add(new AnalisisCliente("c22", 9.0, 16.0, 230.0, -1, "", ""));
        lista.add(new AnalisisCliente("c23", 56.0, 44.0, 970.0, -1, "", ""));
        lista.add(new AnalisisCliente("c24", 121.0, 97.0, 2750.0, -1, "", ""));
        lista.add(new AnalisisCliente("c25", 17.0, 25.0, 410.0, -1, "", ""));
        lista.add(new AnalisisCliente("c26", 265.0, 185.0, 8100.0, -1, "", ""));
        lista.add(new AnalisisCliente("c27", 34.0, 27.0, 520.0, -1, "", ""));
        lista.add(new AnalisisCliente("c28", 78.0, 59.0, 1340.0, -1, "", ""));
        lista.add(new AnalisisCliente("c29", 5.0, 9.0, 95.0, -1, "", ""));
        lista.add(new AnalisisCliente("c30", 149.0, 116.0, 3480.0, -1, "", ""));

        // analizarMetodoDelCodo(lista,10);

        System.out.println("\n\n\n agrupacion");
        agruparClientes(lista);
    }
}
