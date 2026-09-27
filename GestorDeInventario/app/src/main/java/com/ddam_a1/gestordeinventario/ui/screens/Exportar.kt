package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.components.BannerAviso
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.Iconos
import com.ddam_a1.gestordeinventario.ui.components.Pastilla
import com.ddam_a1.gestordeinventario.ui.theme.Anchos
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Medidas
import com.ddam_a1.gestordeinventario.ui.theme.Radios
import com.ddam_a1.gestordeinventario.ui.theme.tituloMedio

// ============================================================
//  PANTALLA 20 - EXPORTAR DATOS   (Figma 71:7375 / 71:7424 / 71:7521)
//  RF29
//
//  Un formato disponible y dos anunciados. Los dos que no estan se dibujan
//  apagados y con su etiqueta "Próximamente" en vez de esconderse: asi se ve
//  que la pantalla esta completa y que falta trabajo, no que algo se rompio.
//
//  La vista previa dice QUE archivos van a salir y CUANTOS renglones lleva
//  cada uno. Es la pregunta de quien va a exportar: si el numero es cero, no
//  hace falta apretar el boton.
// ============================================================

@Composable
fun PantallaExportar(
    archivos: List<ArchivoExportable>,
    rutaGuardada: String?,
    onExportar: () -> Unit,
    onAtras: () -> Unit
) {
    val total = archivos.sumOf { it.registros }

    Marco(barra = { BarraSuperior("Exportar datos", onAtras = onAtras) }) {
        item {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                Column(
                    Modifier.widthIn(max = Anchos.tarjetaFormulario),
                    verticalArrangement = Arrangement.spacedBy(Margenes.xl)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Margenes.md)) {
                        Titulo("Formato")
                        OpcionFormato(
                            titulo = "Archivos .csv",
                            detalle = "Un archivo por conjunto de datos",
                            elegido = true
                        )
                        OpcionFormato(
                            titulo = "Hoja de cálculo",
                            detalle = "Formato no disponible",
                            elegido = false,
                            proximamente = true
                        )
                        OpcionFormato(
                            titulo = "Copia de seguridad",
                            detalle = "Formato no disponible",
                            elegido = false,
                            proximamente = true
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(Margenes.md)) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(Radios.campo))
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .padding(Margenes.md),
                            verticalArrangement = Arrangement.spacedBy(Margenes.sm)
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Titulo("Vista previa")
                                Pastilla(
                                    ".csv",
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            archivos.forEach { archivo ->
                                FilaArchivo(archivo)
                            }
                        }

                        if (rutaGuardada != null) {
                            BannerAviso("Guardado en " + rutaGuardada)
                        }

                        BotonPrincipal(
                            "Exportar",
                            // Exportar cero renglones escribe archivos con solo
                            // los encabezados: no falla, pero tampoco sirve.
                            habilitado = total > 0,
                            onClick = onExportar
                        )
                    }
                }
            }
        }
    }
}

// ---------- PIEZAS ----------

@Composable
private fun Titulo(texto: String) {
    Text(
        texto,
        style = MaterialTheme.typography.tituloMedio,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.semantics { heading() }
    )
}

/**
 * Un formato (Figma 71:7391).
 *
 * `selectable` con Role.RadioButton y no un `clickable`: para un lector de
 * pantalla son tres opciones de las que se elige una, no tres botones.
 * Los que no estan disponibles no son seleccionables, asi que no responden.
 */
@Composable
private fun OpcionFormato(
    titulo: String,
    detalle: String,
    elegido: Boolean,
    proximamente: Boolean = false
) {
    val cs = MaterialTheme.colorScheme
    val forma = RoundedCornerShape(Radios.campo)

    Column(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .clip(forma)
            .background(if (elegido) cs.surfaceContainerLowest else cs.surfaceContainer)
            .border(
                if (elegido) Medidas.bordeGrueso else Medidas.borde,
                if (elegido) cs.tertiary else cs.outlineVariant,
                forma
            )
            .selectable(selected = elegido, enabled = !proximamente, role = Role.RadioButton) { }
            .padding(Margenes.md)
            .semantics(mergeDescendants = true) { },
        verticalArrangement = Arrangement.spacedBy(Margenes.xs)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Margenes.sm)
            ) {
                Icon(
                    if (elegido) Iconos.CirculoCheck else Iconos.Circulo,
                    contentDescription = null,   // el `selectable` ya lo anuncia
                    tint = if (elegido) cs.tertiary else cs.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    titulo,
                    style = MaterialTheme.typography.bodyLarge
                        .copy(fontWeight = FontWeight.Bold),
                    color = if (elegido) cs.onSurface else cs.onSurfaceVariant
                )
            }
            if (proximamente) {
                Pastilla("Próximamente")
            }
        }
        Text(
            detalle,
            style = MaterialTheme.typography.bodyLarge,
            color = cs.onSurfaceVariant
        )
    }
}

@Composable
private fun FilaArchivo(archivo: ArchivoExportable) {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp)
            .clip(RoundedCornerShape(Radios.fila))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(horizontal = Margenes.md, vertical = Margenes.sm)
            .semantics(mergeDescendants = true) { },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margenes.sm)
    ) {
        Icon(
            Iconos.Archivo,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(Medidas.iconoBusqueda)
        )
        Text(
            archivo.nombre + " · " + archivo.registros +
                (if (archivo.registros == 1) " registro" else " registros"),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
