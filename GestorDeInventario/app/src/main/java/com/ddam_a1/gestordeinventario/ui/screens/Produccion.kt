package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ddam_a1.gestordeinventario.ui.cant
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.CampoTexto
import com.ddam_a1.gestordeinventario.ui.components.DialogoSiNo
import com.ddam_a1.gestordeinventario.ui.components.EncabezadoSeccion
import com.ddam_a1.gestordeinventario.ui.components.FilaLista
import com.ddam_a1.gestordeinventario.ui.components.TarjetaSuave
import com.ddam_a1.gestordeinventario.ui.theme.coloresExtra

/**
 * Pantalla 13 - Registrar produccion (RF10, RF11).
 *
 * `error` llega de afuera: lo pone quien intento registrar y no alcanzaron los
 * materiales. La pantalla no sabe por que fallo, solo lo muestra.
 */
@Composable
fun PantallaProduccion(
    nombreProducto: String,
    receta: List<RenglonProduccion>,
    error: String,
    onProducir: (cantidad: Int, descontarMateriales: Boolean) -> Unit,
    onAtras: () -> Unit
) {
    var cantidad by remember { mutableStateOf("1") }
    var preguntar by remember { mutableStateOf(false) }
    val n = cantidad.toIntOrNull() ?: 0

    Marco(barra = {
        BarraSuperior("Registrar produccion", nombreProducto, onAtras = onAtras)
    }) {
        item {
            CampoTexto(cantidad, "Cantidad a producir",
                { nuevo -> cantidad = nuevo.filter { c -> c.isDigit() } }, sufijo = "piezas")
        }
        item { EncabezadoSeccion("Materiales necesarios") }
        items(receta.size) { i ->
            val renglon = receta[i]
            // Multiplicar por las piezas SI es trabajo de aqui: depende de lo
            // que el usuario acaba de escribir, y es aritmetica para mostrar.
            val necesita = renglon.cantidadPorPieza * n
            val alcanza = renglon.disponible >= necesita
            FilaLista(
                titulo = renglon.nombre,
                subtitulo = "Necesitas " + cant(necesita) + " " + renglon.unidad,
                valor = cant(renglon.disponible) + " " + renglon.unidad,
                notaValor = if (alcanza) "disponible" else "insuficiente",
                colorPunto = if (alcanza) MaterialTheme.coloresExtra.correct.color
                             else MaterialTheme.colorScheme.error
            )
        }
        item {
            TarjetaSuave {
                Text("La caducidad del lote se toma de la fecha mas cercana de sus materiales.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (error.isNotBlank()) {
            item {
                Text(error, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error)
            }
        }
        item {
            BotonPrincipal("Registrar produccion", habilitado = n > 0) { preguntar = true }
        }
    }

    if (preguntar) {
        DialogoSiNo(
            titulo = "Descontar los materiales del inventario?",
            mensaje = "Vas a registrar " + n + " piezas de " + nombreProducto +
                ". Puedes descontar ahora los materiales que usaste, o dejarlos como estan si los repusiste aparte.",
            textoSi = "Descontar",
            textoNo = "No descontar",
            onSi = { preguntar = false; onProducir(n, true) },
            onNo = { preguntar = false; onProducir(n, false) },
            onCerrar = { preguntar = false }
        )
    }
}
