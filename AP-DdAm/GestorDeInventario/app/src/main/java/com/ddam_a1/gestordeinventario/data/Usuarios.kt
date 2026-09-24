package com.ddam_a1.gestordeinventario.datos

import com.ddam_a1.gestordeinventario.modelo.Rol
import com.ddam_a1.gestordeinventario.modelo.Usuario
import java.security.MessageDigest
import java.util.UUID

/**
 * Módulo: Usuarios
 * RF25, RF26, RF27, RF28
 */
object Usuarios {

    private val usuarios = mutableListOf<Usuario>()
    private var modoEquipo: Boolean = false

    // Corresponde a "¿Es el primer uso de la app?" en el diagrama de flujo
    fun esPrimerUso(): Boolean = usuarios.isEmpty()

    // Primer usuario que se crea siempre es administrador
    fun crearUsuarioAdministrador(nombreUsuario: String, contrasena: String): Usuario {
        val admin = Usuario(
            id = UUID.randomUUID().toString(),
            nombreUsuario = nombreUsuario,
            contrasenaHash = hashContrasena(contrasena),
            rol = Rol.ADMINISTRADOR
        )
        usuarios.add(admin)
        return admin
    }

    // Corresponde a "Elegir modo: individual o equipo" en el diagrama de flujo
    fun elegirModo(equipo: Boolean) {
        modoEquipo = equipo
    }

    fun esModoEquipo(): Boolean = modoEquipo

    // RF28: Autenticación de usuarios (inicio de sesión)
    fun iniciarSesion(nombreUsuario: String, contrasena: String): Usuario? {
        val hash = hashContrasena(contrasena)
        return usuarios.find { it.nombreUsuario == nombreUsuario && it.contrasenaHash == hash }
    }

    // RF26, RF27: Crear un nuevo usuario y asignarle un rol (solo el administrador puede hacerlo)
    fun crearUsuario(quienCrea: Usuario, nombreUsuario: String, contrasena: String, rol: Rol): Usuario? {
        if (quienCrea.rol != Rol.ADMINISTRADOR) return null // RF25: permisos por rol
        val usuario = Usuario(
            id = UUID.randomUUID().toString(),
            nombreUsuario = nombreUsuario,
            contrasenaHash = hashContrasena(contrasena),
            rol = rol
        )
        usuarios.add(usuario)
        return usuario
    }

    // RF25: Verificar si el rol de un usuario tiene permiso para realizar una acción
    fun tienePermiso(usuario: Usuario, accion: String): Boolean {
        return when (usuario.rol) {
            Rol.ADMINISTRADOR -> true
            Rol.ENCARGADO -> accion in listOf("registrar_venta", "editar_inventario", "ver_estadisticas")
            Rol.EMPLEADO -> accion in listOf("registrar_venta")
        }
    }

    private fun hashContrasena(contrasena: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(contrasena.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun obtenerTodos(): List<Usuario> = usuarios.toList()
}
