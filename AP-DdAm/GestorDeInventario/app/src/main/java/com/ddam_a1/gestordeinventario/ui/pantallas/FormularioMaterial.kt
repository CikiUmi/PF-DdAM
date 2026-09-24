package com.ddam_a1.gestordeinventario.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.datos.AlmacenamientoLocal
import com.ddam_a1.gestordeinventario.datos.InventarioMateriales
import com.ddam_a1.gestordeinventario.datos.CatalogoProductos
import com.ddam_a1.gestordeinventario.ui.*
import com.ddam_a1.gestordeinventario.ui.componentes.*
import com.ddam_a1.gestordeinventario.ui.theme.*

/** Pantalla 8 · Nuevo / editar material (RF1, RF2, RF18, RF19, RF22). */
@Composable
fun PantallaFormularioMaterial(id: String?, onAtras: () -> Unit) {
    val m = id?.let { InventarioMateriales.obtenerMaterialPorId(it) }
    var nombre by remember { mutableStateOf(m?.nombre ?: "") }
    var unidad by remember { mutableStateOf(m?.unidadMedida ?: "kg") }
    var cantidad by remember { mutableStateOf(m?.cantidadDisponible?.toString() ?: "") }
    var costo by remember { mutableStateOf(m?.costoUnitario?.toString() ?: "") }
    var minimo by remember { mutableStateOf(m?.stockMinimo?.toString() ?: "") }
    var caduca by remember { mutableStateOf((m?.diasAvisoCaducidad ?: 0) > 0) }
    var dias by remember { mutableStateOf(m?.diasAvisoCaducidad?.toString() ?: "7") }
    val valido = nombre.isNotBlank() && unidad.isNotBlank() && costo.toDoubleOrNull() != null

    Marco(barra = { BarraSuperior(if (m == null) "Nuevo material" else "Editar material", onAtras = { onAtras() }) }) {
        item { CampoTexto(nombre, "Nombre", { nombre = it }) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.weight(1f)) { CampoTexto(unidad, "Unidad (RF22)", { unidad = it }) }
                Box(Modifier.weight(1f)) { CampoTexto(cantidad, "Cantidad", { cantidad = it }, soloNumeros = true) }
            }
        }
        item { CampoTexto(costo, "Costo unitario", { costo = it }, soloNumeros = true, sufijo = "MXN / $unidad") }
        item { CampoTexto(minimo, "Avisarme cuando baje de", { minimo = it }, soloNumeros = true, sufijo = unidad) }
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Este material caduca", style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface)
                    Text("Podrás registrar varias fechas por lote",
                        style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(caduca, { caduca = it })
            }
        }
        if (caduca) {
            item { CampoTexto(dias, "Avisar antes de caducar", { dias = it }, soloNumeros = true, sufijo = "días") }
        }
        item {
            Spacer(Modifier.height(8.dp))
            BotonPrincipal("Guardar", habilitado = valido) {
                val c = costo.toDoubleOrNull() ?: 0.0
                val destino = if (m == null) {
                    InventarioMateriales.agregarMaterial(nombre.trim(), unidad.trim(), c, cantidad.toDoubleOrNull() ?: 0.0)
                } else {
                    InventarioMateriales.editarMaterial(m.id, nombre.trim(), c); m
                }
                InventarioMateriales.definirStockMinimo(destino.id, minimo.toDoubleOrNull() ?: 0.0)
                InventarioMateriales.definirDiasAvisoCaducidad(destino.id, if (caduca) dias.toIntOrNull() ?: 0 else 0)
                AlmacenamientoLocal.registrarLog(hoy(), "manual",
                    (if (m == null) "Alta" else "Edición") + " de material ${destino.nombre}")
                EstadoApp.datosCambiaron()
                onAtras()
            }
        }
    }
}
