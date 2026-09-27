package com.ddam_a1.gestordeinventario.data.repos.local

import com.ddam_a1.gestordeinventario.data.dao.BitacoraDao
import com.ddam_a1.gestordeinventario.data.dao.UsuarioDao
import com.ddam_a1.gestordeinventario.data.services.hashContrasena
import com.ddam_a1.gestordeinventario.data.repos.SesionRepositorio
import com.ddam_a1.gestordeinventario.modelClasses.RegistroLog
import com.ddam_a1.gestordeinventario.modelClasses.Usuario
import com.ddam_a1.gestordeinventario.modelClasses.enums.Rol
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * La sesion, contra Room.
 *
 * Fijate en lo corto que es comparado con la version en memoria: ya no hay que
 * refrescar ningun `MutableStateFlow` a mano. El Flow del DAO lo emite Room
 * sola cuando la tabla cambia — ese era el punto de toda la costura.
 *
 * `modoEquipo` sigue en memoria a proposito: es una preferencia de la app, no
 * un dato del negocio. Su casa de verdad seria DataStore, no una tabla.
 */
@Singleton
class SesionRepositorioLocal @Inject constructor(
    private val usuarioDao: UsuarioDao,
    private val bitacoraDao: BitacoraDao
) : SesionRepositorio {

    private var modoEquipo: Boolean = false

    override fun usuariosStream(): Flow<List<Usuario>> = usuarioDao.todos()

    override suspend fun esPrimerUso(): Boolean = usuarioDao.cuantos() == 0

    override suspend fun existeUsuario(nombreUsuario: String): Boolean =
        usuarioDao.cuantosConNombre(nombreUsuario.trim()) > 0

    override suspend fun crearUsuarioAdministrador(nombreUsuario: String, contrasena: String): Usuario {
        val admin = Usuario(
            id = UUID.randomUUID().toString(),
            nombreUsuario = nombreUsuario.trim(),
            contrasenaHash = hashContrasena(contrasena),
            rol = Rol.ADMINISTRADOR
        )
        usuarioDao.agregar(admin)
        return admin
    }

    override suspend fun crearUsuario(
        quienCrea: Usuario,
        nombreUsuario: String,
        contrasena: String,
        rol: Rol
    ): Usuario? {
        if (quienCrea.rol != Rol.ADMINISTRADOR) return null // RF25
        val usuario = Usuario(
            id = UUID.randomUUID().toString(),
            nombreUsuario = nombreUsuario.trim(),
            contrasenaHash = hashContrasena(contrasena),
            rol = rol
        )
        usuarioDao.agregar(usuario)
        return usuario
    }

    /** La comparacion la hace SQLite: nunca se trae el hash a Kotlin. */
    override suspend fun iniciarSesion(nombreUsuario: String, contrasena: String): Usuario? =
        usuarioDao.autenticar(nombreUsuario.trim(), hashContrasena(contrasena))

    override suspend fun elegirModo(equipo: Boolean) { modoEquipo = equipo }

    override suspend fun esModoEquipo(): Boolean = modoEquipo

    override suspend fun registrarLog(fecha: String, tipo: String, descripcion: String) {
        bitacoraDao.agregar(
            RegistroLog(UUID.randomUUID().toString(), fecha, tipo, descripcion)
        )
    }

    override fun tienePermiso(usuario: Usuario, accion: String): Boolean = when (usuario.rol) {
        Rol.ADMINISTRADOR -> true
        Rol.ENCARGADO -> accion in listOf("registrar_venta", "editar_inventario", "ver_estadisticas")
        Rol.EMPLEADO -> accion == "registrar_venta"
    }
}