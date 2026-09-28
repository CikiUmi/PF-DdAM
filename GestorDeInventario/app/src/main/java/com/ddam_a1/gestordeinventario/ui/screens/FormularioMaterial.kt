package com.ddam_a1.gestordeinventario.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.BoxWithConstraints
import com.ddam_a1.gestordeinventario.ui.components.BarraPasos
import com.ddam_a1.gestordeinventario.ui.components.BotonSecundario
import com.ddam_a1.gestordeinventario.ui.theme.AnchoPantalla
import com.ddam_a1.gestordeinventario.ui.theme.anchoPantallaDe
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.ddam_a1.gestordeinventario.modelClasses.Material
import com.ddam_a1.gestordeinventario.ui.aTexto
import com.ddam_a1.gestordeinventario.ui.components.BarraSuperior
import com.ddam_a1.gestordeinventario.ui.components.BotonPrincipal
import com.ddam_a1.gestordeinventario.ui.components.CampoTexto
import com.ddam_a1.gestordeinventario.ui.components.Iconos
import com.ddam_a1.gestordeinventario.ui.components.Interruptor
import com.ddam_a1.gestordeinventario.ui.components.TarjetaSuave
import com.ddam_a1.gestordeinventario.ui.dinero
import com.ddam_a1.gestordeinventario.ui.theme.Anchos
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.Medidas
import com.ddam_a1.gestordeinventario.ui.theme.Radios
import com.ddam_a1.gestordeinventario.ui.theme.tituloMedio

// ============================================================
//  PANTALLA 8 - NUEVO / EDITAR MATERIAL
//  (Figma 43:879 / 32:36 / 43:1440 y 112:6214 / 112:6163 / 112:6273)
//  RF1, RF2, RF18, RF19, RF22
//
//  DAR DE ALTA SON DOS PASOS, y no uno largo, porque son dos preguntas
//  distintas: QUE es el material (nombre, unidad, cuando avisar) y QUE ENTRA
//  hoy (el primer lote, con su fecha y su costo). Mezclarlas obligaba a
//  inventarse una cantidad para poder guardar un material que todavia no se ha
//  comprado, y dejaba la fecha del primer lote sin sitio donde preguntarse.
//
//  EDITAR sigue siendo UNA pantalla: los lotes ya no se tocan desde aqui, se
//  agregan y se dan de baja en el detalle del material.
//
//  `material` null significa "vengo a crear". Es la misma idea que ya tenia la
//  ruta con el id opcional, ahora en forma de dato.
//
//  Al guardar NO escribe nada: junta lo que el usuario escribio en un
//  DatosMaterial y lo entrega. Quien decide si eso es un alta o una edicion, y
//  quien lo anota en la bitacora, es el ViewModel.
//
//  En pantallas anchas el formulario NO se estira: una caja de texto de 1200
//  de ancho es peor de leer que una de 520. Se centra y se queda ahi.
// ============================================================

/** Las unidades de siempre. El desplegable evita "Kg", "kgs" y "KG" mezclados. */
private val UNIDADES = listOf("kg", "g", "L", "ml", "pza", "m")

@Composable
fun PantallaFormularioMaterial(
    material: Material?,
    onGuardar: (DatosMaterial) -> Unit,
    onAtras: () -> Unit
) {
    val esNuevo = material == null

    // `remember(material)` y no `remember`: si el material llega un instante
    // despues (porque se estaba leyendo), los campos se vuelven a sembrar con
    // sus valores. Con `remember` a secas se quedarian vacios para siempre.
    var paso by remember(material) { mutableStateOf(1) }
    var nombre by remember(material) { mutableStateOf(material?.nombre ?: "") }
    var unidad by remember(material) { mutableStateOf(material?.unidadMedida ?: "kg") }
    var minimo by remember(material) { mutableStateOf(material?.stockMinimo?.aTexto() ?: "0") }
    var caduca by remember(material) { mutableStateOf((material?.diasAvisoCaducidad ?: 0) > 0) }
    var dias by remember(material) { mutableStateOf(material?.diasAvisoCaducidad?.toString() ?: "7") }

    // ---- Paso 2: el lote inicial ----
    //
    // Los numericos arrancan en "0" y no vacios: asi se ve de entrada que aqui
    // van numeros, y el campo nunca esta en un estado sin valor.
    var caducidad by remember(material) { mutableStateOf("") }
    var cantidad by remember(material) { mutableStateOf("0") }

    // ---- Costo: se captura el TOTAL pagado, se guarda el UNITARIO ----
    //
    // Nadie sabe de memoria cuanto le sale el kilo; lo que si tiene a la mano
    // es el ticket: "pague 450 por 3 kg". La division la hace la app.
    var total by remember(material) { mutableStateOf("0") }
    // Al editar no hay lote que capturar, asi que el unitario se escribe
    // directo: es el dato que usan la receta y el costo de produccion.
    var unitarioEdicion by remember(material) {
        mutableStateOf(material?.costoUnitario?.aTexto() ?: "0")
    }

    val cantidadNum = cantidad.toDoubleOrNull() ?: 0.0
    val totalNum = total.toDoubleOrNull() ?: 0.0
    val unitario = if (cantidadNum > 0.0) totalNum / cantidadNum else 0.0

    // El paso 1 se completa con nombre y unidad. La cantidad ya no entra aqui:
    // un material puede existir sin nada en el almacen, y de hecho es lo normal
    // cuando se captura el catalogo antes de la primera compra.
    val paso1Listo = nombre.isNotBlank() && unidad.isNotBlank() &&
        (!caduca || (dias.toIntOrNull() ?: 0) > 0)
    // Para guardar CON lote hace falta cantidad, y fecha si el material caduca.
    val loteListo = cantidadNum > 0.0 && (!caduca || caducidad.isNotBlank())

    val datosBase = {
        DatosMaterial(
            nombre = nombre.trim(),
            unidad = unidad.trim(),
            cantidad = 0.0,
            costo = unitarioEdicion.toDoubleOrNull() ?: 0.0,
            stockMinimo = minimo.toDoubleOrNull() ?: 0.0,
            diasAvisoCaducidad = if (caduca) (dias.toIntOrNull() ?: 0) else 0
        )
    }

    val guardarSinLote = {
        onGuardar(datosBase())
        onAtras()
    }
    val guardarConLote = {
        onGuardar(
            datosBase().copy(
                cantidad = cantidadNum,
                // Se guarda el unitario, no el total: es lo que usan la receta
                // y el calculo del costo de produccion.
                costo = unitario,
                caducidadInicial = if (caduca) caducidad.trim() else ""
            )
        )
        onAtras()
    }

    // Atras en el paso 2 regresa al paso 1, no se sale del formulario: lo
    // escrito en el primero seguiria ahi pero el usuario no lo sabria.
    val atras: () -> Unit = { if (paso == 2) paso = 1 else onAtras() }
    BackHandler(enabled = paso == 2) { paso = 1 }

    val titulo = when {
        !esNuevo -> "Editar material"
        paso == 1 -> "Nuevo material"
        else -> "Lote inicial"
    }

    BoxWithConstraints {
        // El indicador de pasos va al centro en tableta y en telefono girado,
        // y en linea en un telefono de pie (Figma 43:879 contra 32:36): en 360
        // de ancho, dos renglones centrados se comen el espacio que necesita
        // el formulario.
        val pasosAlCentro = anchoPantallaDe(maxWidth) != AnchoPantalla.COMPACTA

        Marco(barra = {
            BarraSuperior(titulo, onAtras = atras) {
                // El atajo de guardar de la barra solo existe al editar: en el
                // alta, guardar es justamente lo que decide el paso 2.
                if (!esNuevo) AccionGuardar(paso1Listo, guardarSinLote)
            }
        }) {
            item {
                // Una sola columna estrecha y centrada en cualquier medida:
                // widthIn deja de crecer y el Box la mantiene al centro.
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                    Column(
                        Modifier.widthIn(max = Anchos.tarjetaFormulario),
                        verticalArrangement = Arrangement.spacedBy(Margenes.lg)
                    ) {
                        if (esNuevo) BarraPasos(paso, 2, centrado = pasosAlCentro)

                        if (paso == 1) {
                            PasoDatos(
                                nombre = nombre, onNombre = { nombre = it },
                                unidad = unidad, onUnidad = { unidad = it },
                                caduca = caduca, onCaduca = { caduca = it },
                                dias = dias, onDias = { dias = it },
                                minimo = minimo, onMinimo = { minimo = it },
                                esNuevo = esNuevo,
                                unitario = unitarioEdicion, onUnitario = { unitarioEdicion = it }
                            )
                            if (esNuevo) {
                                BotonPrincipal("Siguiente", habilitado = paso1Listo) { paso = 2 }
                            } else {
                                BotonPrincipal(
                                    "Guardar material",
                                    habilitado = paso1Listo,
                                    onClick = guardarSinLote
                                )
                            }
                        } else {
                            PasoLoteInicial(
                                caduca = caduca,
                                caducidad = caducidad, onCaducidad = { caducidad = it },
                                cantidad = cantidad, onCantidad = { cantidad = it },
                                unidad = unidad,
                                total = total, onTotal = { total = it },
                                unitario = unitario, hayCantidad = cantidadNum > 0.0
                            )
                            // Los dos en el mismo renglon (Figma 112:6163):
                            // apilados se comen 120 de alto en una pantalla
                            // que ya va sobrada de espacio abajo.
                            //
                            // El secundario a la IZQUIERDA, que es donde se
                            // espera la salida, y el que guarda de verdad a la
                            // derecha, bajo el pulgar.
                            //
                            // Las etiquetas son cortas por obligacion: a medio
                            // renglon de un telefono de 360 quedan unos 126 de
                            // texto, y "Guardar material sin lote inicial" pide
                            // el doble. Dicen lo mismo con menos.
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Margenes.md)
                            ) {
                                BotonSecundario(
                                    "Omitir lote",
                                    modifier = Modifier.weight(1f),
                                    onClick = guardarSinLote
                                )
                                BotonPrincipal(
                                    "Guardar",
                                    modifier = Modifier.weight(1f),
                                    habilitado = loteListo,
                                    onClick = guardarConLote
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================
//  PASO 1 - QUE ES EL MATERIAL
//
//  Como se llama, en que se mide y cuando hay que avisar de el. Nada de
//  cantidades: eso es del lote, y un material puede existir con el almacen
//  vacio.
// ============================================================

@Composable
private fun PasoDatos(
    nombre: String, onNombre: (String) -> Unit,
    unidad: String, onUnidad: (String) -> Unit,
    caduca: Boolean, onCaduca: (Boolean) -> Unit,
    dias: String, onDias: (String) -> Unit,
    minimo: String, onMinimo: (String) -> Unit,
    esNuevo: Boolean,
    unitario: String, onUnitario: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Margenes.lg)) {
        // Nombre y unidad en el mismo renglon, 4 a 1 (Figma 112:6367): la
        // unidad son dos o tres letras y un campo de ancho completo para "kg"
        // se ve como un error.
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Margenes.md)
        ) {
            Box(Modifier.weight(4f)) {
                CampoTexto(nombre, "Nombre", onNombre, marcador = "Ej. Harina de trigo")
            }
            Box(Modifier.weight(1f)) { SelectorUnidad(unidad, onUnidad) }
        }

        if (!esNuevo) {
            // Al editar no hay lote que capturar, asi que el costo por unidad
            // se escribe tal cual en vez de deducirse de una compra.
            CampoTexto(
                unitario, "Costo unitario", onUnitario,
                soloNumeros = true, sufijo = "MXN / " + unidad
            )
        }

        Etiqueta("Avisos y Notificaciones")

        Row(
            Modifier.fillMaxWidth().padding(vertical = Margenes.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Margenes.md)
        ) {
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Margenes.xs)
            ) {
                Text(
                    "Este material caduca",
                    style = MaterialTheme.typography.bodyLarge
                        .copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Activa para configurar avisos de vencimiento",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Interruptor(caduca, onCambio = onCaduca)
        }

        if (caduca) {
            CampoTexto(
                dias, "Antelación de avisos", onDias,
                soloEnteros = true, sufijo = "días antes"
            )
        }

        CampoTexto(
            minimo, "Stock mínimo para alerta", onMinimo,
            soloNumeros = true, sufijo = unidad
        )
    }
}

// ============================================================
//  PASO 2 - LO QUE ENTRA HOY
//
//  El primer lote. Se puede saltar: dar de alta el catalogo antes de la
//  primera compra es un caso normal, y por eso el boton de guardar sin lote
//  no esta escondido en un menu.
//
//  La fecha solo se pregunta si en el paso 1 se dijo que el material caduca.
//  Preguntarla siempre obligaria a inventarse una para la sal.
// ============================================================

@Composable
private fun PasoLoteInicial(
    caduca: Boolean,
    caducidad: String, onCaducidad: (String) -> Unit,
    cantidad: String, onCantidad: (String) -> Unit,
    unidad: String,
    total: String, onTotal: (String) -> Unit,
    unitario: Double,
    hayCantidad: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(Margenes.lg)) {
        if (caduca) {
            CampoTexto(
                caducidad, "Fecha de caducidad", onCaducidad,
                // El mismo formato que guarda la base: con "aaaa-mm-dd" el
                // orden alfabetico ya es el cronologico, y el sufijo recuerda
                // como se escribe sin tener que probar.
                sufijo = "aaaa-mm-dd"
            )
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Margenes.md)
        ) {
            Box(Modifier.weight(1f)) {
                CampoTexto(
                    cantidad, "Cantidad inicial", onCantidad,
                    soloNumeros = true, sufijo = unidad
                )
            }
            Box(Modifier.weight(1f)) {
                CampoTexto(total, "Costo total", onTotal, soloNumeros = true, sufijo = "MXN")
            }
        }

        // El resultado en vivo: se ve el precio por unidad mientras se
        // escribe, sin tener que guardar para comprobarlo.
        TarjetaSuave {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Costo unitario",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    if (hayCantidad) dinero(unitario) + " / " + unidad
                    else "— / " + unidad,
                    style = MaterialTheme.typography.tituloMedio,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// ---------- PIEZAS ----------

/** El "Guardar" de la barra: el mismo atajo del Figma, sin bajar al final. */
@Composable
private fun AccionGuardar(habilitado: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(Radios.accion))
            .clickable(enabled = habilitado) { onClick() }
            .padding(horizontal = Margenes.md, vertical = Margenes.sm)
    ) {
        Text(
            "Guardar",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = if (habilitado) MaterialTheme.colorScheme.tertiary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Unidad de medida (RF22).
 *
 * Desplegable y no texto libre porque la unidad se compara con la de la receta:
 * "kg" y "Kg" escritos a mano serian dos unidades distintas para el codigo.
 */
@Composable
private fun SelectorUnidad(valor: String, onValor: (String) -> Unit) {
    var abierto by remember { mutableStateOf(false) }
    val forma = RoundedCornerShape(Radios.campo)

    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Margenes.sm)
    ) {
        Text(
            "Unidad",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Box {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(Medidas.control)
                    .clip(forma)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .border(Medidas.borde, MaterialTheme.colorScheme.outlineVariant, forma)
                    .clickable { abierto = true }
                    .padding(horizontal = Margenes.lg),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    valor,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    Iconos.Desplegar,
                    contentDescription = "Elegir unidad",
                    modifier = Modifier.size(Medidas.iconoChico),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            DropdownMenu(abierto, { abierto = false }) {
                UNIDADES.forEach { u ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                u,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = {
                            onValor(u)
                            abierto = false
                        }
                    )
                }
            }
        }
    }
}
