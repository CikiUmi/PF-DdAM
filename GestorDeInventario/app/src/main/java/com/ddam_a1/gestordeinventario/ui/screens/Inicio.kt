package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.ddam_a1.gestordeinventario.modelClasses.Venta
import com.ddam_a1.gestordeinventario.ui.components.AccionRapida
import com.ddam_a1.gestordeinventario.ui.components.BannerAviso
import com.ddam_a1.gestordeinventario.ui.components.BarraInferior
import com.ddam_a1.gestordeinventario.ui.components.BotonIcono
import com.ddam_a1.gestordeinventario.ui.components.DestinoBarra
import com.ddam_a1.gestordeinventario.ui.components.EstadoVacio
import com.ddam_a1.gestordeinventario.ui.components.FilaVenta
import com.ddam_a1.gestordeinventario.ui.components.FilaPareja
import com.ddam_a1.gestordeinventario.ui.components.Iconos
import com.ddam_a1.gestordeinventario.ui.components.TarjetaResumen
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.tituloMedio

// ============================================================
//  PANTALLA 4 - INICIO   (Figma 41:708 / 41:978 / 41:1204)
//
//  Ya no es un tablero de numeros: es "que hago ahora". Arriba lo que
//  necesita atencion, luego las dos cosas que se hacen a diario, luego los
//  atajos y al final las ultimas ventas.
//
//  Los numeros completos se fueron a Rendimiento. Antes Inicio y Estadisticas
//  mostraban casi lo mismo y no habia razon para tener las dos.
// ============================================================

@Composable
fun PantallaInicio(
    avisos: Int,
    materialesBajos: Int,
    /** Materiales con al menos un lote cuya fecha ya paso. */
    materialesCaducados: Int,
    totalMateriales: Int,
    totalProductos: Int,
    ultimasVentas: List<Venta>,
    detalleDeVenta: (Venta) -> String,
    dinero: (Double) -> String,
    onAvisos: () -> Unit,
    onConfiguracion: () -> Unit,
    onNuevaVenta: () -> Unit,
    onInventario: () -> Unit,
    onCatalogo: () -> Unit,
    onVenta: (Venta) -> Unit,
    onRendimiento: () -> Unit,
    onDestino: (DestinoBarra) -> Unit
) {
    Marco(
        barra = {
            // La cabecera de Inicio no es una App Bar de navegacion: no tiene
            // flecha y el titulo es mas grande (Lora 28, Figma 41:720).
            Row(
                Modifier.fillMaxWidth().padding(
                    start = Margenes.pantalla, end = Margenes.sm, top = Margenes.sm
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Inicio",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f).semantics { heading() }
                )
                BotonIcono(
                    Iconos.Campana, "Avisos", { onAvisos() },
                    conPunto = avisos > 0, conFondo = true
                )
                BotonIcono(
                    Iconos.Ajustes, "Configuración", { onConfiguracion() },
                    conFondo = true
                )
            }
        },
        pie = { BarraInferior(DestinoBarra.INICIO, onDestino) }
    ) {
        if (materialesBajos > 0) {
            item {
                BannerAviso(
                    // El numero va en el texto: el color rojo solo no dice
                    // cuantos son, ni sirve a quien no lo distingue.
                    if (materialesBajos == 1) "1 material con stock bajo"
                    else "$materialesBajos materiales con stock bajo",
                    onClick = onInventario
                )
            }
        }

        // Lo caducado va DEBAJO de lo que esta por acabarse, y no arriba, por
        // orden de accion: el stock bajo se resuelve comprando hoy, lo caducado
        // ya solo se retira. Cuenta MATERIALES y no lotes: tres lotes vencidos
        // del mismo material son un solo viaje al almacen.
        if (materialesCaducados > 0) {
            item {
                BannerAviso(
                    if (materialesCaducados == 1) "1 material caducado"
                    else "$materialesCaducados materiales caducados",
                    onClick = onInventario
                )
            }
        }

        item {
            FilaPareja {
                AccionRapida(
                    Iconos.Carrito, "Registrar venta",
                    modifier = Modifier.weight(1f).fillMaxHeight(), onClick = onNuevaVenta
                )
                AccionRapida(
                    Iconos.Tendencia, "Rendimiento",
                    modifier = Modifier.weight(1f).fillMaxHeight(), onClick = onRendimiento
                )
            }
        }

        item {
            FilaPareja {
                TarjetaResumen(
                    etiqueta = "Materiales",
                    valor = "$totalMateriales ítems",
                    nota = if (materialesBajos > 0) "$materialesBajos con stock bajo"
                    else "Todos con stock",
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    notaEsAlerta = materialesBajos > 0,
                    onClick = onInventario
                )
                TarjetaResumen(
                    etiqueta = "Productos",
                    valor = "$totalProductos ítems",
                    nota = "Todos activos",
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    onClick = onCatalogo
                )
            }
        }

        item {
            Text(
                "Últimas ventas",
                style = MaterialTheme.typography.tituloMedio,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.semantics { heading() }
            )
        }

        if (ultimasVentas.isEmpty()) {
            item {
                EstadoVacio(
                    "Sin ventas registradas",
                    "Las ventas registradas aparecerán aquí",
                    textoAccion = "Registrar venta",
                    onAccion = onNuevaVenta
                )
            }
        } else {
            items(ultimasVentas.size) { i ->
                val venta = ultimasVentas[i]
                FilaVenta(
                    titulo = detalleDeVenta(venta),
                    detalle = venta.fecha,
                    total = dinero(venta.total),
                    cancelada = venta.cancelada,
                    onClick = { onVenta(venta) }
                )
            }
        }
    }
}
