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
    val mensaje: String,
    /** Lo puso el repositorio consultando la tabla de descartes. No se guarda aqui. */
    val leido: Boolean = false
) {
    /**
     * Identidad estable del aviso, para recordar cual ya viste.
     *
     * Un aviso NO se guarda en la base: se recalcula del estado real cada vez.
     * Lo unico que se guarda es esta clave cuando lo marcas como leido.
     *
     * Entra el `mensaje` a proposito: un mismo material puede tener varios
     * lotes por caducar, y todos comparten `referenciaId`. El mensaje trae la
     * fecha del lote, que es lo que los distingue. Efecto secundario: si
     * renombras el material, el aviso vuelve a aparecer sin leer — correcto,
     * porque el texto que ya habias visto ya no es el mismo.
     */
    val clave: String get() = tipo.name + "|" + referenciaId + "|" + mensaje
}
