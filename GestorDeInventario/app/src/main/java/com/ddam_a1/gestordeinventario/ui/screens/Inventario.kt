package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.modelClasses.Material
import com.ddam_a1.gestordeinventario.ui.cant
import com.ddam_a1.gestordeinventario.ui.components.BarraBusqueda
import com.ddam_a1.gestordeinventario.ui.components.BarraInferior
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.ChipFiltro
import com.ddam_a1.gestordeinventario.ui.components.DestinoBarra
import com.ddam_a1.gestordeinventario.ui.components.EstadoVacio
import com.ddam_a1.gestordeinventario.ui.components.FilaLista
import com.ddam_a1.gestordeinventario.ui.dinero
import com.ddam_a1.gestordeinventario.ui.theme.coloresExtra

private enum class FiltroInv { TODOS, BAJOS, CADUCAN }

/**
 * Pantalla 6 - Inventario de materiales (RF20).
 *
 * Recibe la lista ya hecha. No sabe de donde sale ni le pide nada a nadie.
 * `esStockBajo` llega como funcion para que la regla ("cuando la cantidad baja
 * del minimo") viva en una sola parte y la pantalla solo la consulte.
 */
@Composable
fun PantallaInventario(
    materiales: List<Material>,
    esStockBajo: (Material) -> Boolean,
    onMaterial: (String) -> Unit,
    onNuevoMaterial: () -> Unit,
    onDestino: (DestinoBarra) -> Unit
) {
    // El texto de busqueda y el chip elegido SI son estado de esta pantalla:
    // nadie mas los necesita y no sobreviven a salir de aqui. Por eso van en
    // un `remember` y no suben al NavHost.
    var texto by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf(FiltroInv.TODOS) }

    // El filtrado se hace aqui sobre la lista que ya llego. Con Room y miles de
    // materiales esto se convertiria en un WHERE en el DAO; con las decenas de
    // un negocio chico, filtrar en memoria es correcto y mas simple.
    val encontrados =
        if (texto.isBlank()) materiales
        else materiales.filter { it.nombre.contains(texto, ignoreCase = true) }

    val lista = when (filtro) {
        FiltroInv.TODOS -> encontrados
        FiltroInv.BAJOS -> encontrados.filter { esStockBajo(it) }
        FiltroInv.CADUCAN -> encontrados.filter { it.fechasCaducidad.isNotEmpty() }
    }

    Marco(
        barra = { BarraSuperior("Inventario", materiales.size.toString() + " materiales") },
        pie = { BarraInferior(DestinoBarra.INVENTARIO, onDestino) }
    ) {
        item { BarraBusqueda(texto, "Buscar material") { texto = it } }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipFiltro("Todos", filtro == FiltroInv.TODOS) { filtro = FiltroInv.TODOS }
                ChipFiltro("Stock bajo", filtro == FiltroInv.BAJOS) { filtro = FiltroInv.BAJOS }
                ChipFiltro("Con caducidad", filtro == FiltroInv.CADUCAN) { filtro = FiltroInv.CADUCAN }
            }
        }
        if (lista.isEmpty()) {
            item { EstadoVacio("Sin materiales", "Toca el boton para agregar el primero") }
        } else {
            items(lista.size) { i ->
                val m = lista[i]
                val bajo = esStockBajo(m)
                FilaLista(
                    m.nombre,
                    dinero(m.costoUnitario) + " / " + m.unidadMedida +
                        (if (m.fechasCaducidad.isNotEmpty()) " - caduca " + m.fechasCaducidad.min() else ""),
                    cant(m.cantidadDisponible) + " " + m.unidadMedida,
                    if (bajo) "stock bajo" else null,
                    if (bajo) MaterialTheme.colorScheme.error else MaterialTheme.coloresExtra.correct.color
                ) { onMaterial(m.id) }
            }
        }
        item {
            Spacer(Modifier.height(4.dp))
            BotonPrincipal("Nuevo material") { onNuevoMaterial() }
        }
    }
}
