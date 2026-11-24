package com.example.rentail_conteo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentail_conteo.data.AccesoRepository
import com.example.rentail_conteo.data.ReporteRepository
import com.example.rentail_conteo.data.remote.FilaIndicadorResponse
import com.example.rentail_conteo.model.FilaIndicador
import com.example.rentail_conteo.model.IndicadoresUIState
import com.example.rentail_conteo.util.Fechas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class IndicadoresViewModel : ViewModel() {
    private val reporteRepositorio = ReporteRepository()
    private val accesoRepositorio = AccesoRepository()

    private val _uiState = MutableStateFlow(IndicadoresUIState())
    val uiState: StateFlow<IndicadoresUIState> = _uiState.asStateFlow()

    fun cargar(idAcceso: Int) {
        viewModelScope.launch {
            try {
                val acceso = accesoRepositorio.listar().firstOrNull { it.id == idAcceso }
                _uiState.update { it.copy(acceso = acceso) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
            calcular()
        }
    }

    fun onCambiarPeriodo(soloHoy: Boolean) {
        _uiState.update { it.copy(soloHoy = soloHoy) }
        viewModelScope.launch { calcular() }
    }

    private suspend fun calcular() {
        val estado = _uiState.value
        val hoy = Fechas.hoy()
        val desde = if (estado.soloHoy) hoy else hoy.take(8) + "01"
        _uiState.update { it.copy(cargando = true, error = null) }
        try {
            val datos = reporteRepositorio.indicadores(desde, hoy, estado.acceso?.id?.toLong())
            val porRango = datos.porRango.map { it.aFila() }
            val porAcceso = if (estado.acceso == null) datos.porAcceso.map { it.aFila() } else emptyList()

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
                    cargando = false,
                    ingresos = datos.totalIngresos,
                    compradores = datos.compradores,
                    porRango = porRango,
                    porAcceso = porAcceso,
                    accesoMasDePaso = masDePaso,
                    perfilQueMasCompra = masCompra?.grupo,
                    perfilQueNoCompra = noCompra?.grupo,
                    metaMes = datos.metaMes,
                    cumpleMeta = datos.cumpleMeta
                )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(cargando = false, error = e.message) }
        }
    }

    private fun FilaIndicadorResponse.aFila() = FilaIndicador(grupo, ingresos, compradores)
}
