package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.modelClasses.Rol
import com.ddam_a1.gestordeinventario.modelClasses.Usuario
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonIcono
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.CampoTexto
import com.ddam_a1.gestordeinventario.ui.components.ChipFiltro
import com.ddam_a1.gestordeinventario.ui.components.EncabezadoSeccion
import com.ddam_a1.gestordeinventario.ui.components.FilaLista
import com.ddam_a1.gestordeinventario.ui.components.Iconos

/**
 * Pantalla 17 - Usuarios (RF26, RF27).
 *
 * `esAdmin` llega resuelto. La pantalla no compara roles ni decide permisos;
 * solo dibuja el formulario o el aviso de que no puede.
 */
@Composable
fun PantallaUsuarios(
    usuarios: List<Usuario>,
    usuarioActual: Usuario?,
    esAdmin: Boolean,
    onCrearUsuario: (nombre: String, clave: String, rol: Rol) -> Unit,
    onPermisos: () -> Unit,
    onAtras: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var rolElegido by remember { mutableStateOf(Rol.EMPLEADO) }

    Marco(barra = {
        BarraSuperior("Equipo", usuarios.size.toString() + " usuarios", onAtras = onAtras) {
            BotonIcono(Iconos.Candado, "Permisos", { onPermisos() })
        }
    }) {
        items(usuarios.size) { i ->
            val usuario = usuarios[i]
            FilaLista(
                titulo = usuario.nombreUsuario,
                subtitulo = if (usuarioActual != null && usuario.id == usuarioActual.id)
                    "Sesion activa" else "Usuario del negocio",
                valor = etiquetaRol(usuario.rol)
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
            item { CampoTexto(clave, "Contrasena", { clave = it }) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Rol.entries.forEach { rol ->
                        ChipFiltro(etiquetaRol(rol), rolElegido == rol, { rolElegido = rol })
                    }
                }
            }
            item {
                BotonPrincipal("Crear usuario",
                    habilitado = nombre.isNotBlank() && clave.length >= 4) {
                    onCrearUsuario(nombre.trim(), clave, rolElegido)
                    nombre = ""
                    clave = ""
                }
            }
        }
    }
}
