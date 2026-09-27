package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Figma "Botón / Tipo=Destructive". Para lo que no se puede deshacer:
 * eliminar, cancelar una venta, cerrar sesion.
 */
@Composable
fun BotonDestructivo(
    texto: String,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    onClick: () -> Unit
) = BotonBase(texto, EstiloBoton.DESTRUCTIVO, modifier, habilitado, onClick)
