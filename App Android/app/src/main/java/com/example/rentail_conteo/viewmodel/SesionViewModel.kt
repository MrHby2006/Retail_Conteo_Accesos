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

class SesionViewModel(application: Application) : AndroidViewModel(application) {
    private val db = RinconSurDbHelper.obtener(application)
    private val sesion = SesionPreferencias(application)

    private val _usuarioActual = MutableStateFlow(db.buscarUsuarioPorId(sesion.leer()))
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    fun iniciarSesion(usuario: Usuario) {
        _usuarioActual.value = usuario
        sesion.guardar(usuario.id)
    }

    fun cerrarSesion() {
        _usuarioActual.value?.let { AvisoTurno.cancelarDeHoy(getApplication(), db.turnosDeHoy(it.id, Fechas.hoy())) }
        _usuarioActual.value = null
        sesion.borrar()
    }
}
