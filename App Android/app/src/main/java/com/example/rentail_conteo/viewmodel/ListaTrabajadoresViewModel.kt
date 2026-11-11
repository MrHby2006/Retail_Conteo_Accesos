package com.example.rentail_conteo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.rentail_conteo.data.RinconSurDbHelper
import com.example.rentail_conteo.model.ListaTrabajadoresUIState
import com.example.rentail_conteo.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ListaTrabajadoresViewModel(application: Application) : AndroidViewModel(application) {
    private val db = RinconSurDbHelper.obtener(application)

    private val _uiState = MutableStateFlow(ListaTrabajadoresUIState())
    val uiState: StateFlow<ListaTrabajadoresUIState> = _uiState.asStateFlow()

    init {
        cargarTrabajadores()
    }

    fun cargarTrabajadores() {
        _uiState.update { it.copy(trabajadores = db.listarTrabajadores()) }
    }

    fun solicitarEliminacion(usuario: Usuario) {
        _uiState.update { it.copy(trabajadorAEliminar = usuario) }
    }

    fun cancelarEliminacion() {
        _uiState.update { it.copy(trabajadorAEliminar = null) }
    }

    fun confirmarEliminacion() {
        val usuario = _uiState.value.trabajadorAEliminar ?: return
        if (db.eliminarUsuario(usuario.id)) {
            _uiState.update {
                it.copy(
                    trabajadorAEliminar = null,
                    mensaje = "Trabajador ${usuario.nombre} eliminado"
                )
            }
            cargarTrabajadores()
        } else {
            _uiState.update { it.copy(mensaje = "No se pudo eliminar al trabajador") }
        }
    }

    fun mensajeMostrado() {
        _uiState.update { it.copy(mensaje = null) }
    }
}