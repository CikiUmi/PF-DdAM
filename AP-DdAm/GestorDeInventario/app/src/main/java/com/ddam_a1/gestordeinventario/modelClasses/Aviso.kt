package com.ddam_a1.gestordeinventario.modelClasses

// tipo: "stock_bajo" o "caducidad"
data class Aviso(val materialId: String, val tipo: String, val mensaje: String)
