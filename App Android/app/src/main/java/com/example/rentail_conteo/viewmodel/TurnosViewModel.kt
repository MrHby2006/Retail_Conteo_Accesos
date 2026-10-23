package com.example.rentail_conteo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.rentail_conteo.data.RinconSurDbHelper
import com.example.rentail_conteo.model.Turno
import com.example.rentail_conteo.model.TurnosUIState
import com.example.rentail_conteo.model.Usuario
import com.example.rentail_conteo.util.Fechas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Lógica del SUPERVISOR DE PISO: asigna a los trabajadores (que ya existen) un turno en la caja o en una puerta.
// No crea trabajadores ni turnos nuevos de la nada: elige quién, cuándo y dónde.
class TurnosViewModel(application: Application) : AndroidViewModel(application) {

    private val db = RinconSurDbHelper.obtener(application)

    private val _uiState = MutableStateFlow(
        TurnosUIState(
            diaElegido = Fechas.hoy(),
            trabajadores = db.listarTrabajadores(),
            // Cualquier trabajador puede ir a la caja o a cualquiera de las 4 puertas
            puestos = listOf(Turno.PUESTO_CAJA) + db.listarAccesos().map { it.codigo },
            turnosDelDia = db.listarTurnos(Fechas.hoy())
        )
    )
    val uiState: StateFlow<TurnosUIState> = _uiState.asStateFlow()

    // Al tocar un día en el calendario se recargan los turnos de ese día
    fun onDiaElegido(dia: String) {
        if (dia == _uiState.value.diaElegido) return
        _uiState.update { it.copy(diaElegido = dia, turnosDelDia = db.listarTurnos(dia)) }
    }

    fun onTrabajadorElegido(trabajador: Usuario) =
        _uiState.update { it.copy(trabajadorElegido = trabajador, errorTrabajador = null, errorFranja = null) }

    fun onFranjaElegida(franja: String) = _uiState.update { it.copy(franjaElegida = franja, errorFranja = null) }

    fun onPuestoElegido(puesto: String) = _uiState.update { it.copy(puestoElegido = puesto, errorPuesto = null) }

    fun mensajeMostrado() = _uiState.update { it.copy(mensaje = null) }

    // Botón "Asignar turno"
    fun onAsignar(idSupervisor: Long) {
        val estado = _uiState.value
        val trabajador = estado.trabajadorElegido
        val franja = estado.franjaElegida
        val puesto = estado.puestoElegido

        val errorTrabajador = if (trabajador == null) "Elige a quién le asignas el turno" else null
        val errorFranja = when {
            franja == null -> "Elige el horario"
            // Regla 1: un trabajador no puede estar en dos lugares en la misma franja
            estado.turnosDelDia.any { it.idUsuario == trabajador?.id && it.franja == franja } ->
                "${trabajador?.primerNombre()} ya tiene turno en la $franja de ese día"
            else -> null
        }
        val errorPuesto = when {
            puesto == null -> "Elige el puesto"
            // Regla 2: cada puerta tiene UNA persona por franja (la caja puede tener varias)
            puesto != Turno.PUESTO_CAJA &&
                    estado.turnosDelDia.any { it.puesto == puesto && it.franja == franja } ->
                "La puerta $puesto ya tiene a alguien en la $franja"
            else -> null
        }

        _uiState.update {
            it.copy(errorTrabajador = errorTrabajador, errorFranja = errorFranja, errorPuesto = errorPuesto)
        }
        if (errorTrabajador != null || errorFranja != null || errorPuesto != null) return

        val id = db.insertarTurno(trabajador!!.id, idSupervisor, estado.diaElegido, franja!!, puesto!!)
        if (id == -1L) {
            _uiState.update { it.copy(mensaje = "No se pudo guardar el turno") }
            return
        }

        // Se limpia la selección pero se queda en el mismo día, para seguir asignando
        _uiState.update {
            it.copy(
                trabajadorElegido = null, franjaElegida = null, puestoElegido = null,
                turnosDelDia = db.listarTurnos(it.diaElegido),
                mensaje = "Turno asignado a ${trabajador.primerNombre()}: ${Fechas.diaCorto(it.diaElegido)}, $franja, " +
                        if (puesto == Turno.PUESTO_CAJA) "caja" else "puerta $puesto"
            )
        }
    }

    fun onQuitar(turno: Turno) {
        db.eliminarTurno(turno.id)
        _uiState.update {
            it.copy(
                turnosDelDia = db.listarTurnos(it.diaElegido),
                mensaje = "Se quitó el turno de ${turno.nombreTrabajador.substringBefore(" ")}"
            )
        }
    }
}
