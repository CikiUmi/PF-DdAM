package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
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
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.CampoTexto

/**
 * Pantalla 8 - Nuevo / editar material (RF1, RF2, RF18, RF19, RF22).
 *
 * `material` null significa "vengo a crear". Es la misma idea que ya tenia la
 * ruta con el id opcional, ahora en forma de dato.
 *
 * Al guardar NO escribe nada: junta lo que el usuario escribio en un
 * DatosMaterial y lo entrega. Quien decide si eso es un alta o una edicion, y
 * quien lo anota en la bitacora, es el ViewModel.
 */
@Composable
fun PantallaFormularioMaterial(
    material: Material?,
    onGuardar: (DatosMaterial) -> Unit,
    onAtras: () -> Unit
) {
    // `remember(material)` y no `remember`: si el material llega un instante
    // despues (porque se estaba leyendo), los campos se vuelven a sembrar con
    // sus valores. Con `remember` a secas se quedarian vacios para siempre.
    var nombre by remember(material) { mutableStateOf(material?.nombre ?: "") }
    var unidad by remember(material) { mutableStateOf(material?.unidadMedida ?: "kg") }
    var cantidad by remember(material) { mutableStateOf(material?.cantidadDisponible?.toString() ?: "") }
    var costo by remember(material) { mutableStateOf(material?.costoUnitario?.toString() ?: "") }
    var minimo by remember(material) { mutableStateOf(material?.stockMinimo?.toString() ?: "") }
    var caduca by remember(material) { mutableStateOf((material?.diasAvisoCaducidad ?: 0) > 0) }
    var dias by remember(material) { mutableStateOf(material?.diasAvisoCaducidad?.toString() ?: "7") }

    val valido = nombre.isNotBlank() && unidad.isNotBlank() && costo.toDoubleOrNull() != null

    Marco(barra = {
        BarraSuperior(if (material == null) "Nuevo material" else "Editar material", onAtras = onAtras)
    }) {
        item { CampoTexto(nombre, "Nombre", { nombre = it }) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.weight(1f)) { CampoTexto(unidad, "Unidad (RF22)", { unidad = it }) }
                Box(Modifier.weight(1f)) { CampoTexto(cantidad, "Cantidad", { cantidad = it }, soloNumeros = true) }
            }
        }
        item { CampoTexto(costo, "Costo unitario", { costo = it }, soloNumeros = true, sufijo = "MXN / " + unidad) }
        item { CampoTexto(minimo, "Avisarme cuando baje de", { minimo = it }, soloNumeros = true, sufijo = unidad) }
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Este material caduca", style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface)
                    Text("Podras registrar varias fechas por lote",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(caduca, { caduca = it })
            }
        }
        if (caduca) {
            item { CampoTexto(dias, "Avisar antes de caducar", { dias = it }, soloNumeros = true, sufijo = "dias") }
        }
        item {
            Spacer(Modifier.height(8.dp))
            BotonPrincipal("Guardar", habilitado = valido) {
                onGuardar(
                    DatosMaterial(
                        nombre = nombre.trim(),
                        unidad = unidad.trim(),
                        cantidad = cantidad.toDoubleOrNull() ?: 0.0,
                        costo = costo.toDoubleOrNull() ?: 0.0,
                        stockMinimo = minimo.toDoubleOrNull() ?: 0.0,
                        diasAvisoCaducidad = if (caduca) (dias.toIntOrNull() ?: 0) else 0
                    )
                )
                onAtras()
            }
        }
    }
}
