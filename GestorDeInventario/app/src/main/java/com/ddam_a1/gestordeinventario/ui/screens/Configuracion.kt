package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.modelClasses.RegistroLog
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonSecundario
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
    modoEquipo: Boolean,
    totalUsuarios: Int,
    bitacora: List<RegistroLog>,
    onExportar: () -> Unit,
    onUsuarios: () -> Unit,
    onAtras: () -> Unit,
    onSalir: () -> Unit
) {
    Marco(barra = { BarraSuperior("Configuracion", onAtras = onAtras) }) {
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
                subtitulo = "SQL, .xlsx o .csv con contrasena",
                onClick = { onExportar() }
            )
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
            Spacer(Modifier.height(8.dp))
            BotonSecundario("Cerrar sesion", color = MaterialTheme.colorScheme.error) { onSalir() }
        }
        item {
            Text("Los datos se guardan solo en este dispositivo.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
