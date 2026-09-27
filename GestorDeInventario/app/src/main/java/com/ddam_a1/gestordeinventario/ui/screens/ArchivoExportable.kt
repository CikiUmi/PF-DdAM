package com.ddam_a1.gestordeinventario.ui.screens

/**
 * Un archivo que la exportacion va a escribir, ya masticado.
 *
 * La pantalla recibe esto y no las listas de datos: contar cuantos productos
 * hay es trabajo de quien los tiene, y la pantalla solo dice "productos.csv ·
 * 248 registros".
 */
data class ArchivoExportable(
    val nombre: String,
    val registros: Int
)
