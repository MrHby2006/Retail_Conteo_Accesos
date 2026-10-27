package com.example.rentail_conteo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.rentail_conteo.data.RinconSurDbHelper
import com.example.rentail_conteo.model.IndicadoresUIState
import com.example.rentail_conteo.model.Opciones
import com.example.rentail_conteo.util.Fechas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class IndicadoresViewModel(application: Application) : AndroidViewModel(application) {
    private val db = RinconSurDbHelper.obtener(application)

    private val _uiState = MutableStateFlow(IndicadoresUIState())
    val uiState: StateFlow<IndicadoresUIState> = _uiState.asStateFlow()

    fun cargar(idAcceso: Int) {
        val acceso = db.listarAccesos().firstOrNull { it.id == idAcceso }
        _uiState.update { it.copy(acceso = acceso) }
        calcular()
    }

    fun onCambiarPeriodo(soloHoy: Boolean) {
        _uiState.update { it.copy(soloHoy = soloHoy) }
        calcular()
    }

    private fun calcular() {
        val estado = _uiState.value
        val idAcceso = estado.acceso?.id?.toLong()
        val fecha = if (estado.soloHoy) Fechas.hoy() else null

        val porRango = db.indicadoresPorRango(idAcceso, fecha)
            .sortedBy { Opciones.rangosEtarios.indexOf(it.grupo) }
        val porCompania = db.indicadoresPorCompania(idAcceso, fecha)
        val porAcceso = if (idAcceso == null) db.indicadoresPorAcceso(fecha) else emptyList()

        val masCompra = porRango
            .filter { it.compradores > 0 }
            .maxWithOrNull(compareBy({ it.conversion }, { it.compradores }))
        val noCompra = porRango
            .filter { it.noCompradores > 0 }
            .maxByOrNull { it.noCompradores }
        val masDePaso = porAcceso
            .filter { it.noCompradores > 0 }
            .maxWithOrNull(compareBy({ 100 - it.conversion }, { it.noCompradores }))

        _uiState.update {
            it.copy(
                ingresos = porRango.sumOf { fila -> fila.ingresos },
                compradores = porRango.sumOf { fila -> fila.compradores },
                porRango = porRango,
                porAcceso = porAcceso,
                accesoMasDePaso = masDePaso,
                perfilQueMasCompra = masCompra?.grupo,
                perfilQueNoCompra = noCompra?.grupo,
                conversionSolos = porCompania.firstOrNull { fila -> fila.grupo == "0" }?.conversion ?: 0,
                conversionAcompanados = porCompania.firstOrNull { fila -> fila.grupo == "1" }?.conversion ?: 0
            )
        }
    }
}
