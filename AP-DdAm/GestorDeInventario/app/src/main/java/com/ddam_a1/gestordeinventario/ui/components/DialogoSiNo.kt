package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
