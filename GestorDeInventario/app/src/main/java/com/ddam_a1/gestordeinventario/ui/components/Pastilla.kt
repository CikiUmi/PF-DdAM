package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.coloresExtra

// ============================================================
//  PASTILLA  (Figma 48:1418, 48:1511, 48:1671, 43:811)
//
//  El dato corto que acompana a un titulo: "5 en stock", "Bajo pedido",
//  "Umbral: 10 kg". Radio 8, relleno 8x4, sin borde.
//
//  Se diferencia de ChipFiltro (se toca, cambia la lista) y de ChipEstado
//  (tres estados fijos del inventario, con punto de color). Esta solo informa
//  y acepta cualquier par de colores, porque el Figma la usa en verde, en rosa
//  y en gris segun lo que diga.
//
//  El icono es opcional y siempre DECORATIVO: el texto de al lado dice lo
//  mismo, asi que no lleva descripcion y el lector de pantalla no lo repite.
// ============================================================

@Composable
fun Pastilla(
    texto: String,
    fondo: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    contenido: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    icono: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .background(fondo)
            .padding(horizontal = Margenes.sm, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margenes.xs)
    ) {
        if (icono != null) {
            Icon(
                icono,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = contenido
            )
        }
        Text(
            texto,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = contenido
        )
    }
}

// ============================================================
//  LA PASTILLA DE ESTADO DEL INVENTARIO
//
//  Lo que le puede pasar a un material o a un producto y hay que atender hoy.
//  Vive aqui y no en cada pantalla porque sale en cuatro sitios —inventario,
//  catalogo, avisos y detalle— y ya se habia escrito tres veces con textos y
//  colores distintos. Un mismo problema tiene que verse igual en todas, o el
//  usuario aprende cuatro lenguajes en vez de uno.
//
//  El estado y su texto son los de `EstadoInventario`, en ChipEstado.kt. Esta
//  es la version con icono; aquella es la del punto de color.
//
//  POR CADUCAR va en `tertiaryContainer`, el mismo rosa de "Bajo pedido", y no
//  en rojo: todavia da tiempo. El rojo se guarda para lo que ya no tiene
//  arreglo —lo caducado— y para lo que deja de venderse —el stock bajo—, asi
//  que cuando aparece, significa algo.
// ============================================================

@Composable
fun PastillaEstado(estado: EstadoInventario, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val correcto = MaterialTheme.coloresExtra.correct
    when (estado) {
        EstadoInventario.DISPONIBLE -> Pastilla(
            estado.etiqueta, correcto.colorContainer, correcto.onColorContainer,
            Iconos.Check, modifier
        )
        EstadoInventario.STOCK_BAJO -> Pastilla(
            estado.etiqueta, cs.errorContainer, cs.onErrorContainer, Iconos.CajaMenos, modifier
        )
        EstadoInventario.POR_CADUCAR -> Pastilla(
            estado.etiqueta, cs.tertiaryContainer, cs.onTertiaryContainer,
            Iconos.CalendarioReloj, modifier
        )
        EstadoInventario.CADUCADO -> Pastilla(
            estado.etiqueta, cs.errorContainer, cs.onErrorContainer, Iconos.Alerta, modifier
        )
    }
}
