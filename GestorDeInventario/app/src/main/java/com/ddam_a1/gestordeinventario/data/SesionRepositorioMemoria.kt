package com.ddam_a1.gestordeinventario.data

import com.ddam_a1.gestordeinventario.modelClasses.Rol
import com.ddam_a1.gestordeinventario.modelClasses.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/** La version en memoria. Envuelve el objeto Usuarios que ya tenias. */
@Singleton
class SesionRepositorioMemoria @Inject constructor() : SesionRepositorio {

    private val _usuarios = MutableStateFlow(Usuarios.obtenerTodos())
    private fun refrescar() { _usuarios.value = Usuarios.obtenerTodos() }

    override fun usuariosStream(): Flow<List<Usuario>> = _usuarios.asStateFlow()

    override suspend fun esPrimerUso(): Boolean = Usuarios.esPrimerUso()

    override suspend fun crearUsuarioAdministrador(nombreUsuario: String, contrasena: String): Usuario {
        val admin = Usuarios.crearUsuarioAdministrador(nombreUsuario, contrasena)
        refrescar()
        return admin
    }

    override suspend fun crearUsuario(quienCrea: Usuario, nombreUsuario: String, contrasena: String, rol: Rol): Usuario? {
        val usuario = Usuarios.crearUsuario(quienCrea, nombreUsuario, contrasena, rol)
        refrescar()
        return usuario
    }

    override suspend fun iniciarSesion(nombreUsuario: String, contrasena: String): Usuario? =
        Usuarios.iniciarSesion(nombreUsuario, contrasena)

    override suspend fun elegirModo(equipo: Boolean) = Usuarios.elegirModo(equipo)

    override suspend fun esModoEquipo(): Boolean = Usuarios.esModoEquipo()

    override suspend fun registrarLog(fecha: String, tipo: String, descripcion: String) {
        AlmacenamientoLocal.registrarLog(fecha, tipo, descripcion)
    }

    override fun tienePermiso(usuario: Usuario, accion: String): Boolean =
        Usuarios.tienePermiso(usuario, accion)
}
