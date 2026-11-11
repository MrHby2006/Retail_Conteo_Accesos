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

class TurnosViewModel(application: Application) : AndroidViewModel(application) {
    private val db = RinconSurDbHelper.obtener(application)

    private val _uiState = MutableStateFlow(
        TurnosUIState(
            trabajadores = db.listarTrabajadores(),
            puestos = listOf(Turno.PUESTO_CAJA) + db.listarAccesos(soloActivos = true).map { it.codigo }
        )
    )
    val uiState: StateFlow<TurnosUIState> = _uiState.asStateFlow()

    init {
        onSemanaElegida(proxima = true)
    }

    fun onSemanaElegida(proxima: Boolean) {
        val dias = Fechas.diasDeLaSemana(Fechas.lunesDeLaSemana(if (proxima) 1 else 0))
        val dia = if (proxima) dias.first() else Fechas.hoy()
        _uiState.update {
            it.copy(esProximaSemana = proxima, diasSemana = dias, diaElegido = dia).sinFormulario()
        }
        recargar()
    }

    fun onDiaElegido(dia: String) {
        if (dia == _uiState.value.diaElegido) return
        _uiState.update { it.copy(diaElegido = dia).sinFormulario() }
        recargar()
    }

    fun onTrabajadorElegido(trabajador: Usuario) =
        _uiState.update { it.copy(trabajadorElegido = trabajador, errorTrabajador = null, errorFranja = null) }

    fun onFranjaElegida(franja: String) = _uiState.update { it.copy(franjaElegida = franja, errorFranja = null) }

    fun onPuestoElegido(puesto: String) = _uiState.update { it.copy(puestoElegido = puesto, errorPuesto = null) }

    fun mensajeMostrado() = _uiState.update { it.copy(mensaje = null) }

    fun onModificar(turno: Turno) {
        val error = when {
            turno.yaTermino() -> "Ese turno ya terminó, no se puede modificar"
            turno.cambioPendiente -> "Ese turno ya tiene un cambio esperando que lo acepten"
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(mensaje = error) }
            return
        }
        _uiState.update {
            it.sinFormulario().copy(
                turnoEnEdicion = turno,
                trabajadorElegido = it.trabajadores.firstOrNull { trabajador -> trabajador.id == turno.idUsuario },
                franjaElegida = turno.franja,
                puestoElegido = turno.puesto
            )
        }
    }

    fun onCancelarModificacion() = _uiState.update { it.sinFormulario() }

    fun onQuitarBorrador(turno: Turno) {
        val ok = db.borrarTurnoBorrador(turno.id)
        _uiState.update {
            it.copy(mensaje = if (ok) "Turno de ${turno.nombreTrabajador} quitado" else "Solo se pueden quitar turnos en borrador")
        }
        recargar()
    }

    fun onPedirPublicar() {
        if (_uiState.value.borradoresSemana == 0) {
            _uiState.update { it.copy(mensaje = "No hay turnos nuevos para publicar") }
            return
        }
        _uiState.update { it.copy(pidiendoPublicar = true) }
    }

    fun onCancelarPublicar() = _uiState.update { it.copy(pidiendoPublicar = false) }

    fun onConfirmarPublicar() {
        val dias = _uiState.value.diasSemana
        val publicados = db.publicarTurnos(dias.first(), dias.last())
        _uiState.update {
            it.copy(
                pidiendoPublicar = false,
                mensaje = "Horario publicado: $publicados turno(s). Los trabajadores ya pueden verlo"
            )
        }
        recargar()
    }

    fun onGuardar(idSupervisor: Long) {
        val estado = _uiState.value
        val enEdicion = estado.turnoEnEdicion
        val trabajador = estado.trabajadorElegido
        val franja = estado.franjaElegida
        val puesto = estado.puestoElegido

        if (enEdicion == null && !estado.esProximaSemana) {
            _uiState.update { it.copy(mensaje = "Los turnos nuevos se arman para la próxima semana") }
            return
        }

        val otrosTurnos = estado.turnosDelDia.filter { it.id != enEdicion?.id }
        val errorTrabajador = if (trabajador == null) "Elige a quién le asignas el turno" else null
        val errorFranja = when {
            franja == null -> "Elige el horario"
            otrosTurnos.any { it.idUsuario == trabajador?.id && it.franja == franja } ->
                "${trabajador?.primerNombre()} ya tiene turno en la $franja de ese día"
            else -> null
        }
        val errorPuesto = when {
            puesto == null -> "Elige el puesto"
            puesto != Turno.PUESTO_CAJA &&
                    otrosTurnos.any { it.puesto == puesto && it.franja == franja } ->
                "La puerta $puesto ya tiene a alguien en la $franja"
            else -> null
        }

        _uiState.update {
            it.copy(errorTrabajador = errorTrabajador, errorFranja = errorFranja, errorPuesto = errorPuesto)
        }
        if (errorTrabajador != null || errorFranja != null || errorPuesto != null) return

        if (enEdicion != null) {
            guardarModificacion(enEdicion, trabajador!!, franja!!, puesto!!, idSupervisor)
            return
        }

        val id = db.insertarTurno(trabajador!!.id, idSupervisor, estado.diaElegido, franja!!, puesto!!)
        if (id == -1L) {
            _uiState.update { it.copy(mensaje = "No se pudo guardar el turno") }
            return
        }
        _uiState.update {
            it.copy(
                mensaje = "Borrador: ${trabajador.primerNombre()}, ${Fechas.diaCorto(it.diaElegido)}, $franja, " +
                        legible(puesto) + ". Recuerda publicar la semana"
            ).sinFormulario()
        }
        recargar()
    }

    private fun guardarModificacion(
        turno: Turno, trabajador: Usuario, franja: String, puesto: String, idSupervisor: Long
    ) {
        if (turno.idUsuario == trabajador.id && turno.franja == franja && turno.puesto == puesto) {
            _uiState.update { it.copy(mensaje = "No cambiaste nada del turno") }
            return
        }

        if (turno.esBorrador) {
            val ok = db.actualizarTurno(turno.id, trabajador.id, franja, puesto)
            _uiState.update {
                it.copy(mensaje = if (ok) "Borrador actualizado" else "No se pudo modificar el turno").sinFormulario()
            }
            recargar()
            return
        }

        val id = db.crearSolicitudCambio(turno.id, idSupervisor, trabajador.id, franja, puesto)
        val quienes = if (turno.idUsuario == trabajador.id) {
            trabajador.primerNombre()
        } else {
            "${turno.nombreTrabajador.substringBefore(" ")} y ${trabajador.primerNombre()}"
        }
        _uiState.update {
            it.copy(
                mensaje = if (id == -1L) "No se pudo enviar la solicitud"
                else "Solicitud enviada. El cambio se aplica cuando $quienes lo acepten"
            ).sinFormulario()
        }
        recargar()
    }

    private fun recargar() {
        _uiState.update {
            it.copy(
                turnosDelDia = db.listarTurnos(it.diaElegido),
                borradoresSemana = db.contarBorradores(it.diasSemana.first(), it.diasSemana.last())
            )
        }
    }

    private fun legible(puesto: String) = if (puesto == Turno.PUESTO_CAJA) "caja" else "puerta $puesto"

    private fun TurnosUIState.sinFormulario() = copy(
        turnoEnEdicion = null, trabajadorElegido = null, franjaElegida = null, puestoElegido = null,
        errorTrabajador = null, errorFranja = null, errorPuesto = null
    )
}
