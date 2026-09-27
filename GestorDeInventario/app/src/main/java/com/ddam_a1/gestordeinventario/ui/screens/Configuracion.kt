package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.ddam_a1.gestordeinventario.modelClasses.RegistroLog
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonDestructivo
import com.ddam_a1.gestordeinventario.ui.components.EncabezadoSeccion
import com.ddam_a1.gestordeinventario.ui.components.FilaLista

/**
 * Pantalla 19 - Configuracion.
 *
 * Es la unica que junta datos de los dos mundos: la bitacora viene del
 * inventario y el modo de equipo de la sesion. Los dos llegan ya resueltos.
 */
@Composable
fun PantallaConfiguracion(
    nombreNegocio: String,
    modoEquipo: Boolean,
    totalUsuarios: Int,
    bitacora: List<RegistroLog>,
    onExportar: () -> Unit,
    onUsuarios: () -> Unit,
    onAtras: () -> Unit,
    onSalir: () -> Unit
) {
    Marco(
        barra = {
            BarraSuperior(
                "Configuracion",
                // Un dato guardado que no se ve en ningun lado es casi tan
                // malo como uno perdido. Aqui es donde se comprueba que si
                // quedo escrito.
                subtitulo = nombreNegocio.ifBlank { null },
                onAtras = onAtras
            )
        }
    ) {
        item { EncabezadoSeccion("Negocio") }
        item {
            FilaLista(
                titulo = "Equipo y permisos",
                subtitulo = if (modoEquipo) "Modo equipo - " + totalUsuarios + " usuarios"
                            else "Modo individual",
                onClick = { onUsuarios() }
            )
        }
        item { EncabezadoSeccion("Datos") }
        item {
            FilaLista(
                titulo = "Exportar datos",
                subtitulo = "Las ventas en un archivo .csv",
                onClick = { onExportar() }
            )
        }
        // Cerrar sesion va ANTES del historial, no despues.
        //
        // Estaba al final, debajo de hasta 15 renglones de bitacora. Con pocos
        // movimientos se alcanzaba a ver; en cuanto registrabas una venta (que
        // deja su propia entrada) el boton se iba abajo del doblez y parecia
        // haber desaparecido.
        item { EncabezadoSeccion("Sesion") }
        item {
            BotonDestructivo("Cerrar sesion") { onSalir() }
        }

        item { EncabezadoSeccion("Historial de cambios - " + bitacora.size) }
        if (bitacora.isEmpty()) {
            item {
                Text("Todavia no hay movimientos registrados.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(minOf(bitacora.size, 15)) { i ->
                val registro = bitacora[i]
                FilaLista(titulo = registro.descripcion,
                    subtitulo = registro.fecha + " - " + registro.tipo)
            }
        }
        item {
            Text("Los datos se guardan solo en este dispositivo.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
