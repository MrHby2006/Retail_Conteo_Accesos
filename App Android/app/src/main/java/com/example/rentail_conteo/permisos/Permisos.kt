package com.example.rentail_conteo.permisos

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat

// Los 4 pasos para usar un permiso "peligroso" (cámara, fotos):
//  1) DECLARAR   <uses-permission> en AndroidManifest.xml
//  2) VERIFICAR  ¿ya está concedido?                 -> tiene()
//  3) PEDIR      diálogo del sistema                  -> RequestPermission (en la pantalla)
//  4) REACCIONAR concedido -> usar / rechazado -> avisar y ofrecer Ajustes -> abrirAjustes()
object Permisos {

    val CAMARA: String = Manifest.permission.CAMERA

    // El permiso para leer fotos cambió de nombre en Android 13 (API 33)
    val IMAGENES: String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES
        else Manifest.permission.READ_EXTERNAL_STORAGE

    fun tiene(context: Context, permiso: String): Boolean =
        ContextCompat.checkSelfPermission(context, permiso) == PackageManager.PERMISSION_GRANTED

    // Si el usuario rechazó 2 veces, Android ya no muestra el diálogo: solo queda ir a Ajustes
    fun abrirAjustes(context: Context) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
