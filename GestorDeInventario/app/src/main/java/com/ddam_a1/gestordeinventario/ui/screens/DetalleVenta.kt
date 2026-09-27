package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonDestructivo
import com.ddam_a1.gestordeinventario.ui.components.DialogoSiNo
import com.ddam_a1.gestordeinventario.ui.components.EstadoVacio
import com.ddam_a1.gestordeinventario.ui.components.ListaPegada
import com.ddam_a1.gestordeinventario.ui.dinero
import com.ddam_a1.gestordeinventario.ui.folioDe
import com.ddam_a1.gestordeinventario.ui.theme.Anchos
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Radios
import com.ddam_a1.gestordeinventario.ui.theme.tituloMedio

// ============================================================
//  PANTALLA 16 - DETALLE DE VENTA   (Figma 51:1588 / 51:1851 / 52:1683)
//  RF12, y la unica puerta a `cancelarVenta`
//
//  Es un recibo: lo que se vendio, a que precio y cuando. Los renglones NO se
//  pueden tocar, porque una venta ya ocurrio; lo unico que se puede hacer es
//  cancelarla entera, y eso devuelve el stock.
//
//  Los precios vienen de la venta, no del catalogo. Si manana sube el pan, lo
//  que se cobro ayer sigue siendo lo que se cobro ayer.
//
//  Una sola columna en las tres medidas, centrada y estrecha (Figma 52:1695:
//  600 de ancho en una pantalla de 1280). Un recibo a lo ancho de una tableta
//  se leeria peor, no mejor.
// ============================================================

@Composable
fun PantallaDetalleVenta(
    ventaId: String,
    /** Ya viene armada como "aaaa-mm-dd HH:mm": la pantalla no junta datos. */
    fecha: String,
    total: Double,
    cancelada: Boolean,
    renglones: List<RenglonVenta>,
    onCancelarVenta: () -> Unit,
    onAtras: () -> Unit
) {
    var preguntar by remember { mutableStateOf(false) }
    val folio = folioDe(ventaId)

    Marco(barra = { BarraSuperior("Detalle de venta", folio, onAtras = onAtras) }) {
        item {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                Column(
                    Modifier.widthIn(max = Anchos.tarjetaFormulario),
                    verticalArrangement = Arrangement.spacedBy(Margenes.xl)
                ) {
                    CajaDatos(fecha, folio, cancelada)

                    Column(verticalArrangement = Arrangement.spacedBy(Margenes.md)) {
                        Text(
                            "Productos entregados",
                            style = MaterialTheme.typography.tituloMedio,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.semantics { heading() }
                        )
                        if (renglones.isEmpty()) {
                            EstadoVacio(
                                "Sin productos",
                                "Esta venta no tiene renglones registrados"
                            )
                        } else {
                            ListaPegada(renglones.size) { i ->
                                val renglon = renglones[i]
                                Column(
                                    Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(Margenes.xs)
                                ) {
                                    Text(
                                        renglon.nombre + " × " + renglon.cantidad,
                                        style = MaterialTheme.typography.bodyLarge
                                            .copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        dinero(renglon.precioUnitario) + " c/u",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    dinero(renglon.importe),
                                    style = MaterialTheme.typography.tituloMedio,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    FilaTotal(total)

                    // Una venta cancelada ya devolvio su stock: volver a
                    // cancelarla lo devolveria dos veces. Por eso el boton no
                    // se apaga, desaparece.
                    if (!cancelada) {
                        BotonDestructivo("Cancelar venta") { preguntar = true }
                    }
                }
            }
        }
    }

    if (preguntar) {
        DialogoSiNo(
            titulo = "¿Cancelar la venta?",
            mensaje = "Se devolverá al inventario el stock de los productos vendidos. " +
                "La venta se queda en el historial, marcada como cancelada.",
            textoSi = "Cancelar venta",
            textoNo = "Volver",
            onSi = {
                preguntar = false
                onCancelarVenta()
            },
            onNo = { preguntar = false },
            onCerrar = { preguntar = false },
            destructivo = true
        )
    }
}

// ---------- PIEZAS ----------

/** Fecha y folio, y el aviso cuando la venta ya no cuenta (Figma 51:1599). */
@Composable
private fun CajaDatos(fecha: String, folio: String, cancelada: Boolean) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radios.campo))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(Margenes.lg),
        verticalArrangement = Arrangement.spacedBy(Margenes.sm)
    ) {
        DatoDeVenta("Fecha:", fecha)
        DatoDeVenta("Folio:", folio)
        if (cancelada) {
            DatoDeVenta("Estado:", "Cancelada", MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun DatoDeVenta(
    etiqueta: String,
    valor: String,
    colorValor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        Modifier.fillMaxWidth().semantics(mergeDescendants = true) { },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            etiqueta,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            valor,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = colorValor
        )
    }
}

@Composable
private fun FilaTotal(total: Double) {
    Row(
        Modifier.fillMaxWidth().semantics(mergeDescendants = true) { },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Total",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            dinero(total),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
