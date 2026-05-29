package CapaLogicaNegocio.Controlador;

import CapaDatos.Logica_Conexion.PromocionDAO;
import CapaLogicaNegocio.Helpers.HelperIA;
import CapaLogicaNegocio.Logica_Negocio.Promocion;

import java.util.ArrayList;

public class PromocionControlador {

    private PromocionDAO promocionDAO;

    public PromocionControlador(){
        this.promocionDAO = new PromocionDAO();
    }

    public RespuestaControlador<ArrayList<Promocion>> actualizarPromociones(){

        int numClusters = 3; // se define asi porque es la logica del negocio, esto con el fin de poder tener los estrellas, estancados y los regulares

        ArrayList<Promocion> listaPromociones = promocionDAO.getDataset();
        HelperIA.agruparProductos(listaPromociones, numClusters);

        return new RespuestaControlador<>(true, "Lista de promociones", listaPromociones);
    }
}
