package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.LiveRegionMode
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.ddam_a1.gestordeinventario.ui.filtrarNumero
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
    /** Decimales: solo digitos y un punto. Teclado numerico. */
    soloNumeros: Boolean = false,
    /** Enteros: solo digitos, ni punto ni coma. Para piezas y dias. */
    soloEnteros: Boolean = false,
    sufijo: String? = null,
    /** Oculta lo escrito y agrega el ojo para mostrarlo (Figma: Tipo=Password). */
    esContrasena: Boolean = false,
    marcador: String = "",
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

    // El foco se escucha con el MISMO interactionSource que recibe el campo.
    // Si se dejara que BasicTextField se hiciera uno propio, nadie de aqui
    // afuera podria enterarse de cuando esta seleccionado.
    val interacciones = remember { MutableInteractionSource() }
    val enfocado by interacciones.collectIsFocusedAsState()

    val numerico = soloNumeros || soloEnteros

    // El error manda sobre el foco: si el campo esta mal, tiene que verse mal
    // aunque el cursor este dentro.
    val colorBorde = when {
        hayError -> MaterialTheme.colorScheme.error
        enfocado -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outlineVariant
    }
    // Borde mas grueso al enfocar: la diferencia no se comunica solo por color.
    val grosorBorde = if (hayError || enfocado) Medidas.bordeGrueso else Medidas.borde

    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Margenes.sm)) {

        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = when {
                hayError -> MaterialTheme.colorScheme.error
                enfocado -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurface
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(Medidas.control)
                .clip(RoundedCornerShape(Radios.campo))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .border(
                    width = grosorBorde,
                    color = colorBorde,
                    shape = RoundedCornerShape(Radios.campo)
                )
                .padding(horizontal = Margenes.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Margenes.sm)
        ) {
            Box(Modifier.weight(1f)) {
                if (valor.isEmpty()) {
                    Text(
                        // Un campo numerico vacio ensena un 0 gris: asi nunca
                        // se ve en blanco, pero ese 0 no es texto que haya que
                        // borrar, es la pista de que ahi van numeros.
                        if (numerico && marcador.isEmpty()) "0" else marcador,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                BasicTextField(
                    value = valor,
                    // El filtro va aqui y no en la pantalla: si dejara pasar la
                    // letra y luego alguien la quitara al guardar, el usuario
                    // veria su letra escrita y desaparecer sin explicacion.
                    onValueChange = { nuevo ->
                        // El filtro SOLO en los campos numericos: en uno de
                        // texto normal se comeria todas las letras.
                        onCambio(
                            if (soloNumeros || soloEnteros)
                                filtrarNumero(valor, nuevo, soloEnteros)
                            else nuevo
                        )
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
                            soloEnteros -> KeyboardType.Number
                            soloNumeros -> KeyboardType.Decimal
                            else -> KeyboardType.Text
                        }
                    ),
                    visualTransformation =
                        if (esContrasena && !visible) PasswordVisualTransformation()
                        else VisualTransformation.None,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    interactionSource = interacciones,
                    // ============================================================
                    //  EL CERO GUIA SE QUITA AL ENTRAR, NO AL TECLEAR
                    //
                    //  Filtrarlo mientras se escribe no basta: si el cursor cae
                    //  ANTES del cero (pasa al enfocar un campo que dice "0"),
                    //  teclear 323 deja "3230" y el cero sobra al final.
                    //
                    //  Asi que el cero desaparece en cuanto se toca el campo.
                    //  Si se sale sin escribir nada vuelve, y el campo nunca
                    //  se queda sin valor.
                    // ============================================================
                    modifier = Modifier
                        .fillMaxWidth()
                        // ============================================================
                        //  LA ETIQUETA TIENE QUE VIAJAR CON EL CAMPO
                        //
                        //  "Nombre" es un Text hermano, arriba de la caja. Para
                        //  quien ve, la cercania basta; para TalkBack no existe
                        //  tal cosa: son dos nodos distintos y al pararse en el
                        //  campo solo anunciaba "cuadro de edicion". Un
                        //  formulario de seis campos sonaba a seis cajas iguales.
                        //
                        //  `contentDescription` aqui NO tapa lo escrito: en un
                        //  campo editable el lector dice la etiqueta y luego el
                        //  texto que contiene.
                        //
                        //  Y `error(...)` hace que se anuncie como invalido, no
                        //  solo con el borde rojo que no se oye.
                        .semantics {
                            contentDescription = etiqueta
                            if (error != null) error(error)
                        }
                        .onFocusChanged { foco ->
                            if (!numerico) return@onFocusChanged
                            if (foco.isFocused && valor == "0") onCambio("")
                            else if (!foco.isFocused && valor.isBlank()) onCambio("0")
                        }
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
                modifier = Modifier
                    .padding(start = Margenes.xs)
                    // Region viva: el mensaje aparece DESPUES de escribir, y sin
                    // esto TalkBack no lo dice hasta que el usuario navegue
                    // hasta el. "Polite" espera a que termine de hablar, en vez
                    // de interrumpir a media palabra.
                    .semantics { liveRegion = LiveRegionMode.Polite }
            )
        }
    }
}
