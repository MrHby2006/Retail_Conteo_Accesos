package com.example.rentail_conteo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentail_conteo.data.AccesoRepository
import com.example.rentail_conteo.model.AccesoItem
import com.example.rentail_conteo.model.DivisionUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DivisionViewModel : ViewModel() {
    private val repositorio = AccesoRepository()
    private val todos = AccesoItem(0, "", "Todos los accesos", true)

    private val _uiState = MutableStateFlow(DivisionUIState())
    val uiState: StateFlow<DivisionUIState> = _uiState.asStateFlow()

    init {
        cargarAccesos()
    }

    private fun cargarAccesos() {
        viewModelScope.launch {
            try {
                val accesos = repositorio.listar()
                _uiState.update { it.copy(accesosDisponibles = listOf(todos) + accesos, errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun seleccionarAcceso(acceso: AccesoItem) {
        _uiState.update { currentState ->
            currentState.copy(accesoSeleccionado = acceso, errorMessage = null)
        }
    }

    fun validarSeleccion(): AccesoItem? {
        val acceso = _uiState.value.accesoSeleccionado
        if (acceso == null) {
            _uiState.update { it.copy(errorMessage = "Elige un acceso (o Todos los accesos)") }
        }
        return acceso
    }

    fun cambiarEstadoAccesos(acceso: AccesoItem, activada: Boolean) {
        viewModelScope.launch {
            try {
                repositorio.cambiarEstado(acceso.id, activada)
                val accesos = repositorio.listar()
                _uiState.update { currentState ->
                    currentState.copy(
                        accesosDisponibles = listOf(todos) + accesos,
                        accesoSeleccionado = if (currentState.accesoSeleccionado?.id == acceso.id && !activada) null
                                             else currentState.accesoSeleccionado
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }
}
