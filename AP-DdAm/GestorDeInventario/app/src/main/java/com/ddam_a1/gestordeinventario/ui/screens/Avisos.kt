package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.ddam_a1.gestordeinventario.datos.Notificaciones
import com.ddam_a1.gestordeinventario.ui.EstadoApp
import com.ddam_a1.gestordeinventario.ui.componentes.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.componentes.EncabezadoSeccion
import com.ddam_a1.gestordeinventario.ui.componentes.FilaLista
import com.ddam_a1.gestordeinventario.ui.componentes.TarjetaSuave
import com.ddam_a1.gestordeinventario.ui.hoy

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
