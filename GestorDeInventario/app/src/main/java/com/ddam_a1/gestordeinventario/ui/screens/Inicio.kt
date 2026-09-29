package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.ddam_a1.gestordeinventario.ui.puede
import com.ddam_a1.gestordeinventario.data.negocio.Accion
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
    productosBajos: Int,
    /** Materiales con al menos un lote cuya fecha ya paso. */
    materialesCaducados: Int,
    productosCaducados: Int,
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
        // Las cuatro franjas, en orden de que se puede hacer: primero lo que
        // se resuelve comprando o produciendo hoy, despues lo que ya solo se
        // retira. Salen todas las que apliquen aunque se junten: un dia con
        // cuatro problemas es un dia con cuatro problemas, y esconder alguno
        // para que la pantalla se vea limpia es esconderle al usuario su
        // propio negocio.
        franjaAviso(materialesBajos, "material con stock bajo", "materiales con stock bajo", onInventario)
        franjaAviso(productosBajos, "producto con stock bajo", "productos con stock bajo", onCatalogo)
        franjaAviso(materialesCaducados, "material caducado", "materiales caducados", onInventario)
        franjaAviso(productosCaducados, "producto caducado", "productos caducados", onCatalogo)

        item {
            FilaPareja {
                AccionRapida(
                    Iconos.Carrito, "Registrar venta",
                    modifier = Modifier.weight(1f).fillMaxHeight(), onClick = onNuevaVenta
                )
                // Sin permiso de ver el rendimiento queda una sola accion,
                // a todo el ancho. Un hueco vacio al lado pareceria un error.
                if (puede(Accion.VER_ESTADISTICAS)) {
                    AccionRapida(
                        Iconos.Tendencia, "Rendimiento",
                        modifier = Modifier.weight(1f).fillMaxHeight(), onClick = onRendimiento
                    )
                }
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

/**
 * Una franja roja, si hay algo que contar.
 *
 * El numero VA EN EL TEXTO: el color rojo por si solo no dice cuantos son, ni
 * le sirve a quien no lo distingue. Y el singular va aparte del plural porque
 * "1 materiales" se lee como un error de la aplicacion.
 */
private fun LazyListScope.franjaAviso(
    cuantos: Int,
    uno: String,
    varios: String,
    onClick: () -> Unit
) {
    if (cuantos <= 0) return
    item {
        BannerAviso(
            if (cuantos == 1) "1 " + uno else cuantos.toString() + " " + varios,
            onClick = onClick
        )
    }
}
