package com.ddam_a1.gestordeinventario.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.datos.AlmacenamientoLocal
import com.ddam_a1.gestordeinventario.datos.InventarioMateriales
import com.ddam_a1.gestordeinventario.datos.CatalogoProductos
import com.ddam_a1.gestordeinventario.ui.navegacion.BarraInferior
import com.ddam_a1.gestordeinventario.ui.EstadoApp
import com.ddam_a1.gestordeinventario.ui.navegacion.Navegador
import com.ddam_a1.gestordeinventario.ui.navegacion.Ruta
import com.ddam_a1.gestordeinventario.ui.cant
import com.ddam_a1.gestordeinventario.ui.componentes.BarraBusqueda
import com.ddam_a1.gestordeinventario.ui.componentes.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.componentes.BotonIcono
import com.ddam_a1.gestordeinventario.ui.componentes.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.componentes.CampoTexto
import com.ddam_a1.gestordeinventario.ui.componentes.ChipFiltro
import com.ddam_a1.gestordeinventario.ui.componentes.EncabezadoSeccion
import com.ddam_a1.gestordeinventario.ui.componentes.EstadoVacio
import com.ddam_a1.gestordeinventario.ui.componentes.FilaLista
import com.ddam_a1.gestordeinventario.ui.componentes.Iconos
import com.ddam_a1.gestordeinventario.ui.componentes.Insignia
import com.ddam_a1.gestordeinventario.ui.componentes.TarjetaSuave
import com.ddam_a1.gestordeinventario.ui.dinero
import com.ddam_a1.gestordeinventario.ui.hoy

/** Pantalla 12 · Seleccionar materiales de la receta (RF6). */
@Composable
fun PantallaReceta(nav: Navegador, productoId: String) {
    EstadoApp.version
    val producto = CatalogoProductos.obtenerProductoPorId(productoId)
    if (producto == null) {
        nav.volver()
        return
    }
    val materiales = InventarioMateriales.obtenerTodos()
    val cantidades = remember { mutableStateMapOf<String, String>() }

    remember(productoId) {
        producto.receta.forEach { ingrediente ->
            cantidades[ingrediente.materialId] = cant(ingrediente.cantidadUsada)
        }
        productoId
    }

    var costo = 0.0
    for (entrada in cantidades) {
        val material = InventarioMateriales.obtenerMaterialPorId(entrada.key)
        val unitario = if (material != null) material.costoUnitario else 0.0
        costo += (entrada.value.toDoubleOrNull() ?: 0.0) * unitario
    }

    Marco(barra = { BarraSuperior("Materiales", producto.nombre, onAtras = { nav.volver() }) }) {
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
                    Text(cantidades.size.toString() + " materiales · costo calculado",
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
                producto.receta.clear()
                for (entrada in cantidades) {
                    val c = entrada.value.toDoubleOrNull() ?: 0.0
                    if (c > 0.0) {
                        CatalogoProductos.agregarIngredienteReceta(productoId, entrada.key, c)
                    }
                }
                CatalogoProductos.calcularCostoProduccion(productoId)
                EstadoApp.datosCambiaron()
                nav.volver()
            }
        }
    }
}
