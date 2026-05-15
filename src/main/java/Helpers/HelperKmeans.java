package Helpers;

import Logica_Negocio.Promocion;

import java.util.ArrayList;
import java.util.Collections;

public class HelperKmeans {

   private static class Centroide{
           Double stock, dias, ventas;
           Centroide(Double stock, Double dias, Double ventas){
                this.stock = stock;
                this.dias = dias;
                this.ventas = ventas;
           }
   }

    public static ArrayList<Double> calcularMaximosMinimos(ArrayList<Promocion> lista){
        ArrayList<Double> maximosMinimos = new ArrayList<>();

        Promocion stockMax = lista.get(0);
        Promocion stockMin = lista.get(0);
        Promocion diasMax = lista.get(0);
        Promocion diasMin = lista.get(0);
        Promocion totalMax = lista.get(0);
        Promocion totalMin = lista.get(0);

        for(Promocion p : lista){

            if(p.getStockActual() > stockMax.getStockActual()){
                stockMax = p;
            }
            if(p.getStockActual() < stockMin.getStockActual()){
                stockMin = p;
            }
            if(p.getDiasSinVender() > diasMax.getDiasSinVender()){
                diasMax = p;
            }
            if(p.getDiasSinVender() < diasMin.getDiasSinVender()){
                diasMin = p;
            }
            if(p.getTotalVendido() > totalMax.getTotalVendido()){
                totalMax = p;
            }
            if(p.getTotalVendido() < totalMin.getTotalVendido()){
                totalMin = p;
            }
        }
        maximosMinimos.add(stockMax.getStockActual()); //0
        maximosMinimos.add(stockMin.getStockActual()); //1
        maximosMinimos.add(diasMax.getDiasSinVender()); //2
        maximosMinimos.add(diasMin.getDiasSinVender()); //3
        maximosMinimos.add(totalMax.getTotalVendido()); //4
        maximosMinimos.add(totalMin.getTotalVendido()); //5

        System.out.println("MAXIMOS Y MINIMOS\n\n");
        for(Double m : maximosMinimos){
            System.out.println(m);
        }

        return maximosMinimos;
    }
    public static ArrayList<Promocion> normalizarDatos(ArrayList<Promocion> lista){

        ArrayList<Double> maximosMinimos = calcularMaximosMinimos(lista);

        for(Promocion promocion : lista){
            promocion.setStockActual((promocion.getStockActual() - maximosMinimos.get(1)) / (maximosMinimos.get(0) - maximosMinimos.get(1)));

            promocion.setDiasSinVender((promocion.getDiasSinVender() - maximosMinimos.get(3)) / (maximosMinimos.get(2) - maximosMinimos.get(3)));

            promocion.setTotalVendido((promocion.getTotalVendido() - maximosMinimos.get(5)) / (maximosMinimos.get(4) - maximosMinimos.get(5)));
        }

        System.out.println("\n\n\nDATOS NORMALIZADOS\n\n");
        for(Promocion promocion : lista){
            System.out.println(promocion.toString());
        }
        return lista;
    }
//    ¡El cambio a Double está perfecto! Ahora tu matriz matemática tiene la precisión necesaria para que la Distancia Manhattan funcione de verdad. Además, la estructura base del método clusters está muy bien planteada.
//
//    Para integrar la Fase 4 (Actualización) y la Fase 5 (Convergencia) en tu código actual, hay un detalle técnico muy importante sobre Java que debes tener en cuenta: Los record son inmutables. Esto significa que no puedes hacer centroide0.setStock(...). Para "mover" al líder, tendrás que crear un nuevo Centroide y sobreescribir la variable (centroide0 = new Centroide(...)).
//
//    Aquí tienes la guía lógica de cómo debes modificar tu método clusters para completarlo, paso a paso:
//
//            1. Preparar el Bucle Maestro (Convergencia)
//    Justo después de declarar tus tres variables Double distancia... y antes de tu ciclo for, debes envolver todo el proceso en un bucle while.
//
//    Declara una variable boolean huboCambios = true;.
//
//    Abre tu while (huboCambios).
//
//    Lo primero que haces al entrar al while es apagar la bandera: huboCambios = false;. Asumimos que nadie cambiará de equipo hasta que se demuestre lo contrario.
//
//            2. El Detector de Movimiento
//    Dentro de tu primer for (el que ya tienes, donde calculas las distancias):
//
//    Antes de hacer los if para asignar el clúster, guarda el estado actual: int clusterAnterior = promocion.getCluster();.
//
//    Justo después de tus if (cuando ya le pusiste el 0, 1 o 2), comparas: si el clusterAnterior es diferente al nuevo clúster asignado, enciendes la bandera: huboCambios = true;.
//
//            3. Crear los Acumuladores (Actualización)
//    Una vez que termine ese for de asignación (pero todavía adentro del while), tienes que sumar las coordenadas de los equipos.
//
//    Declara 9 variables Double inicializadas en 0.0 (ej: sumaStock0, sumaDias0, sumaVentas0, y lo mismo para el grupo 1 y 2).
//
//    Declara 3 variables int inicializadas en 0 (ej: contador0, contador1, contador2).
//
//            4. Sumar las Coordenadas por Equipo
//    Abre un nuevo ciclo for que recorra nuevamente la listaNormalizada.
//
//            Usando condicionales (if (promocion.getCluster() == 0)), vas sumando el stock, los días y las ventas del producto a los acumuladores correspondientes, y le sumas 1 a su contador.
//
//5. Reposicionar a los Líderes (Recalcular Centroides)
//    Al salir de ese segundo for, ya tienes los totales. Ahora creas los nuevos centroides dividiendo las sumas entre los contadores.
//
//    Evalúa con un if (contador0 > 0) para protegerte de divisiones por cero.
//
//    Si es mayor a cero, reasignas la variable usando el promedio:
//    centroide0 = new Centroide(sumaStock0 / contador0, sumaDias0 / contador0, sumaVentas0 / contador0);
//
//    Haces lo mismo para el centroide1 y el centroide2.
//
//    Al terminar este paso, el código llegará a la llave de cierre del while. Si algún producto cambió de equipo, el ciclo se repetirá midiendo las distancias hacia las nuevas posiciones de centroide0, 1 y 2. Si nadie cambió, el bucle termina y el algoritmo se detiene.
//
//    Con estos 5 pasos integrados, tendrás una Inteligencia Artificial completamente funcional escrita desde cero. ¿Quieres intentar armar el bloque de código con esta lógica y revisamos cómo queda la estructura final del while?

    public static void clusters(ArrayList<Promocion> lista){

       ArrayList<Promocion> listaNormalizada = normalizarDatos(lista);

        Collections.shuffle(listaNormalizada);

        Centroide centroide0 = new Centroide(listaNormalizada.get(0).getStockActual(), listaNormalizada.get(0).getDiasSinVender(), listaNormalizada.get(0).getTotalVendido());
        Centroide centroide1 = new Centroide(listaNormalizada.get(1).getStockActual(), listaNormalizada.get(1).getDiasSinVender(), listaNormalizada.get(1).getTotalVendido());
        Centroide centroide2 = new Centroide(listaNormalizada.get(2).getStockActual(), listaNormalizada.get(2).getDiasSinVender(), listaNormalizada.get(2).getTotalVendido());

        Double distancia1;
        Double distancia2;
        Double distancia3;

        boolean huboCambios = true;

        while(huboCambios){
            huboCambios = false;
            
            for(Promocion promocion : listaNormalizada) {

                int clusterAnterior = promocion.getCluster();

                distancia1 = Math.abs(promocion.getStockActual() - centroide0.stock)
                        + Math.abs(promocion.getDiasSinVender() - centroide0.dias)
                        + Math.abs(promocion.getTotalVendido() - centroide0.ventas);

                distancia2 = Math.abs(promocion.getStockActual() - centroide1.stock)
                        + Math.abs(promocion.getDiasSinVender() - centroide1.dias)
                        + Math.abs(promocion.getTotalVendido() - centroide1.ventas);

                distancia3 = Math.abs(promocion.getStockActual() - centroide2.stock)
                        + Math.abs(promocion.getDiasSinVender() - centroide2.dias)
                        + Math.abs(promocion.getTotalVendido() - centroide2.ventas);


                if (distancia1 < distancia2 && distancia1 < distancia3) {
                    promocion.setCluster(0);
                } else if (distancia2 < distancia1 && distancia2 < distancia3) {
                    promocion.setCluster(1);
                } else {
                    promocion.setCluster(2);
                }

                if (promocion.getCluster() != clusterAnterior) {
                    huboCambios = true;
                }
            }
        }

        System.out.println("\n\n\nCLUSTERS\n\n");
        for(Promocion promocion : listaNormalizada){
            System.out.println(promocion.toString());
        }
    }

    public static void main(String[] args){

        ArrayList<Promocion> lista = new ArrayList<>();
        lista.add(new Promocion("A091", 148.0, 12.0, 335.0, -1));
        lista.add(new Promocion("B204", 927.0, 63.0, 114.0, -1));
        lista.add(new Promocion("C317", 451.0, 29.0, 782.0, -1));
        lista.add(new Promocion("D428", 88.0, 745.0, 16.0, -1));
        lista.add(new Promocion("E539", 612.0, 98.0, 430.0, -1));
        lista.add(new Promocion("F640", 301.0, 555.0, 72.0, -1));
        lista.add(new Promocion("G751", 799.0, 44.0, 920.0, -1));
        lista.add(new Promocion("H862", 15.0, 287.0, 631.0, -1));
        lista.add(new Promocion("I973", 564.0, 701.0, 93.0, -1));
        lista.add(new Promocion("J084", 243.0, 18.0, 856.0, -1));
        lista.add(new Promocion("K195", 999.0, 332.0, 120.0, -1));
        lista.add(new Promocion("L206", 407.0, 84.0, 541.0, -1));
        lista.add(new Promocion("M317", 76.0, 918.0, 267.0, -1));
        lista.add(new Promocion("N428", 650.0, 39.0, 704.0, -1));
        lista.add(new Promocion("O539", 182.0, 473.0, 56.0, -1));
        lista.add(new Promocion("P640", 811.0, 207.0, 995.0, -1));
        lista.add(new Promocion("Q751", 523.0, 11.0, 389.0, -1));
        lista.add(new Promocion("R862", 95.0, 640.0, 248.0, -1));
        lista.add(new Promocion("S973", 734.0, 53.0, 817.0, -1));
        lista.add(new Promocion("T084", 268.0, 124.0, 690.0, -1));
        lista.add(new Promocion("U195", 419.0, 876.0, 31.0, -1));
        lista.add(new Promocion("V206", 557.0, 290.0, 143.0, -1));
        lista.add(new Promocion("W317", 61.0, 722.0, 502.0, -1));
        lista.add(new Promocion("X428", 890.0, 15.0, 333.0, -1));
        lista.add(new Promocion("Y539", 346.0, 480.0, 77.0, -1));
        lista.add(new Promocion("Z640", 214.0, 999.0, 615.0, -1));
        lista.add(new Promocion("A751", 785.0, 54.0, 294.0, -1));
        lista.add(new Promocion("B862", 132.0, 661.0, 845.0, -1));
        lista.add(new Promocion("C973", 908.0, 27.0, 470.0, -1));
        lista.add(new Promocion("D084", 375.0, 510.0, 189.0, -1));
        clusters(lista);
    }
}
