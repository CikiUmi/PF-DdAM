package com.ddam_a1.gestordeinventario.modelo

// tipo: "stock_bajo" o "caducidad"
data class Aviso(val materialId: String, val tipo: String, val mensaje: String)
