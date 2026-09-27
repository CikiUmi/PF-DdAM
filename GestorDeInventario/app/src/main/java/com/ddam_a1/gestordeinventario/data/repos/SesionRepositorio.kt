package com.ddam_a1.gestordeinventario.data.repos

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

    suspend fun crearUsuarioAdministrador(nombreUsuario: String, contrasena: String): Usuario
    suspend fun crearUsuario(quienCrea: Usuario, nombreUsuario: String, contrasena: String, rol: Rol): Usuario?

    /** Devuelve el usuario si la contrasena es correcta, o null si no. */
    suspend fun iniciarSesion(nombreUsuario: String, contrasena: String): Usuario?

    suspend fun elegirModo(equipo: Boolean)
    suspend fun esModoEquipo(): Boolean

    /**
     * La bitacora la comparten los dos mundos, asi que los dos repositorios la
     * exponen. No es duplicar: es que "quien hizo que y cuando" no pertenece ni
     * al inventario ni a la sesion, los atraviesa.
     */
    suspend fun registrarLog(fecha: String, tipo: String, descripcion: String)

    /** Tabla de permisos por rol. Calculo puro, no toca la base. */
    fun tienePermiso(usuario: Usuario, accion: String): Boolean
}