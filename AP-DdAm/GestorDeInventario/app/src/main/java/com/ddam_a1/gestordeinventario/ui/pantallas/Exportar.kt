package com.ddam_a1.gestordeinventario.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
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
import com.ddam_a1.gestordeinventario.datos.AlmacenamientoLocal
import com.ddam_a1.gestordeinventario.modelo.Rol
import com.ddam_a1.gestordeinventario.datos.Notificaciones
import com.ddam_a1.gestordeinventario.ui.EstadoApp
import com.ddam_a1.gestordeinventario.ui.componentes.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.componentes.BotonIcono
import com.ddam_a1.gestordeinventario.ui.componentes.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.componentes.BotonSecundario
import com.ddam_a1.gestordeinventario.ui.componentes.CampoTexto
import com.ddam_a1.gestordeinventario.ui.componentes.ChipFiltro
import com.ddam_a1.gestordeinventario.ui.componentes.EncabezadoSeccion
import com.ddam_a1.gestordeinventario.ui.componentes.FilaLista
import com.ddam_a1.gestordeinventario.ui.componentes.Iconos
import com.ddam_a1.gestordeinventario.ui.componentes.Insignia
import com.ddam_a1.gestordeinventario.ui.componentes.TarjetaSuave
import com.ddam_a1.gestordeinventario.ui.hoy
import com.ddam_a1.gestordeinventario.datos.Usuarios
import com.ddam_a1.gestordeinventario.datos.Ventas
import com.ddam_a1.gestordeinventario.ui.componentes.OpcionSimple

/** Pantalla 20 · Exportar datos (RF29). */
@Composable
fun PantallaExportar(onAtras: () -> Unit) {
    var formato by remember { mutableStateOf(0) }
    var proteger by remember { mutableStateOf(true) }
    var clave by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("") }

    val formatos = listOf(
        "Hoja de cálculo (.xlsx)" to "Una pestaña por módulo.",
        "Texto separado por comas (.csv)" to "Un archivo por módulo, dentro de un .zip.",
        "Base de datos (.sql)" to "Volcado de tablas para importar en otro gestor."
    )

    Marco(barra = { BarraSuperior("Exportar datos", onAtras = { onAtras() }) }) {
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
                Text("Proteger el archivo con contraseña",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f))
                Switch(proteger, { proteger = it })
            }
        }
        if (proteger) {
            item { CampoTexto(clave, "Contraseña del archivo", { clave = it }) }
        }
        if (resultado.isNotBlank()) {
            item {
                TarjetaSuave {
                    Text("Vista previa del CSV", style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(6.dp))
                    Text(resultado, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
        item {
            Spacer(Modifier.height(8.dp))
            BotonPrincipal("Exportar", habilitado = !proteger || clave.length >= 4) {
                val ventas = Ventas.obtenerHistorialVentas()
                val csv = AlmacenamientoLocal.exportarACSV(
                    "ventas.csv",
                    listOf("id", "fecha", "total", "cancelada"),
                    ventas.map { v -> listOf(v.id, v.fecha, v.total.toString(), v.cancelada.toString()) },
                    clave
                )
                resultado = if (csv.isBlank()) "Sin datos que exportar todavía." else csv.take(300)
                AlmacenamientoLocal.registrarLog(hoy(), "manual", "Exportación de datos generada")
                EstadoApp.datosCambiaron()
            }
        }
        item {
            Text("Falta escribir el archivo al almacenamiento y cifrarlo (Zip4j). RF29 queda a medias hasta ese paso.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
