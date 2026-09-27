package com.ddam_a1.gestordeinventario.modelClasses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

// ============================================================
//  UN INGREDIENTE DE UNA RECETA (RF6)
//
//  Es una tabla PUENTE: une productos con materiales y guarda cuanto se usa de
//  cada uno. Un producto tiene muchos materiales, y un material aparece en
//  muchos productos: eso es muchos-a-muchos, y en SQL siempre se resuelve con
//  una tabla en medio.
//
//  LA LLAVE PRIMARIA ES COMPUESTA: (producto_id, material_id).
//
//  No hay un `_id` propio porque no hace falta: un producto no puede llevar
//  DOS veces el mismo material. Al hacerlos llave juntos, la base misma
//  garantiza esa regla — si intentas insertar el duplicado, falla. Es la misma
//  idea que el indice UNIQUE del nombre de usuario: la validacion de Kotlin da
//  el mensaje bonito, la base da la garantia.
//
//  DOS LLAVES FORANEAS, las dos en CASCADE: si borras el producto se va su
//  receta entera, y si borras un material desaparece de todas las recetas donde
//  estaba.
// ============================================================

@Entity(
    tableName = "receta",
    primaryKeys = ["producto_id", "material_id"],
    foreignKeys = [
        ForeignKey(
            entity = Producto::class,
            parentColumns = ["_id"],
            childColumns = ["producto_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Material::class,
            parentColumns = ["_id"],
            childColumns = ["material_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["producto_id"]), Index(value = ["material_id"])]
)
data class IngredienteReceta(
    @ColumnInfo(name = "producto_id") val productoId: String = "",
    @ColumnInfo(name = "material_id") val materialId: String,
    @ColumnInfo(name = "cantidad_usada") val cantidadUsada: Double
)
