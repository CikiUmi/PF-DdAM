package com.ddam_a1.gestordeinventario

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.ddam_a1.gestordeinventario.ui.navigation.GestorNavHost
import com.ddam_a1.gestordeinventario.ui.theme.GestorDeInventarioTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Se registra como campo y no dentro de onCreate a proposito: el registro
    // tiene que ocurrir antes de que la Activity este iniciada, o Android
    // lanza una excepcion.
    //
    // La respuesta no se usa: si dice que no, la app funciona igual y los
    // avisos se siguen viendo dentro. Perseguir al usuario con el dialogo cada
    // vez que abre seria peor que quedarse sin notificaciones.
    private val pedirPermisoDeNotificaciones =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        pedirNotificacionesSiHaceFalta()
        setContent {
            GestorDeInventarioTheme {
                GestorNavHost()
            }
        }
    }

    /** Antes de Android 13 el permiso venia dado con instalar la app. */
    private fun pedirNotificacionesSiHaceFalta() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val concedido = ContextCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!concedido) {
            pedirPermisoDeNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
