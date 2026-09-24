package com.ddam_a1.gestordeinventario.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ddam_a1.gestordeinventario.data.SesionRepositorio
import com.ddam_a1.gestordeinventario.modelClasses.Rol
import com.ddam_a1.gestordeinventario.modelClasses.Usuario
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// ============================================================
//  EL VIEWMODEL DE LA SESION
//
//  Login, alta de administrador, modo individual/equipo, usuarios y permisos.
//  Ninguna de esas pantallas toca materiales, productos ni ventas, por eso va
//  aparte del de inventario.
//
//  Aqui es donde termina de vivir `EstadoApp.usuario`.
// ============================================================

@HiltViewModel
class SesionViewModel @Inject constructor(
    private val repo: SesionRepositorio
) : ViewModel() {

    val usuarios: StateFlow<List<Usuario>> = repo.usuariosStream()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Quien esta usando la app ahora mismo.
     *
     * Es `null` mientras nadie ha entrado. Vive en el ViewModel y no en un
     * objeto global para que se muera junto con la app y no sobreviva a un
     * cierre de sesion por accidente.
     */
    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    /**
     * "Es el primer uso" derivado de la lista, no consultado a mano.
     *
     * Asi la pantalla de login se entera sola en cuanto se crea el primer
     * administrador, sin que nadie vuelva a preguntar.
     */
    val primerUso: StateFlow<Boolean> = repo.usuariosStream()
        .map { it.isEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    private val _modoEquipo = MutableStateFlow(false)
    val modoEquipo: StateFlow<Boolean> = _modoEquipo.asStateFlow()

    /** Devuelve el usuario si entro, o null si la contrasena esta mal. */
    suspend fun iniciarSesion(nombreUsuario: String, contrasena: String): Usuario? {
        if (nombreUsuario.isBlank() || contrasena.isBlank()) return null
        val usuario = repo.iniciarSesion(nombreUsuario.trim(), contrasena)
        if (usuario != null) _usuarioActual.value = usuario
        return usuario
    }

    /** El primer usuario de la app siempre es administrador. */
    suspend fun crearUsuarioAdministrador(nombreUsuario: String, contrasena: String): Usuario? {
        if (nombreUsuario.isBlank() || contrasena.isBlank()) return null
        if (repo.existeUsuario(nombreUsuario)) return null
        val admin = repo.crearUsuarioAdministrador(nombreUsuario.trim(), contrasena)
        _usuarioActual.value = admin
        return admin
    }

    /** Solo el administrador puede; devuelve null si quien pide no tiene permiso. */
    /**
     * Crea un usuario y devuelve que paso, para que la pantalla lo diga.
     *
     * Es `suspend` y no un `launch` escondido porque quien llama necesita el
     * resultado: no es lo mismo "no eres administrador" que "ese nombre ya
     * existe", y la pantalla tiene que poder distinguirlos.
     */
    suspend fun crearUsuario(
        nombreUsuario: String,
        contrasena: String,
        rol: Rol,
        fecha: String
    ): ResultadoAltaUsuario {
        val quienCrea = _usuarioActual.value ?: return ResultadoAltaUsuario.SIN_PERMISO
        if (nombreUsuario.isBlank() || contrasena.isBlank()) return ResultadoAltaUsuario.DATOS_INCOMPLETOS
        if (repo.existeUsuario(nombreUsuario)) return ResultadoAltaUsuario.NOMBRE_REPETIDO

        val creado = repo.crearUsuario(quienCrea, nombreUsuario.trim(), contrasena, rol)
            ?: return ResultadoAltaUsuario.SIN_PERMISO

        repo.registrarLog(fecha, "manual", "Alta de usuario " + creado.nombreUsuario)
        return ResultadoAltaUsuario.CREADO
    }

    fun elegirModo(equipo: Boolean) {
        _modoEquipo.value = equipo
        viewModelScope.launch { repo.elegirModo(equipo) }
    }

    fun tienePermiso(usuario: Usuario, accion: String): Boolean = repo.tienePermiso(usuario, accion)

    /** Permisos del rol elegido, sin necesidad de un usuario de verdad. */
    fun permisosDelRol(rol: Rol, accion: String): Boolean = when (rol) {
        Rol.ADMINISTRADOR -> true
        Rol.ENCARGADO -> accion in listOf("registrar_venta", "editar_inventario", "ver_estadisticas")
        Rol.EMPLEADO -> accion == "registrar_venta"
    }

    fun cerrarSesion() { _usuarioActual.value = null }
}

/** Que paso al intentar dar de alta un usuario. */
enum class ResultadoAltaUsuario { CREADO, NOMBRE_REPETIDO, SIN_PERMISO, DATOS_INCOMPLETOS }
