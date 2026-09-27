package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Radios

/**
 * Figma "Diálogo de confirmación" (36:192).
 *
 * Se arma con `Dialog` y no con `AlertDialog` porque el diseno pone los dos
 * botones del MISMO tamano, uno al lado del otro y ocupando el ancho. El
 * AlertDialog de Material los alinea a la derecha y con su propio estilo de
 * texto; forzarlo cuesta mas que dibujar la tarjeta.
 *
 * Del Figma: radio 24, 24 de lado, 32 arriba y 24 abajo, titulo Lora 22
 * centrado, separacion 16.
 *
 * El destructivo se marca con `destructivo = true`: el boton de confirmar
 * pasa a rojo. Para borrar, el color tiene que avisar antes del toque.
 */
@Composable
fun DialogoSiNo(
    titulo: String,
    mensaje: String,
    textoSi: String,
    textoNo: String,
    onSi: () -> Unit,
    onNo: () -> Unit,
    onCerrar: () -> Unit,
    destructivo: Boolean = false
) {
    Dialog(onDismissRequest = onCerrar) {
        Surface(
            shape = RoundedCornerShape(Radios.dialogo),
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            shadowElevation = 8.dp
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = Margenes.xl, end = Margenes.xl,
                        top = Margenes.xxl, bottom = Margenes.xl
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Margenes.lg)
            ) {
                Text(
                    titulo,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Text(
                    mensaje,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Margenes.lg)
                ) {
                    // El "no" primero y de contorno: el boton peligroso no debe
                    // quedar bajo el pulgar por accidente.
                    Row(Modifier.weight(1f)) { BotonSecundario(textoNo, onClick = onNo) }
                    Row(Modifier.weight(1f)) {
                        if (destructivo) BotonDestructivo(textoSi, onClick = onSi)
                        else BotonPrincipal(textoSi, onClick = onSi)
                    }
                }
            }
        }
    }
}
