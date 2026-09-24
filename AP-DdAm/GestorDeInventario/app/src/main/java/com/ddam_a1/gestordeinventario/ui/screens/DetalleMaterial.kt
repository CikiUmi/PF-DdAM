package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.data.AlmacenamientoLocal
import com.ddam_a1.gestordeinventario.data.InventarioMateriales
import com.ddam_a1.gestordeinventario.data.CatalogoProductos
import com.ddam_a1.gestordeinventario.ui.*
import com.ddam_a1.gestordeinventario.ui.components.*

/** Pantalla 7 · Detalle de material (RF3, RF9, RF18, RF19). */
@Composable
fun PantallaDetalleMaterial(
    id: String,
    onProducto: (String) -> Unit,
    onEditar: () -> Unit,
    onAtras: () -> Unit
) {
    EstadoApp.version
    val m = InventarioMateriales.obtenerMaterialPorId(id)
    var nuevaFecha by remember { mutableStateOf("") }

    if (m == null) { onAtras(); return }
    val bajo = InventarioMateriales.esStockBajo(m)
    val usadoEn = CatalogoProductos.obtenerTodos().filter { p -> p.receta.any { it.materialId == id } }

    Marco(barra = {
        BarraSuperior(m.nombre, onAtras = { onAtras() }) {
            BotonIcono(Iconos.Editar, "Editar", { onEditar() })
        }
    }) {
        item {
            TarjetaSuave {
                Row {
                    Column(Modifier.weight(1f)) {
                        Text("En inventario", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${cant(m.cantidadDisponible)} ${m.unidadMedida}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = if (bajo) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                    }
                    Column(Modifier.weight(1f)) {
                        Text("Costo unitario", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(dinero(m.costoUnitario), style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Insignia("Aviso en ${cant(m.stockMinimo)} ${m.unidadMedida}", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
                    if (m.diasAvisoCaducidad > 0)
                        Insignia("${m.diasAvisoCaducidad} días antes", MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.tertiaryContainer)
                }
            }
        }
        item { EncabezadoSeccion("Fechas de caducidad (RF9)") }
        if (m.fechasCaducidad.isEmpty()) {
            item { Text("Este material no registra caducidad.",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            items(m.fechasCaducidad.size) { i ->
                FilaLista("Lote ${i + 1}", "Caduca el ${m.fechasCaducidad[i]}")
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.weight(1f)) { CampoTexto(nuevaFecha, "Nueva fecha (aaaa-mm-dd)", { nuevaFecha = it }) }
                Box(Modifier.width(120.dp)) {
                    BotonSecundario("Agregar") {
                        if (nuevaFecha.isNotBlank()) {
                            InventarioMateriales.agregarFechaCaducidad(id, nuevaFecha.trim())
                            AlmacenamientoLocal.registrarLog(hoy(), "manual", "Caducidad ${nuevaFecha.trim()} en ${m.nombre}")
                            nuevaFecha = ""; EstadoApp.datosCambiaron()
                        }
                    }
                }
            }
        }
        item { EncabezadoSeccion("Se usa en") }
        if (usadoEn.isEmpty()) {
            item { Text("Todavía no forma parte de ninguna receta.",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            items(usadoEn.size) { i ->
                val p = usadoEn[i]
                val ing = p.receta.first { it.materialId == id }
                FilaLista(p.nombre, "${cant(ing.cantidadUsada)} ${m.unidadMedida} por pieza") {
                    onProducto(p.id)
                }
            }
        }
    }
}
