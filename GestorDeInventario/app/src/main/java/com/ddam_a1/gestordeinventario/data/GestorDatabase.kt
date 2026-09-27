package com.ddam_a1.gestordeinventario.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ddam_a1.gestordeinventario.modelClasses.IngredienteReceta
import com.ddam_a1.gestordeinventario.modelClasses.LoteMaterial
import com.ddam_a1.gestordeinventario.modelClasses.Material
import com.ddam_a1.gestordeinventario.modelClasses.Producto
import com.ddam_a1.gestordeinventario.modelClasses.RegistroLog
import com.ddam_a1.gestordeinventario.modelClasses.Usuario

// ============================================================
//  LA BASE
//
//  Abstracta porque Room escribe la implementacion: tu declaras que tablas hay
//  y que DAO quieres, y el generador pone el resto.
//
//  FALTAN VENTAS. Cuando agregues `Venta` e `ItemVendido` a esta lista, el
//  esquema cambia y hay que SUBIR `version` a 2 y escribir la migracion (o
//  desinstalar la app del emulador, que arranca limpio).
// ============================================================

@Database(
    entities = [
        Material::class,
        LoteMaterial::class,
        Producto::class,
        IngredienteReceta::class,
        Usuario::class,
        RegistroLog::class
        // TODO (juntas): Venta::class, ItemVendido::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GestorDatabase : RoomDatabase() {

    abstract fun materialDao(): MaterialDao
    abstract fun loteDao(): LoteDao
    abstract fun productoDao(): ProductoDao
    abstract fun recetaDao(): RecetaDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun bitacoraDao(): BitacoraDao
    // TODO (juntas): abstract fun ventaDao(): VentaDao

    companion object {

        // @Volatile: siempre en memoria principal, nunca en cache de un hilo.
        // Sin esto, dos corrutinas podrian ver valores distintos de INSTANCE y
        // crear dos bases.
        @Volatile
        private var INSTANCE: GestorDatabase? = null

        fun getDatabase(context: Context): GestorDatabase =
            INSTANCE ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    GestorDatabase::class.java,
                    "gestor_inventario_database"
                )
                    // Nota: las llaves foraneas de SQLite vienen apagadas por
                    // omision, pero Room las enciende solo. El ON DELETE CASCADE
                    // de lotes y receta funciona sin que haya que pedir nada.
                    //
                    // Mientras el esquema siga cambiando: si no hay camino de una
                    // version a otra, borra y empieza de cero. El dia que haya
                    // datos de verdad, esto se cambia por addMigrations(...).
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instancia
                instancia
            }
    }
}
