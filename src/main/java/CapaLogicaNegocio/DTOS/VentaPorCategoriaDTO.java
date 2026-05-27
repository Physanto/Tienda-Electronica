package CapaLogicaNegocio.DTOS;

import java.util.Date;

/**
 * Record para transportar los datos para el reporte de Ventas por categoria.
 * @param categoria nombre de la categoria
 * @param productosVendidos total de productos vendidos por categoria
 * @param totalVentas total de las ventas realizadas por categoria
 * @param primeraVenta la primera venta que se hizo de esa categoria
 * @param ultimaVenta la ultima venta que se hizo de esa categoria
 */
public record VentaPorCategoriaDTO(String categoria, Long productosVendidos, Double totalVentas, Date primeraVenta, Date ultimaVenta) {}
