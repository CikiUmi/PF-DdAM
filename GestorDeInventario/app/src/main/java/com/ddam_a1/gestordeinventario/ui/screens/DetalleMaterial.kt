package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.modelClasses.Material
import com.ddam_a1.gestordeinventario.ui.cant
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonIcono
import com.ddam_a1.gestordeinventario.ui.components.BotonSecundario
import com.ddam_a1.gestordeinventario.ui.components.CampoTexto
import com.ddam_a1.gestordeinventario.ui.components.EncabezadoSeccion
import com.ddam_a1.gestordeinventario.ui.components.FilaLista
import com.ddam_a1.gestordeinventario.ui.components.Iconos
import com.ddam_a1.gestordeinventario.ui.components.Insignia
import com.ddam_a1.gestordeinventario.ui.components.TarjetaSuave
import com.ddam_a1.gestordeinventario.ui.dinero

/**
 * Pantalla 7 - Detalle de material (RF3, RF9, RF18, RF19).
 *
 * `material` puede ser null mientras el NavHost todavia lo esta buscando, o si
 * lo borraron desde otra pantalla. La decision de que hacer en ese caso es de
 * quien navega, no de la pantalla: aqui solo se dibuja un hueco.
 */
@Composable
fun PantallaDetalleMaterial(
    material: Material?,
    bajo: Boolean,
    usadoEn: List<UsoEnProducto>,
    onEntrada: (cantidad: Double, caducidad: String?) -> Unit,
    onProducto: (String) -> Unit,
    onEditar: () -> Unit,
    onAtras: () -> Unit
) {
    var nuevaFecha by remember { mutableStateOf("") }
    var entrada by remember { mutableStateOf("") }

    // "Caduca" no es una pregunta suelta: es lo que dijiste al darlo de alta.
    val caduca = material != null && material.diasAvisoCaducidad > 0

    if (material == null) {
        Marco(barra = { BarraSuperior("Material", onAtras = onAtras) }) {
            item { Text("Este material ya no existe.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        return
    }

    Marco(barra = {
        BarraSuperior(material.nombre, onAtras = onAtras) {
            BotonIcono(Iconos.Editar, "Editar", { onEditar() })
        }
    }) {
        item {
            TarjetaSuave {
                Row {
                    Column(Modifier.weight(1f)) {
                        Text("En inventario", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(cant(material.cantidadDisponible) + " " + material.unidadMedida,
                            style = MaterialTheme.typography.headlineSmall,
                            color = if (bajo) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                    }
                    Column(Modifier.weight(1f)) {
                        Text("Costo unitario", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(dinero(material.costoUnitario), style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Insignia(
                        "Aviso en " + cant(material.stockMinimo) + " " + material.unidadMedida,
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                    if (material.diasAvisoCaducidad > 0) {
                        Insignia(
                            material.diasAvisoCaducidad.toString() + " dias antes",
                            MaterialTheme.colorScheme.tertiary,
                            MaterialTheme.colorScheme.tertiaryContainer
                        )
                    }
                }
            }
        }

        // ---------- entrada de inventario ----------
        //
        // Cantidad y caducidad se piden JUNTAS porque son una sola cosa: un
        // lote que entra. Separadas, nada garantizaba que coincidieran.
        item { EncabezadoSeccion("Entrada de inventario") }
        item {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.weight(1f)) {
                    CampoTexto(entrada, "Cantidad que entra", { entrada = it },
                        soloNumeros = true, sufijo = material.unidadMedida)
                }
                if (caduca) {
                    Box(Modifier.weight(1f)) {
                        CampoTexto(nuevaFecha, "Caduca el (aaaa-mm-dd)", { nuevaFecha = it })
                    }
                }
            }
        }
        item {
            val cantidadValida = (entrada.toDoubleOrNull() ?: 0.0) > 0.0
            // Si el material caduca, el lote NO entra sin su fecha.
            val listo = cantidadValida && (!caduca || nuevaFecha.isNotBlank())
            BotonSecundario("Registrar entrada", habilitado = listo) {
                onEntrada(entrada.toDoubleOrNull() ?: 0.0, if (caduca) nuevaFecha else null)
                entrada = ""
                nuevaFecha = ""
            }
        }

        // ---------- lotes registrados ----------
        if (caduca) {
            item { EncabezadoSeccion("Fechas de caducidad (RF9)") }
            if (material.fechasCaducidad.isEmpty()) {
                item {
                    Text("Todavia no hay lotes con fecha registrada.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                items(material.fechasCaducidad.size) { i ->
                    FilaLista("Lote " + (i + 1), "Caduca el " + material.fechasCaducidad[i])
                }
            }
        }

        item { EncabezadoSeccion("Se usa en") }
        if (usadoEn.isEmpty()) {
            item {
                Text("Todavia no forma parte de ninguna receta.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(usadoEn.size) { i ->
                val uso = usadoEn[i]
                FilaLista(
                    uso.nombre,
                    cant(uso.cantidadUsada) + " " + material.unidadMedida + " por pieza"
                ) { onProducto(uso.productoId) }
            }
        }
    }
}
