package CapaLogicaNegocio.Controlador;

import CapaDatos.Logica_Conexion.CategoriaDAO;
import CapaDatos.Logica_Conexion.CategoriaOnlineCRUD;
import CapaLogicaNegocio.Helpers.HelperGestorBD;
import CapaLogicaNegocio.Logica_Negocio.Categoria;
import java_cup.lalr_state;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Clase que implementa las buenas practicas del MVC para disenar un controlador
 * el cual es capaz de hacer la comunicacion efectiva entre la vista y el modelo
 * Este es para el movimiento general de las promociones del sistema
 *
 * @author Manuel Figueroa (Physanto)
 */
public class CategoriaControlador {

        private CategoriaDAO categoriaDAO;
        private CategoriaOnlineCRUD categoriaOnlineCRUD;

        public CategoriaControlador() {
                this.categoriaDAO = new CategoriaDAO();
                this.categoriaOnlineCRUD = new CategoriaOnlineCRUD();
        }

        public RespuestaControlador<Boolean> crearCategorias() {

                UUID u1 = UUID.randomUUID();
                UUID u2 = UUID.randomUUID();
                UUID u3 = UUID.randomUUID();
                UUID u4 = UUID.randomUUID();
                UUID u5 = UUID.randomUUID();

                Categoria categoria = new Categoria(u1.toString(), "Telefono");
                Categoria categoria1 = new Categoria(u2.toString(), "Computador");
                Categoria categoria2 = new Categoria(u3.toString(), "Telefono");
                Categoria categoria3 = new Categoria(u4.toString(), "Auriculares");
                Categoria categoria4 = new Categoria(u5.toString(), "Consola");

                HelperGestorBD.guardarRegistro(categoria, "Categoria", categoria.getId(),
                                () -> categoriaDAO.agregar(categoria),
                                () -> categoriaOnlineCRUD.registrarNube(categoria));

                HelperGestorBD.guardarRegistro(categoria1, "Categoria", categoria1.getId(),
                                () -> categoriaDAO.agregar(categoria1),
                                () -> categoriaOnlineCRUD.registrarNube(categoria1));

                HelperGestorBD.guardarRegistro(categoria2, "Categoria", categoria2.getId(),
                                () -> categoriaDAO.agregar(categoria2),
                                () -> categoriaOnlineCRUD.registrarNube(categoria2));

                HelperGestorBD.guardarRegistro(categoria3, "Categoria", categoria3.getId(),
                                () -> categoriaDAO.agregar(categoria3),
                                () -> categoriaOnlineCRUD.registrarNube(categoria3));

                HelperGestorBD.guardarRegistro(categoria4, "Categoria", categoria4.getId(),
                                () -> categoriaDAO.agregar(categoria4),
                                () -> categoriaOnlineCRUD.registrarNube(categoria4));

                return new RespuestaControlador<>(true, "categorias creadas manualmente", null);
        }

        public RespuestaControlador<ArrayList<Categoria>> buscarTodos() {

                ArrayList<Categoria> listaCategorias = HelperGestorBD.cargarRegistros(
                                () -> categoriaDAO.obteners(),
                                () -> categoriaOnlineCRUD.obtenersNube(Categoria.class));

                return !listaCategorias.isEmpty() && listaCategorias != null
                                ? new RespuestaControlador<>(true, "Mostrando todas las categorias", listaCategorias)
                                : new RespuestaControlador<>(false, "La lista esta vacia o con error", listaCategorias);

        }
}
