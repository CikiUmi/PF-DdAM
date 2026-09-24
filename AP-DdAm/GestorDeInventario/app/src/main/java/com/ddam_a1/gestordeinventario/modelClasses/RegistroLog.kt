package com.ddam_a1.gestordeinventario.modelClasses



// ---------- Módulo: Almacenamiento local ----------

data class RegistroLog(
    val id: String,
    val fecha: String,
    val tipo: String, // "venta" o "manual"
    val descripcion: String
)
