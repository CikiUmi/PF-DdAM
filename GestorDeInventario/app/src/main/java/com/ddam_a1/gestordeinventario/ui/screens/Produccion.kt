package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.ddam_a1.gestordeinventario.ui.cant
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.CampoTexto
import com.ddam_a1.gestordeinventario.ui.components.DialogoSiNo
import com.ddam_a1.gestordeinventario.ui.components.EstadoVacio
import com.ddam_a1.gestordeinventario.ui.components.Iconos
import com.ddam_a1.gestordeinventario.ui.components.Pastilla
import com.ddam_a1.gestordeinventario.ui.theme.AnchoPantalla
import com.ddam_a1.gestordeinventario.ui.theme.Anchos
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Radios
import com.ddam_a1.gestordeinventario.ui.theme.anchoPantallaDe
import com.ddam_a1.gestordeinventario.ui.theme.coloresExtra
import com.ddam_a1.gestordeinventario.ui.theme.tituloMedio

// ============================================================
//  PANTALLA 13 - REGISTRAR PRODUCCION   (Figma 48:1646 / 48:2029 / 48:2373)
//
//  Cuantas piezas se van a hacer y si alcanzan los materiales para hacerlas.
//
//  La multiplicacion (cantidad por pieza x piezas) SI se hace aqui: depende
//  de lo que el usuario acaba de escribir y es aritmetica para mostrar, no
//  una consulta. Quien decide si la produccion procede sigue siendo el
//  repositorio, que revisa otra vez al descontar.
//
//  En tableta la lista se vuelve tabla (Figma 48:2394): con 1200 de ancho se
//  pueden alinear "necesitas" y "disponible" en columnas y compararlos de un
//  vistazo, que es justo la pregunta que trae el usuario.
// ============================================================

@Composable
fun PantallaProduccion(
    nombreProducto: String,
    receta: List<RenglonProduccion>,
    error: String,
    onProducir: (cantidad: Int, descontarMateriales: Boolean) -> Unit,
    onAtras: () -> Unit
) {
    var cantidad by remember { mutableStateOf("0") }
    var preguntar by remember { mutableStateOf(false) }
    val piezas = cantidad.toIntOrNull() ?: 0

    // Sin receta no hay nada que descontar ni que comprobar.
    val alcanzaTodo = receta.all { it.disponible >= it.cantidadPorPieza * piezas }

    BoxWithConstraints {
        val enTabla = anchoPantallaDe(maxWidth) == AnchoPantalla.EXPANDIDA

        Marco(barra = { BarraSuperior("Registrar producción", onAtras = onAtras) }) {
            item {
                Box(
                    Modifier.fillMaxWidth(),
                    contentAlignment = if (enTabla) Alignment.TopStart else Alignment.TopCenter
                ) {
                    Column(
                        if (enTabla) Modifier.fillMaxWidth()
                        else Modifier.widthIn(max = Anchos.tarjetaAncha),
                        verticalArrangement = Arrangement.spacedBy(Margenes.lg)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(Margenes.xs)) {
                            Text(
                                nombreProducto,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.semantics { heading() }
                            )
                            Text(
                                "Configure la cantidad a fabricar con su receta asignada",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // En tableta el campo no se estira a 1200: un numero de
                        // dos cifras en una caja de ese ancho se ve perdido.
                        Box(if (enTabla) Modifier.widthIn(max = Anchos.formularioMedio) else Modifier) {
                            CampoTexto(
                                cantidad, "Cantidad a producir", { cantidad = it },
                                soloEnteros = true, sufijo = "piezas",
                                error = if (piezas <= 0) "Indique la cantidad a producir" else null
                            )
                        }

                        Etiqueta("Materiales necesarios")
                    }
                }
            }

            if (receta.isEmpty()) {
                item {
                    EstadoVacio(
                        "Sin receta",
                        "Asigne una receta al producto para poder registrar producción"
                    )
                }
            } else if (enTabla) {
                item { EncabezadoTablaProduccion() }
                items(receta.size) { i -> RenglonTablaProduccion(receta[i], piezas) }
            } else {
                items(receta.size) { i -> TarjetaMaterialNecesario(receta[i], piezas) }
            }

            if (error.isNotBlank()) {
                item {
                    Text(
                        "⚠ " + error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            item {
                BotonPrincipal(
                    "Producir",
                    habilitado = piezas > 0 && receta.isNotEmpty() && alcanzaTodo
                ) { preguntar = true }
            }
        }
    }

    if (preguntar) {
        // El Figma no dibuja esta pregunta, pero la operacion tiene dos formas
        // distintas y el usuario es el unico que sabe cual: si ya repuso los
        // materiales aparte, descontarlos otra vez los contaria dos veces.
        DialogoSiNo(
            titulo = "¿Descontar los materiales del inventario?",
            mensaje = "Va a registrar " + piezas + " piezas de " + nombreProducto +
                ". Puede descontar ahora los materiales que usó, o dejarlos como están " +
                "si los repuso aparte.",
            textoSi = "Descontar",
            textoNo = "No descontar",
            onSi = {
                preguntar = false
                onProducir(piezas, true)
            },
            onNo = {
                preguntar = false
                onProducir(piezas, false)
            },
            onCerrar = { preguntar = false }
        )
    }
}

// ---------- TELEFONO Y TELEFONO GIRADO ----------

@Composable
private fun TarjetaMaterialNecesario(renglon: RenglonProduccion, piezas: Int) {
    val necesita = renglon.cantidadPorPieza * piezas
    val alcanza = renglon.disponible >= necesita

    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radios.campo))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(Margenes.md)
            .semantics(mergeDescendants = true) { },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margenes.md)
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Margenes.xs)) {
            Text(
                renglon.nombre,
                style = MaterialTheme.typography.tituloMedio,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "Necesita: " + cant(necesita) + " " + renglon.unidad +
                    " · Disponible: " + cant(renglon.disponible) + " " + renglon.unidad,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        InsigniaAlcance(alcanza)
    }
}

// ---------- TABLETA  (Figma 48:2394) ----------

// Los pesos salen del Figma (628/180/180/180 sobre 1200) y se comparten entre
// el encabezado y los renglones: si cambian, cambian en los dos a la vez.
private const val COL_NOMBRE = 628f
private const val COL_NECESITA = 180f
private const val COL_DISPONIBLE = 180f
private const val COL_ESTADO = 180f

@Composable
private fun EncabezadoTablaProduccion() {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = Margenes.lg, vertical = Margenes.md),
        horizontalArrangement = Arrangement.spacedBy(Margenes.lg)
    ) {
        listOf(
            "Material" to COL_NOMBRE,
            "Necesita" to COL_NECESITA,
            "Disponible" to COL_DISPONIBLE,
            "Estado" to COL_ESTADO
        ).forEach { (titulo, peso) ->
            Text(
                titulo,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(peso)
            )
        }
    }
}

@Composable
private fun RenglonTablaProduccion(renglon: RenglonProduccion, piezas: Int) {
    val necesita = renglon.cantidadPorPieza * piezas
    val alcanza = renglon.disponible >= necesita

    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radios.campo))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(Margenes.lg)
            .semantics(mergeDescendants = true) { },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margenes.lg)
    ) {
        Text(
            renglon.nombre,
            style = MaterialTheme.typography.tituloMedio,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(COL_NOMBRE)
        )
        Text(
            cant(necesita) + " " + renglon.unidad,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(COL_NECESITA)
        )
        Text(
            cant(renglon.disponible) + " " + renglon.unidad,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(COL_DISPONIBLE)
        )
        Box(Modifier.weight(COL_ESTADO)) { InsigniaAlcance(alcanza) }
    }
}

// ---------- COMPARTIDO ----------

/** Verde si alcanza, rojo si no. El texto dice cual, no solo el color. */
@Composable
private fun InsigniaAlcance(alcanza: Boolean) {
    val cs = MaterialTheme.colorScheme
    val correcto = MaterialTheme.coloresExtra.correct
    if (alcanza) {
        Pastilla("Disponible", correcto.colorContainer, correcto.onColorContainer, Iconos.Check)
    } else {
        Pastilla("Insuficiente", cs.errorContainer, cs.onErrorContainer, Iconos.Alerta)
    }
}
