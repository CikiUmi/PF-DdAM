package com.ddam_a1.gestordeinventario.ui.componentes

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * Iconos de línea dibujados a mano con datos de trazo (formato SVG).
 * Se definen aquí para no depender de la librería material-icons.
 */
private fun icono(nombre: String, d: String, grosor: Float = 1.8f): ImageVector =
    ImageVector.Builder(
        name = nombre,
        defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f
    ).addPath(
        pathData = PathParser().parsePathString(d).toNodes(),
        stroke = SolidColor(Color.Black),
        strokeLineWidth = grosor,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ).build()

object Iconos {
    val Inicio      = icono("inicio", "M3 11L12 3.5L21 11V20A1 1 0 0 1 20 21H15V14.5H9V21H4A1 1 0 0 1 3 20Z")
    val Inventario  = icono("inventario", "M3 7.5L12 3.5L21 7.5V16.5L12 20.5L3 16.5Z M3 7.5L12 11.5L21 7.5 M12 11.5V20.5")
    val Catalogo    = icono("catalogo", "M3.5 3.5H11L20.5 13L13 20.5L3.5 11Z M7.8 7.8H7.81")
    val Ventas      = icono("ventas", "M2.5 4H4.7L7.3 14.5H18.3L20.5 7H6 M7.5 19A1.5 1.5 0 1 0 10.5 19A1.5 1.5 0 1 0 7.5 19 M15.5 19A1.5 1.5 0 1 0 18.5 19A1.5 1.5 0 1 0 15.5 19")
    val Buscar      = icono("buscar", "M11 4A7 7 0 1 0 11 18A7 7 0 1 0 11 4 M16.2 16.2L20.5 20.5")
    val Agregar     = icono("agregar", "M12 5V19M5 12H19", 2.1f)
    val Quitar      = icono("quitar", "M5 12H19", 2.1f)
    val Atras       = icono("atras", "M15 19L8 12L15 5", 2.1f)
    val Siguiente   = icono("siguiente", "M9.5 5.5L16 12L9.5 18.5", 2.1f)
    val Campana     = icono("campana", "M18 8A6 6 0 1 0 6 8C6 14 3.5 14.5 3.5 16.5H20.5C20.5 14.5 18 14 18 8 M13.8 20.5A2 2 0 0 1 10.2 20.5")
    val Editar      = icono("editar", "M4 20.5H8.2L20 8.7L15.8 4.5L4 16.3Z M14.5 5.8L18.7 10")
    val Filtro      = icono("filtro", "M3.5 5H20.5L13.8 12.8V19L10.2 21V12.8Z")
    val Alerta      = icono("alerta", "M12 3.5L21.5 20.5H2.5Z M12 10V14.5 M12 17.6H12.01")
    val Calendario  = icono("calendario", "M4.5 5.5H19.5A1.5 1.5 0 0 1 21 7V19A1.5 1.5 0 0 1 19.5 20.5H4.5A1.5 1.5 0 0 1 3 19V7A1.5 1.5 0 0 1 4.5 5.5Z M3 10H21 M8 3V7 M16 3V7")
    val Usuario     = icono("usuario", "M12 4.2A3.8 3.8 0 1 0 12 11.8A3.8 3.8 0 1 0 12 4.2 M4.5 20.5C4.5 16.5 8.1 14.5 12 14.5S19.5 16.5 19.5 20.5")
    val Ajustes     = icono("ajustes", "M12 8.8A3.2 3.2 0 1 0 12 15.2A3.2 3.2 0 1 0 12 8.8 M12 2.5V5.1 M12 18.9V21.5 M2.5 12H5.1 M18.9 12H21.5 M5.2 5.2L7.1 7.1 M16.9 16.9L18.8 18.8 M18.8 5.2L16.9 7.1 M7.1 16.9L5.2 18.8")
    val Descargar   = icono("descargar", "M12 3.5V15 M7 10L12 15L17 10 M4 20.5H20")
    val Grafica     = icono("grafica", "M4 20.5V11 M10 20.5V4 M16 20.5V14 M2.5 20.5H21.5")
    val Reloj       = icono("reloj", "M12 3.5A8.5 8.5 0 1 0 12 20.5A8.5 8.5 0 1 0 12 3.5 M12 7V12.3L15.4 14.3")
    val Check       = icono("check", "M4.5 12.5L9.5 17.5L19.5 6.5", 2.2f)
    val Cerrar      = icono("cerrar", "M6 6L18 18M18 6L6 18", 2.1f)
    val Candado     = icono("candado", "M4.5 10.5H19.5A1.5 1.5 0 0 1 21 12V19A1.5 1.5 0 0 1 19.5 20.5H4.5A1.5 1.5 0 0 1 3 19V12A1.5 1.5 0 0 1 4.5 10.5Z M8 10.5V7.5A4 4 0 0 1 16 7.5V10.5")
    val Mas         = icono("mas", "M12 5.4H12.01 M12 12H12.01 M12 18.6H12.01", 2.6f)
    val Caja        = icono("caja", "M20.5 7.5V16.5L12 20.5L3.5 16.5V7.5L12 3.5Z")
    val Etiqueta    = icono("etiqueta", "M3.5 12.5V4.5A1 1 0 0 1 4.5 3.5H12.5L20.5 11.5L12.5 19.5Z M7.5 7.5H7.51")
}
