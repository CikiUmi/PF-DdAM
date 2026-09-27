package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * Los iconos del proyecto, como datos de trazo (formato SVG).
 *
 * Los glifos son los del Figma, que son de la familia Feather: linea de
 * grosor parejo, extremos redondeados y caja de 24.
 *
 * Se escriben aquí en vez de descargar los SVG del plugin de Figma porque
 * esos archivos los sirve un servidor local de la máquina de diseño, que este
 * entorno no alcanza. El trazo es el mismo; lo que cambia es de dónde sale.
 *
 * No se usa la librería material-icons para no arrastrar una dependencia
 * entera por veinticuatro dibujos.
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
    val Inicio      = icono("inicio", "M3 9.5L12 2.5L21 9.5V20A2 2 0 0 1 19 22H5A2 2 0 0 1 3 20Z M9 22V12H15V22")
    val Inventario  = icono("inventario", "M21 8V21H3V8 M1.5 3H22.5V8H1.5Z M10 12H14")
    val Catalogo    = icono("catalogo", "M8 6H21 M8 12H21 M8 18H21 M3.5 6H3.51 M3.5 12H3.51 M3.5 18H3.51", 2.0f)
    val Tendencia   = icono("tendencia", "M23 6.5L13.5 16L8.5 11L1 18.5 M17 6.5H23V12.5", 2.0f)
    val Carrito     = icono("carrito", "M1.5 2H5L7.7 15.4A2 2 0 0 0 9.7 17H19.4A2 2 0 0 0 21.4 15.4L23 6.5H6 M8 21A1.2 1.2 0 1 0 10.4 21A1.2 1.2 0 1 0 8 21 M18 21A1.2 1.2 0 1 0 20.4 21A1.2 1.2 0 1 0 18 21")
    val Buscar      = icono("buscar", "M11 3A8 8 0 1 0 11 19A8 8 0 1 0 11 3 M21 21L16.65 16.65")
    val Agregar     = icono("agregar", "M12 5V19 M5 12H19", 2.1f)
    val Quitar      = icono("quitar", "M5 12H19", 2.1f)
    val Atras       = icono("atras", "M15 18L9 12L15 6", 2.1f)
    val Siguiente   = icono("siguiente", "M9 18L15 12L9 6", 2.1f)
    val Campana     = icono("campana", "M18 8A6 6 0 0 0 6 8C6 15 3 17 3 17H21S18 15 18 8 M13.73 21A2 2 0 0 1 10.27 21")
    val Editar      = icono("editar", "M17 3A2.83 2.83 0 1 1 21 7L7.5 20.5L2 22L3.5 16.5Z")
    val Filtro      = icono("filtro", "M22 3H2L10 12.46V19L14 21V12.46Z")
    val Alerta      = icono("alerta", "M10.29 3.86L1.82 18A2 2 0 0 0 3.53 21H20.47A2 2 0 0 0 22.18 18L13.71 3.86A2 2 0 0 0 10.29 3.86Z M12 9V13 M12 17H12.01")
    val Calendario  = icono("calendario", "M5 4H19A2 2 0 0 1 21 6V20A2 2 0 0 1 19 22H5A2 2 0 0 1 3 20V6A2 2 0 0 1 5 4Z M3 10H21 M16 2V6 M8 2V6")
    val Usuario     = icono("usuario", "M20 21V19A4 4 0 0 0 16 15H8A4 4 0 0 0 4 19V21 M12 3A4 4 0 1 0 12 11A4 4 0 1 0 12 3")
    val Ajustes     = icono("ajustes", "M12 9A3 3 0 1 0 12 15A3 3 0 1 0 12 9 M19.4 15A1.65 1.65 0 0 0 19.73 16.82L19.79 16.88A2 2 0 1 1 16.96 19.71L16.9 19.65A1.65 1.65 0 0 0 15.08 19.32A1.65 1.65 0 0 0 14 20.83V21A2 2 0 1 1 10 21V20.91A1.65 1.65 0 0 0 8.92 19.4A1.65 1.65 0 0 0 7.1 19.73L7.04 19.79A2 2 0 1 1 4.21 16.96L4.27 16.9A1.65 1.65 0 0 0 4.6 15.08A1.65 1.65 0 0 0 3.09 14H3A2 2 0 1 1 3 10H3.09A1.65 1.65 0 0 0 4.6 8.92A1.65 1.65 0 0 0 4.27 7.1L4.21 7.04A2 2 0 1 1 7.04 4.21L7.1 4.27A1.65 1.65 0 0 0 8.92 4.6A1.65 1.65 0 0 0 10 3.09V3A2 2 0 1 1 14 3V3.09A1.65 1.65 0 0 0 15.08 4.6A1.65 1.65 0 0 0 16.9 4.27L16.96 4.21A2 2 0 1 1 19.79 7.04L19.73 7.1A1.65 1.65 0 0 0 19.4 8.92V9A1.65 1.65 0 0 0 20.91 10H21A2 2 0 1 1 21 14H20.91A1.65 1.65 0 0 0 19.4 15Z", 1.6f)
    val Descargar   = icono("descargar", "M21 15V19A2 2 0 0 1 19 21H5A2 2 0 0 1 3 19V15 M7 10L12 15L17 10 M12 15V3")
    val Grafica     = icono("grafica", "M18 20V10 M12 20V4 M6 20V14", 2.0f)
    val Reloj       = icono("reloj", "M12 2A10 10 0 1 0 12 22A10 10 0 1 0 12 2 M12 6V12L16 14")
    val Check       = icono("check", "M20 6L9 17L4 12", 2.2f)
    val Cerrar      = icono("cerrar", "M18 6L6 18 M6 6L18 18", 2.1f)
    val Candado     = icono("candado", "M5 11H19A2 2 0 0 1 21 13V20A2 2 0 0 1 19 22H5A2 2 0 0 1 3 20V13A2 2 0 0 1 5 11Z M7 11V7A5 5 0 0 1 17 7V11")
    val Mas         = icono("mas", "M12 13A1 1 0 1 0 12 11A1 1 0 1 0 12 13 M12 6A1 1 0 1 0 12 4A1 1 0 1 0 12 6 M12 20A1 1 0 1 0 12 18A1 1 0 1 0 12 20", 2.0f)
    val Caja        = icono("caja", "M16.5 9.9L7.5 4.7 M21 16V8A2 2 0 0 0 20 6.27L13 2.27A2 2 0 0 0 11 2.27L4 6.27A2 2 0 0 0 3 8V16A2 2 0 0 0 4 17.73L11 21.73A2 2 0 0 0 13 21.73L20 17.73A2 2 0 0 0 21 16Z M3.3 7L12 12.05L20.7 7 M12 22.1V12")
    val Ojo         = icono("ojo", "M2 12C4.5 7.5 8 5.5 12 5.5S19.5 7.5 22 12C19.5 16.5 16 18.5 12 18.5S4.5 16.5 2 12Z M12 9.2A2.8 2.8 0 1 0 12 14.8A2.8 2.8 0 1 0 12 9.2")
    val OjoOculto   = icono("ojoOculto", "M4 12C6 8.6 8.8 6.6 12 6.6C13.2 6.6 14.3 6.9 15.3 7.3 M19.2 9.6C19.9 10.3 20.5 11.1 21 12C18.5 16.5 15 18.5 12 18.5C11 18.5 10 18.3 9.1 18 M3 3L21 21")
    val Etiqueta    = icono("etiqueta", "M20.59 13.41L13.42 20.58A2 2 0 0 1 10.59 20.58L2 12V2H12L20.59 10.59A2 2 0 0 1 20.59 13.41Z M7 7H7.01")
}
