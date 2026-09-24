package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.modelClasses.Rol
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.ChipFiltro
import com.ddam_a1.gestordeinventario.ui.components.Insignia
import com.ddam_a1.gestordeinventario.ui.components.TarjetaSuave
import com.ddam_a1.gestordeinventario.ui.theme.coloresExtra

/** Pantalla 18 · Permisos por rol (RF25). */
@Composable
fun PantallaPermisos(onAtras: () -> Unit) {
    var rolElegido by remember { mutableStateOf(Rol.ENCARGADO) }
    val acciones = listOf(
        "registrar_venta" to "Registrar ventas",
        "editar_inventario" to "Crear y editar inventario",
        "ver_estadisticas" to "Ver rendimiento del negocio",
        "exportar" to "Exportar datos"
    )

    Marco(barra = { BarraSuperior("Permisos por rol", onAtras = { onAtras() }) }) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Rol.values().forEach { r ->
                    ChipFiltro(etiquetaRol(r), rolElegido == r, { rolElegido = r })
                }
            }
        }
        item {
            TarjetaSuave {
                Text("El administrador siempre tiene todos los permisos. Aquí se muestra lo que el módulo de Usuarios permite hoy para cada rol.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        items(acciones.size) { i ->
            val accion = acciones[i].first
            val texto = acciones[i].second
            val permitido = when (rolElegido) {
                Rol.ADMINISTRADOR -> true
                Rol.ENCARGADO -> accion == "registrar_venta" || accion == "editar_inventario" || accion == "ver_estadisticas"
                Rol.EMPLEADO -> accion == "registrar_venta"
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(texto, style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                Insignia(
                    if (permitido) "Permitido" else "Bloqueado",
                    if (permitido) MaterialTheme.coloresExtra.correct.color else MaterialTheme.colorScheme.error,
                    if (permitido) MaterialTheme.coloresExtra.correct.colorContainer else MaterialTheme.colorScheme.errorContainer
                )
            }
        }
    }
}
