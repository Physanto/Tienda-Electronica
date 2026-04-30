/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Logica_Cliente;

import Helpers.HelperImpresion;
import Helpers.HelperRegistro;
import Helpers.HelperTiempo;
import Helpers.HelperValidacion;
import Logica_Conexion.Conexion;
import Logica_Conexion.PersonaProvider;
import Logica_Negocio.Producto;
import Logica_Negocio.Persona;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author Santiago Lopez
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Conexion.Conectar();

        int opc = 0, num_pcs = 0;

        Persona objper;
        Producto objprodu;
        String producto = "";
        ArrayList<Producto> lsproduglobal = null;
        ArrayList<Persona> lspersonasnube = new ArrayList<>();
        ArrayList<Persona> lspersonalocal = new ArrayList<>();
        Scanner scan = new Scanner(System.in);
        String nombre, apellido, direccion, cedula, marca, serial, nom_produ, nom_img;
        int rta, conteo;

        do {
            System.out.println("1. Registrar Persona");
            System.out.println("2. Visualizar Personas Nube");
            System.out.println("3. Visualizar Persona Local");
            System.out.println("4. Consultar Persona Nube");
            System.out.println("5. Salir");

            do {
                try {

                    System.out.println("Digite una opcion valida");
                    opc = scan.nextInt();

                } catch (InputMismatchException e) {
                    System.out.println("Error");
                }
                scan.nextLine();
            } while (opc <= 0);
  
            switch (opc) {
                case 1:
                    //Empieza Nombre
                    System.out.println("Digite el nombre de la persona");
                    nombre = scan.nextLine();
                    rta = HelperValidacion.ValidarVacio(nombre);

                    while (rta > 0) {
                        System.out.println("Digite el nombre de la persona");
                        nombre = scan.nextLine();
                        rta = HelperValidacion.ValidarVacio(nombre);
                    }

                    conteo = HelperValidacion.ValidarTodo(nombre);

                    while (conteo != 0) {
                        System.out.println("Digite el nombre de la persona");
                        nombre = scan.nextLine();
                        conteo = HelperValidacion.ValidarTodo(nombre);

                    }
                    //Termina Nombre

                    //Empieza apellido
                    System.out.println("Digite el apellido de la persona");
                    apellido = scan.nextLine();

                    rta = HelperValidacion.ValidarVacio(apellido);

                    while (rta > 0) {
                        System.out.println("Digite el apellido de la persona");
                        apellido = scan.nextLine();
                        rta = HelperValidacion.ValidarVacio(apellido);
                    }
                    conteo = HelperValidacion.ValidarTodo(apellido);

                    while (conteo != 0) {
                        System.out.println("Digite el apellido de la persona");
                        apellido = scan.nextLine();
                        conteo = HelperValidacion.ValidarTodo(apellido);

                    }
                    //Termina Apellido

                    //Empeiza Direccion
                    System.out.println("Digite la direccion de la persona");
                    direccion = scan.nextLine();

                    rta = HelperValidacion.ValidarVacio(direccion);

                    while (rta > 0) {
                        System.out.println("Digite la direccion de la persona");
                        direccion = scan.nextLine();
                        rta = HelperValidacion.ValidarVacio(direccion);
                    }
                    conteo = HelperValidacion.ValidarTodoDireccion(direccion);

                    while (conteo != 0) {
                        System.out.println("Digite la direccion de la persona");
                        direccion = scan.nextLine();
                        conteo = HelperValidacion.ValidarTodoDireccion(direccion);

                    }
                    //Termina Direccion

                    //Empieza cedula
                    System.out.println("Digite la cedula de la persona");
                    cedula = scan.nextLine();

                    rta = HelperValidacion.ValidarVacio(cedula);

                    while (rta > 0) {
                        System.out.println("Digite la cedula de la persona");
                        cedula = scan.nextLine();
                        rta = HelperValidacion.ValidarVacio(cedula);
                    }
                    conteo = HelperValidacion.ValidarTodoLetra(cedula);

                    while (conteo != 0) {
                        System.out.println("Digite la cedula de la persona");
                        cedula = scan.nextLine();
                        conteo = HelperValidacion.ValidarTodoLetra(cedula);
                    }
                    //Termica cedula

                    //Nombre Imagen
                    System.out.println("Digite el nombre de la imagen de la persona");
                    nom_img = scan.nextLine();
                    rta = HelperValidacion.ValidarVacio(nom_img);

                    while (rta > 0) {
                        System.out.println("Digite el nombre de la imagen de la persona");
                        nom_img = scan.nextLine();
                        rta = HelperValidacion.ValidarVacio(nom_img);
                    }
                    conteo = HelperValidacion.ValidarTodoSerial(nom_img);

                    while (conteo != 0) {
                        System.out.println("Digite el nombre de la imagen de la persona");
                        nom_img = scan.nextLine();
                        conteo = HelperValidacion.ValidarTodoSerial(nom_img);

                    }

                    //Termina Nombre Imagen
                    ArrayList<Producto> lsprodulocal = new ArrayList<>();

                    do {
                        try {

                            System.out.println("Digite el numero de productos de la persona");
                            num_pcs = scan.nextInt();
                        } catch (InputMismatchException e) {
                            System.out.println("Error");
                        }
                        scan.nextLine();
                    } while (num_pcs <= 0);

                    int vc = HelperValidacion.ValidarCantidadRango(num_pcs);

                    if (vc == 1) {
                        for (int i = 0; i < num_pcs; i++) {
                            //Empieza Nombre producto
                            System.out.println("Digite el nombre del producto" + "\t" + (i + 1));
                            nom_produ = scan.nextLine();
                            rta = HelperValidacion.ValidarVacio(nom_produ);

                            while (rta > 0) {
                                System.out.println("Digite el nombre del producto" + "\t" + (i + 1));
                                nom_produ = scan.nextLine();
                                rta = HelperValidacion.ValidarVacio(nom_produ);
                            }
                            conteo = HelperValidacion.ValidarTodo(nom_produ);

                            while (conteo != 0) {
                                System.out.println("Digite el nombre del producto" + "\t" + (i + 1));
                                nom_produ = scan.nextLine();
                                conteo = HelperValidacion.ValidarTodo(nom_produ);

                            }
                            //Termina nombre producto
                            //Empieza marca
                            System.out.println("Digite la marca del producto" + "\t" + (i + 1));
                            marca = scan.nextLine();
                            rta = HelperValidacion.ValidarVacio(marca);

                            while (rta > 0) {
                                System.out.println("Digite la marca del producto" + "\t" + (i + 1));
                                marca = scan.nextLine();
                                rta = HelperValidacion.ValidarVacio(marca);
                            }
                            conteo = HelperValidacion.ValidarTodo(marca);

                            while (conteo != 0) {
                                System.out.println("Digite la marca del producto" + "\t" + (i + 1));
                                marca = scan.nextLine();
                                conteo = HelperValidacion.ValidarTodo(marca);

                            }
                            //Termina marca
                            //Empieza Serial
                            System.out.println("Digite el serial del producto" + "\t" + (i + 1));
                            serial = scan.nextLine();
                            rta = HelperValidacion.ValidarVacio(serial);

                            while (rta > 0) {
                                System.out.println("Digite el serial del producto" + "\t" + (i + 1));
                                serial = scan.nextLine();
                                rta = HelperValidacion.ValidarVacio(serial);
                            }
                            conteo = HelperValidacion.ValidarTodoSerial(serial);

                            while (conteo != 0) {
                                System.out.println("Digite el serial del producto" + "\t" + (i + 1));
                                serial = scan.nextLine();
                                conteo = HelperValidacion.ValidarTodoSerial(serial);

                            }
                            //Termina Serial

                            objprodu = new Producto(nom_produ, marca, serial);
                            lsprodulocal.add(objprodu);

                        }
                        lsproduglobal = lsprodulocal;
                        lsprodulocal = null;
                    } else {
                        System.out.println("Cantidad Invalida");
                        break;
                    }

                    for (int i = 0; i < lsproduglobal.size(); i++) {
                        producto += lsproduglobal.get(i).getNombre() + "," + lsproduglobal.get(i).getMarca() + "," + lsproduglobal.get(i).getSerial() + ";";

                    }

                    int id = (int) (Math.random() * 100000);

                    objper = new Persona(String.valueOf(id), nombre, apellido, cedula, direccion, producto, nom_img);
                    objper.setProductos(lsproduglobal);
                    lspersonalocal.add(objper);
                    HelperRegistro.RegistrarPersonaNubeI(objper, id, producto);
                    producto = "";
                    break;

                case 2:

                    lspersonasnube = PersonaProvider.CargarInfoPersona();
                    HelperImpresion.ImprimirInfoPersonaNube(lspersonasnube);

                    break;

                case 3:
                    HelperImpresion.ImprimirInfoLocal(lspersonalocal);

                    break;

                case 4:

                    String codigo = "";
                    lspersonasnube = PersonaProvider.CargarInfoPersona();
                    System.out.println("Digite el uid a buscar");
                    codigo = scan.nextLine();
                    long inicio = System.currentTimeMillis();
                    HelperImpresion.BuscarPersonaNube(lspersonasnube, codigo);
                    long fin = System.currentTimeMillis();
                    HelperTiempo.RetornarTiempo(fin, inicio);
                    break;

            }
        } while (opc != 5);
    }

}
