package CapaLogicaNegocio.Controlador;

import CapaDatos.Logica_Conexion.ProductoDAO;
import CapaDatos.Logica_Conexion.ProductoOnlineCRUD;
import CapaLogicaNegocio.DTOS.ProductoDTO;
import CapaLogicaNegocio.Helpers.HelperValidacion;
import CapaLogicaNegocio.Logica_Negocio.Cliente;
import CapaLogicaNegocio.Logica_Negocio.Producto;

public class ProductoControlador {

    private ProductoDAO productoDAO;
    private ProductoOnlineCRUD productoOnlineCRUD;

    public ProductoControlador(){
        this. productoDAO = new ProductoDAO();
        this.productoOnlineCRUD = new ProductoOnlineCRUD();
    }

    public RespuestaControlador<Producto> agregarProducto(ProductoDTO productoDTO){

        if(productoDTO == null) return new RespuestaControlador<>(false, "objeto nulo", null);

        RespuestaControlador<Producto> respuestaControlador = validarCampos(productoDTO);

        if(!respuestaControlador.exito()) return respuestaControlador;

        return null;

    }

    private RespuestaControlador<Producto> validarCampos(ProductoDTO productoDTO){

        if(productoDTO.id() == null || productoDTO.nombre() == null || productoDTO.codigo() == null
        || productoDTO.marca() == null || productoDTO.serie() == null || productoDTO.fechaVencimiento() == null
        || productoDTO.stock() == null || productoDTO.idCategoria() == null || productoDTO.precioActual() == null)
            return new RespuestaControlador<>(false, "Algun campo del objeto es nulo", null);

        if(productoDTO.id().isEmpty()) return new RespuestaControlador<>(false, "El id esta vacio", null);
        if(HelperValidacion.validarUUID(productoDTO.id()) > 0) return new RespuestaControlador<>(false, "El id no tiene el formato (UUID)", null);

        if(HelperValidacion.validarTodoNombres(productoDTO.nombre()) > 0) return new RespuestaControlador<>(false, "El nombre debe tener solo letras", null);

        if(HelperValidacion.validarTodoNombres(productoDTO.codigo()) > 0) return new RespuestaControlador<>(false, "codigo con formato incorrecto", null);

        if(HelperValidacion.validarTodoNombres(productoDTO.marca()) > 0) return new RespuestaControlador<>(false, "marcao con formato incorrecto", null);

        if(HelperValidacion.validarTodoNombres(productoDTO.serie()) > 0) return new RespuestaControlador<>(false, "serie con formato incorrecto", null);

        if(productoDTO.fechaVencimiento().isEmpty()) return new RespuestaControlador<>(false, "la fecha no debe estar vacia", null);

        if(HelperValidacion.ValidarTodoNumero(productoDTO.stock()) > 0) return new RespuestaControlador<>(false, "El stock solo debe contener numeros", null);

        if(HelperValidacion.ValidarTodoNumero(productoDTO.precioActual()) > 0) return new RespuestaControlador<>(false, "El precio actual solo debe contener numeros", null);

        if(productoDTO.idCategoria().isEmpty()) return new RespuestaControlador<>(false, "El idCategoria no debe estar vacio", null);
        if(HelperValidacion.validarUUID(productoDTO.idCategoria()) > 0) return new RespuestaControlador<>(false, "El idCategoria debe tener el formato valido (UUID)", null);

        return null;
    }
}
