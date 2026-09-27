package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Medidas
import com.ddam_a1.gestordeinventario.ui.theme.Radios

// ============================================================
//  CAMPO DE ENTRADA  (Figma 36:65)
//
//  Cinco variantes en el diseno —Default, Filled, Error, Password, Search—
//  que aqui son UN componente con banderas, porque solo cambian el borde, el
//  color del texto y si hay un icono. Cinco composables serian cinco sitios
//  donde olvidarse de cambiar algo.
//
//  Se deja OutlinedTextField y se arma con BasicTextField porque el diseno
//  pone la etiqueta ARRIBA y fija, no flotando dentro del campo. Pelearse con
//  el label flotante de Material sale mas caro que dibujar la caja.
//
//  Del Figma: alto 48, radio 16, fondo surfaceContainerHigh, borde 1 de
//  outlineVariant (2 de error cuando falla), separacion etiqueta-caja 8.
// ============================================================

@Composable
fun CampoTexto(
    valor: String,
    etiqueta: String,
    onCambio: (String) -> Unit,
    modifier: Modifier = Modifier,
    soloNumeros: Boolean = false,
    sufijo: String? = null,
    /** Oculta lo escrito y agrega el ojo para mostrarlo (Figma: Tipo=Password). */
    esContrasena: Boolean = false,
    marcador: String = "Escribe aquí...",
    /**
     * Si no es null: el borde se pone rojo y el mensaje aparece DEBAJO del
     * campo, con la etiqueta intacta. Asi lo compone la pantalla de login
     * (Figma 38:736): el usuario sigue viendo de que campo se trata mientras
     * lee que fallo.
     */
    error: String? = null
) {
    // El ojo es estado de la vista, no del negocio: nadie mas necesita saberlo,
    // asi que se queda aqui y no sube al ViewModel.
    var visible by remember { mutableStateOf(false) }
    val hayError = error != null

    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Margenes.sm)) {

        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(Medidas.control)
                .clip(RoundedCornerShape(Radios.campo))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .border(
                    width = if (hayError) Medidas.bordeGrueso else Medidas.borde,
                    color = if (hayError) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(Radios.campo)
                )
                .padding(horizontal = Margenes.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Margenes.sm)
        ) {
            Box(Modifier.weight(1f)) {
                if (valor.isEmpty()) {
                    Text(
                        marcador,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                BasicTextField(
                    value = valor,
                    onValueChange = { nuevo ->
                        if (!soloNumeros) onCambio(nuevo)
                        else if (nuevo.isEmpty() || nuevo.matches(Regex("^\\d*[.,]?\\d*$")))
                            onCambio(nuevo.replace(',', '.'))
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    // Contrasena: el teclado no guarda estas palabras en su
                    // diccionario ni las sugiere. Numero: teclado numerico.
                    keyboardOptions = KeyboardOptions(
                        keyboardType = when {
                            esContrasena -> KeyboardType.Password
                            soloNumeros -> KeyboardType.Decimal
                            else -> KeyboardType.Text
                        }
                    ),
                    visualTransformation =
                        if (esContrasena && !visible) PasswordVisualTransformation()
                        else VisualTransformation.None,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (sufijo != null) {
                Text(
                    sufijo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (esContrasena) {
                // IconButton y no un Icon con clickable: trae los 48dp de area
                // tocable y el aviso al lector de pantalla ya resueltos.
                IconButton(
                    onClick = { visible = !visible },
                    modifier = Modifier.size(Medidas.areaToque)
                ) {
                    Icon(
                        imageVector = if (visible) Iconos.OjoOculto else Iconos.Ojo,
                        contentDescription =
                            if (visible) "Ocultar contraseña" else "Mostrar contraseña",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(Medidas.icono)
                    )
                }
            }
        }

        if (error != null) {
            // El simbolo va en el texto, no como icono aparte: asi el lector de
            // pantalla lo lee junto al mensaje y no como un dibujo suelto.
            Text(
                "⚠ " + error,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = Margenes.xs)
            )
        }
    }
}
