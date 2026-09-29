package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.ddam_a1.gestordeinventario.ui.puede
import com.ddam_a1.gestordeinventario.data.negocio.Accion
import com.ddam_a1.gestordeinventario.modelClasses.RegistroLog
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.FilaLista
import com.ddam_a1.gestordeinventario.ui.components.FilaTarjeta
import com.ddam_a1.gestordeinventario.ui.components.Iconos
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Medidas
import com.ddam_a1.gestordeinventario.ui.theme.tituloMedio

// ============================================================
//  PANTALLA 19 - CONFIGURACION   (Figma 71:7228 / 71:7277 / 71:7326)
//
//  Grupos de ajustes, cada uno con su titulo y sus filas. Es la unica
//  pantalla que junta datos de los dos mundos: la bitacora viene del
//  inventario y el modo de equipo de la sesion. Los dos llegan ya resueltos.
//
//  El historial de cambios se ABRE Y CIERRA aqui mismo en vez de llevar a
//  otra pantalla. El Figma dibuja un chevron, pero una pantalla entera para
//  quince renglones de bitacora es un viaje de ida y vuelta por nada.
// ============================================================

@Composable
fun PantallaConfiguracion(
    nombreNegocio: String,
    modoEquipo: Boolean,
    totalUsuarios: Int,
    bitacora: List<RegistroLog>,
    /** Lo que dijo la última prueba de notificaciones. Null si nadie la ha pedido. */
    resultadoPrueba: String?,
    onProbarNotificaciones: () -> Unit,
    onExportar: () -> Unit,
    onUsuarios: () -> Unit,
    onAtras: () -> Unit,
    onSalir: () -> Unit
) {
    var verHistorial by remember { mutableStateOf(false) }

    // ============================================================
    //  EL PERMISO SE LEE AQUI, NO DENTRO DE LA LISTA
    //
    //  `puede()` es @Composable, y el bloque de una lista perezosa NO lo es:
    //  es un constructor de items que se ejecuta fuera de la composicion. Leer
    //  el permiso dentro da "@Composable invocations can only happen from the
    //  context of a @Composable function".
    //
    //  Se lee una vez en el cuerpo de la pantalla, que si es composable, y la
    //  lista usa el booleano. Tambien es mas correcto: asi el valor es el
    //  mismo para todos los items de una misma composicion.
    // ============================================================
    val puedeGestionarEquipo = puede(Accion.GESTIONAR_USUARIOS)
    val puedeExportar = puede(Accion.EXPORTAR)

    Marco(barra = {
        BarraSuperior(
            "Configuración",
            // Un dato guardado que no se ve en ningun lado es casi tan malo
            // como uno perdido. Aqui es donde se comprueba que si quedo escrito.
            subtitulo = nombreNegocio.ifBlank { null },
            onAtras = onAtras
        )
    }) {
        if (puedeGestionarEquipo) itemCentrado {
            Grupo("Negocio") {
                FilaTarjeta(
                    icono = Iconos.Equipo,
                    titulo = "Equipo y permisos",
                    subtitulo =
                        if (modoEquipo) totalUsuarios.toString() +
                            (if (totalUsuarios == 1) " usuario" else " usuarios") +
                            " · roles y accesos"
                        else "Modo individual",
                    onClick = onUsuarios
                ) { Chevron() }
            }
        }

        if (puedeExportar) itemCentrado {
            Grupo("Datos") {
                FilaTarjeta(
                    icono = Iconos.Descargar,
                    titulo = "Exportar datos",
                    subtitulo = "Descarga archivos .csv",
                    onClick = onExportar
                ) { Chevron() }
            }
        }

        itemCentrado {
            Grupo("Notificaciones") {
                // La revision de caducidades corre una vez al dia y con la app
                // cerrada, asi que no hay forma de comprobar que funciona sin
                // esperar. Esto la dispara al momento, con la MISMA regla que
                // usa la revision diaria.
                FilaTarjeta(
                    icono = Iconos.Campana,
                    titulo = "Probar notificación",
                    subtitulo = resultadoPrueba ?: "Revisa las caducidades ahora mismo",
                    onClick = onProbarNotificaciones
                )
            }
        }

        itemCentrado {
            Grupo("Historial de cambios") {
                FilaTarjeta(
                    icono = Iconos.Historial,
                    titulo = if (verHistorial) "Ocultar historial" else "Ver historial de cambios",
                    subtitulo = bitacora.size.toString() +
                        (if (bitacora.size == 1) " movimiento" else " movimientos"),
                    onClick = { verHistorial = !verHistorial }
                ) {
                    Icon(
                        if (verHistorial) Iconos.Desplegar else Iconos.Siguiente,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(Medidas.iconoChico)
                    )
                }
            }
        }

        // La bitacora son items DE VERDAD y no un bloque dentro del anterior:
        // asi la lista la desplaza sola por larga que sea, y solo compone los
        // renglones que se ven. Metida en un unico item, la pantalla entera
        // era un solo bloque gigante.
        if (verHistorial) {
            if (bitacora.isEmpty()) {
                itemCentrado {
                    Text(
                        "Todavía no hay movimientos registrados.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                itemsCentrados(bitacora.size) { i ->
                    val registro = bitacora[i]
                    FilaLista(
                        titulo = registro.descripcion,
                        subtitulo = registro.fecha + " · " + registro.tipo
                    )
                }
            }
        }

        // Cerrar sesion va en su propio grupo y al final, en rojo: es lo unico
        // de esta pantalla que te saca de la app.
        itemCentrado {
            Grupo("Sesión", MaterialTheme.colorScheme.error) {
                FilaTarjeta(
                    icono = Iconos.Salir,
                    titulo = "Cerrar sesión",
                    fondoIcono = MaterialTheme.colorScheme.errorContainer,
                    tintaIcono = MaterialTheme.colorScheme.onErrorContainer,
                    colorTitulo = MaterialTheme.colorScheme.error,
                    onClick = onSalir
                )
            }
        }
    }
}

// ---------- PIEZAS ----------

@Composable
private fun Grupo(
    titulo: String,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    contenido: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Margenes.sm)) {
        Text(
            titulo,
            style = MaterialTheme.typography.tituloMedio,
            color = color,
            modifier = Modifier.semantics { heading() }
        )
        contenido()
    }
}

@Composable
private fun Chevron() {
    Icon(
        Iconos.Siguiente,
        contentDescription = null,   // el `clickable` de la fila ya dice que se toca
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(Medidas.iconoChico)
    )
}
