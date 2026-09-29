package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.data.negocio.Accion
import com.ddam_a1.gestordeinventario.data.negocio.Permisos
import com.ddam_a1.gestordeinventario.modelClasses.Usuario
import com.ddam_a1.gestordeinventario.modelClasses.enums.Rol
import com.ddam_a1.gestordeinventario.ui.components.BannerAviso
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonFlotante
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.BotonSecundario
import com.ddam_a1.gestordeinventario.ui.components.CampoTexto
import com.ddam_a1.gestordeinventario.ui.components.ChipFiltro
import com.ddam_a1.gestordeinventario.ui.components.FilaChips
import com.ddam_a1.gestordeinventario.ui.components.DialogoSiNo
import com.ddam_a1.gestordeinventario.ui.components.EstadoVacio
import com.ddam_a1.gestordeinventario.ui.components.FilaPermiso
import com.ddam_a1.gestordeinventario.ui.components.HojaInferior
import com.ddam_a1.gestordeinventario.ui.components.Iconos
import com.ddam_a1.gestordeinventario.ui.components.SelectorPestanas
import com.ddam_a1.gestordeinventario.ui.theme.Anchos
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Medidas
import com.ddam_a1.gestordeinventario.ui.theme.Radios
import com.ddam_a1.gestordeinventario.ui.theme.tituloMedio

// ============================================================
//  PANTALLA 18 - EQUIPO   (Figma 71:6753 / 71:6822 / 71:6891)
//  RF25, RF26, RF27
//
//  Usuarios y permisos en DOS PESTANAS de la misma pantalla, no en dos
//  pantallas. Son la misma pregunta ("quien puede hacer que") y separarlas
//  obligaba a salir y volver a entrar para comprobar una cosa contra la otra.
//
//  Los permisos son de SOLO LECTURA, y se dice con todas sus letras. La app
//  los deriva del rol (`permisosDelRol`), no los guarda: unos interruptores
//  que se pudieran mover sin que nada cambiara serian una mentira.
//
//  Dar de alta y editar viven en una hoja (pantalla 18b), no en otra pantalla:
//  asi la lista se queda detras y se ve a quien se esta tocando.
// ============================================================

/** Lo que la hoja entrega al guardar. */
data class DatosUsuario(
    val id: String?,        // null = alta
    val nombre: String,
    val contrasena: String?, // null = dejar la que tenia
    val rol: Rol
)

@Composable
fun PantallaEquipo(
    usuarios: List<Usuario>,
    usuarioActual: Usuario?,
    esAdmin: Boolean,
    error: String?,
    onGuardarUsuario: (DatosUsuario) -> Unit,
    onEliminarUsuario: (String) -> Unit,
    onLimpiarError: () -> Unit,
    onAtras: () -> Unit
) {
    var pestana by remember { mutableStateOf(0) }
    // null = hoja cerrada. Un Usuario = editando ese. `SinUsuario` = alta.
    var editando by remember { mutableStateOf<Usuario?>(null) }
    var creando by remember { mutableStateOf(false) }

    val cerrarHoja = {
        editando = null
        creando = false
        onLimpiarError()
    }

    Box(Modifier.fillMaxSize()) {
        Marco(
            barra = { BarraSuperior("Equipo", onAtras = onAtras) },
            flotante = {
                // Solo el administrador da de alta (RF25), y solo desde la
                // pestana de usuarios: en la de permisos no hay nada que crear.
                if (esAdmin && pestana == 0) {
                    BotonFlotante(Iconos.Agregar, "Nuevo usuario", onClick = { creando = true })
                }
            }
        ) {
            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                    Column(
                        Modifier.widthIn(max = Anchos.tarjetaFormulario),
                        verticalArrangement = Arrangement.spacedBy(Margenes.lg)
                    ) {
                        SelectorPestanas(listOf("Usuarios", "Permisos"), pestana) { pestana = it }

                        if (pestana == 0) {
                            if (usuarios.isEmpty()) {
                                EstadoVacio(
                                    "Sin usuarios",
                                    "Los usuarios que registre aparecerán aquí"
                                )
                            } else {
                                usuarios.forEach { usuario ->
                                    FilaDeUsuario(
                                        usuario = usuario,
                                        esTu = usuario.id == usuarioActual?.id,
                                        // Sin permiso no hay chevron: un boton
                                        // que no hace nada se toca igual.
                                        onClick = if (esAdmin) ({ editando = usuario }) else null
                                    )
                                }
                            }
                        } else {
                            PanelPermisos()
                        }
                    }
                }
            }
        }

        if (creando || editando != null) {
            HojaUsuario(
                usuario = editando,
                error = error,
                puedeEliminar = editando != null && editando?.id != usuarioActual?.id,
                onGuardar = { datos ->
                    onGuardarUsuario(datos)
                    cerrarHoja()
                },
                onEliminar = { id ->
                    onEliminarUsuario(id)
                    cerrarHoja()
                },
                onCerrar = cerrarHoja
            )
        }
    }
}

// ---------- PESTANA 1: USUARIOS ----------

/**
 * Un usuario (Figma 71:6772): avatar con sus iniciales, nombre y rol.
 *
 * Las iniciales salen del nombre y no hay foto: un negocio chico no va a
 * subir retratos, y dos letras sobre un circulo de color distinguen igual de
 * bien en una lista de cinco personas.
 */
@Composable
private fun FilaDeUsuario(usuario: Usuario, esTu: Boolean, onClick: (() -> Unit)?) {
    Row(
        Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(Radios.campo))
            .clip(RoundedCornerShape(Radios.campo))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = Margenes.md, vertical = Margenes.sm)
            .semantics(mergeDescendants = true) { },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margenes.md)
    ) {
        Box(
            Modifier
                .size(Medidas.avatar)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                inicialesDe(usuario.nombreUsuario),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                usuario.nombreUsuario + (if (esTu) " (tú)" else ""),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                etiquetaRol(usuario.rol),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (onClick != null) {
            Icon(
                Iconos.Siguiente,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(Medidas.iconoChico)
            )
        }
    }
}

/** Dos letras a partir del nombre: "lucia mendez" -> "LM", "ana" -> "AN". */
internal fun inicialesDe(nombre: String): String {
    val palabras = nombre.trim().split(" ").filter { it.isNotBlank() }
    return when {
        palabras.isEmpty() -> "?"
        palabras.size == 1 -> palabras[0].take(2).uppercase()
        else -> (palabras[0].take(1) + palabras[1].take(1)).uppercase()
    }
}

// ---------- PESTANA 2: PERMISOS ----------



@Composable
private fun PanelPermisos() {
    var rol by remember { mutableStateOf(Rol.ENCARGADO) }

    Column(verticalArrangement = Arrangement.spacedBy(Margenes.md)) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Permisos por rol",
                style = MaterialTheme.typography.tituloMedio,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.semantics { heading() }
            )
            // No es un boton: es una etiqueta que avisa de que esto se mira,
            // no se cambia. Sin ella, los interruptores prometerian otra cosa.
            Text(
                "Vista previa",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.tertiary
            )
        }

        FilaChips {
            Rol.entries.forEach { r ->
                ChipFiltro(etiquetaRol(r), rol == r) { rol = r }
            }
        }

        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Radios.campo))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(Margenes.md),
            verticalArrangement = Arrangement.spacedBy(Margenes.xs)
        ) {
            // La lista y la regla salen de `Permisos`, que es lo que de
            // verdad aplica la app. Antes esto era una copia escrita a mano
            // que ya no coincidia: prometia al encargado todo menos exportar,
            // cuando la regla real le da tres acciones.
            Accion.entries.forEach { accion ->
                FilaPermiso(accion.etiqueta, Permisos.puede(rol, accion), soloLectura = true) { }
            }
        }

        Text(
            "El administrador siempre tiene todos los permisos. Estos valores " +
                "los decide el rol y no se editan uno a uno.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ---------- PANTALLA 18b: LA HOJA ----------

@Composable
private fun HojaUsuario(
    usuario: Usuario?,
    error: String?,
    puedeEliminar: Boolean,
    onGuardar: (DatosUsuario) -> Unit,
    onEliminar: (String) -> Unit,
    onCerrar: () -> Unit
) {
    val esNuevo = usuario == null
    var nombre by remember(usuario) { mutableStateOf(usuario?.nombreUsuario ?: "") }
    var clave by remember(usuario) { mutableStateOf("") }
    var rol by remember(usuario) { mutableStateOf(usuario?.rol ?: Rol.EMPLEADO) }
    var preguntarBorrado by remember { mutableStateOf(false) }

    // Al crear, la contrasena es obligatoria. Al editar, en blanco significa
    // "dejala como estaba": cambiar el rol no deberia obligar a reescribirla.
    val valido = nombre.isNotBlank() && (!esNuevo || clave.isNotBlank())

    HojaInferior(
        titulo = if (esNuevo) "Nuevo usuario" else "Editar usuario",
        onCerrar = onCerrar,
        subtitulo =
            if (esNuevo) "Defina sus datos y nivel de acceso."
            else "Actualice sus datos y nivel de acceso.",
        conCerrar = true
    ) {
        if (error != null) {
            BannerAviso(error)
        }

        CampoTexto(nombre, "Usuario", { nombre = it }, marcador = "Ej. lucia")
        CampoTexto(
            clave,
            if (esNuevo) "Contraseña" else "Contraseña nueva (opcional)",
            { clave = it },
            esContrasena = true,
            marcador = if (esNuevo) "" else "Déjela vacía para no cambiarla"
        )

        Column(verticalArrangement = Arrangement.spacedBy(Margenes.sm)) {
            Etiqueta("Rol")
            FilaChips {
                Rol.entries.forEach { r ->
                    ChipFiltro(etiquetaRol(r), rol == r) { rol = r }
                }
            }
        }

        if (puedeEliminar && usuario != null) {
            Text(
                "Eliminar usuario",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .clip(RoundedCornerShape(Radios.accion))
                    .clickable { preguntarBorrado = true }
                    .padding(horizontal = Margenes.md, vertical = Margenes.md)
            )
        }

        BotonSecundario("Cancelar", onClick = onCerrar)
        BotonPrincipal(
            if (esNuevo) "Crear usuario" else "Guardar cambios",
            habilitado = valido
        ) {
            onGuardar(
                DatosUsuario(
                    id = usuario?.id,
                    nombre = nombre.trim(),
                    contrasena = clave.ifBlank { null },
                    rol = rol
                )
            )
        }
    }

    if (preguntarBorrado && usuario != null) {
        DialogoSiNo(
            titulo = "¿Eliminar a " + usuario.nombreUsuario + "?",
            mensaje = "Perderá el acceso a la aplicación. Lo que haya registrado " +
                "se queda en la bitácora.",
            textoSi = "Eliminar",
            textoNo = "Cancelar",
            onSi = {
                preguntarBorrado = false
                onEliminar(usuario.id)
            },
            onNo = { preguntarBorrado = false },
            onCerrar = { preguntarBorrado = false },
            destructivo = true
        )
    }
}
