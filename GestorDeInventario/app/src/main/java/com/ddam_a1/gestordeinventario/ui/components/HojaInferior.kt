package com.ddam_a1.gestordeinventario.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.theme.AnchoPantalla
import com.ddam_a1.gestordeinventario.ui.theme.Anchos
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Medidas
import com.ddam_a1.gestordeinventario.ui.theme.anchoPantallaDe

// ============================================================
//  HOJA INFERIOR   (Figma 43:840 / 43:1131 / 43:1391)
//
//  Un panel que sube desde abajo sobre la pantalla que ya estaba, para pedir
//  un dato corto sin perder de vista el contexto.
//
//  No se usa ModalBottomSheet de Material 3 porque es experimental: su firma
//  ha cambiado entre versiones y arrastraria @OptIn por todo el proyecto.
//  Esto son dos cajas en un Box, y hace exactamente lo mismo.
//
//  En tableta NO sube desde abajo: con 800 de alto, un panel pegado al borde
//  inferior deja el formulario lejos de la vista. Ahi se centra como dialogo.
// ============================================================

@Composable
fun HojaInferior(
    titulo: String,
    onCerrar: () -> Unit,
    /** Una linea de contexto bajo el titulo. Figma 71:7035. */
    subtitulo: String? = null,
    /** La X de la esquina. Se ensena cuando la hoja es un formulario largo y
     *  tocar fuera para cerrarla no seria evidente. */
    conCerrar: Boolean = false,
    contenido: @Composable ColumnScope.() -> Unit
) {
    // El boton de atras del telefono cierra la hoja, no la pantalla: mientras
    // hay algo encima, "atras" significa quitar eso.
    BackHandler(onBack = onCerrar)

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val comoDialogo = anchoPantallaDe(maxWidth) == AnchoPantalla.EXPANDIDA

        // El velo se toca para cerrar, pero sin onda de tinta: no es un boton,
        // es "fuera de la hoja".
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.7f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onCerrar
                )
        )

        val forma =
            if (comoDialogo) RoundedCornerShape(32.dp)
            else RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)

        Column(
            Modifier
                .align(if (comoDialogo) Alignment.Center else Alignment.BottomCenter)
                .then(
                    if (comoDialogo) Modifier.width(Anchos.tarjetaFormulario)
                    else Modifier.fillMaxWidth()
                )
                .clip(forma)
                .background(MaterialTheme.colorScheme.surface)
                .then(
                    if (comoDialogo)
                        Modifier.border(Medidas.borde, MaterialTheme.colorScheme.outlineVariant, forma)
                    else Modifier
                )
                // Sin esto, tocar dentro de la hoja llegaria al velo de atras
                // y la cerraria a media escritura.
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
                .navigationBarsPadding()
                .padding(horizontal = Margenes.pantalla)
                .padding(top = Margenes.md, bottom = Margenes.xxl),
            verticalArrangement = Arrangement.spacedBy(Margenes.xl)
        ) {
            // El tirador solo tiene sentido donde la hoja sube desde el borde.
            if (!comoDialogo) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Box(
                        Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                }
            }

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(Margenes.md)
            ) {
                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        titulo,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.semantics { heading() }
                    )
                    if (subtitulo != null) {
                        Text(
                            subtitulo,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (conCerrar) {
                    BotonIcono(Iconos.Cerrar, "Cerrar", onCerrar)
                }
            }

            contenido()
        }
    }
}
