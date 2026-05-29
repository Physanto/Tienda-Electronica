package CapaLogicaNegocio.Helpers;

import CapaLogicaNegocio.Logica_Negocio.Promocion;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.ManhattanDistance;
import java.util.ArrayList;

public class HelperIA {

    private static SimpleKMeans kmeans;
    /**
     * Metodo que convierte la lista de tipo promocion en una lista que pueda entender Weka para poder procesar los datos el modelo
     * no supervisado
     * @param listaPromocion es la lista de Promocion que contiene id_producto, stock, diasSinVender, totalVendido
     * @return un dataset del tipo que puede entender weka
     */
    private static Instances convertirDatosAWeka(ArrayList<Promocion> listaPromocion) {

        if(listaPromocion.isEmpty()){
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

    public static void main(String[] args){

        ArrayList<Promocion> lista = new ArrayList<>();
        lista.add(new Promocion("A091", 148.0, 12.0, 335.0));
        lista.add(new Promocion("B204", 927.0, 63.0, 114.0));
        lista.add(new Promocion("C317", 451.0, 29.0, 782.0 ));
        lista.add(new Promocion("D428", 88.0, 745.0, 16.0 ));
        lista.add(new Promocion("E539", 612.0, 98.0, 430.0 ));
        lista.add(new Promocion("F640", 301.0, 555.0, 72.0 ));
        lista.add(new Promocion("G751", 799.0, 44.0, 920.0 ));
        lista.add(new Promocion("H862", 15.0, 287.0, 631.0 ));
        lista.add(new Promocion("I973", 564.0, 701.0, 93.0 ));
        lista.add(new Promocion("J084", 243.0, 18.0, 856.0 ));
        lista.add(new Promocion("K195", 999.0, 332.0, 120.0));
        lista.add(new Promocion("L206", 407.0, 84.0, 541.0));
        lista.add(new Promocion("M317", 76.0, 918.0, 267.0));
        lista.add(new Promocion("N428", 650.0, 39.0, 704.0));
        lista.add(new Promocion("O539", 182.0, 473.0, 56.0));
        lista.add(new Promocion("P640", 811.0, 207.0, 995.0));
        lista.add(new Promocion("Q751", 523.0, 11.0, 389.0));
        lista.add(new Promocion("R862", 95.0, 640.0, 248.0));
        lista.add(new Promocion("S973", 734.0, 53.0, 817.0));
        lista.add(new Promocion("T084", 268.0, 124.0, 690.0));
        lista.add(new Promocion("U195", 419.0, 876.0, 31.0));
        lista.add(new Promocion("V206", 557.0, 290.0, 143.0));
        lista.add(new Promocion("W317", 61.0, 722.0, 502.0));
        lista.add(new Promocion("X428", 890.0, 15.0, 333.0));
        lista.add(new Promocion("Y539", 346.0, 480.0, 77.0));
        lista.add(new Promocion("Z640", 214.0, 999.0, 615.0));
        lista.add(new Promocion("A751", 785.0, 54.0, 294.0));
        lista.add(new Promocion("B862", 132.0, 661.0, 845.0));
        lista.add(new Promocion("C973", 908.0, 27.0, 470.0));
        lista.add(new Promocion("D084", 375.0, 510.0, 189.0));

       analizarMetodoDelCodo(lista,10);

        System.out.println("\n\n\n agrupacion");
        agruparProductos(lista, 2);
    }

    public static void analizarMetodoDelCodo(ArrayList<Promocion> lista, int maxClustersAProbar) {
        try {
            Instances dataset = convertirDatosAWeka(lista);

            for (int k = 1; k <= maxClustersAProbar; k++) {

                SimpleKMeans kmeans = new SimpleKMeans();
                kmeans.setNumClusters(k);

                // internamente la libreria weka cuando hace calculo de distancias usa la distancia euclidea

                kmeans.buildClusterer(dataset);

                double error = kmeans.getSquaredError();
                System.out.printf("Para K = %d | El margen de error (SSE) es: %.4f%n", k, error);
            }
            System.out.println("Revisa dónde la caída de error se vuelve menos brusca (Ese es tu codo).");

        } catch (Exception e) {
            System.out.println("Error en el análisis del codo: " + e.getMessage());
        }
    }

    /**
     * Metodo que se encarga de entrenar el modelo a partir de los datos ya convertidos
     * este nos genera los productos que se encuentran en cada cluster.
     * @param lista la lista que se quiere convertir internamente
     * @param numeroClusters es la cantidad de cluster que queremos manejar para nuestros datos
     */
    public static ArrayList<Promocion> agruparProductos(ArrayList<Promocion> lista, int numeroClusters) {
        try {
            Instances dataset = convertirDatosAWeka(lista);

            kmeans = new SimpleKMeans();
            kmeans.setNumClusters(numeroClusters);

            ManhattanDistance manhattan = new ManhattanDistance();
            kmeans.setDistanceFunction(manhattan);

            kmeans.setPreserveInstancesOrder(true);

            kmeans.buildClusterer(dataset);

            int[] asignaciones = kmeans.getAssignments();

            for (int i = 0; i < lista.size(); i++) {
                lista.get(i).setCluster(asignaciones[i]);
            }
            calcularCentroides();
        }
        catch (Exception e) {
            System.out.println("Error ejecutando K-Means: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Metodo que me dice cada cluster que significa, es decir como tenemos 3 cluster
     * entonces este metodo me dice que significa el cluster0, cluester1, cluster2
     * por ejemplo:
     * el cluster0 = Productos con buena rotacion
     * cluster1 = productos con mas o menos rotacion
     */
    public static int[] calcularCentroides(){
        if (kmeans == null) {
            System.out.println("Error: Ejecuta agruparProductos primero.");
            return new int[0];
        }

        Instances centroides = kmeans.getClusterCentroids();

        int idClusterEstrella = -1;
        int idClusterEstancado = -1;

        double diasCluster0 = centroides.instance(0).value(1);
        double diasCluster1 = centroides.instance(1).value(1);

        if (diasCluster0 > diasCluster1) {
            idClusterEstancado = 0;
            idClusterEstrella = 1;
        } else {
            idClusterEstancado = 1;
            idClusterEstrella = 0;
        }

        for (int i = 0; i < 2; i++) {
            System.out.println("Perfil del Clúster " + i +
                    " -> Promedio Días: " + Math.round(centroides.instance(i).value(1)) +
                    " | Promedio Ventas: " + Math.round(centroides.instance(i).value(2)));
        }

        int[] cluster = new int[2];
        cluster[0] = idClusterEstrella;
        cluster[1] = idClusterEstancado;

        System.out.println("\n--- CONCLUSIÓN DEL SISTEMA (2 CLÚSTERES) ---");
        System.out.println("El Clúster de PRODUCTOS ACTIVOS/ESTRELLAS es: " + idClusterEstrella);
        System.out.println("El Clúster de PRODUCTOS ESTANCADOS (Para Promoción) es: " + idClusterEstancado);

        return cluster;
    }
//    public static int[] calcularCentroides() {
//        if (kmeans == null) {
//            System.out.println("Error: No puedes calcular centroides sin antes haber agrupado los productos.");
//            return new int[0];
//        }
//
//        Instances centroides = kmeans.getClusterCentroids();
//
//        int idClusterEstrella = -1;
//        int idClusterEstancado = -1;
//        int idClusterRegular = -1;
//
//        double maxDias = -1;
//
//        // PASO 1: Encontrar el clúster MÁS ESTANCADO (por días sin vender)
//        for (int i = 0; i < centroides.numInstances(); i++) {
//            double promedioDias = centroides.instance(i).value(1); // 1 = diasSinVender
//
//            if (promedioDias > maxDias) {
//                maxDias = promedioDias;
//                idClusterEstancado = i;
//            }
//        }
//
//        // PASO 2: De los dos clústeres restantes, ver cuál tiene más ventas para ser la Estrella
//        double maxVentasRestantes = -1;
//        for (int i = 0; i < centroides.numInstances(); i++) {
//            if (i != idClusterEstancado) { // Ignoramos al estancado
//                double promedioVentas = centroides.instance(i).value(2); // 2 = ventas
//
//                if (promedioVentas > maxVentasRestantes) {
//                    maxVentasRestantes = promedioVentas;
//                    idClusterEstrella = i;
//                }
//            }
//        }
//
//        // PASO 3: El que no es ni Estancado ni Estrella, es el Regular
//        for (int i = 0; i < 2; i++) {
//            if (i != idClusterEstancado && i != idClusterEstrella) {
//                idClusterRegular = i;
//                break;
//            }
//        }
//
//        // Imprimir los perfiles para auditoría
//        for (int i = 0; i < centroides.numInstances(); i++) {
//            System.out.println("Perfil del Clúster " + i + " -> Promedio Días: " + Math.round(centroides.instance(i).value(1)) + " | Promedio Ventas: " + Math.round(centroides.instance(i).value(2)));
//        }
//
//        int[] cluster = new int[3];
//        cluster[0] = idClusterEstrella;
//        cluster[1] = idClusterRegular;
//        cluster[2] = idClusterEstancado;
//
//        System.out.println("\n--- CONCLUSIÓN DEL SISTEMA (CORREGIDA) ---");
//        System.out.println("El Clúster de ESTRELLAS es el número: " + idClusterEstrella); // Debería dar 2
//        System.out.println("El Clúster ESTANCADO (Para Promociones) es el número: " + idClusterEstancado); // Debería dar 1
//        System.out.println("El Clúster REGULAR es el número: " + idClusterRegular); // Debería dar 0
//
//        return cluster;
//    }
}