package com.ddam_a1.gestordeinventario.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.datos.AlmacenamientoLocal
import com.ddam_a1.gestordeinventario.modelo.Rol
import com.ddam_a1.gestordeinventario.datos.Notificaciones
import com.ddam_a1.gestordeinventario.ui.EstadoApp
import com.ddam_a1.gestordeinventario.ui.componentes.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.componentes.BotonIcono
import com.ddam_a1.gestordeinventario.ui.componentes.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.componentes.BotonSecundario
import com.ddam_a1.gestordeinventario.ui.componentes.CampoTexto
import com.ddam_a1.gestordeinventario.ui.componentes.ChipFiltro
import com.ddam_a1.gestordeinventario.ui.componentes.EncabezadoSeccion
import com.ddam_a1.gestordeinventario.ui.componentes.FilaLista
import com.ddam_a1.gestordeinventario.ui.componentes.Iconos
import com.ddam_a1.gestordeinventario.ui.componentes.Insignia
import com.ddam_a1.gestordeinventario.ui.componentes.TarjetaSuave
import com.ddam_a1.gestordeinventario.ui.hoy
import com.ddam_a1.gestordeinventario.datos.Usuarios
import com.ddam_a1.gestordeinventario.datos.Ventas

/** Pantalla 19 · Configuración. */
@Composable
fun PantallaConfiguracion(
    onExportar: () -> Unit,
    onUsuarios: () -> Unit,
    onAtras: () -> Unit,
    onSalir: () -> Unit
) -> Unit) {
    EstadoApp.version
    val logs = AlmacenamientoLocal.consultarHistorial().reversed()

    Marco(barra = { BarraSuperior("Configuración", onAtras = { onAtras() }) }) {
        item { EncabezadoSeccion("Negocio") }
        item {
            FilaLista(
                titulo = "Equipo y permisos",
                subtitulo = if (Usuarios.esModoEquipo())
                    "Modo equipo · " + Usuarios.obtenerTodos().size + " usuarios" else "Modo individual",
                onClick = { onUsuarios() }
            )
        }
        item { EncabezadoSeccion("Datos") }
        item {
            FilaLista(
                titulo = "Exportar datos",
                subtitulo = "SQL, .xlsx o .csv con contraseña",
                onClick = { onExportar() }
            )
        }
        item { EncabezadoSeccion("Historial de cambios · " + logs.size) }
        if (logs.isEmpty()) {
            item {
                Text("Todavía no hay movimientos registrados.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(minOf(logs.size, 15)) { i ->
                val l = logs[i]
                FilaLista(titulo = l.descripcion, subtitulo = l.fecha + " · " + l.tipo)
            }
        }
        item {
            Spacer(Modifier.height(8.dp))
            BotonSecundario("Cerrar sesión", color = MaterialTheme.colorScheme.error) {
                EstadoApp.cerrarSesion()
                onSalir()
            }
        }
        item {
            Text("Los datos se guardan solo en este dispositivo.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
