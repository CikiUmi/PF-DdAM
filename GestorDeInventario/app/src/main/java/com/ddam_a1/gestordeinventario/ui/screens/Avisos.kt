package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.ddam_a1.gestordeinventario.modelClasses.Aviso
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.EncabezadoSeccion
import com.ddam_a1.gestordeinventario.ui.components.FilaLista
import com.ddam_a1.gestordeinventario.ui.components.TarjetaSuave

/** Pantalla 16 - Avisos (RF16, RF17). */
@Composable
fun PantallaAvisos(
    stockBajo: List<Aviso>,
    porCaducar: List<Aviso>,
    onAviso: (Aviso) -> Unit,
    onAtras: () -> Unit
) {
    Marco(barra = { BarraSuperior("Avisos", onAtras = onAtras) }) {
        // Ahora hay dos clases de stock bajo: materiales y productos
        // terminados. Al tocarlos llevan a sitios distintos, y de eso se
        // encarga quien navega.
        item { EncabezadoSeccion("Stock bajo (" + stockBajo.size + ")") }
        if (stockBajo.isEmpty()) {
            item {
                Text("Todo el inventario esta por encima de su umbral.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(stockBajo.size) { i ->
                val aviso = stockBajo[i]
                FilaLista(titulo = aviso.mensaje, colorPunto = MaterialTheme.colorScheme.error,
                    onClick = { onAviso(aviso) })
            }
        }

        item { EncabezadoSeccion("Por caducar (" + porCaducar.size + ")") }
        if (porCaducar.isEmpty()) {
            item {
                Text("Ningun material caduca dentro de su antelacion configurada.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(porCaducar.size) { i ->
                val aviso = porCaducar[i]
                FilaLista(titulo = aviso.mensaje, colorPunto = MaterialTheme.colorScheme.tertiary,
                    onClick = { onAviso(aviso) })
            }
        }

        item {
            TarjetaSuave {
                Text("Los avisos se recalculan cuando cambia el inventario. Ajusta los umbrales en cada material.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
