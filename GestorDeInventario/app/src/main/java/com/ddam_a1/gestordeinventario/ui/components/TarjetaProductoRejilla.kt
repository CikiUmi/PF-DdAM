package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.coloresExtra
import com.ddam_a1.gestordeinventario.ui.theme.tituloMedio

// ============================================================
//  TARJETA DE PRODUCTO EN REJILLA  (Figma 48:1413)
//
//  La del catalogo nuevo: foto arriba, nombre, costo y venta en una linea, y
//  abajo una pastilla con el stock o "Bajo pedido".
//
//  Es distinta de TarjetaProducto (la fila ancha con miniatura de 64) porque
//  aqui el producto se MIRA: van dos, tres o cuatro por renglon segun el
//  ancho, y la imagen manda. La otra sigue sirviendo para listas densas.
//
//  El stock y "bajo pedido" son excluyentes: un producto bajo pedido no
//  guarda existencias, se hace cuando alguien lo encarga. Por eso es un
//  `if/else` y no dos pastillas que podrian salir juntas y contradecirse.
// ============================================================

@Composable
fun TarjetaProductoRejilla(
    nombre: String,
    /** Solo el importe, ya formateado. La etiqueta "Costo:" la pone el diseno. */
    costo: String,
    /** Idem para "Venta:". */
    venta: String,
    esBajoPedido: Boolean,
    stock: Int,
    stockBajo: Boolean,
    /** Dias que faltan para caducar, negativo si ya caduco; null si no esta en riesgo. */
    diasParaCaducar: Int? = null,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val cs = MaterialTheme.colorScheme
    val correcto = MaterialTheme.coloresExtra.correct

    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cs.surfaceContainerHigh)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(Margenes.md)
            .semantics(mergeDescendants = true) { },
        // Cuatro piezas separadas por 10, como el Figma 97:4999: imagen,
        // textos, HUECO FLEXIBLE y pastilla.
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HuecoImagen()

        Text(
            nombre,
            style = MaterialTheme.typography.tituloMedio,
            color = cs.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        // Costo y venta van en su propia columna SIN separacion: son la misma
        // idea (lo que cuesta y lo que deja) y pegados se leen como un bloque.
        // El hueco que los separa del nombre es el de la tarjeta, el mismo que
        // hay entre la imagen y el texto, asi que la tarjeta entera lleva un
        // solo ritmo en vez de dos.
        //
        // Dos renglones y no uno con " · " en medio (Figma 97:5003 y 97:5014):
        // juntos, en una tarjeta estrecha, el corte de linea caia donde queria
        // y dejaba "Venta:" separado de su importe.
        Column {
            Text(
                "Costo: " + costo,
                style = MaterialTheme.typography.bodyLarge,
                color = cs.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "Venta: " + venta,
                style = MaterialTheme.typography.bodyLarge,
                color = cs.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // El hueco que sobra cuando la tarjeta se estira para igualar a su
        // vecina se va AQUI, entre el texto y la pastilla. Dos cosas a la vez:
        // las pastillas de un renglon quedan todas abajo y a la misma altura,
        // y el espacio deja de ser relleno y pasa a separar dos grupos. Sin
        // sobrante mide cero y la tarjeta se ve igual que antes.
        Spacer(Modifier.weight(1f))

        // UNA sola pastilla, por orden de urgencia. Dos juntas no caben en
        // una tarjeta de rejilla, y la primera ya dice lo que hay que hacer:
        // sin stock no se vende, y lo que caduca hay que sacarlo antes.
        if (esBajoPedido) {
            Pastilla("Bajo pedido", cs.tertiaryContainer, cs.onTertiaryContainer, Iconos.Reloj)
        } else if (stockBajo) {
            // El rojo no viaja solo: dice el numero, que es lo que preocupa.
            Pastilla(stock.toString() + " en stock", cs.errorContainer, cs.onErrorContainer, Iconos.Alerta)
        } else if (diasParaCaducar != null) {
            // Dias y no la fecha completa: la pastilla cabe en unos 92dp de
            // renglon (150 de tarjeta menos su relleno, el icono y el suyo) y
            // "Caduca 2026-10-02" se parte en dos. La fecha exacta esta en el
            // detalle del producto, que es donde se decide que hacer con ella.
            val aviso = when {
                diasParaCaducar < 0 -> "Caducado"
                diasParaCaducar == 0 -> "Caduca hoy"
                else -> "Caduca " + diasParaCaducar + " d"
            }
            // Rojo solo cuando ya caduco. Mientras todavia da tiempo va en el
            // rosa de `tertiaryContainer`, el mismo de "Bajo pedido": si todo
            // lo que preocupa fuera rojo, el rojo dejaria de significar algo.
            if (diasParaCaducar < 0) {
                Pastilla(aviso, cs.errorContainer, cs.onErrorContainer, Iconos.Alerta)
            } else {
                Pastilla(aviso, cs.tertiaryContainer, cs.onTertiaryContainer, Iconos.CalendarioReloj)
            }
        } else {
            Pastilla(
                stock.toString() + " en stock",
                correcto.colorContainer, correcto.onColorContainer, Iconos.Check
            )
        }
    }
}
