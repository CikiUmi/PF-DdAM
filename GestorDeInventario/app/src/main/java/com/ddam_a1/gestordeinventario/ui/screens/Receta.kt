package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.modelClasses.Material
import com.ddam_a1.gestordeinventario.ui.cant
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.CampoTexto
import com.ddam_a1.gestordeinventario.ui.components.EstadoVacio
import com.ddam_a1.gestordeinventario.ui.components.TarjetaSuave
import com.ddam_a1.gestordeinventario.ui.dinero

/**
 * Pantalla 12 - Seleccionar materiales de la receta (RF6).
 *
 * Lo que se esta escribiendo es estado de ESTA pantalla: mientras marcas y
 * tecleas cantidades no se guarda nada. Al tocar Guardar entrega el mapa
 * completo y quien lo escribe es el ViewModel.
 *
 * Antes hacia `producto.receta.clear()` y agregaba uno por uno desde el boton:
 * la vista escribiendo en los datos, y si algo fallaba a medias la receta
 * quedaba incompleta.
 */
@Composable
fun PantallaReceta(
    nombreProducto: String,
    materiales: List<Material>,
    recetaActual: Map<String, Double>,
    onGuardar: (Map<String, Double>) -> Unit,
    onAtras: () -> Unit
) {
    // Texto y no Double: el usuario puede estar a medio escribir "1." y eso no
    // es un numero todavia.
    val cantidades = remember(recetaActual) {
        mutableStateMapOf<String, String>().apply {
            recetaActual.forEach { (materialId, usada) -> put(materialId, cant(usada)) }
        }
    }

    var costo = 0.0
    for (entrada in cantidades) {
        val material = materiales.find { it.id == entrada.key }
        val unitario = if (material != null) material.costoUnitario else 0.0
        costo += (entrada.value.toDoubleOrNull() ?: 0.0) * unitario
    }

    Marco(barra = { BarraSuperior("Materiales", nombreProducto, onAtras = onAtras) }) {
        if (materiales.isEmpty()) {
            item { EstadoVacio("No hay materiales", "Agrega materiales al inventario primero") }
        }
        items(materiales.size) { i ->
            val material = materiales[i]
            val marcado = cantidades.containsKey(material.id)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = marcado,
                    onCheckedChange = { activado ->
                        if (activado) {
                            cantidades[material.id] = ""
                        } else {
                            cantidades.remove(material.id)
                        }
                    }
                )
                Column(Modifier.weight(1f)) {
                    Text(material.nombre, style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface)
                    Text(dinero(material.costoUnitario) + " / " + material.unidadMedida,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (marcado) {
                    Box(Modifier.width(110.dp)) {
                        CampoTexto(
                            valor = cantidades[material.id] ?: "",
                            etiqueta = material.unidadMedida,
                            onCambio = { nuevo -> cantidades[material.id] = nuevo },
                            soloNumeros = true
                        )
                    }
                }
            }
        }
        item {
            TarjetaSuave {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(cantidades.size.toString() + " materiales - costo calculado",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f))
                    Text(dinero(costo), style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
        item {
            BotonPrincipal("Guardar receta") {
                val ingredientes = mutableMapOf<String, Double>()
                for (entrada in cantidades) {
                    val valor = entrada.value.toDoubleOrNull() ?: 0.0
                    if (valor > 0.0) ingredientes[entrada.key] = valor
                }
                onGuardar(ingredientes)
                onAtras()
            }
        }
    }
}
