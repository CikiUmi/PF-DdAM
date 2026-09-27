package com.ddam_a1.gestordeinventario.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ddam_a1.gestordeinventario.ui.theme.AnchoPantalla
import com.ddam_a1.gestordeinventario.ui.theme.Anchos
import com.ddam_a1.gestordeinventario.ui.theme.Margenes
import com.ddam_a1.gestordeinventario.ui.theme.anchoPantallaDe

// ============================================================
//  EL LIENZO DE LAS PANTALLAS DE ACCESO
//
//  Las tres pantallas de acceso (login, crear admin, elegir modo) tienen la
//  misma caja: barra opcional arriba, contenido en medio y controles abajo.
//  Y las tres se adaptan igual, que es lo que el Figma dibuja tres veces:
//
//    COMPACTA   el contenido ocupa el ancho, 24 de margen
//    MEDIA      el contenido se queda en 400 y se centra; no se estira
//    EXPANDIDA  el contenido va en una TARJETA centrada en la pantalla
//
//  Una sola pantalla de codigo para los tres tamanos. `BoxWithConstraints`
//  mide el ancho REAL disponible, no el del dispositivo: si manana esto
//  viviera en medio pantalla, tambien acertaria.
//
//  Del Figma: 24 de lado, 24 arriba, 48 abajo, y 24 entre el contenido y los
//  controles de abajo (32 cuando hay barra).
// ============================================================

@Composable
fun Lienzo(
    modifier: Modifier = Modifier,
    /** App Bar. Login no la lleva; las otras dos si. */
    barra: @Composable () -> Unit = {},
    /** Botones fijos abajo. */
    pie: @Composable ColumnScope.() -> Unit = {},
    /**
     * Centra el contenido verticalmente en el espacio libre.
     *
     * En el Figma, Login usa `justify-between` con la barra de estado arriba y
     * el indicador abajo: como esos dos los dibuja el sistema, lo que queda es
     * un bloque centrado. Crear administrador y Elegir modo NO lo usan: ahi el
     * contenido arranca arriba y los botones se van al fondo.
     */
    centrado: Boolean = false,
    anchoTarjeta: Dp = Anchos.tarjetaAcceso,
    /** Separacion entre los bloques del contenido. */
    separacion: Dp = Margenes.xl,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Surface(modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        BoxWithConstraints {
            val tamano = anchoPantallaDe(maxWidth)
            val enTarjeta = tamano == AnchoPantalla.EXPANDIDA
            val anchoMax = when (tamano) {
                AnchoPantalla.COMPACTA -> Dp.Unspecified
                AnchoPantalla.MEDIA -> Anchos.formularioMedio
                AnchoPantalla.EXPANDIDA -> anchoTarjeta
            }

            Column(
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                // En tableta la tarjeta se centra verticalmente; en telefono el
                // contenido arranca arriba y el pie se va al fondo.
                verticalArrangement = if (enTarjeta) Arrangement.Center else Arrangement.Top
            ) {
                val interior: @Composable ColumnScope.() -> Unit = {
                    barra()

                    // La caja se queda con el espacio libre y la columna de
                    // adentro se alinea arriba o al centro. No se puede poner
                    // `Arrangement.Center` directamente en una columna con
                    // scroll: al tener scroll, la columna mide lo que mide su
                    // contenido y no hay espacio sobrante que repartir.
                    val cajaContenido: @Composable (Modifier) -> Unit = { m ->
                        Box(m.fillMaxWidth()) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    // Figma: el contenido arranca 16 debajo de
                                    // la App Bar. Login no lleva barra, asi que
                                    // ahi no hay nada de lo que separarse.
                                    .padding(top = if (centrado) 0.dp else Margenes.lg)
                                    .align(
                                        if (centrado) Alignment.Center
                                        else Alignment.TopCenter
                                    )
                                    // Scroll: con el teclado abierto en un
                                    // telefono chico, cuatro campos no caben.
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(separacion)
                            ) { contenido() }
                        }
                    }

                    if (enTarjeta) cajaContenido(Modifier) else cajaContenido(Modifier.weight(1f))

                    Column(
                        Modifier
                            .fillMaxWidth()
                            // Sin botones abajo no hay que separar nada de
                            // nada: ese hueco empujaba el bloque hacia arriba.
                            .then(if (centrado) Modifier else Modifier.padding(top = Margenes.xxl)),
                        verticalArrangement = Arrangement.spacedBy(Margenes.xl),
                        content = pie
                    )
                }

                if (enTarjeta) {
                    Surface(
                        Modifier.widthIn(max = anchoMax),
                        shape = RoundedCornerShape(28.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLowest,
                        tonalElevation = 0.dp,
                        shadowElevation = 2.dp
                    ) {
                        Column(Modifier.padding(48.dp), content = interior)
                    }
                } else {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .then(
                                if (anchoMax == Dp.Unspecified) Modifier
                                else Modifier.widthIn(max = anchoMax)
                            )
                            .padding(
                                start = Margenes.xl,
                                end = Margenes.xl,
                                top = Margenes.xl,
                                bottom = 48.dp
                            ),
                        content = interior
                    )
                }
            }
        }
    }
}
