package com.ddam_a1.gestordeinventario.ui.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import com.ddam_a1.gestordeinventario.ui.theme.Acento
import com.ddam_a1.gestordeinventario.ui.theme.PrimarioClaro
import com.ddam_a1.gestordeinventario.ui.theme.Secundario

/* ---------------------------------------------------------------- superficies */

@Composable
fun TarjetaSuave(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    relleno: Int = 16,
    contenido: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    val forma = RoundedCornerShape(20.dp)
    Card(
        modifier = if (onClick != null) modifier.clickable { onClick() } else modifier,
        shape = forma,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.padding(relleno.dp), content = contenido)
    }
}

/* ---------------------------------------------------------------- barras */

@Composable
fun BarraSuperior(
    titulo: String,
    subtitulo: String? = null,
    onAtras: (() -> Unit)? = null,
    acciones: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onAtras != null) {
            BotonIcono(Iconos.Atras, "Atrás", onAtras)
        } else {
            Spacer(Modifier.width(8.dp))
        }
        Column(Modifier.weight(1f).padding(horizontal = 4.dp)) {
            Text(
                titulo,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitulo != null) {
                Text(
                    subtitulo,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        acciones()
    }
}

@Composable
fun BotonIcono(
    icono: ImageVector,
    descripcion: String,
    onClick: () -> Unit,
    tinte: Color? = null,
    conPunto: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icono,
            contentDescription = descripcion,
            tint = tinte ?: MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )
        if (conPunto) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = 10.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error)
            )
        }
    }
}

/* ---------------------------------------------------------------- botones */

@Composable
fun BotonPrincipal(
    texto: String,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    onClick: () -> Unit
) {
    val degradado = Brush.horizontalGradient(
        listOf(MaterialTheme.colorScheme.primary, PrimarioClaro)
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (habilitado) degradado
                else Brush.horizontalGradient(
                    listOf(MaterialTheme.colorScheme.outline, MaterialTheme.colorScheme.outline)
                )
            )
            .clickable(enabled = habilitado) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            texto,
            style = MaterialTheme.typography.labelLarge,
            color = Color.White
        )
    }
}

@Composable
fun BotonSecundario(
    texto: String,
    modifier: Modifier = Modifier,
    color: Color? = null,
    onClick: () -> Unit
) {
    val c = color ?: MaterialTheme.colorScheme.primary
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.5.dp, c, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(texto, style = MaterialTheme.typography.labelLarge, color = c)
    }
}

@Composable
fun BotonFlotante(icono: ImageVector, descripcion: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(Acento, Secundario)))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icono, descripcion, tint = Color.White, modifier = Modifier.size(26.dp))
    }
}

/* ---------------------------------------------------------------- entradas */

@Composable
fun CampoTexto(
    valor: String,
    etiqueta: String,
    onCambio: (String) -> Unit,
    modifier: Modifier = Modifier,
    soloNumeros: Boolean = false,
    sufijo: String? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = { nuevo ->
            if (!soloNumeros) onCambio(nuevo)
            else if (nuevo.isEmpty() || nuevo.matches(Regex("^\\d*[.,]?\\d*$"))) onCambio(nuevo.replace(',', '.'))
        },
        label = { Text(etiqueta, style = MaterialTheme.typography.bodyMedium) },
        suffix = if (sufijo != null) {
            { Text(sufijo, style = MaterialTheme.typography.bodyMedium) }
        } else null,
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun BarraBusqueda(texto: String, marcador: String, onCambio: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(25.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Iconos.Buscar, null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(19.dp)
        )
        Spacer(Modifier.width(10.dp))
        Box(Modifier.weight(1f)) {
            if (texto.isEmpty()) {
                Text(
                    marcador,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            androidx.compose.foundation.text.BasicTextField(
                value = texto,
                onValueChange = onCambio,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ChipFiltro(texto: String, activo: Boolean, onClick: () -> Unit) {
    val fondo = if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val letra = if (activo) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(fondo)
            .border(
                1.dp,
                if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 9.dp)
    ) {
        Text(texto, style = MaterialTheme.typography.labelMedium, color = letra)
    }
}

/* ---------------------------------------------------------------- piezas de contenido */

@Composable
fun EncabezadoSeccion(
    titulo: String,
    accion: String? = null,
    onAccion: (() -> Unit)? = null
) {
    Row(
        Modifier.fillMaxWidth().padding(bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            titulo,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        if (accion != null && onAccion != null) {
            Text(
                accion,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onAccion() }
            )
        }
    }
}

@Composable
fun Insignia(texto: String, color: Color, fondo: Color) {
    Box(
        Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(fondo)
            .padding(horizontal = 9.dp, vertical = 4.dp)
    ) {
        Text(texto, style = MaterialTheme.typography.labelSmall, color = color)
    }
}

@Composable
fun TarjetaMetrica(
    etiqueta: String,
    valor: String,
    nota: String? = null,
    colorFondo: Color,
    colorTexto: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(colorFondo, colorFondo.copy(alpha = 0.55f))
                )
            )
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(16.dp)
    ) {
        Column {
            Text(
                etiqueta,
                style = MaterialTheme.typography.labelSmall,
                color = colorTexto.copy(alpha = 0.85f)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                valor,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = colorTexto,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (nota != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    nota,
                    style = MaterialTheme.typography.labelSmall,
                    color = colorTexto.copy(alpha = 0.75f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun FilaLista(
    titulo: String,
    subtitulo: String? = null,
    valor: String? = null,
    notaValor: String? = null,
    colorPunto: Color? = null,
    onClick: (() -> Unit)? = null
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
                .padding(vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (colorPunto != null) {
                Box(
                    Modifier.size(9.dp).clip(CircleShape).background(colorPunto)
                )
                Spacer(Modifier.width(11.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(
                    titulo,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitulo != null) {
                    Spacer(Modifier.height(3.dp))
                    Text(
                        subtitulo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (valor != null) {
                Spacer(Modifier.width(10.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        valor,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (notaValor != null) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            notaValor,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        Box(
            Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outline)
        )
    }
}

@Composable
fun BannerAviso(titulo: String, detalle: String, color: Color, fondo: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(fondo)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Iconos.Alerta, null, tint = color, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium, color = color)
            Text(detalle, style = MaterialTheme.typography.bodyMedium, color = color.copy(alpha = 0.85f))
        }
        Icon(Iconos.Siguiente, null, tint = color, modifier = Modifier.size(18.dp))
    }
}

@Composable
fun EstadoVacio(mensaje: String, sugerencia: String? = null) {
    Column(
        Modifier.fillMaxWidth().padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Iconos.Caja, null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(52.dp)
        )
        Spacer(Modifier.height(14.dp))
        Text(
            mensaje,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (sugerencia != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                sugerencia,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/* ---------------------------------------------------------------- gráficas */

@Composable
fun GraficaBarras(
    datos: List<Pair<String, Double>>,
    color: Color,
    modifier: Modifier = Modifier
) {
    val maximo = (datos.maxOfOrNull { it.second } ?: 0.0).coerceAtLeast(1.0)
    Column(modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().height(150.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            datos.forEach { (_, valor) ->
                val proporcion = (valor / maximo).toFloat().coerceIn(0.04f, 1f)
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight(proporcion)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(
                            Brush.verticalGradient(listOf(color, color.copy(alpha = 0.45f)))
                        )
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            datos.forEach { (etiqueta, _) ->
                Text(
                    etiqueta,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun GraficaDona(
    porciones: List<Pair<Color, Float>>,
    textoCentro: String,
    etiquetaCentro: String,
    modifier: Modifier = Modifier
) {
    val total = porciones.sumOf { it.second.toDouble() }.toFloat().coerceAtLeast(0.0001f)
    Box(modifier.size(190.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val grosor = 26.dp.toPx()
            val radio = size.minDimension / 2 - grosor / 2
            val esquina = Offset(size.width / 2 - radio, size.height / 2 - radio)
            val medida = Size(radio * 2, radio * 2)
            var inicio = -90f
            porciones.forEach { (color, valor) ->
                val barrido = valor / total * 360f
                drawArc(
                    color = color,
                    startAngle = inicio + 1.2f,
                    sweepAngle = (barrido - 2.4f).coerceAtLeast(0f),
                    useCenter = false,
                    topLeft = esquina,
                    size = medida,
                    style = Stroke(width = grosor, cap = StrokeCap.Round)
                )
                inicio += barrido
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                etiquetaCentro,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                textoCentro,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun Superficie(contenido: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        content = contenido
    )
}
