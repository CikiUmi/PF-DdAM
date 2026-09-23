package com.ddam_a1.gestordeinventario.modelo

import com.ddam_a1.gestordeinventario.datos.InventarioMateriales
import java.text.SimpleDateFormat
import java.util.Locale

// tipo: "stock_bajo" o "caducidad"
data class Aviso(val materialId: String, val tipo: String, val mensaje: String)
