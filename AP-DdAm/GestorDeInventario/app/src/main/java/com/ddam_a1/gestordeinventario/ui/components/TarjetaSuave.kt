package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/* ---------------------------------------------------------------- superficies */

@Composable
fun TarjetaSuave(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    relleno: Int = 16,
    contenido: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    val forma = RoundedCornerShape(20.dp)
    Card(
        modifier = if (onClick != null) modifier.clickable { onClick() } else modifier,
        shape = forma,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.padding(relleno.dp), content = contenido)
    }
}
