package com.ddam_a1.gestordeinventario.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ddam_a1.gestordeinventario.data.ErrorVenta
import com.ddam_a1.gestordeinventario.data.ResultadoVenta
import com.ddam_a1.gestordeinventario.modelClasses.Periodo
import com.ddam_a1.gestordeinventario.modelClasses.Rol
import com.ddam_a1.gestordeinventario.ui.hoy
import com.ddam_a1.gestordeinventario.ui.screens.ResumenInicio
import com.ddam_a1.gestordeinventario.ui.screens.VentaPorProducto
import com.ddam_a1.gestordeinventario.ui.screens.RenglonProduccion
import com.ddam_a1.gestordeinventario.ui.screens.RenglonReceta
import com.ddam_a1.gestordeinventario.ui.screens.UsoEnProducto
import com.ddam_a1.gestordeinventario.ui.components.DestinoBarra
import com.ddam_a1.gestordeinventario.ui.screens.PantallaAvisos
import com.ddam_a1.gestordeinventario.ui.screens.PantallaCatalogo
import com.ddam_a1.gestordeinventario.ui.screens.PantallaConfiguracion
import com.ddam_a1.gestordeinventario.ui.screens.PantallaCrearAdmin
import com.ddam_a1.gestordeinventario.ui.screens.PantallaDetalleMaterial
import com.ddam_a1.gestordeinventario.ui.screens.PantallaDetalleProducto
import com.ddam_a1.gestordeinventario.ui.screens.PantallaElegirModo
import com.ddam_a1.gestordeinventario.ui.screens.PantallaEstadisticas
import com.ddam_a1.gestordeinventario.ui.screens.PantallaExportar
import com.ddam_a1.gestordeinventario.ui.screens.PantallaFormularioMaterial
import com.ddam_a1.gestordeinventario.ui.screens.PantallaFormularioProducto
import com.ddam_a1.gestordeinventario.ui.screens.PantallaHistorialVentas
import com.ddam_a1.gestordeinventario.ui.screens.PantallaInicio
import com.ddam_a1.gestordeinventario.ui.screens.PantallaInventario
import com.ddam_a1.gestordeinventario.ui.screens.PantallaLogin
import com.ddam_a1.gestordeinventario.ui.screens.PantallaNuevaVenta
import com.ddam_a1.gestordeinventario.ui.screens.PantallaPermisos
import com.ddam_a1.gestordeinventario.ui.screens.PantallaProduccion
import com.ddam_a1.gestordeinventario.ui.screens.PantallaReceta
import com.ddam_a1.gestordeinventario.ui.screens.PantallaUsuarios
import com.ddam_a1.gestordeinventario.viewModel.InventarioViewModel
import com.ddam_a1.gestordeinventario.viewModel.ResultadoAltaUsuario
import com.ddam_a1.gestordeinventario.viewModel.SesionViewModel
import kotlinx.coroutines.launch

// ============================================================
//  EL NAVHOST
//
//  El unico que conoce el NavController. Las pantallas reciben datos y
//  callbacks; ninguna sabe a donde lleva el boton que dibuja, solo que al
//  tocarlo hay que avisar.
//
//  Esto es lo que antes hacia `App.kt` con un `when` sobre una sealed class.
//  Cambia a NavHost real para tener pila de verdad, boton atras del sistema y
//  argumentos con tipo.
// ============================================================

@Composable
fun GestorNavHost(modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    // UN solo ViewModel para toda la app: se pide AQUI, arriba del NavHost, no
    // dentro de cada destino. Si lo pidieras adentro, Compose te daria uno
    // distinto por pantalla (uno por NavBackStackEntry) y el detalle de un
    // material no se enteraria de lo que acabas de guardar en su formulario.
    val inventarioVm: InventarioViewModel = hiltViewModel()
    val sesionVm: SesionViewModel = hiltViewModel()

    // Para las operaciones que devuelven un resultado y hay que esperarlo
    // antes de decidir a donde ir (crear un producto, registrar produccion).
    val scope = rememberCoroutineScope()

    // Ir a uno de los cuatro destinos de la barra de abajo.
    //
    // `popUpTo` evita que se apilen inicios e inventarios infinitos al ir y
    // venir; `launchSingleTop` evita dos copias de la misma pantalla si le
    // picas dos veces al mismo icono.
    fun irADestino(destino: DestinoBarra) {
        navController.navigate(destino.ruta) {
            popUpTo(RUTA_INICIO) { inclusive = false }
            launchSingleTop = true
        }
    }

    // Entrar a la app despues del login: se limpia todo lo anterior para que el
    // boton atras no regrese a la pantalla de contrasena.
    fun entrarALaApp() {
        navController.navigate(RUTA_INICIO) {
            popUpTo(0) { inclusive = true }
        }
    }

    val atras: () -> Unit = { navController.popBackStack() }

    NavHost(
        navController = navController,
        startDestination = RUTA_LOGIN,
        modifier = modifier
    ) {

        // ---------- ACCESO ----------

        composable(RUTA_LOGIN) {
            val primerUso by sesionVm.primerUso.collectAsState()
            var errorLogin by remember { mutableStateOf<String?>(null) }

            PantallaLogin(
                primerUso = primerUso,
                error = errorLogin,
                onEntrar = { usuario, clave ->
                    scope.launch {
                        // El ViewModel guarda la sesion si la contrasena es
                        // correcta; aqui solo se decide que hacer con el "no".
                        if (sesionVm.iniciarSesion(usuario, clave) == null) {
                            errorLogin = "Usuario o contrasena incorrectos"
                        } else {
                            entrarALaApp()
                        }
                    }
                },
                onLimpiarError = { errorLogin = null },
                onConfigurar = { navController.navigate(RUTA_CREAR_ADMIN) }
            )
        }

        composable(RUTA_CREAR_ADMIN) {
            PantallaCrearAdmin(
                onCrear = { usuario, clave ->
                    scope.launch {
                        // Devuelve null si el nombre ya esta tomado; en ese caso
                        // no se avanza.
                        if (sesionVm.crearUsuarioAdministrador(usuario, clave) != null) {
                            navController.navigate(RUTA_ELEGIR_MODO)
                        }
                    }
                }
            )
        }

        composable(RUTA_ELEGIR_MODO) {
            PantallaElegirModo(
                onEmpezar = { equipo ->
                    sesionVm.elegirModo(equipo)
                    entrarALaApp()
                }
            )
        }

        // ---------- INICIO ----------

        composable(RUTA_INICIO) {
            val materiales by inventarioVm.materiales.collectAsState()
            val productos by inventarioVm.productos.collectAsState()
            val ventas by inventarioVm.ventas.collectAsState()
            val usuario by sesionVm.usuarioActual.collectAsState()
            val avisos by inventarioVm.avisos(hoy()).collectAsState(initial = emptyList())

            val delMes = inventarioVm.filtrarVentasPorPeriodo(ventas, Periodo.MENSUAL, hoy())

            PantallaInicio(
                nombreUsuario = usuario?.nombreUsuario,
                avisos = avisos,
                resumen = ResumenInicio(
                    ingresosDelMes = inventarioVm.calcularIngresos(delMes),
                    gananciaDelMes = inventarioVm.calcularGanancias(delMes),
                    ventasDelMes = delMes.size,
                    totalMateriales = materiales.size,
                    materialesBajos = materiales.count { inventarioVm.esStockBajo(it) },
                    totalProductos = productos.size
                ),
                masVendidos = inventarioVm.productosMasVendidos(delMes, 3).map { (id, piezas) ->
                    val producto = productos.find { it.id == id }
                    VentaPorProducto(producto?.nombre ?: "Producto", piezas, producto?.precioVenta ?: 0.0)
                },
                onAvisos = { navController.navigate(RUTA_AVISOS) },
                onConfiguracion = { navController.navigate(RUTA_CONFIGURACION) },
                onEstadisticas = { navController.navigate(RUTA_ESTADISTICAS) },
                onNuevaVenta = { navController.navigate(RUTA_NUEVA_VENTA) },
                onInventario = { irADestino(DestinoBarra.INVENTARIO) },
                onCatalogo = { irADestino(DestinoBarra.CATALOGO) },
                onDestino = { destino -> irADestino(destino) }
            )
        }

        composable(RUTA_ESTADISTICAS) {
            val ventas by inventarioVm.ventas.collectAsState()
            val productos by inventarioVm.productos.collectAsState()

            // Arranca en MENSUAL; el historial arranca en DIARIO. Son dos
            // estados independientes a proposito.
            var periodo by remember { mutableStateOf(Periodo.MENSUAL) }

            val delPeriodo = inventarioVm.filtrarVentasPorPeriodo(ventas, periodo, hoy())
            val ingresos = inventarioVm.calcularIngresos(delPeriodo)
            val ganancia = inventarioVm.calcularGanancias(delPeriodo)

            PantallaEstadisticas(
                periodo = periodo,
                onPeriodo = { nuevo -> periodo = nuevo },
                ingresos = ingresos,
                ganancia = ganancia,
                // El costo no se consulta: es lo que queda. El coerce es por si
                // una venta cancelada deja la ganancia arriba de los ingresos.
                costo = (ingresos - ganancia).coerceAtLeast(0.0),
                masVendidos = inventarioVm.productosMasVendidos(delPeriodo, 5).map { (id, piezas) ->
                    val producto = productos.find { it.id == id }
                    VentaPorProducto(producto?.nombre ?: "Producto", piezas, producto?.precioVenta ?: 0.0)
                },
                onAtras = atras
            )
        }

        // ---------- INVENTARIO ----------

        composable(RUTA_INVENTARIO) {
            // `collectAsState` se suscribe al StateFlow y convierte cada emision
            // en estado de Compose. Esa suscripcion es la que hace que la lista
            // se redibuje SOLA cuando cambia el inventario. Ya nadie llama a
            // `EstadoApp.datosCambiaron()`.
            //
            // Y va AQUI DENTRO, no arriba: asi nace y muere con la pantalla,
            // que es lo que el WhileSubscribed(5000) del ViewModel espera para
            // poder soltar el flow.
            val materiales by inventarioVm.materiales.collectAsState()

            PantallaInventario(
                materiales = materiales,
                esStockBajo = { material -> inventarioVm.esStockBajo(material) },
                onMaterial = { id -> navController.navigate(rutaDetalleMaterial(id)) },
                onNuevoMaterial = { navController.navigate(rutaFormularioMaterial(null)) },
                onDestino = { destino -> irADestino(destino) }
            )
        }

        composable(
            route = RUTA_DETALLE_MATERIAL,
            arguments = listOf(navArgument(ARG_ID) { type = NavType.StringType })
        ) { entrada ->
            val id = entrada.arguments?.getString(ARG_ID).orEmpty()
            val materiales by inventarioVm.materiales.collectAsState()
            val productos by inventarioVm.productos.collectAsState()

            // El material se saca de la MISMA lista en vivo, no con una lectura
            // suelta por id. Asi, al agregar un lote, la lista vuelve a emitir y
            // esta pantalla se actualiza sola.
            val material = materiales.find { it.id == id }

            // Masticar la receta es trabajo de aqui, que tiene los productos.
            // La pantalla recibe la respuesta, no los ingredientes crudos.
            val usadoEn = productos.mapNotNull { producto ->
                producto.receta.firstOrNull { it.materialId == id }
                    ?.let { ingrediente ->
                        UsoEnProducto(producto.id, producto.nombre, ingrediente.cantidadUsada)
                    }
            }

            PantallaDetalleMaterial(
                material = material,
                bajo = material != null && inventarioVm.esStockBajo(material),
                usadoEn = usadoEn,
                onAgregarCaducidad = { caducidad ->
                    if (material != null) {
                        inventarioVm.agregarLoteConCaducidad(material.id, material.nombre, caducidad, hoy())
                    }
                },
                onProducto = { productoId -> navController.navigate(rutaDetalleProducto(productoId)) },
                onEditar = { navController.navigate(rutaFormularioMaterial(id)) },
                onAtras = atras
            )
        }

        composable(
            route = RUTA_FORMULARIO_MATERIAL,
            arguments = listOf(navArgument(ARG_ID) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { entrada ->
            val id = entrada.arguments?.getString(ARG_ID)
            val materiales by inventarioVm.materiales.collectAsState()

            PantallaFormularioMaterial(
                material = id?.let { buscado -> materiales.find { it.id == buscado } },
                onGuardar = { datos ->
                    inventarioVm.guardarMaterial(
                        id = id,
                        nombre = datos.nombre,
                        unidad = datos.unidad,
                        cantidad = datos.cantidad,
                        costo = datos.costo,
                        stockMinimo = datos.stockMinimo,
                        diasAvisoCaducidad = datos.diasAvisoCaducidad,
                        fecha = hoy()
                    )
                },
                onAtras = atras
            )
        }

        // ---------- CATALOGO ----------

        composable(RUTA_CATALOGO) {
            val productos by inventarioVm.productos.collectAsState()
            val materiales by inventarioVm.materiales.collectAsState()

            PantallaCatalogo(
                productos = productos,
                // Se recalcula solo: depende de las dos listas. Si sube el
                // precio de un material, el costo de sus productos cambia aqui.
                costos = inventarioVm.costosDeProduccion(productos, materiales),
                onProducto = { id -> navController.navigate(rutaDetalleProducto(id)) },
                onNuevoProducto = { navController.navigate(rutaFormularioProducto(null)) },
                onDestino = { destino -> irADestino(destino) }
            )
        }

        composable(
            route = RUTA_DETALLE_PRODUCTO,
            arguments = listOf(navArgument(ARG_ID) { type = NavType.StringType })
        ) { entrada ->
            val id = entrada.arguments?.getString(ARG_ID).orEmpty()
            val productos by inventarioVm.productos.collectAsState()
            val materiales by inventarioVm.materiales.collectAsState()

            val producto = productos.find { it.id == id }
            val receta = producto?.receta.orEmpty().map { ingrediente ->
                val material = materiales.find { it.id == ingrediente.materialId }
                RenglonReceta(
                    nombre = material?.nombre ?: "Material",
                    unidad = material?.unidadMedida ?: "",
                    costoUnitario = material?.costoUnitario ?: 0.0,
                    cantidadUsada = ingrediente.cantidadUsada
                )
            }

            PantallaDetalleProducto(
                producto = producto,
                costo = inventarioVm.costosDeProduccion(productos, materiales)[id] ?: 0.0,
                receta = receta,
                onEditar = { navController.navigate(rutaFormularioProducto(id)) },
                onProducir = { navController.navigate(rutaProduccion(id)) },
                onReceta = { navController.navigate(rutaReceta(id)) },
                onAtras = atras
            )
        }

        composable(
            route = RUTA_FORMULARIO_PRODUCTO,
            arguments = listOf(navArgument(ARG_ID) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { entrada ->
            val id = entrada.arguments?.getString(ARG_ID)
            val productos by inventarioVm.productos.collectAsState()

            PantallaFormularioProducto(
                producto = id?.let { buscado -> productos.find { it.id == buscado } },
                onGuardar = { datos ->
                    // `guardarProducto` devuelve el id, y hay que esperarlo para
                    // saber a donde ir. Por eso va en el scope y no es un
                    // `launch` escondido dentro del ViewModel.
                    scope.launch {
                        val nuevoId = inventarioVm.guardarProducto(
                            id = id,
                            nombre = datos.nombre,
                            precioVenta = datos.precioVenta,
                            esBajoPedido = datos.esBajoPedido,
                            fecha = hoy()
                        )
                        // Un producto recien creado se va derecho a su receta.
                        if (id == null && nuevoId != null) {
                            navController.navigate(rutaReceta(nuevoId))
                        } else {
                            navController.popBackStack()
                        }
                    }
                },
                onAtras = atras
            )
        }

        composable(
            route = RUTA_RECETA,
            arguments = listOf(navArgument(ARG_ID) { type = NavType.StringType })
        ) { entrada ->
            val productoId = entrada.arguments?.getString(ARG_ID).orEmpty()
            val productos by inventarioVm.productos.collectAsState()
            val materiales by inventarioVm.materiales.collectAsState()
            val producto = productos.find { it.id == productoId }

            PantallaReceta(
                nombreProducto = producto?.nombre ?: "",
                materiales = materiales,
                recetaActual = producto?.receta.orEmpty()
                    .associate { it.materialId to it.cantidadUsada },
                onGuardar = { ingredientes -> inventarioVm.guardarReceta(productoId, ingredientes) },
                onAtras = atras
            )
        }

        composable(
            route = RUTA_PRODUCCION,
            arguments = listOf(navArgument(ARG_ID) { type = NavType.StringType })
        ) { entrada ->
            val productoId = entrada.arguments?.getString(ARG_ID).orEmpty()
            val productos by inventarioVm.productos.collectAsState()
            val materiales by inventarioVm.materiales.collectAsState()
            val producto = productos.find { it.id == productoId }

            // El mensaje de "no alcanzan los materiales" vive aqui porque es la
            // respuesta a una operacion, no un dato de la pantalla.
            var errorProduccion by remember(productoId) { mutableStateOf("") }

            val receta = producto?.receta.orEmpty().map { ingrediente ->
                val material = materiales.find { it.id == ingrediente.materialId }
                RenglonProduccion(
                    nombre = material?.nombre ?: "Material",
                    unidad = material?.unidadMedida ?: "",
                    cantidadPorPieza = ingrediente.cantidadUsada,
                    disponible = material?.cantidadDisponible ?: 0.0
                )
            }

            PantallaProduccion(
                nombreProducto = producto?.nombre ?: "",
                receta = receta,
                error = errorProduccion,
                onProducir = { cantidad, descontar ->
                    scope.launch {
                        val ok = inventarioVm.registrarProduccion(productoId, cantidad, descontar, hoy())
                        if (ok) {
                            navController.popBackStack()
                        } else {
                            errorProduccion =
                                "No alcanzan los materiales para producir " + cantidad + " piezas."
                        }
                    }
                },
                onAtras = atras
            )
        }

        // ---------- VENTAS ----------

        composable(RUTA_NUEVA_VENTA) {
            val productos by inventarioVm.productos.collectAsState()
            var errorVenta by remember { mutableStateOf<String?>(null) }

            PantallaNuevaVenta(
                productos = productos,
                error = errorVenta,
                onDescartarError = { errorVenta = null },
                onConfirmar = { ticket ->
                    scope.launch {
                        val resultado = inventarioVm.registrarVenta(
                            hoy(), ticket.map { it.key to it.value }
                        )
                        // Traducir el motivo a algo que se pueda leer es trabajo
                        // de la capa de interfaz, no del ViewModel ni de la
                        // pantalla: aqui es donde se sabe que se va a mostrar.
                        when (resultado) {
                            is ResultadoVenta.Exito -> {
                                navController.navigate(RUTA_HISTORIAL_VENTAS) {
                                    popUpTo(RUTA_INICIO) { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                            is ResultadoVenta.Fallo -> errorVenta = when (resultado.motivo) {
                                ErrorVenta.MATERIALES_INSUFICIENTES -> "No alcanzan los materiales para todo el ticket."
                                ErrorVenta.STOCK_INSUFICIENTE -> "No hay stock suficiente de alguno de los productos."
                                ErrorVenta.CANTIDAD_INVALIDA -> "Hay una cantidad invalida."
                                ErrorVenta.PRODUCTO_NO_EXISTE -> "Un producto del ticket ya no existe."
                                ErrorVenta.TICKET_VACIO -> "El ticket esta vacio."
                            }
                        }
                    }
                },
                onAtras = atras
            )
        }

        composable(RUTA_HISTORIAL_VENTAS) {
            val ventas by inventarioVm.ventas.collectAsState()
            val productos by inventarioVm.productos.collectAsState()

            // El periodo vive aqui porque de el dependen la lista filtrada y las
            // metricas, y esos los calcula el ViewModel.
            var periodo by remember { mutableStateOf(Periodo.DIARIO) }

            val filtradas = inventarioVm.filtrarVentasPorPeriodo(ventas.reversed(), periodo, hoy())

            PantallaHistorialVentas(
                ventas = filtradas,
                nombreProducto = { id -> productos.find { it.id == id }?.nombre ?: "?" },
                ingresos = inventarioVm.calcularIngresos(filtradas),
                ganancias = inventarioVm.calcularGanancias(filtradas),
                periodo = periodo,
                onPeriodo = { nuevo -> periodo = nuevo },
                onNuevaVenta = { navController.navigate(RUTA_NUEVA_VENTA) },
                onDestino = { destino -> irADestino(destino) }
            )
        }

        // ---------- ADMINISTRACION ----------

        composable(RUTA_AVISOS) {
            // `avisos` cuelga del flujo de materiales: si baja el stock de algo,
            // el aviso aparece sin que nadie vuelva a preguntar.
            val avisos by inventarioVm.avisos(hoy()).collectAsState(initial = emptyList())

            PantallaAvisos(
                stockBajo = avisos.filter { it.tipo == "stock_bajo" },
                porCaducar = avisos.filter { it.tipo == "caducidad" },
                onMaterial = { id -> navController.navigate(rutaDetalleMaterial(id)) },
                onAtras = atras
            )
        }

        composable(RUTA_USUARIOS) {
            val usuarios by sesionVm.usuarios.collectAsState()
            val actual by sesionVm.usuarioActual.collectAsState()

            var errorUsuario by remember { mutableStateOf<String?>(null) }

            PantallaUsuarios(
                usuarios = usuarios,
                usuarioActual = actual,
                esAdmin = actual?.rol == Rol.ADMINISTRADOR,
                error = errorUsuario,
                onCrearUsuario = { nombre, clave, rol ->
                    scope.launch {
                        errorUsuario = when (sesionVm.crearUsuario(nombre, clave, rol, hoy())) {
                            ResultadoAltaUsuario.CREADO -> null
                            ResultadoAltaUsuario.NOMBRE_REPETIDO -> "Ya existe un usuario con ese nombre."
                            ResultadoAltaUsuario.SIN_PERMISO -> "Solo el administrador puede crear usuarios."
                            ResultadoAltaUsuario.DATOS_INCOMPLETOS -> "Falta el usuario o la contrasena."
                        }
                    }
                },
                onPermisos = { navController.navigate(RUTA_PERMISOS) },
                onAtras = atras
            )
        }

        composable(RUTA_PERMISOS) {
            PantallaPermisos(onAtras = atras)
        }

        composable(RUTA_CONFIGURACION) {
            // La unica pantalla que junta los dos mundos: la bitacora es del
            // inventario y el modo de equipo es de la sesion.
            val modoEquipo by sesionVm.modoEquipo.collectAsState()
            val usuarios by sesionVm.usuarios.collectAsState()
            val bitacora by inventarioVm.bitacora.collectAsState()

            PantallaConfiguracion(
                modoEquipo = modoEquipo,
                totalUsuarios = usuarios.size,
                bitacora = bitacora.reversed(),
                onExportar = { navController.navigate(RUTA_EXPORTAR) },
                onUsuarios = { navController.navigate(RUTA_USUARIOS) },
                onAtras = atras,
                onSalir = {
                    sesionVm.cerrarSesion()
                    navController.navigate(RUTA_LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable(RUTA_EXPORTAR) {
            val ventas by inventarioVm.ventas.collectAsState()
            var vistaPrevia by remember { mutableStateOf("") }

            PantallaExportar(
                vistaPrevia = vistaPrevia,
                onExportar = { clave ->
                    scope.launch {
                        val csv = inventarioVm.exportarACSV(
                            "ventas.csv",
                            listOf("id", "fecha", "total", "cancelada"),
                            ventas.map { v ->
                                listOf(v.id, v.fecha, v.total.toString(), v.cancelada.toString())
                            },
                            clave
                        )
                        vistaPrevia =
                            if (csv.isBlank()) "Sin datos que exportar todavia." else csv.take(300)
                        inventarioVm.registrarLog(hoy(), "manual", "Exportacion de datos generada")
                    }
                },
                onAtras = atras
            )
        }
    }
}
