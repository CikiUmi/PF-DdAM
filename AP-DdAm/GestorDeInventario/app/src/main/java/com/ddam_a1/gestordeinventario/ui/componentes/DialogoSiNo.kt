package com.ddam_a1.gestordeinventario.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun DialogoSiNo(
    titulo: String, mensaje: String, textoSi: String, textoNo: String,
    onSi: () -> Unit, onNo: () -> Unit, onCerrar: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onCerrar,
        title = { androidx.compose.material3.Text(titulo, style = MaterialTheme.typography.titleLarge) },
        text = { androidx.compose.material3.Text(mensaje, style = MaterialTheme.typography.bodyLarge) },
        confirmButton = {
            androidx.compose.material3.TextButton(onSi) {
                androidx.compose.material3.Text(textoSi, color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onNo) {
                androidx.compose.material3.Text(textoNo, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
