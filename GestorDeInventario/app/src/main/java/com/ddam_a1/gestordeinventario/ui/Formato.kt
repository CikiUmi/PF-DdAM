package com.ddam_a1.gestordeinventario.ui

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun hoy(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

fun dinero(v: Double): String = "$" + String.format(Locale.getDefault(), "%,.2f", v)

fun cant(v: Double): String =
    if (v % 1.0 == 0.0) v.toInt().toString() else String.format(Locale.getDefault(), "%.2f", v)

/**
 * Un numero como texto para METER en un campo, no para mostrarlo.
 *
 * `toString()` de un Double escribe "3.0" y "0.0"; en un campo de captura eso
 * obliga al usuario a borrar el ".0" antes de escribir. Aqui se recorta cuando
 * no hay decimales, y no se usa separador de miles porque el campo no lo
 * aceptaria de vuelta.
 */
fun Double.aTexto(): String =
    if (this == this.toLong().toDouble()) this.toLong().toString() else this.toString()

/**
 * Dias que faltan para una fecha "aaaa-mm-dd". Negativo si ya paso.
 *
 * Devuelve null cuando el texto no es una fecha, en vez de 0: un 0 se leeria
 * como "caduca hoy" y pintaria de rojo un lote que en realidad tiene la fecha
 * mal escrita. Quien llama decide que hacer con el null.
 *
 * Es el gemelo del `diasEntre` privado del repositorio. Estan separados a
 * proposito: `data/` no importa de `ui/`.
 */
fun diasHasta(fecha: String, desde: String = hoy()): Int? {
    val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val a = runCatching { formato.parse(desde) }.getOrNull() ?: return null
    val b = runCatching { formato.parse(fecha) }.getOrNull() ?: return null
    return ((b.time - a.time) / (1000L * 60 * 60 * 24)).toInt()
}

/**
 * Agrupa por dia de la semana para la grafica de barras.
 *
 * Devuelve SIEMPRE los siete dias, aunque alguno no tenga ventas: una grafica
 * a la que le faltan columnas segun el dato se lee mal, porque el eje cambia
 * de forma cada vez.
 *
 * Las fechas son "aaaa-mm-dd" (por eso ordenar alfabeticamente ya es ordenar
 * por fecha); aqui se convierten para saber en que dia cayeron.
 */
fun <T> ventasPorDiaDeLaSemana(
    elementos: List<T>,
    /** Sin valor por omision a proposito: un default aqui se equivocaria en silencio. */
    fechaDe: (T) -> String,
    valorDe: (T) -> Double
): List<Pair<String, Double>> {
    val dias = listOf("Lun", "Mar", "Mie", "Jue", "Vie", "Sab", "Dom")
    val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val acumulado = DoubleArray(7)

    for (e in elementos) {
        val fecha = runCatching { formato.parse(fechaDe(e)) }.getOrNull() ?: continue
        val cal = Calendar.getInstance().apply { time = fecha }
        // Calendar.MONDAY es 2 y DOMINGO 1: se recorre para que la semana
        // empiece en lunes, como en el diseno.
        val indice = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
        acumulado[indice] += valorDe(e)
    }
    return dias.mapIndexed { i, d -> d to acumulado[i] }
}

// ============================================================
//  EL FILTRO DE LOS CAMPOS NUMERICOS
//
//  Vive aqui y no dentro de CampoTexto porque hay dos clases de campo que
//  aceptan numeros: el CampoTexto de siempre y el campito corto de la receta,
//  que va dentro de un renglon y no puede usarlo. Antes cada uno filtraba a su
//  manera y solo uno quitaba el cero de la izquierda; por eso en la receta
//  quedaba un "3230" con el cero sobrante al final.
//
//  Devuelve el valor ANTERIOR cuando la tecla no vale. Es a proposito: si se
//  dejara pasar la letra y luego alguien la quitara al guardar, el usuario
//  veria su letra escrita y desaparecer sin explicacion.
// ============================================================

fun filtrarNumero(actual: String, nuevo: String, soloEnteros: Boolean = false): String {
    // La coma del teclado numerico se acepta como punto: en un teclado en
    // espanol la coma es el separador decimal y nadie busca el punto.
    val n = nuevo.replace(',', '.')
    if (n.isEmpty()) return n

    val valido =
        if (soloEnteros) n.all { it.isDigit() }
        else n.all { it.isDigit() || it == '.' } && n.count { it == '.' } <= 1
    if (!valido) return actual

    // El cero guia se va en cuanto se escribe encima: los campos arrancan en
    // "0" para ensenar que ahi va un numero, no para que el numero empiece
    // por cero. "0222" -> "222", "007" -> "7", pero "0.5" se respeta entero
    // y "000" vuelve a ser "0" en vez de quedarse vacio.
    val sinCeros = n.trimStart('0')
    return when {
        sinCeros.isEmpty() -> "0"
        sinCeros.startsWith('.') -> "0" + sinCeros
        else -> sinCeros
    }
}
