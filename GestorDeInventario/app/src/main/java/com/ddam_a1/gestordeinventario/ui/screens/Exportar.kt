package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.CampoTexto
import com.ddam_a1.gestordeinventario.ui.components.EncabezadoSeccion
import com.ddam_a1.gestordeinventario.ui.components.OpcionSimple
import com.ddam_a1.gestordeinventario.ui.components.TarjetaSuave

/** Pantalla 20 - Exportar datos (RF29). */
@Composable
fun PantallaExportar(
    vistaPrevia: String,
    onExportar: (clave: String) -> Unit,
    onAtras: () -> Unit
) {
    var formato by remember { mutableStateOf(0) }
    var proteger by remember { mutableStateOf(true) }
    var clave by remember { mutableStateOf("") }

    val formatos = listOf(
        "Hoja de calculo (.xlsx)" to "Una pestana por modulo.",
        "Texto separado por comas (.csv)" to "Un archivo por modulo, dentro de un .zip.",
        "Base de datos (.sql)" to "Volcado de tablas para importar en otro gestor."
    )

    Marco(barra = { BarraSuperior("Exportar datos", onAtras = onAtras) }) {
        item {
            Text("Se exporta todo: materiales, productos, recetas, ventas y el historial de cambios.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item { EncabezadoSeccion("Formato") }
        items(formatos.size) { i ->
            OpcionSimple(formatos[i].first, formatos[i].second, formato == i, { formato = i })
        }
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Proteger el archivo con contrasena",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f))
                Switch(proteger, { proteger = it })
            }
        }
        if (proteger) {
            item { CampoTexto(clave, "Contrasena del archivo", { clave = it }) }
        }
        if (vistaPrevia.isNotBlank()) {
            item {
                TarjetaSuave {
                    Text("Vista previa del CSV", style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(6.dp))
                    Text(vistaPrevia, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
        item {
            Spacer(Modifier.height(8.dp))
            BotonPrincipal("Exportar", habilitado = !proteger || clave.length >= 4) {
                onExportar(if (proteger) clave else "")
            }
        }
        item {
            Text("Falta escribir el archivo al almacenamiento y cifrarlo (Zip4j). RF29 queda a medias hasta ese paso.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
