package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    leidos: List<Aviso>,
    onAviso: (Aviso) -> Unit,
    onMarcarLeido: (Aviso) -> Unit,
    onMarcarTodos: () -> Unit,
    onRestaurar: () -> Unit,
    onAtras: () -> Unit
) {
    Marco(barra = { BarraSuperior("Avisos", onAtras = onAtras) }) {
        val pendientes = stockBajo.size + porCaducar.size
        if (pendientes > 0) {
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "" + pendientes + " sin leer",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onMarcarTodos) { Text("Marcar todos") }
                }
            }
        }

        // Ahora hay dos clases de stock bajo: materiales y productos
        // terminados. Al tocarlos llevan a sitios distintos, y de eso se
        // encarga quien navega.
        item { EncabezadoSeccion("Stock bajo (" + stockBajo.size + ")") }
        if (stockBajo.isEmpty()) {
            item {
                Text(
                    "Todo el inventario esta por encima de su umbral.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(stockBajo.size) { i ->
                AvisoPendiente(stockBajo[i], MaterialTheme.colorScheme.error, onAviso, onMarcarLeido)
            }
        }

        item { EncabezadoSeccion("Por caducar (" + porCaducar.size + ")") }
        if (porCaducar.isEmpty()) {
            item {
                Text(
                    "Ningun material caduca dentro de su antelacion configurada.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(porCaducar.size) { i ->
                AvisoPendiente(porCaducar[i], MaterialTheme.colorScheme.tertiary, onAviso, onMarcarLeido)
            }
        }

        if (leidos.isNotEmpty()) {
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.weight(1f)) {
                        EncabezadoSeccion("Leidos (" + leidos.size + ")")
                    }
                    TextButton(onClick = onRestaurar) { Text("Restaurar") }
                }
            }
            items(leidos.size) { i ->
                val aviso = leidos[i]
                // Sin punto de color: leido no es urgente. La diferencia no se
                // comunica solo por color, tambien por la seccion en la que esta.
                FilaLista(titulo = aviso.mensaje, onClick = { onAviso(aviso) })
            }
        }

        item {
            TarjetaSuave {
                Text(
                    "Los avisos se recalculan cuando cambia el inventario. Si repones un material, " +
                            "su aviso desaparece; si vuelve a bajar, reaparece sin leer.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/** Un aviso sin leer: el renglon lleva al detalle y el boton lo archiva. */
@Composable
private fun AvisoPendiente(
    aviso: Aviso,
    color: Color,
    onAviso: (Aviso) -> Unit,
    onMarcarLeido: (Aviso) -> Unit
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Row(Modifier.weight(1f)) {
            FilaLista(titulo = aviso.mensaje, colorPunto = color, onClick = { onAviso(aviso) })
        }
        TextButton(onClick = { onMarcarLeido(aviso) }) { Text("Leido") }
    }
}
