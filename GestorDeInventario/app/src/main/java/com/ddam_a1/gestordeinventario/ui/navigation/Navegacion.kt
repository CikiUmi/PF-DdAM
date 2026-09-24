package com.ddam_a1.gestordeinventario.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ddam_a1.gestordeinventario.ui.EstadoApp
import com.ddam_a1.gestordeinventario.ui.hoy
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
            PantallaLogin(
                onEntrar = { usuario ->
                    EstadoApp.usuario = usuario
                    entrarALaApp()
                },
                onConfigurar = { navController.navigate(RUTA_CREAR_ADMIN) }
            )
        }

        composable(RUTA_CREAR_ADMIN) {
            PantallaCrearAdmin(onContinuar = { navController.navigate(RUTA_ELEGIR_MODO) })
        }

        composable(RUTA_ELEGIR_MODO) {
            PantallaElegirModo(onEmpezar = { entrarALaApp() })
        }

        // ---------- INICIO ----------

        composable(RUTA_INICIO) {
            PantallaInicio(
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
            PantallaEstadisticas(onAtras = atras)
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
            PantallaCatalogo(
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
            PantallaDetalleProducto(
                id = id,
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
            PantallaFormularioProducto(
                id = entrada.arguments?.getString(ARG_ID),
                onReceta = { productoId -> navController.navigate(rutaReceta(productoId)) },
                onAtras = atras
            )
        }

        composable(
            route = RUTA_RECETA,
            arguments = listOf(navArgument(ARG_ID) { type = NavType.StringType })
        ) { entrada ->
            PantallaReceta(
                productoId = entrada.arguments?.getString(ARG_ID).orEmpty(),
                onAtras = atras
            )
        }

        composable(
            route = RUTA_PRODUCCION,
            arguments = listOf(navArgument(ARG_ID) { type = NavType.StringType })
        ) { entrada ->
            PantallaProduccion(
                productoId = entrada.arguments?.getString(ARG_ID).orEmpty(),
                onAtras = atras
            )
        }

        // ---------- VENTAS ----------

        composable(RUTA_NUEVA_VENTA) {
            PantallaNuevaVenta(
                onVentaRegistrada = {
                    navController.navigate(RUTA_HISTORIAL_VENTAS) {
                        popUpTo(RUTA_INICIO) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onAtras = atras
            )
        }

        composable(RUTA_HISTORIAL_VENTAS) {
            PantallaHistorialVentas(
                onNuevaVenta = { navController.navigate(RUTA_NUEVA_VENTA) },
                onDestino = { destino -> irADestino(destino) }
            )
        }

        // ---------- ADMINISTRACION ----------

        composable(RUTA_AVISOS) {
            PantallaAvisos(
                onMaterial = { id -> navController.navigate(rutaDetalleMaterial(id)) },
                onAtras = atras
            )
        }

        composable(RUTA_USUARIOS) {
            PantallaUsuarios(
                onPermisos = { navController.navigate(RUTA_PERMISOS) },
                onAtras = atras
            )
        }

        composable(RUTA_PERMISOS) {
            PantallaPermisos(onAtras = atras)
        }

        composable(RUTA_CONFIGURACION) {
            PantallaConfiguracion(
                onExportar = { navController.navigate(RUTA_EXPORTAR) },
                onUsuarios = { navController.navigate(RUTA_USUARIOS) },
                onAtras = atras,
                onSalir = {
                    EstadoApp.cerrarSesion()
                    navController.navigate(RUTA_LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable(RUTA_EXPORTAR) {
            PantallaExportar(onAtras = atras)
        }
    }
}
