package com.example.rentail_conteo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.rentail_conteo.data.RinconSurDbHelper
import com.example.rentail_conteo.model.HorarioUIState
import com.example.rentail_conteo.util.Fechas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HorarioViewModel(application: Application) : AndroidViewModel(application) {
    private val db = RinconSurDbHelper.obtener(application)

    private val _uiState = MutableStateFlow(HorarioUIState())
    val uiState: StateFlow<HorarioUIState> = _uiState.asStateFlow()

    init {
        onSemanaElegida(proxima = false)
    }

    fun onSemanaElegida(proxima: Boolean) {
        val dias = Fechas.diasDeLaSemana(Fechas.lunesDeLaSemana(if (proxima) 1 else 0))
        _uiState.update {
            it.copy(
                esProximaSemana = proxima,
                diasSemana = dias,
                turnos = db.listarTurnosPublicados(dias.first(), dias.last())
            )
        }
    }

    fun onSoloMisTurnos(valor: Boolean) = _uiState.update { it.copy(soloMisTurnos = valor) }
}
