package CapaLogicaNegocio.DTOS;

import CapaLogicaNegocio.Logica_Negocio.Venta;

import java.util.Date;

/**
 * clase encargada de encapsular los records que se usan para el dashboard de las ventas
 * Se usa solo para transportar los datos para las validaciones
 *
 * @author Manuel Figueroa (Physanto)
 */
public class VentasDTO{

    public record VentaDTO(String id, String fechaVenta, String totalVenta, Venta.MetodoPago metodoPago, String idCliente) {}

    /**
     * Record para transportar los datos para el reporte de Ventas por categoria.
     * @param categoria nombre de la categoria
     * @param productosVendidos total de productos vendidos por categoria
     * @param totalVentas total de las ventas realizadas por categoria
     * @param primeraVenta la primera venta que se hizo de esa categoria
     * @param ultimaVenta la ultima venta que se hizo de esa categoria
     */
    public record VentaPorCategoriaDTO(String categoria, Long productosVendidos, Double totalVentas, Date primeraVenta, Date ultimaVenta) {}
}

