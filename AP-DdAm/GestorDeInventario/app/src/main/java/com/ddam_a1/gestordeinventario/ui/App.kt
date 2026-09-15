package com.ddam_a1.gestordeinventario.ui

import androidx.compose.runtime.Composable
import com.ddam_a1.gestordeinventario.ui.pantallas.*
import com.ddam_a1.gestordeinventario.usuarios.Usuarios

@Composable
fun App() {
    val nav = recordarNavegador(Ruta.Login)

    when (val r = nav.actual) {
        Ruta.Login -> PantallaLogin(
            onEntrar = { u -> EstadoApp.usuario = u; nav.irARaiz(Ruta.Inicio) },
            onConfigurar = { nav.ir(Ruta.CrearAdmin) }
        )
        Ruta.CrearAdmin -> PantallaCrearAdmin { nav.ir(Ruta.ElegirModo) }
        Ruta.ElegirModo -> PantallaElegirModo {
            EstadoApp.usuario = Usuarios.obtenerTodos().firstOrNull()
            nav.irARaiz(Ruta.Inicio)
        }
        Ruta.Inicio -> PantallaInicio(nav)
        Ruta.Estadisticas -> PantallaEstadisticas(nav)
        Ruta.Inventario -> PantallaInventario(nav)
        is Ruta.DetalleMaterial -> PantallaDetalleMaterial(nav, r.id)
        is Ruta.FormularioMaterial -> PantallaFormularioMaterial(nav, r.id)
        Ruta.Catalogo -> PantallaCatalogo(nav)
        is Ruta.DetalleProducto -> PantallaDetalleProducto(nav, r.id)
        is Ruta.FormularioProducto -> PantallaFormularioProducto(nav, r.id)
        is Ruta.Receta -> PantallaReceta(nav, r.productoId)
        is Ruta.Produccion -> PantallaProduccion(nav, r.productoId)
        Ruta.NuevaVenta -> PantallaNuevaVenta(nav)
        Ruta.HistorialVentas -> PantallaHistorialVentas(nav)
        Ruta.Avisos -> PantallaAvisos(nav)
        Ruta.Usuarios -> PantallaUsuarios(nav)
        Ruta.Permisos -> PantallaPermisos(nav)
        Ruta.Configuracion -> PantallaConfiguracion(nav) { nav.irARaiz(Ruta.Login) }
        Ruta.Exportar -> PantallaExportar(nav)
    }
}
