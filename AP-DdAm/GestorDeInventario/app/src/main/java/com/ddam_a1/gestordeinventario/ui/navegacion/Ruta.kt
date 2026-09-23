package com.ddam_a1.gestordeinventario.ui.navegacion

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
