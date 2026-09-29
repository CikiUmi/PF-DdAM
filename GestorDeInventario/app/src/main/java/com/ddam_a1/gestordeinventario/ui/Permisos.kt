package com.ddam_a1.gestordeinventario.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.ddam_a1.gestordeinventario.data.negocio.Accion

// ============================================================
//  LOS PERMISOS, AL ALCANCE DE CUALQUIER PANTALLA
//
//  Quien esta dentro y que puede hacer es un dato AMBIENTAL: no cambia en toda
//  la sesion y lo necesitan hojas muy hondas del arbol —un boton flotante, una
//  opcion de un menu, una fila que se desliza—.
//
//  Pasarlo como parametro obligaria a que cada pantalla lo reciba y lo reparta
//  a sus dos o tres funciones internas de medida, aunque no lo use: seis
//  pantallas, veinte firmas tocadas, y a la primera que se olvide de repartirlo
//  el boton reaparece.
//
//  Un CompositionLocal lo pone en el aire. Es el mismo mecanismo que ya usa
//  `LocalColoresExtra` para los colores que Material no trae.
//
//  `staticCompositionLocalOf` y no `compositionLocalOf` porque esto cambia una
//  vez por sesion: el estatico no lleva la cuenta de quien lo lee y es mas
//  barato, a cambio de recomponer todo el subarbol cuando cambia. Aqui eso
//  pasa al entrar y al salir, que es justo cuando la pantalla entera deberia
//  volver a pintarse.
//
//  POR OMISION PERMITE TODO. Sin sesion no se sabe el rol, y esconderlo todo
//  dejaria la aplicacion inservible; ademas asi una vista previa o una prueba
//  que no envuelva nada se ve completa. La restriccion se aplica cuando se
//  sabe a quien aplicarsela.
// ============================================================

fun interface ReglaDePermisos {
    fun puede(accion: Accion): Boolean
}

val LocalPermisos = staticCompositionLocalOf { ReglaDePermisos { true } }

/**
 * Lo que se escribe en las pantallas:
 *
 *     if (puede(Accion.EDITAR_INVENTARIO)) { BotonFlotante(...) }
 */
@Composable
@ReadOnlyComposable
fun puede(accion: Accion): Boolean = LocalPermisos.current.puede(accion)
