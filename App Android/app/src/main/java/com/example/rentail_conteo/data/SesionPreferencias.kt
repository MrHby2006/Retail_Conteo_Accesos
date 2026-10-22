package com.example.rentail_conteo.data

import android.content.Context
import androidx.core.content.edit

// SharedPreferences: un archivo pequeño "clave -> valor" para recordar quién inició sesión.
// Se guarda SOLO el id del usuario; sus datos se buscan en la BD con ese id.
class SesionPreferencias(context: Context) {

    // MODE_PRIVATE = solo esta app puede leer el archivo
    private val prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)

    companion object {
        private const val CLAVE_ID_USUARIO = "id_usuario"
        private const val SIN_SESION = -1L
    }

    fun guardar(idUsuario: Long) {
        prefs.edit { putLong(CLAVE_ID_USUARIO, idUsuario) }
    }

    // -1 si no hay nadie guardado
    fun leer(): Long = prefs.getLong(CLAVE_ID_USUARIO, SIN_SESION)

    fun borrar() {
        prefs.edit { remove(CLAVE_ID_USUARIO) }
    }
}
