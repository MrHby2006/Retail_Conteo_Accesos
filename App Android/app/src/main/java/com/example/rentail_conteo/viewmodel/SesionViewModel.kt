package com.example.rentail_conteo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentail_conteo.data.SesionPreferencias
import com.example.rentail_conteo.data.UsuarioRepository
import com.example.rentail_conteo.model.Usuario
import com.example.rentail_conteo.notificaciones.AvisoTurno
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SesionViewModel(application: Application) : AndroidViewModel(application) {
    private val repositorio = UsuarioRepository()
    private val sesion = SesionPreferencias(application)

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    init {
        restaurarSesion()
    }

    private fun restaurarSesion() {
        val id = sesion.leer()
        if (id == -1L) {
            _cargando.value = false
            return
        }
        viewModelScope.launch {
            try {
                _usuarioActual.value = repositorio.obtener(id)
            } catch (e: Exception) {
                _usuarioActual.value = null
            } finally {
                _cargando.value = false
            }
        }
    }

    fun iniciarSesion(usuario: Usuario) {
        _usuarioActual.value = usuario
        sesion.guardar(usuario.id)
    }

    fun cerrarSesion() {
        AvisoTurno.cancelarTodos(getApplication())
        _usuarioActual.value = null
        sesion.borrar()
    }
}
