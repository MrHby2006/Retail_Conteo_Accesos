package com.example.rentail_conteo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentail_conteo.data.TurnoRepository
import com.example.rentail_conteo.model.HorarioUIState
import com.example.rentail_conteo.util.Fechas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HorarioViewModel : ViewModel() {
    private val repositorio = TurnoRepository()

    private val _uiState = MutableStateFlow(HorarioUIState())
    val uiState: StateFlow<HorarioUIState> = _uiState.asStateFlow()

    init {
        onSemanaElegida(proxima = false)
    }

    fun onSemanaElegida(proxima: Boolean) {
        val dias = Fechas.diasDeLaSemana(Fechas.lunesDeLaSemana(if (proxima) 1 else 0))
        _uiState.update { it.copy(esProximaSemana = proxima, diasSemana = dias, turnos = emptyList(), error = null) }
        viewModelScope.launch {
            try {
                val turnos = repositorio.semana(dias.first(), dias.last(), soloPublicados = true)
                _uiState.update { it.copy(turnos = turnos) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun onSoloMisTurnos(valor: Boolean) = _uiState.update { it.copy(soloMisTurnos = valor) }
}
