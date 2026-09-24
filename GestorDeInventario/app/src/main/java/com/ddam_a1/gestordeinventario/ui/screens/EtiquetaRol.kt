package com.ddam_a1.gestordeinventario.ui.screens

import com.ddam_a1.gestordeinventario.modelClasses.Rol

fun etiquetaRol(rol: Rol): String =
    rol.name.lowercase().replaceFirstChar { c -> c.uppercase() }
