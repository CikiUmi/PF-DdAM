package com.ddam_a1.gestordeinventario.modelClasses

/**
 * De que avisa.
 *
 * Antes `tipo` era un String suelto ("stock_bajo" / "caducidad"): un typo no
 * compilaba mal, compilaba bien y el filtro dejaba de encontrar nada. Con enum,
 * el compilador revisa que el `when` cubra todos los casos.
 */
enum class TipoAviso { STOCK_BAJO_MATERIAL, STOCK_BAJO_PRODUCTO, CADUCIDAD }

/**
 * Un aviso del inventario.
 *
 * `referenciaId` y no `materialId` porque desde que los productos tambien
 * avisan, el id puede ser de un material o de un producto. El `tipo` dice cual
 * de los dos, y con eso la pantalla sabe a donde llevar al tocarlo.
 */
data class Aviso(
    val referenciaId: String,
    val tipo: TipoAviso,
    val mensaje: String
)
