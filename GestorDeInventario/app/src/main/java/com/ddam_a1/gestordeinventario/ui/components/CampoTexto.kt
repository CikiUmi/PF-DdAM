package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/* ---------------------------------------------------------------- entradas */

@Composable
fun CampoTexto(
    valor: String,
    etiqueta: String,
    onCambio: (String) -> Unit,
    modifier: Modifier = Modifier,
    soloNumeros: Boolean = false,
    sufijo: String? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = { nuevo ->
            if (!soloNumeros) onCambio(nuevo)
            else if (nuevo.isEmpty() || nuevo.matches(Regex("^\\d*[.,]?\\d*$"))) onCambio(nuevo.replace(',', '.'))
        },
        label = { Text(etiqueta, style = MaterialTheme.typography.bodyMedium) },
        suffix = if (sufijo != null) {
            { Text(sufijo, style = MaterialTheme.typography.bodyMedium) }
        } else null,
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier.fillMaxWidth()
    )
}
