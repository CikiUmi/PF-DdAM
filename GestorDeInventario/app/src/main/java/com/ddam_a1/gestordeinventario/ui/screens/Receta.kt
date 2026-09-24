package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.modelClasses.Material
import com.ddam_a1.gestordeinventario.ui.cant
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.CampoTexto
import com.ddam_a1.gestordeinventario.ui.components.DialogoSiNo
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
    esProductoNuevo: Boolean,
    onGuardar: (Map<String, Double>) -> Unit,
    onDescartar: () -> Unit,
    onAtras: () -> Unit
) {
    // Si vienes de crear el producto, salirse de aqui es CANCELAR el alta: el
    // producto ya existe y quedaria a medias, sin receta y sin costo. Por eso se
    // pregunta en vez de dejarte ir en silencio.
    var preguntarDescarte by remember { mutableStateOf(false) }
    val salir = { if (esProductoNuevo) preguntarDescarte = true else onAtras() }

    // El boton fisico de atras tiene que hacer lo mismo que la flecha de la
    // barra; si no, seria la puerta trasera para dejar el producto huerfano.
    BackHandler(enabled = esProductoNuevo) { preguntarDescarte = true }

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

    Marco(barra = { BarraSuperior("Materiales", nombreProducto, onAtras = salir) }) {
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

    if (preguntarDescarte) {
        DialogoSiNo(
            titulo = "Descartar el producto?",
            mensaje = "Todavia no le pusiste receta a \"" + nombreProducto +
                "\". Si sales ahora el producto no se guarda.",
            textoSi = "Descartar",
            textoNo = "Seguir editando",
            onSi = { preguntarDescarte = false; onDescartar() },
            onNo = { preguntarDescarte = false },
            onCerrar = { preguntarDescarte = false }
        )
    }
}
