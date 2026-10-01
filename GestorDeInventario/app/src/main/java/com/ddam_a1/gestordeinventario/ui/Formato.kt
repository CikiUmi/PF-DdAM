package com.ddam_a1.gestordeinventario.ui

import com.ddam_a1.gestordeinventario.modelClasses.enums.Periodo

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun hoy(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

/**
 * La hora del reloj, "HH:mm". Gemela de `hoy()`.
 *
 * Formato de 24 horas a proposito: es el que ordena alfabeticamente igual que
 * cronologicamente, que es de lo que vive el ORDER BY de VentaDao.
 */
fun ahora(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

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

// ============================================================
//  IMPORTES CORTOS
//
//  "$1,571.09" son once caracteres. Al centro de una dona de 110 no caben, y
//  el texto se sale del anillo y se encima con el arco.
//
//  Aqui se pierde precision A PROPOSITO, y solo donde el hueco manda: la cifra
//  exacta sigue estando al lado, en la leyenda.
//
//  SE TRUNCA, NO SE REDONDEA.
//
//  Son dinero. $39,996 redondeado da "$40k", que son 4 pesos de distancia y es
//  mas exacto que "$39.9k"; pero le dice al usuario que movio CUARENTA MIL
//  cuando no los movio, y en una cifra de dinero decir de mas es peor error
//  que decir de menos. Truncando, el numero corto nunca pasa del real: es
//  siempre "por lo menos esto".
//
//  Se trunca a la precision que se va a ENSENAR, no al entero: en la rama de
//  los miles con un decimal, 1,571.09 tiene que quedar "1.5k" y no "1.0k".
// ============================================================

fun dineroCorto(v: Double): String {
    val signo = if (v < 0) "-" else ""
    val abs = kotlin.math.abs(v)
    return when {
        abs >= 1_000_000 ->
            signo + "$" + String.format(Locale.getDefault(), "%.1fM", truncar(abs / 1_000_000, 1))
        abs >= 10_000 ->
            signo + "$" + String.format(Locale.getDefault(), "%.0fk", truncar(abs / 1_000, 0))
        abs >= 1_000 ->
            signo + "$" + String.format(Locale.getDefault(), "%.1fk", truncar(abs / 1_000, 1))
        else ->
            signo + "$" + String.format(Locale.getDefault(), "%.0f", truncar(abs, 0))
    }
}

/**
 * Corta los decimales de sobra en vez de redondearlos.
 *
 * `floor` y no `toInt()` ni `trunc`: el valor que llega aqui ya es positivo
 * —el signo se saco antes—, asi que los tres hacen lo mismo, pero `floor`
 * dice en una palabra que va hacia abajo y no depende de que el signo se
 * siga sacando arriba.
 *
 * El `+ 1e-9` es por el punto flotante: 0.7 guardado en Double puede ser
 * 0.6999999999999, y sin la holgura un "0.7" exacto se truncaria a "0.6".
 * Nueve ceros estan muy por debajo de cualquier centavo y muy por encima del
 * error de la division.
 */
private fun truncar(valor: Double, decimales: Int): Double {
    val factor = Math.pow(10.0, decimales.toDouble())
    return kotlin.math.floor(valor * factor + 1e-9) / factor
}

// ============================================================
//  LAS BARRAS, SEGUN EL PERIODO QUE SE ESTE MIRANDO
//
//  El eje tiene que medir lo mismo que el filtro. Siete dias de la semana
//  puestos bajo el chip "Hoy" no dicen nada: seis de esas columnas son de
//  dias que el usuario no esta mirando, y la unica que importa queda sola.
//  Bajo "Mes" es al reves: cuatro semanas amontonadas en siete columnas
//  suman lunes de semanas distintas como si fueran el mismo dia.
//
//    DIARIO   seis tramos de cuatro horas. Veinticuatro columnas no caben en
//             un telefono, y a nadie le importa la diferencia entre las 3 y
//             las 4 de la manana.
//    SEMANAL  los siete dias, de lunes a domingo.
//    MENSUAL  las cinco semanas que puede tener un mes.
//
//  En los tres casos se devuelven TODAS las columnas aunque esten en cero: una
//  grafica que cambia de forma segun el dato se lee mal, porque el eje deja de
//  ser una referencia fija.
// ============================================================

/** El titulo de la grafica, que tambien tiene que decir en que unidad va. */
fun tituloDeVentas(periodo: Periodo): String = when (periodo) {
    Periodo.DIARIO -> "Ventas por hora"
    Periodo.SEMANAL -> "Ventas por día"
    Periodo.MENSUAL -> "Ventas por semana"
}

fun <T> ventasPorPeriodo(
    periodo: Periodo,
    elementos: List<T>,
    /** Sin valor por omision a proposito: un default aqui se equivocaria en silencio. */
    fechaDe: (T) -> String,
    /** "HH:mm". Vacia en las ventas viejas, que caen en el primer tramo. */
    horaDe: (T) -> String,
    valorDe: (T) -> Double
): List<Pair<String, Double>> = when (periodo) {
    Periodo.DIARIO -> porTramoDeHoras(elementos, horaDe, valorDe)
    Periodo.SEMANAL -> ventasPorDiaDeLaSemana(elementos, fechaDe, valorDe)
    Periodo.MENSUAL -> porSemanaDelMes(elementos, fechaDe, valorDe)
}

private fun <T> porTramoDeHoras(
    elementos: List<T>,
    horaDe: (T) -> String,
    valorDe: (T) -> Double
): List<Pair<String, Double>> {
    val etiquetas = listOf("0-4", "4-8", "8-12", "12-16", "16-20", "20-24")
    val acumulado = DoubleArray(6)
    for (e in elementos) {
        // "14:35" -> 14. Una venta sin hora (las de antes de que se guardara)
        // cae en el primer tramo; es preferible a descartarla del total.
        val hora = horaDe(e).substringBefore(":").toIntOrNull() ?: 0
        val tramo = (hora / 4).coerceIn(0, 5)
        acumulado[tramo] += valorDe(e)
    }
    return etiquetas.mapIndexed { i, t -> t to acumulado[i] }
}

private fun <T> porSemanaDelMes(
    elementos: List<T>,
    fechaDe: (T) -> String,
    valorDe: (T) -> Double
): List<Pair<String, Double>> {
    val etiquetas = listOf("S1", "S2", "S3", "S4", "S5")
    val acumulado = DoubleArray(5)
    for (e in elementos) {
        // El dia del mes sale del texto "aaaa-mm-dd" sin parsear la fecha:
        // los dias 1 a 7 son la semana 1, el 8 al 14 la 2, y asi.
        val dia = fechaDe(e).substringAfterLast("-").toIntOrNull() ?: continue
        val semana = ((dia - 1) / 7).coerceIn(0, 4)
        acumulado[semana] += valorDe(e)
    }
    return etiquetas.mapIndexed { i, t -> t to acumulado[i] }
}

/**
 * Agrupa por dia de la semana para la grafica de barras.
 *
 * Devuelve SIEMPRE los siete dias, aunque alguno no tenga ventas.
 *
 * Las fechas son "aaaa-mm-dd" (por eso ordenar alfabeticamente ya es ordenar
 * por fecha); aqui se convierten para saber en que dia cayeron.
 */
fun <T> ventasPorDiaDeLaSemana(
    elementos: List<T>,
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

/**
 * Un folio legible a partir del id de una venta.
 *
 * Las ventas se identifican con un UUID, que es lo correcto para la base
 * (nunca choca, no hace falta un contador) pero imposible de leer o de dictar
 * por telefono. Aqui se recortan los ultimos seis caracteres y se ponen en
 * mayusculas: "VTA-3F91C2".
 *
 * NO es un consecutivo. El Figma ensena "Venta #047", que si lo parece; para
 * tener eso de verdad haria falta una columna numero_de_venta en la tabla y
 * decidir que pasa al borrar una. Mientras tanto, esto identifica sin mentir.
 */
fun folioDe(id: String): String {
    val limpio = id.filter { it.isLetterOrDigit() }
    val cola = if (limpio.length <= 6) limpio else limpio.takeLast(6)
    return "VTA-" + cola.uppercase()
}

/**
 * "2026-09-27 14:32", o solo la fecha si la venta es de antes de que se
 * guardara la hora (la migracion 5->6 las dejo con la hora vacia).
 */
fun fechaYHora(fecha: String, hora: String): String =
    if (hora.isBlank()) fecha else fecha + " " + hora
