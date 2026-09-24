package com.ddam_a1.gestordeinventario.modelClasses



// ---------- Módulo: Inventario de Materiales ----------

data class Material(
    val id: String,
    var nombre: String,
    var unidadMedida: String,      // RF22
    var costoUnitario: Double,     // RF2
    var cantidadDisponible: Double,
    val fechasCaducidad: MutableList<String> = mutableListOf(), // RF3, RF9 (opcional, puede haber varias)
    var stockMinimo: Double = 0.0, // RF19
    var diasAvisoCaducidad: Int = 0 // RF18
)
