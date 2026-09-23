package com.ddam_a1.gestordeinventario.modelo



data class Usuario(
    val id: String,
    var nombreUsuario: String,
    var contrasenaHash: String,
    var rol: Rol
)
