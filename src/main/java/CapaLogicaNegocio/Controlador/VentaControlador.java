package CapaLogicaNegocio.Controlador;

import CapaDatos.Logica_Conexion.VentaDAO;
import CapaDatos.Logica_Conexion.VentaOnlineCRUD;
import CapaLogicaNegocio.DTOS.VentaDTO;
import CapaLogicaNegocio.Helpers.HelperGestorBD;
import CapaLogicaNegocio.Helpers.HelperValidacion;
import CapaLogicaNegocio.Logica_Negocio.Venta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class VentaControlador {

    private VentaDAO ventaDAO;
    private VentaOnlineCRUD ventaOnlineCRUD;

    public VentaControlador(){
        this.ventaDAO = new VentaDAO();
        this.ventaOnlineCRUD = new VentaOnlineCRUD();
    }


    public RespuestaControlador<Venta> agregarVenta(VentaDTO ventaDTO){

        if(ventaDAO == null) return new RespuestaControlador<>(false, "el objeto de la venta es nulo", null);

        RespuestaControlador<Venta> respuestaControlador = validarCampos(ventaDTO);

        if(!respuestaControlador.exito()) return respuestaControlador;

        Date fechaVenta = null;
        Double totalVenta = 0D;

        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            fechaVenta = simpleDateFormat.parse(ventaDTO.fechaVenta());
            totalVenta = Double.valueOf(ventaDTO.totalVenta());
        }
        catch(Exception e){
            return new RespuestaControlador<>(false, "la fecha, total de venta no tienen un formato valido", null);
        }

        Venta venta = new Venta(ventaDTO.id(), fechaVenta, totalVenta,
                ventaDTO.metodoPago(), ventaDTO.idCliente());

        boolean exito = HelperGestorBD.guardarRegistro(venta, "Venta", ventaDTO.id(),
                () -> ventaDAO.agregar(venta),
                () -> ventaOnlineCRUD.registrarNube(venta)
        );

        return exito
                ? new RespuestaControlador<>(true, "Se ingresa la venta correctamente", null)
                : new RespuestaControlador<>(false, "No se puedo agregar la venta al sistema", null);
    }

    public RespuestaControlador<Venta> buscarVentaId(String id){

       if(id == null) return new RespuestaControlador<>(false, "el objeto no debe ser nulo", null);

       if(id.isEmpty()) return new RespuestaControlador<>(false, "El id no debe estar vacio", null);

       boolean exito = HelperGestorBD.eliminarRegistro(id, "Venta",
               () -> ventaDAO.eliminar(id),
               () -> ventaOnlineCRUD.eliminarNube(id)
       );

       return exito
               ? new RespuestaControlador<>(true, "Se elimina correctamente la venta", null)
               : new RespuestaControlador<>(false, "No se pudo eliminar un cliente", null);

    }

    public RespuestaControlador<Venta> actualizarVenta(VentaDTO ventaDTO){

        if(ventaDAO == null) return new RespuestaControlador<>(false, "el objeto de la venta es nulo", null);

        RespuestaControlador<Venta> respuestaControlador = validarCampos(ventaDTO);

        if(!respuestaControlador.exito()) return respuestaControlador;

        Date fechaVenta = null;
        Double totalVenta = 0D;

        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            fechaVenta = simpleDateFormat.parse(ventaDTO.fechaVenta());
            totalVenta = Double.valueOf(ventaDTO.totalVenta());
        }
        catch(Exception e){
            return new RespuestaControlador<>(false, "la fecha, total de venta no tienen un formato valido", null);
        }

        Venta venta = new Venta(ventaDTO.id(), fechaVenta, totalVenta,
                ventaDTO.metodoPago(), ventaDTO.idCliente());

        boolean exito = HelperGestorBD.actualizarRegistro(venta, "Venta", ventaDTO.id(),
                () -> ventaDAO.actualizar(venta),
                () -> ventaOnlineCRUD.actualizarNube(venta)
        );

        return exito
                ? new RespuestaControlador<>(true, "Se actualiza correctamente la venta", null)
                : new RespuestaControlador<>(false, "No se puede ingresar la venta al sistema", null);
    }

    public RespuestaControlador<Venta> eliminarProducto(String id){

        if(id == null) return new RespuestaControlador<>(false, "El id es un objeto nulo", null);

        if(id.trim().isEmpty()) return new RespuestaControlador<>(false, "El campo id esta vacio", null);

        boolean exito = HelperGestorBD.eliminarRegistro(id, "Producto",
                () -> ventaDAO.eliminar(id),
                () -> ventaOnlineCRUD.eliminarNube(id)
        );

        return exito
                ? new RespuestaControlador<>(true, "Venta eliminada con exito", null)
                : new RespuestaControlador<>(false, "Error al intentar eliminar la venta en el sistema.", null);
    }

    public RespuestaControlador<ArrayList<Venta>> buscarTodos(){

        ArrayList<Venta> listaVentas = new ArrayList<>();
        listaVentas = HelperGestorBD.cargarRegistros(
                () -> ventaDAO.obteners(),
                () -> ventaOnlineCRUD.obtenersNube(Venta.class)
        );

        return !listaVentas.isEmpty()
                ? new RespuestaControlador<>(true, "Imprimiendo todas las ventas", listaVentas)
                : new RespuestaControlador<>(false, "Error en la consulta de todas de las ventas", null);
    }

    public RespuestaControlador<Venta> validarCampos(VentaDTO ventaDTO){

        if(ventaDTO.id() == null || ventaDTO.fechaVenta() == null || ventaDTO.totalVenta() == null
        || ventaDTO.metodoPago() == null || ventaDTO.idCliente() == null){
            return new RespuestaControlador<>(false, "Alguno de los campos son nulos", null);
        }

       if(HelperValidacion.validarUUID(ventaDTO.id()) > 0) return new RespuestaControlador<>(false, "el id debe tener un formato valido (UUID)", null);

       if(ventaDTO.fechaVenta().isEmpty())return new RespuestaControlador<>(false, "La fecha esta vacia", null);

       if(HelperValidacion.validarNumero(ventaDTO.totalVenta()) > 0) return new RespuestaControlador<>(false, "Debe tener solo numeros el total venta", null);

       if(ventaDTO.metodoPago().name().isEmpty()) return new RespuestaControlador<>(false, "el metodo de pago no debe estar vacio", null);

       if(HelperValidacion.validarUUID(ventaDTO.idCliente()) > 0) return new RespuestaControlador<>(false, "El id debe tener un formato valido (UUID)", null);

       return new RespuestaControlador<>(true, "todos los campos estan correctos", null);
    }
}
