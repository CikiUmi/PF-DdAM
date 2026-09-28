package com.ddam_a1.gestordeinventario.ui.theme
import com.ddam_a1.gestordeinventario.R

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val bodyFontFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Nunito"),
        fontProvider = provider,
    )
)

val displayFontFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Lora"),
        fontProvider = provider,
        weight = FontWeight.SemiBold
    ),
    Font(
        googleFont = GoogleFont("Lora"),
        fontProvider = provider,
        weight = FontWeight.Normal
    )
)

// Default Material 3 typography values
val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily, fontWeight = FontWeight.SemiBold),
    displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily, fontWeight = FontWeight.SemiBold),
    displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily, fontWeight = FontWeight.SemiBold),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily, fontWeight = FontWeight.SemiBold),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily, fontWeight = FontWeight.SemiBold),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily, fontWeight = FontWeight.SemiBold),
    titleLarge = baseline.titleLarge.copy(fontFamily = displayFontFamily, fontWeight = FontWeight.SemiBold),
    titleMedium = baseline.titleMedium.copy(fontFamily = displayFontFamily, fontWeight = FontWeight.SemiBold),
    titleSmall = baseline.titleSmall.copy(fontFamily = displayFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    // ============================================================
    //  NINGUNA LETRA BAJA DE 16
    //
    //  La escala de Material trae body/label chicos: bodyMedium 14, bodySmall
    //  y labelMedium 12, labelSmall 11. En un telefono, a un metro de
    //  distancia y con las manos ocupadas, 11 puntos no se leen.
    //
    //  Se suben TODOS a 16 aqui y no en cada Text por dos motivos: queda un
    //  solo sitio que cambiar si el dia de manana se decide otro minimo, y
    //  cualquier pantalla que se escriba manana hereda la regla sin que nadie
    //  se acuerde de ella.
    //
    //  La jerarquia no se pierde: sigue estando en el PESO y en el COLOR
    //  (onSurface contra onSurfaceVariant), que es como la usa el Figma. Lo
    //  que se pierde es distinguir dos textos solo por su tamano, y eso ya no
    //  funcionaba para quien no ve de cerca.
    // ============================================================
    bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
    //  Y CON LA LETRA SUBE EL RENGLON
    //
    //  `fontSize` solo cambia el tamano de la letra; el alto del renglon es
    //  otro valor. Los tres estilos chicos de Material traen renglon de 16,
    //  que le quedaba bien a una letra de 11 o 12. Con la letra en 16, la
    //  letra mide lo mismo que su renglon: las colas de la g y la p se salen
    //  de la caja y el texto se pega a lo que tenga debajo.
    //
    //  20 es lo que Material usa para sus estilos de 14, con la misma
    //  proporcion. Los otros tres ya venian con 20 y no hay que tocarlos.
    bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily, fontSize = 16.sp),
    bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily, fontSize = 16.sp, lineHeight = 20.sp),
    labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily, fontSize = 16.sp),
    labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily, fontSize = 16.sp, lineHeight = 20.sp),
    labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily, fontSize = 16.sp, lineHeight = 20.sp),
)

// ============================================================
//  UN TAMANO QUE MATERIAL NO TRAE
//
//  El Figma usa Lora SemiBold 20 en un monton de sitios: titulo de la App Bar,
//  encabezados de seccion, el valor de las tarjetas de resumen, el precio del
//  catalogo. En la escala de Material no hay 20: titleLarge son 22 y
//  titleMedium 16.
//
//  En vez de escribir `.copy(fontSize = 20.sp)` en quince lugares, se nombra
//  una vez. Es lo mismo que hace Margenes.kt con los dp.
// ============================================================

val Typography.tituloMedio: TextStyle
    get() = titleLarge.copy(fontSize = 20.sp)
