package com.ddam_a1.gestordeinventario.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.datos.AlmacenamientoLocal
import com.ddam_a1.gestordeinventario.datos.InventarioMateriales
import com.ddam_a1.gestordeinventario.datos.CatalogoProductos
import com.ddam_a1.gestordeinventario.ui.*
import com.ddam_a1.gestordeinventario.ui.componentes.*
import com.ddam_a1.gestordeinventario.ui.theme.*
import com.ddam_a1.gestordeinventario.ui.navegacion.BarraInferior
import com.ddam_a1.gestordeinventario.ui.navegacion.Navegador
import com.ddam_a1.gestordeinventario.ui.navegacion.Ruta

private enum class FiltroInv { TODOS, BAJOS, CADUCAN }

/** Pantalla 6 · Inventario de materiales (RF20). */
@Composable
fun PantallaInventario(nav: Navegador) {
    EstadoApp.version
    var texto by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf(FiltroInv.TODOS) }

    val todos = if (texto.isBlank()) InventarioMateriales.obtenerTodos()
    else InventarioMateriales.buscarMaterial(texto)
    val lista = when (filtro) {
        FiltroInv.TODOS -> todos
        FiltroInv.BAJOS -> todos.filter { InventarioMateriales.esStockBajo(it) }
        FiltroInv.CADUCAN -> todos.filter { it.fechasCaducidad.isNotEmpty() }
    }

    Marco(
        barra = { BarraSuperior("Inventario", "${todos.size} materiales") },
        pie = { BarraInferior(nav.actual) { nav.irARaiz(it) } }
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
            item { EstadoVacio("Sin materiales", "Toca + para agregar el primero") }
        } else {
            items(lista.size) { i ->
                val m = lista[i]
                val bajo = InventarioMateriales.esStockBajo(m)
                FilaLista(
                    m.nombre,
                    "${dinero(m.costoUnitario)} / ${m.unidadMedida}" +
                        if (m.fechasCaducidad.isNotEmpty()) " · caduca ${m.fechasCaducidad.min()}" else "",
                    "${cant(m.cantidadDisponible)} ${m.unidadMedida}",
                    if (bajo) "stock bajo" else null,
                    if (bajo) MaterialTheme.colorScheme.error else MaterialTheme.coloresExtra.correct.color
                ) { nav.ir(Ruta.DetalleMaterial(m.id)) }
            }
        }
        item {
            Spacer(Modifier.height(4.dp))
            BotonPrincipal("Nuevo material") { nav.ir(Ruta.FormularioMaterial(null)) }
        }
    }
}
