package com.ddam_a1.gestordeinventario.modelo

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

// ---------- Módulo: Catálogo de productos ----------

data class IngredienteReceta(
    val materialId: String,
    val cantidadUsada: Double
)

data class Producto(
    val id: String,
    var nombre: String,
    var precioVenta: Double,       // RF7
    var esBajoPedido: Boolean,     // RF4
    val receta: MutableList<IngredienteReceta> = mutableListOf(), // RF6
    var stockDisponible: Int = 0,  // RF10
    var costoProduccion: Double = 0.0, // RF5
    var caducidadMasCercana: String? = null // RF10
)

// ---------- Módulo: Ventas ----------

data class ItemVendido(
    val productoId: String,
    val cantidad: Int,
    val precioUnitario: Double,          // congelado -> RF30
    val costoUnitarioProduccion: Double  // congelado -> RF30 (el costo tampoco es retroactivo)
)

data class Venta(
    val id: String,
    val fecha: String,
    val items: MutableList<ItemVendido>,
    val total: Double,
    var cancelada: Boolean = false // solo se puede cancelar durante el proceso de creación
)

// ---------- Módulo: Usuarios ----------

enum class Rol { ADMINISTRADOR, ENCARGADO, EMPLEADO } // RF25

data class Usuario(
    val id: String,
    var nombreUsuario: String,
    var contrasenaHash: String,
    var rol: Rol
)

// ---------- Módulo: Almacenamiento local ----------

data class RegistroLog(
    val id: String,
    val fecha: String,
    val tipo: String, // "venta" o "manual"
    val descripcion: String
)
