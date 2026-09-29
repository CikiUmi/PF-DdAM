package com.ddam_a1.gestordeinventario.data.negocio

import com.ddam_a1.gestordeinventario.modelClasses.Usuario
import com.ddam_a1.gestordeinventario.modelClasses.enums.Rol

// ============================================================
//  QUIEN PUEDE HACER QUE
//  RF25, RF26
//
//  LA REGLA VIVE AQUI Y EN NINGUN OTRO SITIO. Estaba escrita tres veces —el
//  repositorio, el ViewModel y la tabla que se le ensena al usuario en
//  Equipo— y las tres no decian lo mismo: la tabla prometia al encargado todo
//  menos exportar, mientras la regla de verdad le daba tres acciones. La
//  pantalla que documenta los permisos estaba mintiendo.
//
//  Las acciones son un `enum` y no textos sueltos: con textos, un
//  "editar_inventerio" mal escrito no falla al compilar, devuelve `false` en
//  silencio y esconde un boton para siempre sin que nadie entienda por que.
//
//  El rol manda y no se edita permiso por permiso. Es a proposito: tres roles
//  con nombre se explican en una frase ("el encargado no exporta"), y una
//  matriz de quince interruptores no se explica nunca.
// ============================================================

enum class Accion(val etiqueta: String) {
    REGISTRAR_VENTA("Registrar ventas"),
    EDITAR_INVENTARIO("Crear y editar inventario"),
    VER_ESTADISTICAS("Ver rendimiento del negocio"),
    EXPORTAR("Exportar datos"),
    GESTIONAR_USUARIOS("Administrar el equipo")
}

object Permisos {

    fun puede(rol: Rol, accion: Accion): Boolean = when (rol) {
        Rol.ADMINISTRADOR -> true
        Rol.ENCARGADO -> accion in listOf(
            Accion.REGISTRAR_VENTA,
            Accion.EDITAR_INVENTARIO,
            Accion.VER_ESTADISTICAS
        )
        Rol.EMPLEADO -> accion == Accion.REGISTRAR_VENTA
    }

    /**
     * La misma regla para quien todavia no ha entrado.
     *
     * Devuelve `true`: sin sesion no se sabe el rol, y esconderlo todo dejaria
     * la aplicacion inservible tras un reinicio del sistema. La restriccion se
     * aplica cuando se sabe A QUIEN aplicarsela.
     *
     * Para que sobreviva a que Android mate el proceso habria que guardar la
     * sesion; hoy vive en memoria y se pierde al cerrar la app.
     */
    fun puede(usuario: Usuario?, accion: Accion): Boolean =
        usuario == null || puede(usuario.rol, accion)
}
