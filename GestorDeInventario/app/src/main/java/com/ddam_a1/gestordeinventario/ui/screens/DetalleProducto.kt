package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.data.InventarioMateriales
import com.ddam_a1.gestordeinventario.data.CatalogoProductos
import com.ddam_a1.gestordeinventario.ui.EstadoApp
import com.ddam_a1.gestordeinventario.ui.cant
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonIcono
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.EncabezadoSeccion
import com.ddam_a1.gestordeinventario.ui.components.FilaLista
import com.ddam_a1.gestordeinventario.ui.components.Iconos
import com.ddam_a1.gestordeinventario.ui.components.Insignia
import com.ddam_a1.gestordeinventario.ui.components.TarjetaSuave
import com.ddam_a1.gestordeinventario.ui.dinero
import com.ddam_a1.gestordeinventario.ui.theme.coloresExtra

/** Pantalla 10 · Detalle de producto (RF5, RF6, RF7, RF10). */
@Composable
fun PantallaDetalleProducto(
    id: String,
    onEditar: () -> Unit,
    onProducir: () -> Unit,
    onReceta: () -> Unit,
    onAtras: () -> Unit
) {
    EstadoApp.version
    val producto = CatalogoProductos.obtenerProductoPorId(id)
    if (producto == null) {
        onAtras()
        return
    }
    val costo = CatalogoProductos.calcularCostoProduccion(id)
    val ganancia = producto.precioVenta - costo
    val caducidad = producto.caducidadMasCercana

    Marco(barra = {
        BarraSuperior(producto.nombre, onAtras = { onAtras() }) {
            BotonIcono(Iconos.Editar, "Editar", { onEditar() })
        }
    }) {
        item {
            TarjetaSuave {
                Row {
                    Column(Modifier.weight(1f)) {
                        Text("Precio", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(dinero(producto.precioVenta), style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                    Column(Modifier.weight(1f)) {
                        Text("Costo", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(dinero(costo), style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                    Column(Modifier.weight(1f)) {
                        Text("Ganancia", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(dinero(ganancia), style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.coloresExtra.correct.color)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (producto.esBajoPedido) {
                        Insignia("Bajo pedido", MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.tertiaryContainer)
                    } else {
                        Insignia(producto.stockDisponible.toString() + " piezas en stock", MaterialTheme.coloresExtra.correct.color, MaterialTheme.coloresExtra.correct.colorContainer)
                    }
                    if (caducidad != null) {
                        Insignia("Caduca " + caducidad, MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.errorContainer)
                    }
                }
            }
        }

        item { EncabezadoSeccion("Receta", "Editar", { onReceta() }) }
        if (producto.receta.isEmpty()) {
            item {
                Text("Sin materiales ligados. Este producto no calcula costo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(producto.receta.size) { i ->
                val ingrediente = producto.receta[i]
                val material = InventarioMateriales.obtenerMaterialPorId(ingrediente.materialId)
                val costoUnit = if (material != null) material.costoUnitario else 0.0
                val unidad = if (material != null) material.unidadMedida else ""
                FilaLista(
                    titulo = if (material != null) material.nombre else "Material",
                    subtitulo = cant(ingrediente.cantidadUsada) + " " + unidad + " × " + dinero(costoUnit),
                    valor = dinero(costoUnit * ingrediente.cantidadUsada)
                )
            }
        }

        item {
            Spacer(Modifier.height(4.dp))
            if (!producto.esBajoPedido) {
                BotonPrincipal("Registrar producción") { onProducir() }
            } else {
                Text("Los productos bajo pedido descuentan sus materiales al momento de la venta.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
