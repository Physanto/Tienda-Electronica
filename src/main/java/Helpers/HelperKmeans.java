package Helpers;

import Logica_Negocio.Promocion;

import java.util.ArrayList;
import java.util.Collections;

public class HelperKmeans {

   private record Centroide(Long stock, Long dias, Long ventas) {}

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
        maximosMinimos.add(Double.valueOf(stockMax.getStockActual())); //0
        maximosMinimos.add(Double.valueOf(stockMin.getStockActual())); //1
        maximosMinimos.add(Double.valueOf(diasMax.getDiasSinVender())); //2
        maximosMinimos.add(Double.valueOf(diasMin.getDiasSinVender())); //3
        maximosMinimos.add(Double.valueOf(totalMax.getTotalVendido())); //4
        maximosMinimos.add(Double.valueOf(totalMin.getTotalVendido())); //5

        System.out.println("MAXIMOS Y MINIMOS\n\n");
        for(double m : maximosMinimos){
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

    public static void clusters(ArrayList<Promocion> lista){

       ArrayList<Promocion> listaNormalizada = normalizarDatos(lista);

        Collections.shuffle(listaNormalizada);

        Centroide centroide0 = new Centroide(listaNormalizada.get(0).getStockActual(), listaNormalizada.get(0).getDiasSinVender(), listaNormalizada.get(0).getTotalVendido());
        Centroide centroide1 = new Centroide(listaNormalizada.get(1).getStockActual(), listaNormalizada.get(1).getDiasSinVender(), listaNormalizada.get(1).getTotalVendido());
        Centroide centroide2 = new Centroide(listaNormalizada.get(2).getStockActual(), listaNormalizada.get(2).getDiasSinVender(), listaNormalizada.get(2).getTotalVendido());

        Long distancia1;
        Long distancia2;
        Long distancia3;

        for(Promocion promocion : listaNormalizada){

            distancia1 = Math.abs(promocion.getStockActual() - centroide0.stock)
                    + Math.abs(promocion.getDiasSinVender() - centroide0.dias)
                    + Math.abs(promocion.getTotalVendido() - centroide0.ventas);

            distancia2 = Math.abs(promocion.getStockActual() - centroide1.stock)
                    + Math.abs(promocion.getDiasSinVender() - centroide1.dias)
                    + Math.abs(promocion.getTotalVendido() - centroide1.ventas);

            distancia3 = Math.abs(promocion.getStockActual() - centroide2.stock)
                    + Math.abs(promocion.getDiasSinVender() - centroide2.dias)
                    + Math.abs(promocion.getTotalVendido() - centroide2.ventas);


            if(distancia1 < distancia2 && distancia1 < distancia3){
                promocion.setCluster(0);
            }
            else if(distancia2 < distancia1 && distancia2 < distancia3){
                promocion.setCluster(1);
            }
            else{
                promocion.setCluster(2);
            }
        }

        System.out.println("\n\n\nCLUSTERS\n\n");
        for(Promocion promocion : listaNormalizada){
            System.out.println(promocion.toString());
        }
    }

    public static void prueba(){

    }

    public static void main(String[] args){

        ArrayList<Promocion> lista = new ArrayList<>();

        lista.add(new Promocion("A001", 125L, 14L, 320L, -1));
        lista.add(new Promocion("A002", 78L, 7L, 150L, -1));
        lista.add(new Promocion("A003", 310L, 21L, 540L, -1));
        lista.add(new Promocion("A004", 45L, 3L, 89L, -1));
        lista.add(new Promocion("A005", 200L, 11L, 410L, -1));
        lista.add(new Promocion("A006", 167L, 18L, 275L, -1));
        lista.add(new Promocion("A007", 92L, 5L, 133L, -1));
        lista.add(new Promocion("A008", 400L, 30L, 890L, -1));
        lista.add(new Promocion("A009", 56L, 9L, 120L, -1));
        lista.add(new Promocion("A010", 245L, 16L, 470L, -1));

        lista.add(new Promocion("A071", 150L, 11L, 325L, -1));
        lista.add(new Promocion("A072", 86L, 6L, 165L, -1));
        lista.add(new Promocion("A073", 355L, 28L, 730L, -1));
        lista.add(new Promocion("A074", 52L, 2L, 76L, -1));
        lista.add(new Promocion("A075", 230L, 16L, 515L, -1));
        lista.add(new Promocion("A076", 192L, 14L, 390L, -1));
        lista.add(new Promocion("A077", 108L, 8L, 205L, -1));
        lista.add(new Promocion("A078", 440L, 36L, 980L, -1));
        lista.add(new Promocion("A079", 71L, 4L, 108L, -1));
        lista.add(new Promocion("A080", 265L, 19L, 545L, -1));

        lista.add(new Promocion("A081", 136L, 10L, 298L, -1));
        lista.add(new Promocion("A082", 79L, 5L, 149L, -1));
        lista.add(new Promocion("A083", 320L, 24L, 650L, -1));
        lista.add(new Promocion("A084", 55L, 3L, 82L, -1));
        lista.add(new Promocion("A085", 212L, 15L, 475L, -1));
        lista.add(new Promocion("A086", 181L, 13L, 370L, -1));
        lista.add(new Promocion("A087", 112L, 7L, 198L, -1));
        lista.add(new Promocion("A088", 405L, 31L, 910L, -1));
        lista.add(new Promocion("A089", 66L, 2L, 94L, -1));
        lista.add(new Promocion("A090", 275L, 20L, 575L, -1));

        lista.add(new Promocion("A091", 148L, 12L, 335L, -1));
        lista.add(new Promocion("A092", 88L, 6L, 172L, -1));
        lista.add(new Promocion("A093", 360L, 29L, 760L, -1));
        lista.add(new Promocion("A094", 58L, 3L, 85L, -1));
        lista.add(new Promocion("A095", 240L, 17L, 530L, -1));
        lista.add(new Promocion("A096", 195L, 14L, 400L, -1));
        lista.add(new Promocion("A097", 120L, 9L, 220L, -1));
        lista.add(new Promocion("A098", 450L, 38L, 1000L, -1));
        lista.add(new Promocion("A099", 73L, 4L, 112L, -1));
        lista.add(new Promocion("A100", 290L, 22L, 610L, -1));

        clusters(lista);
    }
}
