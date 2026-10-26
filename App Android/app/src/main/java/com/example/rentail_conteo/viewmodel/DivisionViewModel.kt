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

class DivisionViewModel(application: Application) : AndroidViewModel(application) {
    private val db = RinconSurDbHelper.obtener(application)

    private val _uiState = MutableStateFlow(
        DivisionUIState(accesosDisponibles = listOf(AccesoItem(0, "", "Todos los accesos")) + db.listarAccesos())
    )
    val uiState: StateFlow<DivisionUIState> = _uiState.asStateFlow()

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
}
