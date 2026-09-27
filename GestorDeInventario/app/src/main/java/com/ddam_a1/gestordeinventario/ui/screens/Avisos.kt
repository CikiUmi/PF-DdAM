package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.modelClasses.Aviso
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonIcono
import com.ddam_a1.gestordeinventario.ui.components.BotonSecundario
import com.ddam_a1.gestordeinventario.ui.components.FilaTarjeta
import com.ddam_a1.gestordeinventario.ui.components.Iconos
import com.ddam_a1.gestordeinventario.ui.theme.Anchos
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Radios
import com.ddam_a1.gestordeinventario.ui.theme.coloresExtra
import com.ddam_a1.gestordeinventario.ui.theme.tituloMedio

// ============================================================
//  PANTALLA 17 - AVISOS   (Figma 71:6562 / 71:6615 / 71:6676)
//  RF18, RF19
//
//  Tres secciones: stock bajo, por caducar y leidos. Cada una con su cuenta
//  al lado del titulo, porque lo primero que se quiere saber es CUANTOS hay.
//
//  Un aviso no se borra: se marca como leido y baja a la tercera seccion. Si
//  el problema sigue (el material sigue bajo) el aviso sigue existiendo, solo
//  que ya lo viste. Cuando repones, desaparece solo.
//
//  Una sola columna centrada en las tres medidas: son avisos cortos que se
//  leen de arriba abajo, y a lo ancho de una tableta se leerian peor.
// ============================================================

@Composable
fun PantallaAvisos(
    stockBajo: List<Aviso>,
    porCaducar: List<Aviso>,
    leidos: List<Aviso>,
    onAviso: (Aviso) -> Unit,
    onMarcarLeido: (Aviso) -> Unit,
    onMarcarTodos: () -> Unit,
    onRestaurar: () -> Unit,
    onAtras: () -> Unit
) {
    val pendientes = stockBajo.size + porCaducar.size

    Marco(barra = {
        BarraSuperior("Avisos", onAtras = onAtras) {
            if (pendientes > 0) {
                BotonIcono(Iconos.Check, "Marcar todos como leídos", onMarcarTodos)
            }
        }
    }) {
        item {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                Column(
                    Modifier.widthIn(max = Anchos.tarjetaFormulario),
                    verticalArrangement = Arrangement.spacedBy(Margenes.lg)
                ) {
                    Seccion("Stock bajo", stockBajo.size) {
                        if (stockBajo.isEmpty()) {
                            SinAvisos("Ningún material está por acabarse.")
                        } else {
                            stockBajo.forEach { aviso ->
                                AvisoPendiente(aviso, Iconos.CajaMenos, onAviso, onMarcarLeido)
                            }
                        }
                    }

                    Seccion("Por caducar", porCaducar.size) {
                        if (porCaducar.isEmpty()) {
                            SinAvisos("Ningún lote está próximo a caducar.")
                        } else {
                            porCaducar.forEach { aviso ->
                                AvisoPendiente(
                                    aviso, Iconos.CalendarioReloj, onAviso, onMarcarLeido
                                )
                            }
                        }
                    }

                    Seccion("Leídos", leidos.size) {
                        if (leidos.isEmpty()) {
                            TodoEnOrden()
                        } else {
                            leidos.forEach { aviso -> AvisoLeido(aviso, onAviso) }
                            BotonSecundario("Restaurar avisos", onClick = onRestaurar)
                        }
                    }
                }
            }
        }
    }
}

// ---------- PIEZAS ----------

/**
 * Titulo de seccion con su cuenta al lado.
 *
 * La cuenta NO sale cuando es cero: un "0" en una pastilla rosa llama la
 * atencion igual que un numero de verdad, y ahi no hay nada que atender.
 */
@Composable
private fun Seccion(titulo: String, cuantos: Int, contenido: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Margenes.sm)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Margenes.sm)
        ) {
            Text(
                titulo,
                style = MaterialTheme.typography.tituloMedio,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.semantics { heading() }
            )
            if (cuantos > 0) {
                Box(
                    Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        cuantos.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
        contenido()
    }
}

@Composable
private fun AvisoPendiente(
    aviso: Aviso,
    icono: ImageVector,
    onAviso: (Aviso) -> Unit,
    onMarcarLeido: (Aviso) -> Unit
) {
    FilaTarjeta(
        icono = icono,
        titulo = aviso.titulo,
        subtitulo = aviso.mensaje,
        fondoIcono = MaterialTheme.colorScheme.tertiaryContainer,
        tintaIcono = MaterialTheme.colorScheme.onTertiaryContainer,
        onClick = { onAviso(aviso) }
    ) {
        BotonIcono(Iconos.Cerrar, "Marcar como leído", { onMarcarLeido(aviso) })
    }
}

@Composable
private fun AvisoLeido(aviso: Aviso, onAviso: (Aviso) -> Unit) {
    FilaTarjeta(
        icono = Iconos.Check,
        titulo = aviso.titulo,
        subtitulo = aviso.mensaje,
        fondoIcono = MaterialTheme.coloresExtra.correct.colorContainer,
        tintaIcono = MaterialTheme.coloresExtra.correct.onColorContainer,
        colorTitulo = MaterialTheme.colorScheme.onSurfaceVariant,
        onClick = { onAviso(aviso) }
    )
}

@Composable
private fun SinAvisos(texto: String) {
    Text(
        texto,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

/**
 * El estado vacio de "Leidos" (Figma 71:6604).
 *
 * La ilustracion del Figma son tres SVG superpuestos que sirve el plugin
 * desde la maquina de diseno. Aqui se compone con las piezas que ya hay: el
 * circulo de fondo, la campana encima y la palomita verde en la esquina.
 */
@Composable
private fun TodoEnOrden() {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radios.campo))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(Margenes.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Margenes.sm)
    ) {
        Box(Modifier.size(64.dp).clearAndSetSemantics { }) {
            Box(
                Modifier
                    .size(56.dp)
                    .align(Alignment.TopStart)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            )
            Icon(
                Iconos.Campana,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(28.dp).align(Alignment.Center)
            )
            Box(
                Modifier
                    .size(26.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(MaterialTheme.coloresExtra.correct.colorContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Iconos.Check,
                    contentDescription = null,
                    tint = MaterialTheme.coloresExtra.correct.onColorContainer,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Text(
            "Todo en orden",
            style = MaterialTheme.typography.tituloMedio,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            "Los avisos que marque como leídos aparecerán aquí.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
