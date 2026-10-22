package com.example.rentail_conteo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.rentail_conteo.data.RinconSurDbHelper
import com.example.rentail_conteo.model.AccesoItem
import com.example.rentail_conteo.model.DivisionUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// AndroidViewModel: necesita el Context para leer los accesos desde la BD
class DivisionViewModel(application: Application) : AndroidViewModel(application) {

    private val db = RinconSurDbHelper.obtener(application)

    private val _uiState = MutableStateFlow(DivisionUIState(accesosDisponibles = db.listarAccesos()))
    val uiState: StateFlow<DivisionUIState> = _uiState.asStateFlow()

    fun seleccionarAcceso(acceso: AccesoItem) {
        _uiState.update { currentState ->
            currentState.copy(accesoSeleccionado = acceso)
        }
    }

    fun actualizarFecha(anio: Int, mes: String, dia: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                anioSeleccionado = anio,
                mesSeleccionado = mes,
                diaSeleccionado = dia
            )
        }
    }

    fun limpiarError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
