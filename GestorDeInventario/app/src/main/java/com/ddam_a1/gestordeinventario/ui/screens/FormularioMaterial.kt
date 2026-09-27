package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.ddam_a1.gestordeinventario.modelClasses.Material
import com.ddam_a1.gestordeinventario.ui.aTexto
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.CampoTexto
import com.ddam_a1.gestordeinventario.ui.components.Iconos
import com.ddam_a1.gestordeinventario.ui.components.Interruptor
import com.ddam_a1.gestordeinventario.ui.components.TarjetaSuave
import com.ddam_a1.gestordeinventario.ui.dinero
import com.ddam_a1.gestordeinventario.ui.theme.Anchos
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Medidas
import com.ddam_a1.gestordeinventario.ui.theme.Radios
import com.ddam_a1.gestordeinventario.ui.theme.tituloMedio

// ============================================================
//  PANTALLA 8 - NUEVO / EDITAR MATERIAL   (Figma 43:879 / 43:1170 / 43:1440)
//  RF1, RF2, RF18, RF19, RF22
//
//  `material` null significa "vengo a crear". Es la misma idea que ya tenia la
//  ruta con el id opcional, ahora en forma de dato.
//
//  Al guardar NO escribe nada: junta lo que el usuario escribio en un
//  DatosMaterial y lo entrega. Quien decide si eso es un alta o una edicion, y
//  quien lo anota en la bitacora, es el ViewModel.
//
//  En pantallas anchas el formulario NO se estira: una caja de texto de 1200
//  de ancho es peor de leer que una de 520. Se centra y se queda ahi.
// ============================================================

/** Las unidades de siempre. El desplegable evita "Kg", "kgs" y "KG" mezclados. */
private val UNIDADES = listOf("kg", "g", "L", "ml", "pza", "m")

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

    val guardar = {
        onGuardar(
            DatosMaterial(
                nombre = nombre.trim(),
                unidad = unidad.trim(),
                cantidad = cantidadNum,
                // Se guarda el unitario, no el total: es lo que usan la receta
                // y el calculo del costo de produccion.
                costo = unitario,
                stockMinimo = minimo.toDoubleOrNull() ?: 0.0,
                diasAvisoCaducidad = if (caduca) (dias.toIntOrNull() ?: 0) else 0
            )
        )
        onAtras()
    }

    Marco(barra = {
        BarraSuperior(
            if (material == null) "Nuevo material" else "Editar material",
            onAtras = onAtras
        ) {
            AccionGuardar(valido, guardar)
        }
    }) {
        item {
            // Una sola columna estrecha y centrada en cualquier medida:
            // widthIn deja de crecer y el Box la mantiene al centro.
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                Column(
                    Modifier.widthIn(max = Anchos.tarjetaFormulario),
                    verticalArrangement = Arrangement.spacedBy(Margenes.lg)
                ) {
                    CampoTexto(nombre, "Nombre", { nombre = it }, marcador = "Ej. Harina de trigo")

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Margenes.md)
                    ) {
                        Box(Modifier.weight(1f)) { SelectorUnidad(unidad) { unidad = it } }
                        Box(Modifier.weight(1f)) {
                            CampoTexto(
                                cantidad, "Cantidad", { cantidad = it }, soloNumeros = true,
                                error = if (cantidadNum <= 0.0) "Indique la cantidad adquirida" else null
                            )
                        }
                    }

                    CampoTexto(total, "Costo total", { total = it }, soloNumeros = true, sufijo = "MXN")

                    // El resultado en vivo: se ve el precio por unidad mientras
                    // se escribe, sin tener que guardar para comprobarlo.
                    TarjetaSuave {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Costo unitario",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                if (cantidadNum > 0.0) dinero(unitario) + " / " + unidad
                                else "— / " + unidad,
                                style = MaterialTheme.typography.tituloMedio,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    CampoTexto(
                        minimo, "Avisar cuando el inventario baje de", { minimo = it },
                        soloNumeros = true, sufijo = unidad
                    )

                    Row(
                        Modifier.fillMaxWidth().padding(vertical = Margenes.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Margenes.md)
                    ) {
                        Column(
                            Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(Margenes.xs)
                        ) {
                            Text(
                                "Este material caduca",
                                style = MaterialTheme.typography.bodyLarge
                                    .copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Activa para configurar avisos de vencimiento",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Interruptor(caduca) { caduca = it }
                    }

                    if (caduca) {
                        CampoTexto(
                            dias, "Avisar días antes", { dias = it },
                            soloEnteros = true, sufijo = "días antes"
                        )
                    }

                    BotonPrincipal("Guardar material", habilitado = valido, onClick = guardar)
                }
            }
        }
    }
}

// ---------- PIEZAS ----------

/** El "Guardar" de la barra: el mismo atajo del Figma, sin bajar al final. */
@Composable
private fun AccionGuardar(habilitado: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(Radios.accion))
            .clickable(enabled = habilitado) { onClick() }
            .padding(horizontal = Margenes.md, vertical = Margenes.sm)
    ) {
        Text(
            "Guardar",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = if (habilitado) MaterialTheme.colorScheme.tertiary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Unidad de medida (RF22).
 *
 * Desplegable y no texto libre porque la unidad se compara con la de la receta:
 * "kg" y "Kg" escritos a mano serian dos unidades distintas para el codigo.
 */
@Composable
private fun SelectorUnidad(valor: String, onValor: (String) -> Unit) {
    var abierto by remember { mutableStateOf(false) }
    val forma = RoundedCornerShape(Radios.campo)

    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Margenes.sm)
    ) {
        Text(
            "Unidad",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Box {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(Medidas.control)
                    .clip(forma)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .border(Medidas.borde, MaterialTheme.colorScheme.outlineVariant, forma)
                    .clickable { abierto = true }
                    .padding(horizontal = Margenes.lg),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    valor,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    Iconos.Desplegar,
                    contentDescription = "Elegir unidad",
                    modifier = Modifier.size(Medidas.iconoChico),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            DropdownMenu(abierto, { abierto = false }) {
                UNIDADES.forEach { u ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                u,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = {
                            onValor(u)
                            abierto = false
                        }
                    )
                }
            }
        }
    }
}
