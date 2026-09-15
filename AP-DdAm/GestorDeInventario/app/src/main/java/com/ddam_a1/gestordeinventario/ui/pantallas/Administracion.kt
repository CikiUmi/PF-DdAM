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
import com.ddam_a1.gestordeinventario.almacenamiento.AlmacenamientoLocal
import com.ddam_a1.gestordeinventario.modelo.Rol
import com.ddam_a1.gestordeinventario.notificaciones.Notificaciones
import com.ddam_a1.gestordeinventario.ui.EstadoApp
import com.ddam_a1.gestordeinventario.ui.Navegador
import com.ddam_a1.gestordeinventario.ui.Ruta
import com.ddam_a1.gestordeinventario.ui.hoy
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
import com.ddam_a1.gestordeinventario.ui.theme.Acento
import com.ddam_a1.gestordeinventario.ui.theme.Exito
import com.ddam_a1.gestordeinventario.ui.theme.ExitoSuave
import com.ddam_a1.gestordeinventario.ui.theme.Peligro
import com.ddam_a1.gestordeinventario.ui.theme.PeligroSuave
import com.ddam_a1.gestordeinventario.usuarios.Usuarios
import com.ddam_a1.gestordeinventario.ventas.Ventas

private fun etiquetaRol(rol: Rol): String =
    rol.name.lowercase().replaceFirstChar { c -> c.uppercase() }

/** Pantalla 16 · Avisos (RF16, RF17). */
@Composable
fun PantallaAvisos(nav: Navegador) {
    EstadoApp.version
    val bajos = Notificaciones.revisarStockBajo()
    val caducan = Notificaciones.revisarCaducidadesProximas(hoy())

    Marco(barra = { BarraSuperior("Avisos", onAtras = { nav.volver() }) }) {
        item { EncabezadoSeccion("Stock bajo (" + bajos.size + ")") }
        if (bajos.isEmpty()) {
            item {
                Text("Todo el inventario está por encima de su umbral.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(bajos.size) { i ->
                val a = bajos[i]
                FilaLista(titulo = a.mensaje, colorPunto = Peligro,
                    onClick = { nav.ir(Ruta.DetalleMaterial(a.materialId)) })
            }
        }

        item { EncabezadoSeccion("Por caducar (" + caducan.size + ")") }
        if (caducan.isEmpty()) {
            item {
                Text("Ningún material caduca dentro de su antelación configurada.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(caducan.size) { i ->
                val a = caducan[i]
                FilaLista(titulo = a.mensaje, colorPunto = Acento,
                    onClick = { nav.ir(Ruta.DetalleMaterial(a.materialId)) })
            }
        }

        item {
            TarjetaSuave {
                Text("Los avisos se revisan al abrir la app. Ajusta los umbrales en cada material.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

/** Pantalla 17 · Usuarios (RF26, RF27). */
@Composable
fun PantallaUsuarios(nav: Navegador) {
    EstadoApp.version
    val lista = Usuarios.obtenerTodos()
    var nombre by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var rolElegido by remember { mutableStateOf(Rol.EMPLEADO) }
    val quien = EstadoApp.usuario
    val esAdmin = quien != null && quien.rol == Rol.ADMINISTRADOR

    Marco(barra = {
        BarraSuperior("Equipo", lista.size.toString() + " usuarios", onAtras = { nav.volver() }) {
            BotonIcono(Iconos.Candado, "Permisos", { nav.ir(Ruta.Permisos) })
        }
    }) {
        items(lista.size) { i ->
            val u = lista[i]
            FilaLista(
                titulo = u.nombreUsuario,
                subtitulo = if (quien != null && u.id == quien.id) "Sesión activa" else "Usuario del negocio",
                valor = etiquetaRol(u.rol)
            )
        }

        if (!esAdmin) {
            item {
                Text("Solo el administrador puede crear usuarios.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            item { EncabezadoSeccion("Nuevo usuario") }
            item { CampoTexto(nombre, "Usuario", { nombre = it }) }
            item { CampoTexto(clave, "Contraseña", { clave = it }) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Rol.values().forEach { r ->
                        ChipFiltro(etiquetaRol(r), rolElegido == r, { rolElegido = r })
                    }
                }
            }
            item {
                BotonPrincipal("Crear usuario", habilitado = nombre.isNotBlank() && clave.length >= 4) {
                    Usuarios.crearUsuario(quien, nombre.trim(), clave, rolElegido)
                    AlmacenamientoLocal.registrarLog(hoy(), "manual", "Alta de usuario " + nombre.trim())
                    nombre = ""
                    clave = ""
                    EstadoApp.datosCambiaron()
                }
            }
        }
    }
}

/** Pantalla 18 · Permisos por rol (RF25). */
@Composable
fun PantallaPermisos(nav: Navegador) {
    var rolElegido by remember { mutableStateOf(Rol.ENCARGADO) }
    val acciones = listOf(
        "registrar_venta" to "Registrar ventas",
        "editar_inventario" to "Crear y editar inventario",
        "ver_estadisticas" to "Ver rendimiento del negocio",
        "exportar" to "Exportar datos"
    )

    Marco(barra = { BarraSuperior("Permisos por rol", onAtras = { nav.volver() }) }) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Rol.values().forEach { r ->
                    ChipFiltro(etiquetaRol(r), rolElegido == r, { rolElegido = r })
                }
            }
        }
        item {
            TarjetaSuave {
                Text("El administrador siempre tiene todos los permisos. Aquí se muestra lo que el módulo de Usuarios permite hoy para cada rol.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        items(acciones.size) { i ->
            val accion = acciones[i].first
            val texto = acciones[i].second
            val permitido = when (rolElegido) {
                Rol.ADMINISTRADOR -> true
                Rol.ENCARGADO -> accion == "registrar_venta" || accion == "editar_inventario" || accion == "ver_estadisticas"
                Rol.EMPLEADO -> accion == "registrar_venta"
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(texto, style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                Insignia(
                    if (permitido) "Permitido" else "Bloqueado",
                    if (permitido) Exito else Peligro,
                    if (permitido) ExitoSuave else PeligroSuave
                )
            }
        }
    }
}

/** Pantalla 19 · Configuración. */
@Composable
fun PantallaConfiguracion(nav: Navegador, onSalir: () -> Unit) {
    EstadoApp.version
    val logs = AlmacenamientoLocal.consultarHistorial().reversed()

    Marco(barra = { BarraSuperior("Configuración", onAtras = { nav.volver() }) }) {
        item { EncabezadoSeccion("Negocio") }
        item {
            FilaLista(
                titulo = "Equipo y permisos",
                subtitulo = if (Usuarios.esModoEquipo())
                    "Modo equipo · " + Usuarios.obtenerTodos().size + " usuarios" else "Modo individual",
                onClick = { nav.ir(Ruta.Usuarios) }
            )
        }
        item { EncabezadoSeccion("Datos") }
        item {
            FilaLista(
                titulo = "Exportar datos",
                subtitulo = "SQL, .xlsx o .csv con contraseña",
                onClick = { nav.ir(Ruta.Exportar) }
            )
        }
        item { EncabezadoSeccion("Historial de cambios · " + logs.size) }
        if (logs.isEmpty()) {
            item {
                Text("Todavía no hay movimientos registrados.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(minOf(logs.size, 15)) { i ->
                val l = logs[i]
                FilaLista(titulo = l.descripcion, subtitulo = l.fecha + " · " + l.tipo)
            }
        }
        item {
            Spacer(Modifier.height(8.dp))
            BotonSecundario("Cerrar sesión", color = Peligro) {
                EstadoApp.cerrarSesion()
                onSalir()
            }
        }
        item {
            Text("Los datos se guardan solo en este dispositivo.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** Pantalla 20 · Exportar datos (RF29). */
@Composable
fun PantallaExportar(nav: Navegador) {
    var formato by remember { mutableStateOf(0) }
    var proteger by remember { mutableStateOf(true) }
    var clave by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("") }

    val formatos = listOf(
        "Hoja de cálculo (.xlsx)" to "Una pestaña por módulo.",
        "Texto separado por comas (.csv)" to "Un archivo por módulo, dentro de un .zip.",
        "Base de datos (.sql)" to "Volcado de tablas para importar en otro gestor."
    )

    Marco(barra = { BarraSuperior("Exportar datos", onAtras = { nav.volver() }) }) {
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
