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

/** Pantalla 16 · Avisos (RF16, RF17). */
@Composable
fun PantallaAvisos(onMaterial: (String) -> Unit, onAtras: () -> Unit) {
    EstadoApp.version
    val bajos = Notificaciones.revisarStockBajo()
    val caducan = Notificaciones.revisarCaducidadesProximas(hoy())

    Marco(barra = { BarraSuperior("Avisos", onAtras = { onAtras() }) }) {
        item { EncabezadoSeccion("Stock bajo (" + bajos.size + ")") }
        if (bajos.isEmpty()) {
            item {
                Text("Todo el inventario está por encima de su umbral.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(bajos.size) { i ->
                val a = bajos[i]
                FilaLista(titulo = a.mensaje, colorPunto = MaterialTheme.colorScheme.error,
                    onClick = { onMaterial(a.materialId) })
            }
        }

        item { EncabezadoSeccion("Por caducar (" + caducan.size + ")") }
        if (caducan.isEmpty()) {
            item {
                Text("Ningún material caduca dentro de su antelación configurada.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(caducan.size) { i ->
                val a = caducan[i]
                FilaLista(titulo = a.mensaje, colorPunto = MaterialTheme.colorScheme.tertiary,
                    onClick = { onMaterial(a.materialId) })
            }
        }

        item {
            TarjetaSuave {
                Text("Los avisos se revisan al abrir la app. Ajusta los umbrales en cada material.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
