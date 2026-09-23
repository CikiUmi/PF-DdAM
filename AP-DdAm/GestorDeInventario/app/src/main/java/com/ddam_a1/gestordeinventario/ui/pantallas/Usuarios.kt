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
import com.ddam_a1.gestordeinventario.ui.navegacion.Navegador
import com.ddam_a1.gestordeinventario.ui.navegacion.Ruta
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
