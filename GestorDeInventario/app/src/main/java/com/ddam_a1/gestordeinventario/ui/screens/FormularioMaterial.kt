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
import com.ddam_a1.gestordeinventario.ui.components.TarjetaSuave
import com.ddam_a1.gestordeinventario.ui.aTexto
import com.ddam_a1.gestordeinventario.ui.dinero
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.tituloMedio

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
    // Los numericos arrancan en "0" y no vacios: asi se ve de entrada que
    // aqui van numeros, y el campo nunca esta en un estado sin valor.
    var cantidad by remember(material) { mutableStateOf(material?.cantidadDisponible?.aTexto() ?: "0") }
    var minimo by remember(material) { mutableStateOf(material?.stockMinimo?.aTexto() ?: "0") }
    var caduca by remember(material) { mutableStateOf((material?.diasAvisoCaducidad ?: 0) > 0) }
    var dias by remember(material) { mutableStateOf(material?.diasAvisoCaducidad?.toString() ?: "7") }

    // ---- Costo: se captura el TOTAL pagado, se guarda el UNITARIO ----
    //
    // Nadie sabe de memoria cuanto le sale el kilo; lo que si tiene a la mano
    // es el ticket: "pague 450 por 3 kg". La division la hace la app.
    //
    // Al editar se siembra al reves (unitario x cantidad) para que el campo no
    // aparezca vacio, pero lo que se guarda sigue siendo el unitario: es lo que
    // usan la receta y el costo de produccion.
    var total by remember(material) {
        mutableStateOf(
            if (material == null) "0"
            else (material.costoUnitario * material.cantidadDisponible).aTexto()
        )
    }

    val cantidadNum = cantidad.toDoubleOrNull() ?: 0.0
    val totalNum = total.toDoubleOrNull() ?: 0.0
    val unitario = if (cantidadNum > 0.0) totalNum / cantidadNum else 0.0

    // Sin cantidad no hay division posible, asi que no se puede guardar.
    val valido = nombre.isNotBlank() && unidad.isNotBlank() && cantidadNum > 0.0

    Marco(barra = {
        BarraSuperior(if (material == null) "Nuevo material" else "Editar material", onAtras = onAtras)
    }) {
        item { CampoTexto(nombre, "Nombre", { nombre = it }) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(Margenes.md)) {
                Box(Modifier.weight(1f)) { CampoTexto(unidad, "Unidad", { unidad = it }) }
                Box(Modifier.weight(1f)) {
                    CampoTexto(
                        cantidad, "Cantidad adquirida", { cantidad = it }, soloNumeros = true,
                        error = if (cantidadNum <= 0.0) "Indique la cantidad adquirida" else null
                    )
                }
            }
        }
        item {
            CampoTexto(
                total, "Costo total", { total = it },
                soloNumeros = true, sufijo = "MXN"
            )
        }
        item {
            // El resultado en vivo: se ve el precio por unidad mientras se
            // escribe, sin tener que guardar para comprobarlo.
            TarjetaSuave {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Costo unitario",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        if (cantidadNum > 0.0) dinero(unitario) + " / " + unidad.ifBlank { "unidad" }
                        else "— / " + unidad.ifBlank { "unidad" },
                        style = MaterialTheme.typography.tituloMedio,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        item {
            CampoTexto(
                minimo, "Avisar cuando el inventario baje de", { minimo = it },
                soloNumeros = true, sufijo = unidad
            )
        }
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Este material caduca", style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface)
                    Text("Permite registrar una fecha por lote",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(caduca, { caduca = it })
            }
        }
        if (caduca) {
            item {
                CampoTexto(
                    dias, "Avisar antes de caducar", { dias = it },
                    soloEnteros = true, sufijo = "días"
                )
            }
        }
        item {
            Spacer(Modifier.height(8.dp))
            BotonPrincipal("Guardar", habilitado = valido) {
                onGuardar(
                    DatosMaterial(
                        nombre = nombre.trim(),
                        unidad = unidad.trim(),
                        cantidad = cantidadNum,
                        // Se guarda el unitario, no el total: es lo que usan la
                        // receta y el calculo del costo de produccion.
                        costo = unitario,
                        stockMinimo = minimo.toDoubleOrNull() ?: 0.0,
                        diasAvisoCaducidad = if (caduca) (dias.toIntOrNull() ?: 0) else 0
                    )
                )
                onAtras()
            }
        }
    }
}
