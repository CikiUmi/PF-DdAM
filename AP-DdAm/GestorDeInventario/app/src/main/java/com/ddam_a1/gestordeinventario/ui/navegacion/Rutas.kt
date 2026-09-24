package com.ddam_a1.gestordeinventario.ui.navegacion

// ============================================================
//  LAS RUTAS
//
//  Constantes y no strings sueltos: si escribes "catalogo" en un lado y
//  "Catalogo" en otro, el compilador no dice nada y la app truena al navegar.
//  Con constantes, un typo no compila.
//
//  Antes esto era una `sealed class Ruta` con un `when` en App.kt. El NavHost
//  trabaja con rutas de texto, asi que cada destino es una constante y los que
//  llevan id traen ademas una funcion que arma la ruta ya con el valor puesto.
// ============================================================

const val RUTA_LOGIN = "login"
const val RUTA_CREAR_ADMIN = "crearAdmin"
const val RUTA_ELEGIR_MODO = "elegirModo"

const val RUTA_INICIO = "inicio"
const val RUTA_ESTADISTICAS = "estadisticas"
const val RUTA_INVENTARIO = "inventario"
const val RUTA_CATALOGO = "catalogo"
const val RUTA_NUEVA_VENTA = "nuevaVenta"
const val RUTA_HISTORIAL_VENTAS = "historialVentas"

const val RUTA_AVISOS = "avisos"
const val RUTA_USUARIOS = "usuarios"
const val RUTA_PERMISOS = "permisos"
const val RUTA_CONFIGURACION = "configuracion"
const val RUTA_EXPORTAR = "exportar"

/** El nombre del argumento. Se escribe una vez y se reusa en las tres formas. */
const val ARG_ID = "id"

// ---------- rutas con id OBLIGATORIO ----------
//
// "material/{id}" no se puede visitar sin id: ver un material sin decir cual
// no significa nada.

const val RUTA_DETALLE_MATERIAL = "material/{" + ARG_ID + "}"
fun rutaDetalleMaterial(id: String) = "material/" + id

const val RUTA_DETALLE_PRODUCTO = "producto/{" + ARG_ID + "}"
fun rutaDetalleProducto(id: String) = "producto/" + id

const val RUTA_RECETA = "receta/{" + ARG_ID + "}"
fun rutaReceta(productoId: String) = "receta/" + productoId

const val RUTA_PRODUCCION = "produccion/{" + ARG_ID + "}"
fun rutaProduccion(productoId: String) = "produccion/" + productoId


// ---------- rutas con id OPCIONAL ----------
//
// Los formularios sirven para dos cosas y el id decide cual:
//
//     materialForm            -> dar de alta uno nuevo   (id = null)
//     materialForm?id=abc-123 -> editar ese
//
// Es la misma idea que ya tenian `FormularioMaterial(id: String?)`: un id nulo
// significa "vengo a crear". La ruta y la pantalla hablan el mismo idioma.

const val RUTA_FORMULARIO_MATERIAL = "materialForm?" + ARG_ID + "={" + ARG_ID + "}"
fun rutaFormularioMaterial(id: String?) =
    if (id == null) "materialForm" else "materialForm?" + ARG_ID + "=" + id

const val RUTA_FORMULARIO_PRODUCTO = "productoForm?" + ARG_ID + "={" + ARG_ID + "}"
fun rutaFormularioProducto(id: String?) =
    if (id == null) "productoForm" else "productoForm?" + ARG_ID + "=" + id
