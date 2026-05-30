package CapaLogicaNegocio.Controlador;

import CapaDatos.Logica_Conexion.PromocionDAO;
import CapaLogicaNegocio.DTOS.PromocionAplicadaDTO;
import CapaLogicaNegocio.Helpers.HelperIA;
import CapaLogicaNegocio.Logica_Negocio.Promocion;

import java.util.ArrayList;

public class PromocionControlador {

    private PromocionDAO promocionDAO;

    public PromocionControlador(){
        this.promocionDAO = new PromocionDAO();
    }

    /**
     * Metodo que devuelve una lista de productos que se pueden aplicarle una promocion arbitraria
     * @return una lista con los productos, retorna null si la lista esta vacia o ha habido un problema
     */
    public RespuestaControlador<ArrayList<PromocionAplicadaDTO>> obtenerProductosPromocion(){

        ArrayList<Promocion> listaPromociones = HelperIA.agruparProductos(promocionDAO.getDataset());

        ArrayList<PromocionAplicadaDTO> lista = promocionDAO.datosPromociones();
        ArrayList<PromocionAplicadaDTO> listaProductos = new ArrayList<>();

        for (PromocionAplicadaDTO promocionAplicadaDTO : lista) {
            for (Promocion promocion : listaPromociones) {

                if (promocionAplicadaDTO.id().equals(promocion.getId()) && promocion.getCluster() == 0) {
                    listaProductos.add(promocionAplicadaDTO);
                }
            }
        }
//        Peromociones
//                - Retornar en las promociones activas los campos: Nombre promoción, Producto, Descuento, Precio original, Precio con descuento.
//                - Retornar en las promociones personalizadas por cliente los campos: Nombre, Apellido, Cédula, Total Compras, Días desde la última sesión.

        return !listaProductos.isEmpty()
                ? new RespuestaControlador<>(true, "Lista de productos para la promocion", listaProductos)
                : new RespuestaControlador<>(false, "No hay productos en promocion", null);
    }

    /**
     * aplica la promocion al producto especifico
     * @param idProducto es el id del producto al cual se le va aplicar la promocion
     * @param descuento es el descuento en porcentaje a aplicar al producto seleccionado
     * @return un record en el cual contiene un campo .exito() de true si aplico correctamente la promocion, un campo de mensaje
     */
    public RespuestaControlador<Boolean> aplicarPromocionProducto(String idProducto, String descuento){

        if(idProducto == null || idProducto.isEmpty()) return new RespuestaControlador<>(false, "id del producto esta vacio o en null", null);

        //if(descuento)

       return new RespuestaControlador<>(false, "", null);
    }

    public RespuestaControlador<Boolean> validarCampos(){

        return new RespuestaControlador<>(false, "nfdaf", null);

    }
}
