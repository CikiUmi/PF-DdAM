package com.ddam_a1.gestordeinventario.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.ddam_a1.gestordeinventario.R

// ============================================================
//  EL LOGO DE LA APLICACION
//
//  Un solo sitio que sabe cual es el archivo del logo y como se dibuja. Lo
//  usan la pantalla de acceso y el panel lateral de tableta; si manana el
//  logo cambia, se cambia aqui y no en cada pantalla.
//
//  El archivo vive en `drawable` y no en `mipmap`, aunque sea el mismo
//  dibujo que el icono de la app. `mipmap` guarda el icono del lanzador, que
//  desde Android 8 es ADAPTATIVO: un XML de dos capas que cada telefono
//  recorta con su propia forma. `painterResource` no sabe leer ese XML y
//  falla en tiempo de ejecucion, no al compilar. La copia de `drawable` es
//  un mapa de bits normal, con las esquinas ya redondeadas y transparentes,
//  y esta en las cinco densidades para que no se vea borroso en ninguna
//  pantalla.
// ============================================================

@Composable
fun LogoApp(tam: Dp, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.logo_app),
        // Decorativo: en los dos sitios donde aparece, el nombre de la
        // aplicacion esta escrito justo al lado.
        contentDescription = null,
        modifier = modifier.size(tam)
    )
}
