package CapaLogicaNegocio.Controlador;

import CapaDatos.Logica_Conexion.CategoriaDAO;
import CapaDatos.Logica_Conexion.CategoriaOnlineCRUD;
import CapaLogicaNegocio.Helpers.HelperGestorBD;
import CapaLogicaNegocio.Logica_Negocio.Categoria;

import java.util.ArrayList;
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


    public CategoriaControlador(){
        this.categoriaDAO = new CategoriaDAO();
        this.categoriaOnlineCRUD = new CategoriaOnlineCRUD();
    }

    public RespuestaControlador<ArrayList<Categoria>> buscarTodos(){

        ArrayList<Categoria> listaCategorias = HelperGestorBD.cargarRegistros(
                () -> categoriaDAO.obteners(),
                () -> categoriaOnlineCRUD.obtenersNube(Categoria.class)
        );

        return listaCategorias.isEmpty()
                ? new RespuestaControlador<>(true,"Mostrando todas las categorias", listaCategorias)
                : new RespuestaControlador<>(false, "La lista esta vacia o con error", null);

    }
}
