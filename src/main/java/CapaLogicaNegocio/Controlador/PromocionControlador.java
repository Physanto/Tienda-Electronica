package CapaLogicaNegocio.Controlador;

import CapaDatos.Logica_Conexion.*;
import CapaLogicaNegocio.DTOS.AnalisisCliente;
import CapaLogicaNegocio.DTOS.Promociones;
import CapaLogicaNegocio.DTOS.PromocionesDTO;
import CapaLogicaNegocio.Helpers.*;
import CapaLogicaNegocio.Logica_Negocio.Promocion;
import CapaLogicaNegocio.Logica_Negocio.PromocionCliente;
import CapaLogicaNegocio.Logica_Negocio.PromocionProducto;
import CapaLogicaNegocio.Logica_Negocio.Sincronizadora;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.*;
import java.util.stream.Collectors;

/** Promociones del administrador: base local y cola durable hacia Firebase. */
public class PromocionControlador {
    private final PromocionDAO promocionDAO = new PromocionDAO();
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);

    public RespuestaControlador<ArrayList<PromocionesDTO.PromocionAplicadaDTO>> obtenerProductosPromocion() {
        var dataset = promocionDAO.getDataset();
        if (dataset.isEmpty()) return new RespuestaControlador<>(true, "No hay productos para analizar", new ArrayList<>());
        HelperIAPromociones.agruparProductos(dataset);
        var perfiles = HelperIAPromociones.clasificarInventario();
        int grupo = perfiles.stream().filter(p -> "ESTANCADO".equals(p.getClasificacion()))
                .mapToInt(Promocion::getCluster).findFirst().orElse(-1);
        if (grupo < 0) throw new IllegalStateException("No se pudo identificar el grupo de inventario a revisar");
        Set<String> candidatos = dataset.stream().filter(p -> p.getCluster() == grupo && p.getStockActual() > 0)
                .map(Promocion::getId).collect(Collectors.toSet());
        ArrayList<PromocionesDTO.PromocionAplicadaDTO> resultado = new ArrayList<>();
        for (var producto : promocionDAO.datosPromociones()) if (candidatos.contains(producto.id())) resultado.add(producto);
        resultado.sort(Comparator.comparing(PromocionesDTO.PromocionAplicadaDTO::nombre));
        return new RespuestaControlador<>(true, "Productos sugeridos según inventario y ventas", resultado);
    }

    public RespuestaControlador<Boolean> aplicarPromocionProducto(String id, PromocionesDTO.PromocionessDTO dto) {
        return aplicar(id, dto, false);
    }

    public RespuestaControlador<Boolean> aplicarPromocionCliente(String id, PromocionesDTO.PromocionessDTO dto) {
        return aplicar(id, dto, true);
    }

    private RespuestaControlador<Boolean> aplicar(String destino, PromocionesDTO.PromocionessDTO dto, boolean cliente) {
        Promociones promocion;
        try {
            if (dto == null) throw new IllegalArgumentException("Faltan los datos de la promoción");
            UUID.fromString(destino);
            UUID.fromString(dto.id());
            String nombre = dto.nombre() == null ? "" : dto.nombre().trim();
            if (nombre.isEmpty() || nombre.length() > 50) throw new IllegalArgumentException("El nombre debe tener entre 1 y 50 caracteres");
            double descuento = Double.parseDouble(dto.descuento());
            if (!Double.isFinite(descuento) || descuento <= 0 || descuento > 90)
                throw new IllegalArgumentException("El descuento debe ser mayor que 0 y no superar el 90 %");
            Date inicio = Date.from(LocalDateTime.parse(dto.fechaInicio(), FECHA).atZone(ZoneId.systemDefault()).toInstant());
            Date fin = Date.from(LocalDateTime.parse(dto.fechaFin(), FECHA).atZone(ZoneId.systemDefault()).toInstant());
            if (!fin.after(inicio) || !fin.after(new Date()))
                throw new IllegalArgumentException("El fin debe ser posterior al inicio y a la fecha actual");
            String tipo = cliente ? "GENERAL" : "ESPECIFICA";
            if (!tipo.equals(dto.tipo())) throw new IllegalArgumentException("El tipo de promoción no corresponde al destino");
            promocion = new Promociones(dto.id(), nombre, descuento, inicio, fin, Promociones.TipoPromocion.valueOf(tipo));
        } catch (Exception e) {
            String mensaje = e instanceof IllegalArgumentException && e.getMessage() != null
                    ? e.getMessage() : "Revisa el destino, el descuento y las fechas (dd/MM/aaaa HH:mm)";
            return new RespuestaControlador<>(false, mensaje, false);
        }
        String relacionId = UUID.randomUUID().toString();
        if (cliente) promocion.setIdCliente(destino); else promocion.setIdProducto(destino);
        Object relacion = cliente ? new PromocionCliente(relacionId, promocion.getId(), destino)
                : new PromocionProducto(relacionId, promocion.getId(), destino);
        boolean guardado = HelperGestorBD.guardarOperacion(() -> {
            if (!promocionDAO.destinoDisponible(destino, cliente, promocion.getFechaInicio(), promocion.getFechaFin())) return false;
            if (!promocionDAO.agregarPromocionPersonalizada(promocion)) return false;
            return cliente ? promocionDAO.agregarPromocionCliente(
                    new PromocionesDTO.PromocionClienteDTO(relacionId, promocion.getId(), destino))
                    : promocionDAO.agregarPromocionProducto(
                    new PromocionesDTO.PromocionProductoDTO(relacionId, promocion.getId(), destino));
        }, new HelperGestorBD.CambioPendiente(promocion, "Promocion", promocion.getId(), Sincronizadora.Accion.INSERT),
                new HelperGestorBD.CambioPendiente(relacion, cliente ? "PromocionCliente" : "PromocionProducto",
                        relacionId, Sincronizadora.Accion.INSERT));
        return new RespuestaControlador<>(guardado, guardado
                ? "Promoción guardada; se sincronizará con Firebase"
                : "No se guardó la promoción. Comprueba que el destino exista y no tenga otra promoción en esas fechas.", guardado);
    }

    /** Incluye las programadas; las finalizadas no se vuelven a activar ni se alteran. */
    public RespuestaControlador<ArrayList<Promociones>> obtenerPromocionesActivas() {
        Date ahora = new Date();
        ArrayList<Promociones> resultado = new ArrayList<>();
        for (Promociones p : promocionDAO.obtenerPromocionesPersonalizadas()) {
            if (p.getFechaFin() != null && p.getFechaFin().after(ahora)) resultado.add(p);
        }
        return new RespuestaControlador<>(true, "Promociones vigentes y programadas", resultado);
    }

    public RespuestaControlador<Boolean> desactivarPromocion(Date fechaActual, String id) {
        if (fechaActual == null || id == null) return new RespuestaControlador<>(false, "Selecciona una promoción", false);
        Promociones seleccionada = promocionDAO.obtenerPromocionesPersonalizadas().stream()
                .filter(p -> id.equals(p.getId())).findFirst().orElse(null);
        if (seleccionada == null) return new RespuestaControlador<>(false, "La promoción ya no existe en la base local", false);
        // MySQL TIMESTAMP sin fracciones puede redondear al segundo siguiente.
        long instante = Math.min(fechaActual.getTime(), System.currentTimeMillis());
        Date fin = new Date(Math.floorDiv(instante, 1000L) * 1000L);
        if (seleccionada.getFechaFin() != null && !seleccionada.getFechaFin().after(fin))
            return new RespuestaControlador<>(true, "La promoción ya finalizó", true);
        seleccionada.setFechaFin(fin);
        boolean exito = HelperGestorBD.actualizarRegistro(seleccionada, "Promocion", id,
                () -> promocionDAO.desactivarPromocion(fin, id), () -> false);
        return new RespuestaControlador<>(exito, exito ? "Promoción desactivada; cambio pendiente de sincronizar si no hay conexión"
                : "No se pudo guardar la desactivación", exito);
    }

    public RespuestaControlador<ArrayList<PromocionesDTO.PromocioPersonalizadaCliente>> obtenerResumeComprasClientes() {
        var clientes = promocionDAO.obtenerResumeComprasCliente();
        clientes.sort(Comparator.comparing(PromocionesDTO.PromocioPersonalizadaCliente::nombre)
                .thenComparing(PromocionesDTO.PromocioPersonalizadaCliente::apellido));
        return new RespuestaControlador<>(true, "Historial local de compras", clientes);
    }

    public RespuestaControlador<AnalisisCliente> analizarCliente(String id) {
        if (id == null || id.isBlank()) return new RespuestaControlador<>(false, "Selecciona un cliente", null);
        var dataset = promocionDAO.getDatasetClientes();
        AnalisisCliente seleccionado = dataset.stream().filter(c -> id.equals(c.getId())).findFirst().orElse(null);
        if (seleccionado == null) return new RespuestaControlador<>(false, "No se encontró el cliente", null);
        if (seleccionado.getCantCompras() == 0) {
            seleccionado.setEtiquetaNegocio("NUEVO");
            seleccionado.setDescuentoRecomendado("5");
        } else {
            ArrayList<AnalisisCliente> compradores = new ArrayList<>();
            for (AnalisisCliente c : dataset) if (c.getCantCompras() > 0) compradores.add(c);
            HelperIASegmentadorClientes.agruparClientes(compradores);
            var perfiles = HelperIASegmentadorClientes.analizarYEtiquetarClusters();
            for (AnalisisCliente perfil : perfiles) if (perfil.getCluster() == seleccionado.getCluster()) {
                seleccionado.setEtiquetaNegocio(perfil.getEtiquetaNegocio());
                seleccionado.setDescuentoRecomendado(perfil.getDescuentoRecomendado());
            }
            if (seleccionado.getEtiquetaNegocio() == null || seleccionado.getEtiquetaNegocio().isBlank())
                return new RespuestaControlador<>(false, "No se pudo calcular el perfil de compra", null);
        }
        return new RespuestaControlador<>(true, "Perfil calculado según el historial local", seleccionado);
    }

    public record PromocionVista(Promociones promocion, String destino, Double precioOriginal) { }

    public ArrayList<PromocionVista> listarParaAdministrar() {
        Map<String, CapaLogicaNegocio.Logica_Negocio.Producto> productos = new HashMap<>();
        for (var p : new ProductoDAO().obteners()) productos.put(p.getId(), p);
        Map<String, String> clientes = new HashMap<>();
        for (var c : new ClienteDAO().obteners()) clientes.put(c.getId(), c.getNombre() + " " + c.getApellido() + " · " + c.getCedula());
        ArrayList<PromocionVista> vistas = new ArrayList<>();
        for (var p : obtenerPromocionesActivas().dato()) {
            var producto = productos.get(p.getIdProducto());
            String destino = p.getIdCliente() != null ? clientes.getOrDefault(p.getIdCliente(), "Cliente no disponible")
                    : producto != null ? producto.getNombre() : "Producto no disponible";
            vistas.add(new PromocionVista(p, destino, producto == null ? null : producto.getPrecioActual()));
        }
        return vistas;
    }
}
