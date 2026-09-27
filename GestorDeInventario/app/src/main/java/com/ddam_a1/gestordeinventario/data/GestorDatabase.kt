package com.ddam_a1.gestordeinventario.data

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

