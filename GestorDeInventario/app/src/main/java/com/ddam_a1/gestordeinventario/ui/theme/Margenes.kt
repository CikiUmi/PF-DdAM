package com.ddam_a1.gestordeinventario.ui.theme

import androidx.compose.ui.unit.dp

// ============================================================
//  LAS MEDIDAS, EN UN SOLO LUGAR
//
//  Los colores y la tipografia ya vienen por rol desde el tema. Las medidas
//  no: Compose no tiene "variables" de dp, asi que este objeto hace ese papel.
//  Los valores salen del Figma, no de la intuicion.
//
//  Si el diseno cambia una medida, se cambia AQUI y no en 166 sitios.
// ============================================================

object Margenes {
    /** Separaciones. El paso base es 4. */
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val xxl = 32.dp

    /** Margen lateral de una pantalla. */
    val pantalla = 16.dp
}

object Medidas {
    /** Alto de campos y botones. Coincide con el minimo tocable de Android. */
    val control = 48.dp

    /** Area minima que un dedo puede tocar sin fallar (Material: 48x48). */
    val areaToque = 48.dp

    val iconoChico = 18.dp
    val iconoBusqueda = 20.dp
    val avatar = 48.dp
    val miniatura = 64.dp

    /** Alto de una fila de ajuste o de permiso (Figma 36:213, 36:231). */
    val fila = 56.dp

    /** Alto de una fila con dos renglones: usuario, venta, material. */
    val filaDoble = 72.dp

    val fab = 56.dp
    val interruptorAncho = 44.dp
    val interruptorAlto = 24.dp

    /** Ancho del panel lateral en tableta (Figma 45:385). */
    val panelLateral = 256.dp
    val icono = 24.dp

    /** Alto de la barra de arriba (Figma 36:159). */
    val barraSuperior = 56.dp

    /** Alto de la barra de navegacion inferior (Figma 45:223). */
    val barraInferior = 64.dp

    val borde = 1.dp
    val bordeGrueso = 2.dp
}

object Radios {
    val campo = 16.dp
    val boton = 24.dp
    val busqueda = 28.dp
    val tarjeta = 18.dp
    val chip = 19.dp
    /** Tarjetas grandes de Inicio: acciones, atajos y el banner (Figma 41:729). */
    val accion = 20.dp
    val pestanas = 24.dp
    val pestanaActiva = 20.dp
    val dialogo = 24.dp
    val fila = 12.dp
    val miniatura = 12.dp
}
