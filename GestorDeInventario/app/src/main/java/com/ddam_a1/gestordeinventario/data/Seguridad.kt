package com.ddam_a1.gestordeinventario.data

import java.security.MessageDigest

/**
 * El hash de una contrasena, en UN solo lugar.
 *
 * Estaba escrito dentro del objeto `Usuarios`. Al llegar Room hacia falta otra
 * vez en `SesionRepositorioLocal`, y tener dos copias de esto es peligroso: el
 * dia que cambies una y no la otra, nadie podria volver a entrar.
 *
 * Nota honesta: SHA-256 a secas NO es lo correcto para contrasenas reales (le
 * falta sal y ser lento a proposito, tipo bcrypt o Argon2). Para el alcance de
 * este proyecto esta bien, pero vale la pena saberlo.
 */
fun hashContrasena(contrasena: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(contrasena.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}
