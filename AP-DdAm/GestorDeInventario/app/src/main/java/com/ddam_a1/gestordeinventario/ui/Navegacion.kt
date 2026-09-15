package com.ddam_a1.gestordeinventario.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.componentes.Iconos

sealed class Ruta {
    data object Login : Ruta()
    data object CrearAdmin : Ruta()
    data object ElegirModo : Ruta()
    data object Inicio : Ruta()
    data object Estadisticas : Ruta()
    data object Inventario : Ruta()
    data class DetalleMaterial(val id: String) : Ruta()
    data class FormularioMaterial(val id: String?) : Ruta()
    data object Catalogo : Ruta()
    data class DetalleProducto(val id: String) : Ruta()
    data class FormularioProducto(val id: String?) : Ruta()
    data class Receta(val productoId: String) : Ruta()
    data class Produccion(val productoId: String) : Ruta()
    data object NuevaVenta : Ruta()
    data object HistorialVentas : Ruta()
    data object Avisos : Ruta()
    data object Usuarios : Ruta()
    data object Permisos : Ruta()
    data object Configuracion : Ruta()
    data object Exportar : Ruta()
}

/** Pila de navegación mínima, sin dependencias extra. */
class Navegador(inicial: Ruta) {
    private val pila = mutableStateListOf(inicial)
    val actual: Ruta get() = pila.last()
    val puedeVolver: Boolean get() = pila.size > 1
    fun ir(r: Ruta) { pila.add(r) }
    fun irARaiz(r: Ruta) { pila.clear(); pila.add(r) }
    fun volver() { if (puedeVolver) pila.removeAt(pila.lastIndex) }
}

@Composable
fun recordarNavegador(inicial: Ruta): Navegador = remember { Navegador(inicial) }

private data class Destino(val ruta: Ruta, val icono: ImageVector, val etiqueta: String)

private val destinos = listOf(
    Destino(Ruta.Inicio, Iconos.Inicio, "Inicio"),
    Destino(Ruta.Inventario, Iconos.Inventario, "Inventario"),
    Destino(Ruta.Catalogo, Iconos.Catalogo, "Catálogo"),
    Destino(Ruta.HistorialVentas, Iconos.Ventas, "Ventas")
)

@Composable
fun BarraInferior(actual: Ruta, onIr: (Ruta) -> Unit) {
    Column {
        Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outline))
        Row(
            Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .navigationBarsPadding()
                .padding(vertical = 8.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            destinos.forEach { d ->
                val activo = actual == d.ruta
                Column(
                    Modifier.weight(1f).clip(RoundedCornerShape(16.dp))
                        .clickable { onIr(d.ruta) }.padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier.clip(RoundedCornerShape(14.dp))
                            .background(if (activo) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                            .padding(horizontal = 18.dp, vertical = 4.dp)
                    ) {
                        Icon(d.icono, d.etiqueta,
                            tint = if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.height(3.dp))
                    Text(d.etiqueta, style = MaterialTheme.typography.labelSmall,
                        color = if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
