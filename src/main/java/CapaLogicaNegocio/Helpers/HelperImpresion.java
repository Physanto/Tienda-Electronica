package CapaLogicaNegocio.Helpers;

import CapaLogicaNegocio.Logica_Negocio.Cliente;
import CapaLogicaNegocio.Logica_Negocio.Producto;
import java.util.ArrayList;

/**
 * Clase encargada de la Impresion de datos para el usuario final
 * @author Santiago Lopez
 */
public class HelperImpresion {

    /** Imprime la informacion de las personas y los productos asociados a cada una de ellas
     *
     * @param lspersonasnube es la lista de personas que se quiere imprimir
     */

    public static void ImprimirInfoPersonaNube(ArrayList<Cliente> lspersonasnube) {
        String[] parts = null, parts2 = null;
        Producto objprodu;
        ArrayList<Producto> lsnube = new ArrayList<>();

        for(int i = 0; i < lspersonasnube.size(); i++) {
            System.out.println("Cliente" + "\t" + (i + 1) + "\n"
                    + "El idCliente de la persona es:" + lspersonasnube.get(i).getId() + "\n"
                    + "El nombre de la persona es" + "\t" + lspersonasnube.get(i).getNombre() + "\n"
                    + "El Apellido de la persona es" + "\t" + lspersonasnube.get(i).getApellido() + "\n"
                    + "La cedula de la persona es" + "\t" + lspersonasnube.get(i).getCedula() + "\n"
                    + "La direccion de la persona es" + "\t" + lspersonasnube.get(i).getDireccion() + "\n"
            );
//            parts = lspersonasnube.get(i).getProducto().split(";");
//
//            for (int j = 0; j < parts.length; j++) {
//
//                parts2 = parts[j].split(",");
//                objprodu = new Producto(parts2[0], parts2[1], parts2[2]);
//                lsnube.add(objprodu);
//            }

            for (int k = 0; k < lsnube.size(); k++) {
                System.out.println("Producto:" + "\t" + (k + 1));
                System.out.println("Nombre" + "\t" + lsnube.get(k).getNombre());
                System.out.println("Marca" + "\t" + lsnube.get(k).getMarca());
                System.out.println("Serial" + "\t" + lsnube.get(k).getSerie() + "\n");
            }

            lsnube.clear();
            parts = null;
            parts2 = null;

        }

    }
    // se podria hacer un metodo que extraiga la informacion de forma mas generica para que sirva tanto para global como para local
    /**
     * Imprime las personas de la base de datos local con sus productos asociados
     * @param lspersonalocal es la lista de personas de la base de datos local
     */
    public static void ImprimirInfoLocal(ArrayList<Cliente> lspersonalocal) {
        if (!lspersonalocal.isEmpty()) {
            for (int i = 0; i < lspersonalocal.size(); i++) {
                System.out.println("Cliente" + "\t" + (i + 1) + "\n"
                        + "El idCliente de la persona es" + "\t" + lspersonalocal.get(i).getId() + "\n"
                        + "El nombre de la persona es" + "\t" + lspersonalocal.get(i).getNombre() + "\n"
                        + "El Apellido de la persona es" + "\t" + lspersonalocal.get(i).getApellido() + "\n"
                        + "La cedula de la persona es" + "\t" + lspersonalocal.get(i).getCedula() + "\n"
                        + "La direccion de la persona es" + "\t" + lspersonalocal.get(i).getDireccion() + "\n"
                );
//                for (int j = 0; j < lspersonalocal.get(i).getProductos().size(); j++) {
//                    System.out.println("Producto:" + "\t" + (j + 1));
//                    System.out.println("Nombre" + "\t" + lspersonalocal.get(i).getProductos().get(j).getNombre());
//                    System.out.println("Marca" + "\t" + lspersonalocal.get(i).getProductos().get(j).getMarca());
//                    System.out.println("Serial" + "\t" + lspersonalocal.get(i).getProductos().get(j).getSerial() + "\n");
//                }
            }
        }
        else {
            System.out.println("No se ha registrado ningun usuario local, por favor registre una persona");
        }
    }

    public static void BuscarPersonaNube(ArrayList<Cliente> lspersonasnube, String codigo) {
        String[] partes = null, partes2 = null;
        int bandera = 0;
        Producto objprodu;
        ArrayList<Producto> lsnube = new ArrayList<>();

        for (int i = 0; i < lspersonasnube.size(); i++) {
            if (codigo.equals(lspersonasnube.get(i).getId())) {
                bandera = 1;
                System.out.println("Cliente" + "\t" + (i + 1) + "\n"
                        + "El idCliente de la persona es:" + lspersonasnube.get(i).getId() + "\n"
                        + "El nombre de la persona es" + "\t" + lspersonasnube.get(i).getNombre() + "\n"
                        + "El Apellido de la persona es" + "\t" + lspersonasnube.get(i).getApellido() + "\n"
                        + "La cedula de la persona es" + "\t" + lspersonasnube.get(i).getCedula() + "\n"
                        + "La direccion de la persona es" + "\t" + lspersonasnube.get(i).getDireccion() + "\n"
                );
//                partes = lspersonasnube.get(i).getProducto().split(";");
//
//                for (int j = 0; j < partes.length; j++) {
//
//                    partes2 = partes[j].split(",");
//                    objprodu = new Producto(partes2[0], partes2[1], partes2[2]);
//                    lsnube.add(objprodu);
//                }

                for (int k = 0; k < lsnube.size(); k++) {
                    System.out.println("Producto:" + "\t" + (k + 1));
                    System.out.println("Nombre" + "\t" + lsnube.get(k).getNombre());
                    System.out.println("Marca" + "\t" + lsnube.get(k).getMarca());
                    System.out.println("Serial" + "\t" + lsnube.get(k).getSerie() + "\n");
                }

                lsnube.clear();
                partes = null;
                partes2 = null;

            }

        }
        if (bandera == 0) {
            System.out.println("El uid buscado no se encuenttra en la lista");
        }
    }

    public static String ImprimirInfoInterfaz(ArrayList<Cliente> lspersonasnube) {
        String info = "";
        String[] parts = null,
                parts2 = null;
        Producto objprodu;
        ArrayList<Producto> lsnube = new ArrayList<>();

        for (int i = 0; i < lspersonasnube.size(); i++) {

            info += "Cliente" + "\t" + (i + 1) + "\n"
                    + "El idCliente de la persona es:" + lspersonasnube.get(i).getId() + "\n"
                    + "El nombre de la persona es" + "\t" + lspersonasnube.get(i).getNombre() + "\n"
                    + "El Apellido de la persona es" + "\t" + lspersonasnube.get(i).getApellido() + "\n"
                    + "La cedula de la persona es" + "\t" + lspersonasnube.get(i).getCedula() + "\n"
                    + "La direccion de la persona es" + "\t" + lspersonasnube.get(i).getDireccion() + "\n"
                    + "\n";

//            parts = lspersonasnube.get(i).getProducto().split(";");
//
//            for (int j = 0; j < parts.length; j++) {
//
//                parts2 = parts[j].split(",");
//                objprodu = new Producto(parts2[0], parts2[1], parts2[2]);
//                lsnube.add(objprodu);
//            }

            for (int k = 0; k < lsnube.size(); k++) {
                info += "Producto:" + "\t" + (k + 1) + "\n"
                        + "Nombre" + "\t" + lsnube.get(k).getNombre() + "\n"
                        + "Marca" + "\t" + lsnube.get(k).getMarca() + "\n"
                        + "Serial" + "\t" + lsnube.get(k).getSerie() + "\n"
                        + "\n";
            }

            lsnube.clear();
            parts = null;
            parts2 = null;

        }

        return info;
    }

    public static String ImprimirInfoInterfazNube(ArrayList<Cliente> lspersonasnube, String codigo) {
        String info = "";
        String[] parts = null,
                parts2 = null;
        Producto objprodu;
        ArrayList<Producto> lsnube = new ArrayList<>();
        int bandera = 0;

        for (int i = 0; i < lspersonasnube.size(); i++) {
            if (codigo.equals(lspersonasnube.get(i).getId())) {
                bandera = 1;
                info += "Cliente" + "\t" + (i + 1) + "\n"
                        + "El idCliente de la persona es:" + lspersonasnube.get(i).getId() + "\n"
                        + "El nombre de la persona es" + "\t" + lspersonasnube.get(i).getNombre() + "\n"
                        + "El Apellido de la persona es" + "\t" + lspersonasnube.get(i).getApellido() + "\n"
                        + "La cedula de la persona es" + "\t" + lspersonasnube.get(i).getCedula() + "\n"
                        + "La direccion de la persona es" + "\t" + lspersonasnube.get(i).getDireccion() + "\n"
                        + "\n";

//                parts = lspersonasnube.get(i).getProducto().split(";");
//
//                for (int j = 0; j < parts.length; j++) {
//
//                    parts2 = parts[j].split(",");
//                    objprodu = new Producto(parts2[0], parts2[1], parts2[2]);
//                    lsnube.add(objprodu);
//                }

                for (int k = 0; k < lsnube.size(); k++) {
                    info += "Producto:" + "\t" + (k + 1) + "\n"
                            + "Nombre" + "\t" + lsnube.get(k).getNombre() + "\n"
                            + "Marca" + "\t" + lsnube.get(k).getMarca() + "\n"
                            + "Serial" + "\t" + lsnube.get(k).getSerie() + "\n"
                            + "\n";
                }

                lsnube.clear();
                parts = null;
                parts2 = null;

            }

        }
        if (bandera == 0) {
            System.out.println("El uid buscado no se encuenttra en la lista");
        }
        return info;
    }
    
    public static String ImprimirInfoInterfazLocal(Cliente per) {
        String info = "";
        String[] parts = null,
                parts2 = null;
        Producto objprodu;
        ArrayList<Producto> lsnube = new ArrayList<>();
        int bandera = 0;

                info += "Cliente"  + "\n"
                        + "El idCliente de la persona es:" + per.getId() + "\n"
                        + "El nombre de la persona es" + "\t" + per.getNombre() + "\n"
                        + "El Apellido de la persona es" + "\t" + per.getApellido() + "\n"
                        + "La cedula de la persona es" + "\t" + per.getCedula() + "\n"
                        + "La direccion de la persona es" + "\t" + per.getDireccion() + "\n"
                        + "\n";

//                parts = per.getProducto().split(";");
//
//                for (int j = 0; j < parts.length; j++) {
//
//                    parts2 = parts[j].split(",");
//                    objprodu = new Producto(parts2[0], parts2[1], parts2[2]);
//                    lsnube.add(objprodu);
//                }

                for (int k = 0; k < lsnube.size(); k++) {
                    info += "Producto:" + "\t" + (k + 1) + "\n"
                            + "Nombre" + "\t" + lsnube.get(k).getNombre() + "\n"
                            + "Marca" + "\t" + lsnube.get(k).getMarca() + "\n"
                            + "Serial" + "\t" + lsnube.get(k).getSerie() + "\n"
                            + "\n";
                }

                lsnube.clear();
                parts = null;
                parts2 = null;
                
         return info;

            }

        
        
       

    }
    
        

