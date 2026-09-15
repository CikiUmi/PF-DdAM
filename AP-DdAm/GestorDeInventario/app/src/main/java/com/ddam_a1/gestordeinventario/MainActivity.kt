package com.ddam_a1.gestordeinventario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ddam_a1.gestordeinventario.ui.App
import com.ddam_a1.gestordeinventario.ui.pantallas.PantallaLogin
import com.ddam_a1.gestordeinventario.ui.theme.GestorDeInventarioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GestorDeInventarioTheme {
                App()
            }
        }
    }
}

@Preview(name = "Inicio de sesión", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun VistaPreviaLogin() {
    GestorDeInventarioTheme {
        PantallaLogin(onEntrar = {}, onConfigurar = {})
    }
}

@Preview(name = "Inicio de sesión · oscuro", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun VistaPreviaLoginOscuro() {
    GestorDeInventarioTheme(darkTheme = true) {
        PantallaLogin(onEntrar = {}, onConfigurar = {})
    }
}
