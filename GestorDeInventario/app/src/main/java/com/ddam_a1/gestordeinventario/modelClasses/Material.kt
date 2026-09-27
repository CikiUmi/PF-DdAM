package com.ddam_a1.gestordeinventario.modelClasses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

// ---------- Modulo: Inventario de Materiales ----------

@Entity(tableName = "materiales")
data class Material(
    @PrimaryKey @ColumnInfo(name = "_id") val id: String,
    @ColumnInfo(name = "nombre") var nombre: String,
    @ColumnInfo(name = "unidad_medida") var unidadMedida: String,      // RF22
    @ColumnInfo(name = "costo_unitario") var costoUnitario: Double,    // RF2
    @ColumnInfo(name = "cantidad_disponible") var cantidadDisponible: Double,
    @ColumnInfo(name = "stock_minimo") var stockMinimo: Double = 0.0,  // RF19
    @ColumnInfo(name = "dias_aviso_caducidad") var diasAvisoCaducidad: Int = 0 // RF18
) {
    // ============================================================
    //  LAS FECHAS DE CADUCIDAD NO SON UNA COLUMNA
    //
    //  Antes esto era `val fechasCaducidad: MutableList<String>` dentro del
    //  data class. En SQL eso no existe: una columna guarda UN valor, no una
    //  lista. Se guarda al reves, en su propia tabla `lotes`, donde cada
    //  renglon apunta a SU material.
    //
    //  `@Ignore` le dice a Room "esta propiedad no es tuya, ni la guardes ni la
    //  leas". Y va en el CUERPO de la clase, no en el constructor: si estuviera
    //  arriba, Room no sabria como construir el objeto al leer de la base.
    //
    //  Quien la llena es el repositorio, despues de leer: trae los materiales
    //  por un lado, sus lotes por el otro, y arma el objeto completo. La
    //  interfaz nunca se entero de que ahora son dos tablas.
    // ============================================================
    @Ignore
    var fechasCaducidad: MutableList<String> = mutableListOf()
}
