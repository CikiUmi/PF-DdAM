package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.ddam_a1.gestordeinventario.ui.theme.Margenes

// ============================================================
//  FILA DE TARJETAS PAREJAS
//
//  Un renglon de tarjetas que terminan TODAS al alto de la mas alta.
//
//  Por omision, un Row deja que cada hijo mida lo que necesita y los alinea
//  arriba: si una tarjeta trae dos renglones de texto y su vecina uno, quedan
//  desiguales y el grupo se ve roto.
//
//  `height(IntrinsicSize.Min)` le pregunta antes a los hijos cuanto necesitan,
//  se queda con el mayor, y fija el renglon a esa altura. Cada hijo se estira
//  con `fillMaxHeight()`, que es lo que hay que recordar al usarla:
//
//      FilaPareja {
//          TarjetaCifra(..., modifier = Modifier.weight(1f).fillMaxHeight())
//          TarjetaCifra(..., modifier = Modifier.weight(1f).fillMaxHeight())
//      }
//
//  OJO: los hijos tienen que saber medirse por intrinsecos. Textos, columnas
//  y cajas si; los campos de texto NO siempre, asi que las parejas de
//  CampoTexto se quedan alineadas arriba y esta fila no se usa con ellas.
// ============================================================

@Composable
fun FilaPareja(
    modifier: Modifier = Modifier,
    separacion: Dp = Margenes.md,
    contenido: @Composable RowScope.() -> Unit
) {
    Row(
        modifier.fillMaxWidth().height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(separacion),
        content = contenido
    )
}
