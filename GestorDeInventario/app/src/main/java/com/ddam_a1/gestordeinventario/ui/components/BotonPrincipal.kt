package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Figma "Botón / Tipo=Primary". La accion principal de la pantalla; solo una por pantalla. */
@Composable
fun BotonPrincipal(
    texto: String,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    onClick: () -> Unit
) = BotonBase(texto, EstiloBoton.RELLENO, modifier, habilitado, onClick)
