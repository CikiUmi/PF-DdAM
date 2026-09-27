package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.TarjetaSuave

/** Pantalla 20 - Exportar datos (RF29). */
@Composable
fun PantallaExportar(
    vistaPrevia: String,
    rutaGuardada: String?,
    onExportar: () -> Unit,
    onAtras: () -> Unit
) {
    Marco(barra = { BarraSuperior("Exportar datos", onAtras = onAtras) }) {
        item {
            Text(
                "Se exportan las ventas a un archivo .csv, separado por comas.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (vistaPrevia.isNotBlank()) {
            item {
                TarjetaSuave {
                    Text(
                        "Vista previa", style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        vistaPrevia, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        item {
            Spacer(Modifier.height(8.dp))
            BotonPrincipal("Exportar", onClick = onExportar)
        }
        if (rutaGuardada != null) {
            item {
                TarjetaSuave {
                    Text(
                        "Archivo guardado en", style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        rutaGuardada, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
