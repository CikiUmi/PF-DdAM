package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.theme.Margenes

// ============================================================
//  BARRA DE PASOS  (Figma 48:1563 / 48:1607)
//
//  "Paso 1 de 2" y dos rayitas. Dar de alta un producto son DOS pantallas
//  (datos y receta) y sin esto la segunda parece otra cosa que aparecio sola.
//
//  El texto va primero y las rayas despues: las rayas estan marcadas como
//  decorativas porque "Paso 1 de 2" ya lo dice todo, y un lector de pantalla
//  leyendo dos rectangulos no ayuda a nadie.
// ============================================================

@Composable
fun BarraPasos(
    paso: Int,
    total: Int,
    modifier: Modifier = Modifier,
    /**
     * Texto arriba y rayas debajo, todo al centro (Figma 32:36 y 112:6163).
     *
     * En una pantalla ancha el indicador en linea deja el texto pegado a un
     * borde y las rayas al otro, a medio metro de distancia, y deja de leerse
     * como una sola cosa. Al centro vuelve a ser un solo objeto.
     */
    centrado: Boolean = false
) {
    val texto: @Composable () -> Unit = {
        Text(
            "Paso " + paso + " de " + total,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    // Las rayas van marcadas como decorativas: "Paso 1 de 2" ya lo dice todo,
    // y un lector de pantalla leyendo dos rectangulos no ayuda a nadie.
    val rayas: @Composable (Dp) -> Unit = { ancho ->
        Row(
            Modifier.width(ancho).clearAndSetSemantics { },
            horizontalArrangement = Arrangement.spacedBy(Margenes.xs)
        ) {
            repeat(total) { i ->
                Box(
                    Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (i < paso) MaterialTheme.colorScheme.tertiary
                            else MaterialTheme.colorScheme.outlineVariant
                        )
                )
            }
        }
    }

    if (centrado) {
        Column(
            modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Margenes.md)
        ) {
            texto()
            rayas(160.dp)
        }
    } else {
        Row(
            modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            texto()
            rayas(120.dp)
        }
    }
}
