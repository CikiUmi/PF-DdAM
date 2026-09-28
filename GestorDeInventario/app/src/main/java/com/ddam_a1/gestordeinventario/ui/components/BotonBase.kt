package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Medidas
import com.ddam_a1.gestordeinventario.ui.theme.Radios

// ============================================================
//  EL BOTON, UNA SOLA VEZ  (Figma 36:41)
//
//  El diseno trae 9 variantes: 3 tipos x 3 estados. Pero "Pressed" y
//  "Disabled" no son tipos distintos de boton: son el MISMO boton en otro
//  momento. Aqui los tipos son un enum y los estados salen solos de
//  `habilitado` y de si el dedo esta encima.
//
//  Del Figma: alto 48, radio 24, texto Nunito Bold 16, sombra 4dp en los
//  rellenos, borde de 2 en el de contorno, y 60% de opacidad al deshabilitar.
//
//  NOTA DE DISENO: el boton principal usa `tertiary` (el vino), no `primary`
//  (el azul). Viene asi del Figma; si algun dia quieres el azul, se cambia
//  aqui y cambia en toda la app.
// ============================================================

internal enum class EstiloBoton { RELLENO, CONTORNO, DESTRUCTIVO }

@Composable
internal fun BotonBase(
    texto: String,
    estilo: EstiloBoton,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    onClick: () -> Unit
) {
    // collectIsPressedAsState escucha al mismo interactionSource que recibe el
    // clickable: por eso hay que crearlo aqui y pasarselo, en vez de dejar que
    // clickable se haga uno propio que nadie mas puede ver.
    val interacciones = remember { MutableInteractionSource() }
    val presionado by interacciones.collectIsPressedAsState()

    val esquina = RoundedCornerShape(Radios.boton)
    val cs = MaterialTheme.colorScheme

    val fondo: Color
    val contenido: Color
    val borde: BorderStroke?

    when {
        !habilitado && estilo == EstiloBoton.CONTORNO -> {
            fondo = Color.Transparent
            contenido = cs.onSurfaceVariant
            borde = BorderStroke(Medidas.bordeGrueso, cs.outlineVariant)
        }
        !habilitado -> {
            fondo = cs.surfaceContainerHighest
            contenido = cs.onSurfaceVariant
            borde = null
        }
        estilo == EstiloBoton.RELLENO && presionado -> {
            fondo = cs.tertiaryContainer; contenido = cs.onTertiaryContainer; borde = null
        }
        estilo == EstiloBoton.RELLENO -> {
            fondo = cs.tertiary; contenido = cs.onTertiary; borde = null
        }
        estilo == EstiloBoton.CONTORNO && presionado -> {
            fondo = cs.tertiaryContainer
            contenido = cs.onTertiaryContainer
            borde = BorderStroke(Medidas.bordeGrueso, cs.tertiary)
        }
        estilo == EstiloBoton.CONTORNO -> {
            fondo = Color.Transparent
            contenido = cs.tertiary
            borde = BorderStroke(Medidas.bordeGrueso, cs.tertiary)
        }
        presionado -> {   // DESTRUCTIVO presionado
            fondo = cs.errorContainer; contenido = cs.onSurface; borde = null
        }
        else -> {         // DESTRUCTIVO
            fondo = cs.error; contenido = cs.onError; borde = null
        }
    }

    // La sombra solo va en los rellenos y solo cuando estan activos: un boton
    // apagado que proyecta sombra parece que se puede tocar.
    val conSombra = habilitado && !presionado && estilo != EstiloBoton.CONTORNO

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Medidas.control)
            .alpha(if (habilitado) 1f else 0.6f)
            .then(if (conSombra) Modifier.shadow(4.dp, esquina) else Modifier)
            .clip(esquina)
            .background(fondo)
            .then(if (borde != null) Modifier.border(borde, esquina) else Modifier)
            .clickable(
                enabled = habilitado,
                interactionSource = interacciones,
                indication = null      // el cambio de color YA es la respuesta al toque
            ) { onClick() }
            // 16 y no 32 de relleno lateral: en un boton de ancho completo
            // da igual, porque el texto va centrado y le sobra sitio, pero en
            // una pareja de botones a medio renglon esos 32 por lado se comen
            // 64 de los 150 que hay.
            .padding(horizontal = Margenes.lg),
        contentAlignment = Alignment.Center
    ) {
        Text(
            texto,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = contenido,
            textAlign = TextAlign.Center,
            // Un renglon y punto: el boton tiene ALTO FIJO, asi que un texto
            // que se parte en dos no crece la caja, se sale de ella y se corta
            // por la mitad de las letras.
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
