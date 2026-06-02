package CapaPresentacion.GUI_Admin;

import CapaDatos.Logica_Conexion.Conexion;
import CapaPresentacion.GUI_Cliente.MenuCliente;
import CapaLogicaNegocio.Controlador.CategoriaControlador;
import CapaLogicaNegocio.Controlador.ClienteControlador;
import CapaLogicaNegocio.Controlador.ProductoControlador;
import CapaLogicaNegocio.Controlador.RespuestaControlador;
import CapaLogicaNegocio.Controlador.VentaControlador;
import CapaLogicaNegocio.Helpers.HelperCifrado;
import CapaLogicaNegocio.Helpers.HelperMonitorRed;
import CapaLogicaNegocio.Helpers.OSHelper;
import CapaLogicaNegocio.Logica_Negocio.Administrador;
import CapaLogicaNegocio.Logica_Negocio.Carrito;
import CapaLogicaNegocio.Logica_Negocio.Categoria;
import CapaLogicaNegocio.Logica_Negocio.Cliente;
import CapaLogicaNegocio.Logica_Negocio.Producto;
import CapaLogicaNegocio.Logica_Negocio.Usuario;
import CapaLogicaNegocio.Logica_Negocio.Venta;

import java.awt.Color;
import java.awt.Image;
import java.text.SimpleDateFormat;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import CapaLogicaNegocio.DTOS.ProductoDTO;
import CapaLogicaNegocio.DTOS.VentasDTO.VentaDTO;
import CapaLogicaNegocio.DTOS.ClientesDTO.ClienteDTO;;

/**
 * Formulario de inicio de sesión.
 *
 * @author Marlon Vargas
 */
public class InicioSesion extends javax.swing.JFrame {

        Usuario usuAdmin;

        // Paleta de colores centralizada para mantener coherencia visual
        private static final Color COLOR_BOTON = new Color(0, 95, 115);
        private static final Color COLOR_LINEA = Color.WHITE;
        private static final Color COLOR_ERROR = Color.RED;

        public InicioSesion() {
                initComponents();
                // cargarDatosIniciales();
                setLocationRelativeTo(null);
                aplicarEstilosCampos();
                aplicarEstiloBoton();
                addPlaceholders();

                establecerFondo();
                establecerIconoUsuario();

                javax.swing.Timer timer = new javax.swing.Timer(100, e -> requestFocusInWindow());
                timer.setRepeats(false);
                timer.start();

                // El monitor de red (sincronizacion Local->Nube) es maquinaria EXCLUSIVA del
                // Admin: arranca
                // solo cuando inicia sesion el Administrador (ver iniciarSesionAdmin). El
                // modulo Cliente opera
                // unicamente contra la nube y no debe levantar la sincronizacion ni tocar la
                // base local.
        }

        public void cargarDatosIniciales() {

                ClienteControlador clienteControlador = new ClienteControlador();

                String idCliente1 = UUID.randomUUID().toString();
                String idCliente2 = UUID.randomUUID().toString();
                String idCliente3 = UUID.randomUUID().toString();
                String idCliente4 = UUID.randomUUID().toString();
                String idCliente5 = UUID.randomUUID().toString();
                String idCliente6 = UUID.randomUUID().toString();
                String idCliente7 = UUID.randomUUID().toString();
                String idCliente8 = UUID.randomUUID().toString();
                String idCliente9 = UUID.randomUUID().toString();
                String idCliente10 = UUID.randomUUID().toString();
                String idCliente11 = UUID.randomUUID().toString();

                RespuestaControlador<Cliente> cliente = clienteControlador.agregarCliente(new ClienteDTO(
                                idCliente1, "juan", "perez", "1234", "cra 4b"));

                RespuestaControlador<Cliente> cliente2 = clienteControlador.agregarCliente(new ClienteDTO(
                                idCliente2, "pedro", "gomez", "5678", "calle 10"));

                RespuestaControlador<Cliente> cliente3 = clienteControlador.agregarCliente(new ClienteDTO(
                                idCliente3, "matias", "rodriguez", "9012", "avenida 5"));

                RespuestaControlador<Cliente> cliente4 = clienteControlador.agregarCliente(new ClienteDTO(
                                idCliente4, "jordan", "lopez", "3456", "diagonal 23"));

                RespuestaControlador<Cliente> cliente5 = clienteControlador.agregarCliente(new ClienteDTO(
                                idCliente5, "lucas", "martinez", "7890", "transversal 8"));

                RespuestaControlador<Cliente> cliente6 = clienteControlador.agregarCliente(new ClienteDTO(
                                idCliente6, "luis", "gonzalez", "2345", "manzana v"));

                RespuestaControlador<Cliente> cliente7 = clienteControlador.agregarCliente(new ClienteDTO(
                                idCliente7, "lian", "sanchez", "6789", "vereda norte"));

                RespuestaControlador<Cliente> cliente8 = clienteControlador.agregarCliente(new ClienteDTO(
                                idCliente8, "francisco", "ramirez", "0123", "sector central"));

                RespuestaControlador<Cliente> cliente9 = clienteControlador.agregarCliente(new ClienteDTO(
                                idCliente9, "luisa", "torres", "4567", "pasaje peatonal"));

                RespuestaControlador<Cliente> cliente10 = clienteControlador.agregarCliente(new ClienteDTO(
                                idCliente10, "andrea", "flores", "8901", "kilometro 1"));

                RespuestaControlador<Cliente> cliente11 = clienteControlador.agregarCliente(new ClienteDTO(
                                idCliente11, "felipe", "guzman", "97904", "avenida las palmas"));

                CategoriaControlador categoriaControlador = new CategoriaControlador();

                String idCategoria1 = UUID.randomUUID().toString();
                String idCategoria2 = UUID.randomUUID().toString();
                String idCategoria3 = UUID.randomUUID().toString();
                String idCategoria4 = UUID.randomUUID().toString();

                RespuestaControlador<Boolean> categoria = categoriaControlador.crearCategorias(new Categoria(
                                idCategoria1, "Telefono"));
                RespuestaControlador<Boolean> categoria1 = categoriaControlador.crearCategorias(new Categoria(
                                idCategoria2, "Tablet"));
                RespuestaControlador<Boolean> categoria2 = categoriaControlador.crearCategorias(new Categoria(
                                idCategoria3, "Computador"));
                RespuestaControlador<Boolean> categoria4 = categoriaControlador.crearCategorias(new Categoria(
                                idCategoria4, "Consolas"));

                ProductoControlador productoControlador = new ProductoControlador();

                String idProducto1 = UUID.randomUUID().toString();
                String idProducto2 = UUID.randomUUID().toString();
                String idProducto3 = UUID.randomUUID().toString();
                String idProducto4 = UUID.randomUUID().toString();

                String idProducto5 = UUID.randomUUID().toString();
                String idProducto6 = UUID.randomUUID().toString();
                String idProducto7 = UUID.randomUUID().toString();
                String idProducto8 = UUID.randomUUID().toString();

                String idProducto9 = UUID.randomUUID().toString();
                String idProducto10 = UUID.randomUUID().toString();
                String idProducto11 = UUID.randomUUID().toString();
                String idProducto12 = UUID.randomUUID().toString();

                String idProducto13 = UUID.randomUUID().toString();
                String idProducto14 = UUID.randomUUID().toString();
                String idProducto15 = UUID.randomUUID().toString();
                String idProducto16 = UUID.randomUUID().toString();
                String idProducto17 = UUID.randomUUID().toString();

                // ==================== CATEGORÍA 1: Telefono ====================

                RespuestaControlador<Producto> producto1 = productoControlador
                                .agregarProducto(new ProductoDTO(idProducto1, "10023457", "Samsung Galaxy S24 Ultra",
                                                "Samsung",
                                                "SN01XYZ490", "78", "3296433", "19/07/2028", idCategoria1));

                RespuestaControlador<Producto> producto2 = productoControlador
                                .agregarProducto(new ProductoDTO(idProducto2, "10046906", "Apple iPhone 15 Pro",
                                                "Apple",
                                                "SN02XYZ723", "17", "3357636", "06/04/2028", idCategoria1));

                RespuestaControlador<Producto> producto3 = productoControlador
                                .agregarProducto(new ProductoDTO(idProducto3, "10070352", "Xiaomi Redmi Note 13 Pro",
                                                "Xiaomi",
                                                "SN03XYZ808", "38", "3412996", "08/01/2027", idCategoria1));

                RespuestaControlador<Producto> producto4 = productoControlador
                                .agregarProducto(new ProductoDTO(idProducto4, "10093801", "Motorola Edge 50 Ultra",
                                                "Motorola",
                                                "SN04XYZ714", "38", "3983296", "20/02/2028", idCategoria1));

                // ==================== CATEGORÍA 2: Tablet ====================

                RespuestaControlador<Producto> producto8 = productoControlador
                                .agregarProducto(new ProductoDTO(idProducto5, "10187609", "Apple iPad Pro M4", "Apple",
                                                "SN08XYZ262", "56", "1943002", "14/04/2027", idCategoria2));

                RespuestaControlador<Producto> producto9 = productoControlador
                                .agregarProducto(new ProductoDTO(idProducto6, "10211052", "Samsung Galaxy Tab S9",
                                                "Samsung",
                                                "SN09XYZ974", "34", "3050143", "15/01/2028", idCategoria2));

                RespuestaControlador<Producto> producto10 = productoControlador
                                .agregarProducto(new ProductoDTO(idProducto7, "10234501", "Lenovo Tab P12 Pro",
                                                "Lenovo",
                                                "SN10XYZ109", "19", "2384007", "25/10/2027", idCategoria2));

                RespuestaControlador<Producto> producto11 = productoControlador
                                .agregarProducto(new ProductoDTO(idProducto8, "10257951", "Xiaomi Pad 6 Max", "Xiaomi",
                                                "SN11XYZ340", "65", "1612719", "09/04/2028", idCategoria2));

                // ==================== CATEGORÍA 3: Computador ====================

                RespuestaControlador<Producto> producto15 = productoControlador
                                .agregarProducto(new ProductoDTO(idProducto9, "10351758", "ASUS Zenbook 14 OLED",
                                                "ASUS",
                                                "SN15XYZ274", "53", "3478849", "02/07/2027", idCategoria3));

                RespuestaControlador<Producto> producto16 = productoControlador
                                .agregarProducto(new ProductoDTO(idProducto10, "10375204", "HP Pavilion 15", "HP",
                                                "SN16XYZ710", "34", "4400996", "05/03/2028", idCategoria3));

                RespuestaControlador<Producto> producto17 = productoControlador
                                .agregarProducto(new ProductoDTO(idProducto11, "10398654", "Dell XPS 13 9340", "Dell",
                                                "SN17XYZ889", "22", "4692243", "13/09/2028", idCategoria3));

                RespuestaControlador<Producto> producto18 = productoControlador
                                .agregarProducto(new ProductoDTO(idProducto12, "10422106", "Lenovo ThinkPad E14",
                                                "Lenovo",
                                                "SN18XYZ474", "32", "4646483", "15/04/2028", idCategoria3));

                // ==================== CATEGORÍA 4: Consolas ====================

                RespuestaControlador<Producto> producto22 = productoControlador
                                .agregarProducto(new ProductoDTO(idProducto13, "10515907", "Sony PlayStation 5 Slim",
                                                "Sony",
                                                "SN22XYZ602", "35", "3559313", "05/03/2028", idCategoria4));

                RespuestaControlador<Producto> producto23 = productoControlador
                                .agregarProducto(new ProductoDTO(idProducto14, "10539354", "Microsoft Xbox Series X",
                                                "Microsoft",
                                                "SN23XYZ262", "40", "2300676", "30/08/2028", idCategoria4));

                RespuestaControlador<Producto> producto24 = productoControlador
                                .agregarProducto(new ProductoDTO(idProducto15, "10562802", "Nintendo Switch OLED",
                                                "Nintendo",
                                                "SN24XYZ929", "58", "2247912", "11/08/2027", idCategoria4));

                RespuestaControlador<Producto> producto25 = productoControlador
                                .agregarProducto(new ProductoDTO(idProducto16, "10586259", "ASUS ROG Ally Z1 Extreme",
                                                "ASUS",
                                                "SN25XYZ424", "43", "2762100", "02/01/2027", idCategoria4));

                RespuestaControlador<Producto> producto56 = productoControlador
                                .agregarProducto(new ProductoDTO(idProducto17, "105862597", "ACER NITRO 5",
                                                "ACER",
                                                "AN515-56", "100", "3800000", "02/01/2027", idCategoria3));

                VentaControlador ventaControlador = new VentaControlador();

                try {
                        // Venta 1
                        Carrito carrito1 = new Carrito();
                        carrito1.agregar(producto1.dato(), 2L);
                        carrito1.agregar(producto8.dato(), 1L);
                        carrito1.agregar(producto22.dato(), 1L);
                        RespuestaControlador<Venta> venta1 = ventaControlador.registrarVentaAdmin(carrito1, idCliente1,
                                        Venta.MetodoPago.TARJETA,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("15/01/2025"));

                        // Venta 2
                        Carrito carrito2 = new Carrito();
                        carrito2.agregar(producto2.dato(), 1L);
                        carrito2.agregar(producto15.dato(), 1L);
                        RespuestaControlador<Venta> venta2 = ventaControlador.registrarVentaAdmin(carrito2, idCliente2,
                                        Venta.MetodoPago.PSE,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("20/01/2025"));

                        // Venta 3
                        Carrito carrito3 = new Carrito();
                        carrito3.agregar(producto3.dato(), 3L);
                        carrito3.agregar(producto24.dato(), 1L);
                        RespuestaControlador<Venta> venta3 = ventaControlador.registrarVentaAdmin(carrito3, idCliente3,
                                        Venta.MetodoPago.EFECTY,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("05/02/2025"));

                        // Venta 4
                        Carrito carrito4 = new Carrito();
                        carrito4.agregar(producto4.dato(), 1L);
                        carrito4.agregar(producto9.dato(), 2L);
                        carrito4.agregar(producto23.dato(), 1L);
                        RespuestaControlador<Venta> venta4 = ventaControlador.registrarVentaAdmin(carrito4, idCliente4,
                                        Venta.MetodoPago.TARJETA,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("18/02/2025"));

                        // Venta 5
                        Carrito carrito5 = new Carrito();
                        carrito5.agregar(producto10.dato(), 2L);
                        carrito5.agregar(producto17.dato(), 1L);
                        RespuestaControlador<Venta> venta5 = ventaControlador.registrarVentaAdmin(carrito5, idCliente5,
                                        Venta.MetodoPago.PSE,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("10/03/2025"));

                        // Venta 6
                        Carrito carrito6 = new Carrito();
                        carrito6.agregar(producto11.dato(), 1L);
                        carrito6.agregar(producto18.dato(), 1L);
                        carrito6.agregar(producto25.dato(), 1L);
                        RespuestaControlador<Venta> venta6 = ventaControlador.registrarVentaAdmin(carrito6, idCliente6,
                                        Venta.MetodoPago.EFECTY,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("22/03/2025"));

                        // Venta 7
                        Carrito carrito7 = new Carrito();
                        carrito7.agregar(producto1.dato(), 1L);
                        carrito7.agregar(producto16.dato(), 2L);
                        RespuestaControlador<Venta> venta7 = ventaControlador.registrarVentaAdmin(carrito7, idCliente7,
                                        Venta.MetodoPago.TARJETA,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("05/04/2025"));

                        // Venta 8
                        Carrito carrito8 = new Carrito();
                        carrito8.agregar(producto22.dato(), 2L);
                        carrito8.agregar(producto24.dato(), 1L);
                        RespuestaControlador<Venta> venta8 = ventaControlador.registrarVentaAdmin(carrito8, idCliente8,
                                        Venta.MetodoPago.PSE,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("17/04/2025"));

                        // Venta 9
                        Carrito carrito9 = new Carrito();
                        carrito9.agregar(producto2.dato(), 1L);
                        carrito9.agregar(producto8.dato(), 2L);
                        carrito9.agregar(producto17.dato(), 1L);
                        RespuestaControlador<Venta> venta9 = ventaControlador.registrarVentaAdmin(carrito9, idCliente9,
                                        Venta.MetodoPago.EFECTY,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("29/04/2025"));

                        // Venta 10
                        Carrito carrito10 = new Carrito();
                        carrito10.agregar(producto4.dato(), 2L);
                        carrito10.agregar(producto25.dato(), 1L);
                        RespuestaControlador<Venta> venta10 = ventaControlador.registrarVentaAdmin(carrito10,
                                        idCliente10,
                                        Venta.MetodoPago.TARJETA,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("12/05/2025"));

                        // Venta 11
                        Carrito carrito11 = new Carrito();
                        carrito11.agregar(producto3.dato(), 2L);
                        carrito11.agregar(producto9.dato(), 1L);
                        carrito11.agregar(producto17.dato(), 1L);
                        RespuestaControlador<Venta> venta11 = ventaControlador.registrarVentaAdmin(carrito11,
                                        idCliente1,
                                        Venta.MetodoPago.PSE,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("20/05/2025"));

                        // Venta 12
                        Carrito carrito12 = new Carrito();
                        carrito12.agregar(producto22.dato(), 1L);
                        carrito12.agregar(producto23.dato(), 1L);
                        RespuestaControlador<Venta> venta12 = ventaControlador.registrarVentaAdmin(carrito12,
                                        idCliente2,
                                        Venta.MetodoPago.TARJETA,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("01/06/2025"));

                        // Venta 13
                        Carrito carrito13 = new Carrito();
                        carrito13.agregar(producto4.dato(), 2L);
                        carrito13.agregar(producto10.dato(), 1L);
                        RespuestaControlador<Venta> venta13 = ventaControlador.registrarVentaAdmin(carrito13,
                                        idCliente3,
                                        Venta.MetodoPago.EFECTY,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("10/06/2025"));

                        // Venta 14
                        Carrito carrito14 = new Carrito();
                        carrito14.agregar(producto15.dato(), 1L);
                        carrito14.agregar(producto18.dato(), 2L);
                        carrito14.agregar(producto24.dato(), 1L);
                        RespuestaControlador<Venta> venta14 = ventaControlador.registrarVentaAdmin(carrito14,
                                        idCliente4,
                                        Venta.MetodoPago.PSE,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("15/06/2025"));

                        // Venta 15
                        Carrito carrito15 = new Carrito();
                        carrito15.agregar(producto2.dato(), 1L);
                        carrito15.agregar(producto8.dato(), 3L);
                        RespuestaControlador<Venta> venta15 = ventaControlador.registrarVentaAdmin(carrito15,
                                        idCliente5,
                                        Venta.MetodoPago.TARJETA,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("18/06/2025"));

                        // Venta 16
                        Carrito carrito16 = new Carrito();
                        carrito16.agregar(producto11.dato(), 2L);
                        carrito16.agregar(producto25.dato(), 1L);
                        RespuestaControlador<Venta> venta16 = ventaControlador.registrarVentaAdmin(carrito16,
                                        idCliente6,
                                        Venta.MetodoPago.EFECTY,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("22/06/2025"));

                        // Venta 17
                        Carrito carrito17 = new Carrito();
                        carrito17.agregar(producto1.dato(), 1L);
                        carrito17.agregar(producto22.dato(), 1L);
                        carrito17.agregar(producto24.dato(), 2L);
                        RespuestaControlador<Venta> venta17 = ventaControlador.registrarVentaAdmin(carrito17,
                                        idCliente7,
                                        Venta.MetodoPago.PSE,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("25/06/2025"));

                        // Venta 18
                        Carrito carrito18 = new Carrito();
                        carrito18.agregar(producto9.dato(), 2L);
                        carrito18.agregar(producto10.dato(), 1L);
                        carrito18.agregar(producto11.dato(), 1L);
                        RespuestaControlador<Venta> venta18 = ventaControlador.registrarVentaAdmin(carrito18,
                                        idCliente8,
                                        Venta.MetodoPago.TARJETA,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("27/06/2025"));

                        // Venta 19
                        Carrito carrito19 = new Carrito();
                        carrito19.agregar(producto16.dato(), 1L);
                        carrito19.agregar(producto17.dato(), 1L);
                        RespuestaControlador<Venta> venta19 = ventaControlador.registrarVentaAdmin(carrito19,
                                        idCliente9,
                                        Venta.MetodoPago.EFECTY,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("28/06/2025"));

                        // Venta 20
                        Carrito carrito20 = new Carrito();
                        carrito20.agregar(producto23.dato(), 2L);
                        carrito20.agregar(producto25.dato(), 1L);
                        RespuestaControlador<Venta> venta20 = ventaControlador.registrarVentaAdmin(carrito20,
                                        idCliente10,
                                        Venta.MetodoPago.PSE,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("30/06/2025"));

                        // Venta 21
                        Carrito carrito21 = new Carrito();
                        carrito21.agregar(producto2.dato(), 2L);
                        carrito21.agregar(producto4.dato(), 1L);
                        carrito21.agregar(producto15.dato(), 1L);
                        RespuestaControlador<Venta> venta21 = ventaControlador.registrarVentaAdmin(carrito21,
                                        idCliente1,
                                        Venta.MetodoPago.TARJETA,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("03/07/2025"));

                        // Venta 22
                        Carrito carrito22 = new Carrito();
                        carrito22.agregar(producto8.dato(), 2L);
                        carrito22.agregar(producto18.dato(), 1L);
                        RespuestaControlador<Venta> venta22 = ventaControlador.registrarVentaAdmin(carrito22,
                                        idCliente2,
                                        Venta.MetodoPago.EFECTY,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("10/07/2025"));

                        // Venta 23
                        Carrito carrito23 = new Carrito();
                        carrito23.agregar(producto24.dato(), 1L);
                        carrito23.agregar(producto22.dato(), 1L);
                        carrito23.agregar(producto23.dato(), 1L);
                        RespuestaControlador<Venta> venta23 = ventaControlador.registrarVentaAdmin(carrito23,
                                        idCliente3,
                                        Venta.MetodoPago.PSE,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("18/07/2025"));

                        // Venta 24
                        Carrito carrito24 = new Carrito();
                        carrito24.agregar(producto3.dato(), 1L);
                        carrito24.agregar(producto11.dato(), 2L);
                        RespuestaControlador<Venta> venta24 = ventaControlador.registrarVentaAdmin(carrito24,
                                        idCliente4,
                                        Venta.MetodoPago.TARJETA,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("22/07/2025"));

                        // Venta 25
                        Carrito carrito25 = new Carrito();
                        carrito25.agregar(producto1.dato(), 3L);
                        carrito25.agregar(producto17.dato(), 1L);
                        carrito25.agregar(producto25.dato(), 1L);
                        RespuestaControlador<Venta> venta25 = ventaControlador.registrarVentaAdmin(carrito25,
                                        idCliente5,
                                        Venta.MetodoPago.EFECTY,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("01/08/2025"));

                        // Venta 26
                        Carrito carrito26 = new Carrito();
                        carrito26.agregar(producto2.dato(), 2L);
                        carrito26.agregar(producto9.dato(), 1L);
                        RespuestaControlador<Venta> venta26 = ventaControlador.registrarVentaAdmin(carrito26,
                                        idCliente6,
                                        Venta.MetodoPago.TARJETA,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("08/08/2025"));

                        // Venta 27
                        Carrito carrito27 = new Carrito();
                        carrito27.agregar(producto15.dato(), 1L);
                        carrito27.agregar(producto22.dato(), 1L);
                        carrito27.agregar(producto24.dato(), 2L);
                        RespuestaControlador<Venta> venta27 = ventaControlador.registrarVentaAdmin(carrito27,
                                        idCliente7,
                                        Venta.MetodoPago.PSE,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("15/08/2025"));

                        // Venta 28
                        Carrito carrito28 = new Carrito();
                        carrito28.agregar(producto4.dato(), 3L);
                        carrito28.agregar(producto10.dato(), 1L);
                        RespuestaControlador<Venta> venta28 = ventaControlador.registrarVentaAdmin(carrito28,
                                        idCliente8,
                                        Venta.MetodoPago.EFECTY,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("20/08/2025"));

                        // Venta 29
                        Carrito carrito29 = new Carrito();
                        carrito29.agregar(producto11.dato(), 2L);
                        carrito29.agregar(producto18.dato(), 1L);
                        RespuestaControlador<Venta> venta29 = ventaControlador.registrarVentaAdmin(carrito29,
                                        idCliente9,
                                        Venta.MetodoPago.TARJETA,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("28/08/2025"));

                        // Venta 30
                        Carrito carrito30 = new Carrito();
                        carrito30.agregar(producto23.dato(), 1L);
                        carrito30.agregar(producto25.dato(), 1L);
                        RespuestaControlador<Venta> venta30 = ventaControlador.registrarVentaAdmin(carrito30,
                                        idCliente10,
                                        Venta.MetodoPago.PSE,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("05/09/2025"));

                        // Venta 31
                        Carrito carrito31 = new Carrito();
                        carrito31.agregar(producto1.dato(), 2L);
                        carrito31.agregar(producto8.dato(), 2L);
                        carrito31.agregar(producto17.dato(), 1L);
                        RespuestaControlador<Venta> venta31 = ventaControlador.registrarVentaAdmin(carrito31,
                                        idCliente1,
                                        Venta.MetodoPago.EFECTY,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("12/09/2025"));

                        // Venta 32
                        Carrito carrito32 = new Carrito();
                        carrito32.agregar(producto22.dato(), 2L);
                        carrito32.agregar(producto24.dato(), 1L);
                        RespuestaControlador<Venta> venta32 = ventaControlador.registrarVentaAdmin(carrito32,
                                        idCliente2,
                                        Venta.MetodoPago.TARJETA,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("18/09/2025"));

                        // Venta 33
                        Carrito carrito33 = new Carrito();
                        carrito33.agregar(producto3.dato(), 1L);
                        carrito33.agregar(producto10.dato(), 2L);
                        carrito33.agregar(producto16.dato(), 1L);
                        RespuestaControlador<Venta> venta33 = ventaControlador.registrarVentaAdmin(carrito33,
                                        idCliente3,
                                        Venta.MetodoPago.PSE,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("25/09/2025"));

                        // Venta 34
                        Carrito carrito34 = new Carrito();
                        carrito34.agregar(producto2.dato(), 1L);
                        carrito34.agregar(producto11.dato(), 3L);
                        RespuestaControlador<Venta> venta34 = ventaControlador.registrarVentaAdmin(carrito34,
                                        idCliente4,
                                        Venta.MetodoPago.EFECTY,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("02/10/2025"));

                        // Venta 35
                        Carrito carrito35 = new Carrito();
                        carrito35.agregar(producto15.dato(), 2L);
                        carrito35.agregar(producto18.dato(), 1L);
                        carrito35.agregar(producto25.dato(), 1L);
                        RespuestaControlador<Venta> venta35 = ventaControlador.registrarVentaAdmin(carrito35,
                                        idCliente5,
                                        Venta.MetodoPago.TARJETA,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("10/10/2025"));

                        // Venta 36
                        Carrito carrito36 = new Carrito();
                        carrito36.agregar(producto9.dato(), 1L);
                        carrito36.agregar(producto22.dato(), 1L);
                        RespuestaControlador<Venta> venta36 = ventaControlador.registrarVentaAdmin(carrito36,
                                        idCliente6,
                                        Venta.MetodoPago.PSE,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("05/01/2026"));

                        // Venta 37
                        Carrito carrito37 = new Carrito();
                        carrito37.agregar(producto1.dato(), 3L);
                        carrito37.agregar(producto23.dato(), 1L);
                        RespuestaControlador<Venta> venta37 = ventaControlador.registrarVentaAdmin(carrito37,
                                        idCliente7,
                                        Venta.MetodoPago.EFECTY,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("10/01/2026"));

                        // Venta 38
                        Carrito carrito38 = new Carrito();
                        carrito38.agregar(producto4.dato(), 2L);
                        carrito38.agregar(producto17.dato(), 1L);
                        carrito38.agregar(producto24.dato(), 1L);
                        RespuestaControlador<Venta> venta38 = ventaControlador.registrarVentaAdmin(carrito38,
                                        idCliente8,
                                        Venta.MetodoPago.TARJETA,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("15/01/2026"));

                        // Venta 39
                        Carrito carrito39 = new Carrito();
                        carrito39.agregar(producto8.dato(), 2L);
                        carrito39.agregar(producto10.dato(), 1L);
                        RespuestaControlador<Venta> venta39 = ventaControlador.registrarVentaAdmin(carrito39,
                                        idCliente9,
                                        Venta.MetodoPago.PSE,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("20/01/2026"));

                        // Venta 40
                        Carrito carrito40 = new Carrito();
                        carrito40.agregar(producto16.dato(), 2L);
                        carrito40.agregar(producto18.dato(), 2L);
                        RespuestaControlador<Venta> venta40 = ventaControlador.registrarVentaAdmin(carrito40,
                                        idCliente10,
                                        Venta.MetodoPago.EFECTY,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("25/01/2026"));

                        // Venta 41
                        Carrito carrito41 = new Carrito();
                        carrito41.agregar(producto2.dato(), 1L);
                        carrito41.agregar(producto22.dato(), 1L);
                        carrito41.agregar(producto25.dato(), 1L);
                        RespuestaControlador<Venta> venta41 = ventaControlador.registrarVentaAdmin(carrito41,
                                        idCliente1,
                                        Venta.MetodoPago.TARJETA,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("02/02/2026"));

                        // Venta 42
                        Carrito carrito42 = new Carrito();
                        carrito42.agregar(producto11.dato(), 2L);
                        carrito42.agregar(producto23.dato(), 1L);
                        RespuestaControlador<Venta> venta42 = ventaControlador.registrarVentaAdmin(carrito42,
                                        idCliente2,
                                        Venta.MetodoPago.PSE,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("08/02/2026"));

                        // Venta 43
                        Carrito carrito43 = new Carrito();
                        carrito43.agregar(producto3.dato(), 2L);
                        carrito43.agregar(producto15.dato(), 1L);
                        carrito43.agregar(producto24.dato(), 1L);
                        RespuestaControlador<Venta> venta43 = ventaControlador.registrarVentaAdmin(carrito43,
                                        idCliente3,
                                        Venta.MetodoPago.EFECTY,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("14/02/2026"));

                        // Venta 44
                        Carrito carrito44 = new Carrito();
                        carrito44.agregar(producto9.dato(), 3L);
                        carrito44.agregar(producto17.dato(), 1L);
                        RespuestaControlador<Venta> venta44 = ventaControlador.registrarVentaAdmin(carrito44,
                                        idCliente4,
                                        Venta.MetodoPago.TARJETA,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("20/02/2026"));

                        // Venta 45
                        Carrito carrito45 = new Carrito();
                        carrito45.agregar(producto1.dato(), 1L);
                        carrito45.agregar(producto10.dato(), 1L);
                        carrito45.agregar(producto22.dato(), 1L);
                        RespuestaControlador<Venta> venta45 = ventaControlador.registrarVentaAdmin(carrito45,
                                        idCliente5,
                                        Venta.MetodoPago.PSE,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("26/02/2026"));

                        // Venta 46
                        Carrito carrito46 = new Carrito();
                        carrito46.agregar(producto4.dato(), 2L);
                        carrito46.agregar(producto18.dato(), 1L);
                        RespuestaControlador<Venta> venta46 = ventaControlador.registrarVentaAdmin(carrito46,
                                        idCliente6,
                                        Venta.MetodoPago.EFECTY,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("05/03/2026"));

                        // Venta 47
                        Carrito carrito47 = new Carrito();
                        carrito47.agregar(producto8.dato(), 1L);
                        carrito47.agregar(producto24.dato(), 2L);
                        carrito47.agregar(producto25.dato(), 1L);
                        RespuestaControlador<Venta> venta47 = ventaControlador.registrarVentaAdmin(carrito47,
                                        idCliente7,
                                        Venta.MetodoPago.TARJETA,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("12/03/2026"));

                        // Venta 48
                        Carrito carrito48 = new Carrito();
                        carrito48.agregar(producto2.dato(), 2L);
                        carrito48.agregar(producto16.dato(), 1L);
                        RespuestaControlador<Venta> venta48 = ventaControlador.registrarVentaAdmin(carrito48,
                                        idCliente8,
                                        Venta.MetodoPago.PSE,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("20/03/2026"));

                        // Venta 49
                        Carrito carrito49 = new Carrito();
                        carrito49.agregar(producto3.dato(), 1L);
                        carrito49.agregar(producto11.dato(), 2L);
                        carrito49.agregar(producto23.dato(), 1L);
                        RespuestaControlador<Venta> venta49 = ventaControlador.registrarVentaAdmin(carrito49,
                                        idCliente9,
                                        Venta.MetodoPago.EFECTY,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("28/03/2026"));

                        // Venta 50
                        Carrito carrito50 = new Carrito();
                        carrito50.agregar(producto15.dato(), 2L);
                        carrito50.agregar(producto17.dato(), 1L);
                        carrito50.agregar(producto22.dato(), 1L);
                        RespuestaControlador<Venta> venta50 = ventaControlador.registrarVentaAdmin(carrito50,
                                        idCliente10,
                                        Venta.MetodoPago.TARJETA,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("05/04/2026"));

                        // Venta 51
                        Carrito carrito51 = new Carrito();
                        carrito51.agregar(producto1.dato(), 1L);
                        carrito51.agregar(producto8.dato(), 2L);
                        RespuestaControlador<Venta> venta51 = ventaControlador.registrarVentaAdmin(carrito51,
                                        idCliente1,
                                        Venta.MetodoPago.EFECTY,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("12/04/2026"));

                        // Venta 52
                        Carrito carrito52 = new Carrito();
                        carrito52.agregar(producto22.dato(), 3L);
                        carrito52.agregar(producto24.dato(), 1L);
                        RespuestaControlador<Venta> venta52 = ventaControlador.registrarVentaAdmin(carrito52,
                                        idCliente3,
                                        Venta.MetodoPago.TARJETA,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("19/04/2026"));

                        // Venta 53
                        Carrito carrito53 = new Carrito();
                        carrito53.agregar(producto4.dato(), 2L);
                        carrito53.agregar(producto11.dato(), 2L);
                        RespuestaControlador<Venta> venta53 = ventaControlador.registrarVentaAdmin(carrito53,
                                        idCliente5,
                                        Venta.MetodoPago.PSE,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("26/04/2026"));

                        // Venta 54
                        Carrito carrito54 = new Carrito();
                        carrito54.agregar(producto9.dato(), 1L);
                        carrito54.agregar(producto25.dato(), 3L);
                        RespuestaControlador<Venta> venta54 = ventaControlador.registrarVentaAdmin(carrito54,
                                        idCliente7,
                                        Venta.MetodoPago.EFECTY,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("08/05/2026"));

                        // Venta 55
                        Carrito carrito55 = new Carrito();
                        carrito55.agregar(producto2.dato(), 2L);
                        carrito55.agregar(producto16.dato(), 2L);
                        RespuestaControlador<Venta> venta55 = ventaControlador.registrarVentaAdmin(carrito55,
                                        idCliente9,
                                        Venta.MetodoPago.TARJETA,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("20/05/2026"));

                        // Venta 56
                        Carrito carrito56 = new Carrito();
                        carrito56.agregar(producto56.dato(), 1L);
                        RespuestaControlador<Venta> venta56 = ventaControlador.registrarVentaAdmin(carrito56,
                                        idCliente11,
                                        Venta.MetodoPago.TARJETA,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("20/05/2026"));

                        // Venta 57
                        Carrito carrito57 = new Carrito();
                        carrito57.agregar(producto56.dato(), 2L);
                        RespuestaControlador<Venta> venta57 = ventaControlador.registrarVentaAdmin(carrito57,
                                        idCliente6,
                                        Venta.MetodoPago.EFECTY,
                                        new SimpleDateFormat("dd/MM/yyyy").parse("20/01/2026"));

                } catch (Exception e) {
                        System.out.println("Error en fecha");
                }

        }

        /**
         * Aplica estilos visuales a los campos de texto y contraseña:
         * fondo transparente, sin borde exterior y línea inferior blanca,
         * alineados con el estilo minimalista de la imagen de referencia.
         */
        private void aplicarEstilosCampos() {
                for (javax.swing.JComponent campo : new javax.swing.JComponent[] { tx_user, tx_passwd }) {
                        campo.setOpaque(false);
                        campo.setBackground(new Color(0, 0, 0, 0));
                        campo.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_LINEA));
                        campo.setForeground(COLOR_LINEA);
                        javax.swing.UIManager.put("TextField.background", new Color(0, 0, 0, 0));
                        javax.swing.UIManager.put("PasswordField.background", new Color(0, 0, 0, 0));
                }
        }

        /**
         * Configura el botón "INICIAR SESIÓN" con el estilo de referencia:
         * color azul oscuro, texto en mayúsculas, sin borde pintado y cursor de mano.
         */
        private void aplicarEstiloBoton() {
                btn_login.setBackground(COLOR_BOTON);
                btn_login.setForeground(COLOR_LINEA);
                btn_login.setFont(new java.awt.Font("Segoe UI Light", java.awt.Font.BOLD, 13));
                btn_login.setText("INICIAR SESIÓN");
                btn_login.setBorderPainted(false);
                btn_login.setFocusPainted(false);
                btn_login.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        }

        /**
         * Registra los placeholders (texto gris de ayuda) en los campos.
         * El campo de contraseña muestra el texto sin máscara hasta que el usuario
         * escribe.
         */
        private void addPlaceholders() {
                setPlaceholder(tx_user, "Ingresa tu Usuario");
                setPasswordPlaceholder(tx_passwd, "Ingresa tu Contraseña");
        }

        /**
         * Placeholder genérico para JTextField: muestra texto blanco al perder foco
         * y lo limpia al ganar foco si el contenido sigue siendo el placeholder.
         */
        private void setPlaceholder(javax.swing.JTextField field, String ph) {
                field.setText(ph);
                field.setForeground(java.awt.Color.WHITE);
                field.addFocusListener(new java.awt.event.FocusAdapter() {
                        @Override
                        public void focusGained(java.awt.event.FocusEvent e) {
                                if (field.getText().equals(ph)) {
                                        field.setText("");
                                        field.setForeground(COLOR_LINEA);
                                }
                        }

                        @Override
                        public void focusLost(java.awt.event.FocusEvent e) {
                                if (field.getText().isEmpty()) {
                                        field.setText(ph);
                                        field.setForeground(java.awt.Color.WHITE);
                                }
                        }
                });
        }

        /**
         * Placeholder para JPasswordField: sin máscara al mostrar el texto de ayuda,
         * activa el carácter '•' al ganar foco y lo desactiva si queda vacío.
         */
        private void setPasswordPlaceholder(javax.swing.JPasswordField pass, String ph) {
                pass.setText(ph);
                pass.setForeground(java.awt.Color.WHITE);
                pass.setEchoChar('\0');
                pass.addFocusListener(new java.awt.event.FocusAdapter() {
                        @Override
                        public void focusGained(java.awt.event.FocusEvent e) {
                                if (String.valueOf(pass.getPassword()).equals(ph)) {
                                        pass.setText("");
                                        pass.setEchoChar('•');
                                        pass.setForeground(COLOR_LINEA);
                                }
                        }

                        @Override
                        public void focusLost(java.awt.event.FocusEvent e) {
                                if (pass.getPassword().length == 0) {
                                        pass.setText(ph);
                                        pass.setEchoChar('\0');
                                        pass.setForeground(java.awt.Color.WHITE);
                                }
                        }
                });
        }

        /**
         * Lógica principal de autenticación. Valida entradas y enruta segun el tipo de
         * usuario:
         * <ul>
         * <li><b>Admin</b> (usuario "Admin", contraseña "12345"): abre el panel de
         * administracion.
         * El Admin trabaja contra LAS DOS bases de datos (local + nube) y arranca la
         * sincronizacion (monitor de red).</li>
         * <li><b>Cliente</b> (uno de los usuarios de demostracion, ej. juan/juan): abre
         * el menu del
         * cliente. El cliente trabaja UNICAMENTE contra la nube; el sistema lo asocia
         * al Cliente
         * real cuyo nombre coincide con el usuario.</li>
         * </ul>
         * Marca los campos con borde rojo si la validación falla.
         */
        public void InicioSesion() {
                String usuario = tx_user.getText().trim();
                String contrasena = String.valueOf(tx_passwd.getPassword());

                if (usuario.isEmpty() || contrasena.isEmpty()) {
                        mostrarErrorCampos();
                        return;
                }

                if (usuario.equals("Admin")) {
                        iniciarSesionAdmin(usuario, contrasena);
                } else if (CapaLogicaNegocio.Logica_Negocio.CredencialesClienteDemo.esUsuarioCliente(usuario)) {
                        iniciarSesionClienteDemo(usuario, contrasena);
                } else {
                        mostrarErrorCampos();
                }
        }

        /**
         * Flujo de inicio de sesion del Administrador. Valida credenciales (SHA-256),
         * asegura la
         * conexion a las DOS bases de datos (local + nube), arranca el monitor de red
         * para la
         * sincronizacion Local->Nube y abre el panel de administracion.
         */
        private void iniciarSesionAdmin(String usuario, String contrasena) {
                usuAdmin = new Administrador("1", "Admin", "12345", "1", "10");

                String cifrarusu = HelperCifrado.CifrarSHA256(usuario);
                String cifrarcontra = HelperCifrado.CifrarSHA256(contrasena);

                if (!usuAdmin.LogOn(cifrarusu, cifrarcontra)) {
                        mostrarErrorCampos();
                        return;
                }

                // El Admin usa local + nube: se asegura la conexion local y se levanta la
                // sincronizacion.
                Conexion.getConexionLocal();
                Conexion.getConexionNube();

                HelperMonitorRed monitor = new HelperMonitorRed();
                monitor.setDaemon(true);
                monitor.start();

                JOptionPane.showMessageDialog(null, "Bienvenido Administrador");
                new MenuAdmin().setVisible(true);
                dispose();
        }

        /**
         * Flujo de inicio de sesion de un Cliente de demostracion. El cliente opera
         * SOLO contra la nube:
         * no se toca la base local ni se arranca la sincronizacion. Se valida la
         * credencial predefinida y
         * se busca en la nube el Cliente real con el mismo nombre para asociar la
         * sesion.
         */
        private void iniciarSesionClienteDemo(String usuario, String contrasena) {
                // El cliente solo necesita la nube.
                Conexion.getConexionNube();

                CapaLogicaNegocio.Controlador.ClienteControlador clienteControlador = new CapaLogicaNegocio.Controlador.ClienteControlador();
                CapaLogicaNegocio.Controlador.RespuestaControlador<CapaLogicaNegocio.Logica_Negocio.Cliente> respuesta = clienteControlador
                                .iniciarSesionCliente(usuario, contrasena);

                if (!respuesta.exito() || respuesta.dato() == null) {
                        JOptionPane.showMessageDialog(null,
                                        respuesta.mensaje() != null ? respuesta.mensaje()
                                                        : "Usuario o contraseña inválida");
                        mostrarErrorCampos();
                        return;
                }

                CapaLogicaNegocio.Logica_Negocio.SesionCliente.iniciarSesion(respuesta.dato());
                JOptionPane.showMessageDialog(null, "Bienvenido " + respuesta.dato().getNombre());
                new MenuCliente().setVisible(true);
                dispose();
        }

        /**
         * Muestra el mensaje de error y resalta los campos con borde rojo.
         * Centraliza la lógica repetida de error en un único punto.
         */
        private void mostrarErrorCampos() {
                tx_user.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_ERROR));
                tx_passwd.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_ERROR));
                JOptionPane.showMessageDialog(null, "Usuario o contraseña inválida");
        }

        /**
         * Carga la imagen de fondo desde el sistema de archivos y la asigna al JLabel
         * fondo.
         */
        public void establecerFondo() {
                Image img = OSHelper.cargarImagenPng("imgFondo2");
                if (img != null) fondo.setIcon(new ImageIcon(img));
        }

        public void establecerIconoUsuario() {
                Image img = OSHelper.cargarImagenPng("user");
                if (img != null) icono_user.setIcon(new ImageIcon(img));
        }

        // <editor-fold defaultstate="collapsed" desc="Generated
        // Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                icono_user = new javax.swing.JLabel();
                tx_user = new javax.swing.JTextField();
                tx_passwd = new javax.swing.JPasswordField();
                btn_login = new javax.swing.JButton();
                fondo = new javax.swing.JLabel();

                setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
                setTitle("Iniciar Sesion Administrador");
                setMinimumSize(new java.awt.Dimension(490, 560));
                setPreferredSize(new java.awt.Dimension(490, 560));
                setResizable(false);
                getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
                getContentPane().add(icono_user, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 70, 160, 150));

                tx_user.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
                tx_user.setForeground(new java.awt.Color(0, 0, 0));
                getContentPane().add(tx_user, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 270, 260, 40));

                tx_passwd.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
                tx_passwd.setForeground(new java.awt.Color(0, 0, 0));
                tx_passwd.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                tx_passwdActionPerformed(evt);
                        }
                });
                getContentPane().add(tx_passwd, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 330, 260, 40));

                btn_login.setBackground(new java.awt.Color(0, 204, 102));
                btn_login.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
                btn_login.setForeground(new java.awt.Color(255, 255, 255));
                btn_login.setText("Iniciar Sesion");
                btn_login.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                btn_loginActionPerformed(evt);
                        }
                });
                getContentPane().add(btn_login, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 410, 120, 50));

                fondo.setText("jLabel1");
                getContentPane().add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 480, 560));

                setBounds(0, 0, 494, 597);
        }// </editor-fold>//GEN-END:initComponents

        private void tx_passwdActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tx_passwdActionPerformed
                // TODO add your handling code here:
        }// GEN-LAST:event_tx_passwdActionPerformed

        private void btn_loginActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_loginActionPerformed
                InicioSesion();
        }// GEN-LAST:event_btn_loginActionPerformed

        public static void main(String args[]) {
                // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
                // (optional) ">
                /*
                 * If Nimbus (introduced in Java SE 6) is not available, stay with the default
                 * look and feel.
                 * For details see
                 * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
                 */
                try {
                        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
                                        .getInstalledLookAndFeels()) {
                                if ("Nimbus".equals(info.getName())) {
                                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                                        break;
                                }
                        }
                } catch (ClassNotFoundException ex) {
                        java.util.logging.Logger.getLogger(InicioSesion.class.getName()).log(
                                        java.util.logging.Level.SEVERE, null,
                                        ex);
                } catch (InstantiationException ex) {
                        java.util.logging.Logger.getLogger(InicioSesion.class.getName()).log(
                                        java.util.logging.Level.SEVERE, null,
                                        ex);
                } catch (IllegalAccessException ex) {
                        java.util.logging.Logger.getLogger(InicioSesion.class.getName()).log(
                                        java.util.logging.Level.SEVERE, null,
                                        ex);
                } catch (javax.swing.UnsupportedLookAndFeelException ex) {
                        java.util.logging.Logger.getLogger(InicioSesion.class.getName()).log(
                                        java.util.logging.Level.SEVERE, null,
                                        ex);
                }
                // </editor-fold>
                java.awt.EventQueue.invokeLater(new Runnable() {
                        public void run() {
                                new InicioSesion().setVisible(true);
                        }
                });
        }

        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JButton btn_login;
        private javax.swing.JLabel fondo;
        private javax.swing.JLabel icono_user;
        private javax.swing.JPasswordField tx_passwd;
        private javax.swing.JTextField tx_user;
        // End of variables declaration//GEN-END:variables
}
