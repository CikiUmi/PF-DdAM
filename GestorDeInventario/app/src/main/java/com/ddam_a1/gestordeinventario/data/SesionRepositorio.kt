package com.ddam_a1.gestordeinventario.data

import com.ddam_a1.gestordeinventario.modelClasses.Rol
import com.ddam_a1.gestordeinventario.modelClasses.Usuario
import kotlinx.coroutines.flow.Flow

// ============================================================
//  EL REPOSITORIO DE LA SESION
//
//  Va aparte del de inventario porque no comparten NADA. Se midio pantalla por
//  pantalla: ninguna de las cinco de usuarios toca materiales, productos ni
//  ventas. Dos mundos que no se hablan son dos repositorios.
// ============================================================

interface SesionRepositorio {

    fun usuariosStream(): Flow<List<Usuario>>

    /** "Es el primer uso de la app" del diagrama de flujo: no hay ni un usuario. */
    suspend fun esPrimerUso(): Boolean

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
