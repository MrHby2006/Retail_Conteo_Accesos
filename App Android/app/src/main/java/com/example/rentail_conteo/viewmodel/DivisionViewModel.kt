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

    // Primera opción "Todos los accesos" (id 0: no existe en la BD, significa "sin filtro de acceso")
    private val _uiState = MutableStateFlow(
        DivisionUIState(accesosDisponibles = listOf(AccesoItem(0, "", "Todos los accesos")) + db.listarAccesos())
    )
    val uiState: StateFlow<DivisionUIState> = _uiState.asStateFlow()

    fun seleccionarAcceso(acceso: AccesoItem) {
        _uiState.update { currentState ->
            currentState.copy(accesoSeleccionado = acceso, errorMessage = null)
        }
    }

    // Botón "Consultar Indicadores": devuelve el acceso elegido, o null y deja el mensaje de error
    fun validarSeleccion(): AccesoItem? {
        val acceso = _uiState.value.accesoSeleccionado
        if (acceso == null) {
            _uiState.update { it.copy(errorMessage = "Elige un acceso (o Todos los accesos)") }
        }
        return acceso
    }
}
