package com.ddam_a1.gestordeinventario.data.repos.local

import androidx.room.withTransaction
import com.ddam_a1.gestordeinventario.data.dao.BitacoraDao
import com.ddam_a1.gestordeinventario.data.dao.NegocioDao
import com.ddam_a1.gestordeinventario.data.database.GestorDatabase
import com.ddam_a1.gestordeinventario.data.dao.UsuarioDao
import com.ddam_a1.gestordeinventario.data.services.hashContrasena
import com.ddam_a1.gestordeinventario.data.repos.SesionRepositorio
import com.ddam_a1.gestordeinventario.modelClasses.Negocio
import com.ddam_a1.gestordeinventario.modelClasses.RegistroLog
import com.ddam_a1.gestordeinventario.data.negocio.Accion
import com.ddam_a1.gestordeinventario.data.negocio.Permisos
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
 * `modoEquipo` ya NO vive en memoria. Estaba como un `var` suelto, y eso
 * significaba que elegir "mi equipo" en el alta se olvidaba al cerrar la app:
 * al volver a abrirla estabas otra vez en modo individual. Ahora vive en la
 * tabla `negocio`, junto al nombre, porque son lo mismo: la configuracion que
 * se decide una vez al arrancar.
 */
@Singleton
class SesionRepositorioLocal @Inject constructor(
    private val usuarioDao: UsuarioDao,
    private val bitacoraDao: BitacoraDao,
    private val negocioDao: NegocioDao,
    private val db: GestorDatabase
) : SesionRepositorio {

    override fun usuariosStream(): Flow<List<Usuario>> = usuarioDao.todos()

    override suspend fun esPrimerUso(): Boolean = usuarioDao.cuantos() == 0

    override suspend fun existeUsuario(nombreUsuario: String): Boolean =
        usuarioDao.cuantosConNombre(nombreUsuario.trim()) > 0

    override suspend fun crearUsuarioAdministrador(
        nombreUsuario: String,
        contrasena: String,
        nombreNegocio: String
    ): Usuario {
        val admin = Usuario(
            id = UUID.randomUUID().toString(),
            nombreUsuario = nombreUsuario.trim(),
            contrasenaHash = hashContrasena(contrasena),
            rol = Rol.ADMINISTRADOR
        )
        // Las dos escrituras juntas: o hay administrador Y negocio, o no hay
        // nada. Sin esto se podria crear al admin y perder el nombre.
        db.withTransaction {
            usuarioDao.agregar(admin)
            negocioDao.guardar(
                (negocioDao.leer() ?: Negocio()).copy(nombre = nombreNegocio.trim())
            )
        }
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

    override suspend fun editarUsuario(
        quienEdita: Usuario,
        usuarioId: String,
        nombreUsuario: String,
        contrasena: String?,
        rol: Rol
    ): Boolean {
        if (quienEdita.rol != Rol.ADMINISTRADOR) return false      // RF25
        val actual = usuarioDao.leer(usuarioId) ?: return false

        // Degradar al ultimo administrador deja el negocio sin quien
        // administre. Se comprueba aqui y no en la pantalla porque es una
        // regla del negocio, no un detalle de como se ve.
        if (actual.rol == Rol.ADMINISTRADOR && rol != Rol.ADMINISTRADOR &&
            usuarioDao.cuantosAdministradores() <= 1
        ) return false

        usuarioDao.actualizar(
            actual.copy(
                nombreUsuario = nombreUsuario.trim(),
                // La contrasena en null se deja como estaba: editar el rol no
                // deberia obligar a volver a teclearla.
                contrasenaHash =
                    if (contrasena.isNullOrBlank()) actual.contrasenaHash
                    else hashContrasena(contrasena),
                rol = rol
            )
        )
        return true
    }

    override suspend fun eliminarUsuario(quienElimina: Usuario, usuarioId: String): Boolean {
        if (quienElimina.rol != Rol.ADMINISTRADOR) return false     // RF25
        // Nadie se borra a si mismo: te quedarias con la sesion abierta de un
        // usuario que ya no existe.
        if (quienElimina.id == usuarioId) return false

        val victima = usuarioDao.leer(usuarioId) ?: return false
        if (victima.rol == Rol.ADMINISTRADOR && usuarioDao.cuantosAdministradores() <= 1) {
            return false
        }
        return usuarioDao.borrar(usuarioId) > 0
    }

    /** La comparacion la hace SQLite: nunca se trae el hash a Kotlin. */
    override suspend fun iniciarSesion(nombreUsuario: String, contrasena: String): Usuario? =
        usuarioDao.autenticar(nombreUsuario.trim(), hashContrasena(contrasena))

    // ---------- NEGOCIO ----------

    override fun negocioStream(): Flow<Negocio?> = negocioDao.observar()

    override suspend fun definirNombreNegocio(nombre: String) {
        // `?: Negocio()` porque la fila puede no existir todavia. Con `copy`
        // se cambia un campo sin pisar el otro: escribir el nombre no debe
        // borrar el modo que ya estaba elegido.
        negocioDao.guardar((negocioDao.leer() ?: Negocio()).copy(nombre = nombre.trim()))
    }

    override suspend fun elegirModo(equipo: Boolean) {
        negocioDao.guardar((negocioDao.leer() ?: Negocio()).copy(modoEquipo = equipo))
    }

    override suspend fun esModoEquipo(): Boolean = negocioDao.leer()?.modoEquipo ?: false

    override suspend fun registrarLog(fecha: String, tipo: String, descripcion: String) {
        bitacoraDao.agregar(
            RegistroLog(UUID.randomUUID().toString(), fecha, tipo, descripcion)
        )
    }

    // La regla NO se escribe aqui: vive en `Permisos`, que es lo unico que
    // sabe quien puede que. Esta funcion solo la expone a traves de la interfaz.
    override fun tienePermiso(usuario: Usuario, accion: Accion): Boolean =
        Permisos.puede(usuario, accion)
}