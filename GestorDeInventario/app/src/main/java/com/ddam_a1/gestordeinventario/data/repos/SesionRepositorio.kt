package com.ddam_a1.gestordeinventario.data.repos

import com.ddam_a1.gestordeinventario.modelClasses.Negocio
import com.ddam_a1.gestordeinventario.data.negocio.Accion
import com.ddam_a1.gestordeinventario.modelClasses.Usuario
import com.ddam_a1.gestordeinventario.modelClasses.enums.Rol
import kotlinx.coroutines.flow.Flow

interface SesionRepositorio {

    fun usuariosStream(): Flow<List<Usuario>>

    /** "Es el primer uso de la app" del diagrama de flujo: no hay ni un usuario. */
    suspend fun esPrimerUso(): Boolean

    /**
     * Si ya hay alguien con ese nombre.
     *
     * Vive en el repositorio y no en el ViewModel porque es una pregunta a los
     * datos. Cuando entre Room, esto se refuerza ademas con un indice UNIQUE en
     * la columna: la validacion de aqui da el mensaje bonito, y el indice
     * garantiza que no se cuele ni por una condicion de carrera.
     */
    suspend fun existeUsuario(nombreUsuario: String): Boolean

    /**
     * Crea al administrador Y deja escrito el negocio, en una sola operacion.
     *
     * Van juntos porque son el mismo momento: el alta inicial. Si se
     * partieran en dos llamadas, una podria salir bien y la otra no, y
     * quedaria un administrador sin negocio o al reves.
     */
    suspend fun crearUsuarioAdministrador(
        nombreUsuario: String,
        contrasena: String,
        nombreNegocio: String
    ): Usuario
    suspend fun crearUsuario(quienCrea: Usuario, nombreUsuario: String, contrasena: String, rol: Rol): Usuario?

    /**
     * RF26: cambia nombre, rol y (si se manda) contrasena de un usuario.
     *
     * `contrasena` en null significa "dejala como esta": es lo que pasa al
     * editar solo el rol, y evita tener que volver a teclearla.
     */
    suspend fun editarUsuario(
        quienEdita: Usuario,
        usuarioId: String,
        nombreUsuario: String,
        contrasena: String?,
        rol: Rol
    ): Boolean

    /**
     * RF27: borra un usuario.
     *
     * Devuelve false si se intenta borrar al ULTIMO administrador: un negocio
     * sin administrador queda sin nadie que pueda dar de alta a nadie, y eso
     * no tiene vuelta atras desde dentro de la app.
     */
    suspend fun eliminarUsuario(quienElimina: Usuario, usuarioId: String): Boolean

    /** Devuelve el usuario si la contrasena es correcta, o null si no. */
    suspend fun iniciarSesion(nombreUsuario: String, contrasena: String): Usuario?

    // ---------- NEGOCIO ----------

    /** Null mientras no se ha completado el alta inicial. */
    fun negocioStream(): Flow<Negocio?>

    suspend fun definirNombreNegocio(nombre: String)

    suspend fun elegirModo(equipo: Boolean)
    suspend fun esModoEquipo(): Boolean

    /**
     * La bitacora la comparten los dos mundos, asi que los dos repositorios la
     * exponen. No es duplicar: es que "quien hizo que y cuando" no pertenece ni
     * al inventario ni a la sesion, los atraviesa.
     */
    suspend fun registrarLog(fecha: String, tipo: String, descripcion: String)

    /** Tabla de permisos por rol. Calculo puro, no toca la base. */
    fun tienePermiso(usuario: Usuario, accion: Accion): Boolean
}