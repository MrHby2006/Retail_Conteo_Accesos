package com.example.rentail_conteo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.rentail_conteo.data.RinconSurDbHelper
import com.example.rentail_conteo.data.SesionPreferencias
import com.example.rentail_conteo.model.Usuario
import com.example.rentail_conteo.notificaciones.AvisoTurno
import com.example.rentail_conteo.util.Fechas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Quién está usando la app. Se crea UNA vez en AppNavegacion y lo comparten todas las pantallas.
// AndroidViewModel (y no ViewModel) porque la BD y SharedPreferences necesitan el Context de la app.
class SesionViewModel(application: Application) : AndroidViewModel(application) {

    private val db = RinconSurDbHelper.obtener(application)
    private val sesion = SesionPreferencias(application)

    // Al abrir la app: id guardado -> se busca ese usuario en la BD (null si no había sesión)
    private val _usuarioActual = MutableStateFlow(db.buscarUsuarioPorId(sesion.leer()))
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    fun iniciarSesion(usuario: Usuario) {
        _usuarioActual.value = usuario
        sesion.guardar(usuario.id)
    }

    fun cerrarSesion() {
        // Los avisos de fin de turno son de esta persona: si se va, no deben sonarle al siguiente que entre
        _usuarioActual.value?.let { AvisoTurno.cancelarDeHoy(getApplication(), db.turnosDeHoy(it.id, Fechas.hoy())) }
        _usuarioActual.value = null
        sesion.borrar()
    }
}
