package com.example.rentail_conteo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentail_conteo.data.TurnoRepository
import com.example.rentail_conteo.data.UsuarioRepository
import com.example.rentail_conteo.data.remote.FranjaResponse
import com.example.rentail_conteo.data.remote.PuestoResponse
import com.example.rentail_conteo.data.remote.SolicitudRequest
import com.example.rentail_conteo.data.remote.TurnoRequest
import com.example.rentail_conteo.model.Turno
import com.example.rentail_conteo.model.TurnosUIState
import com.example.rentail_conteo.model.Usuario
import com.example.rentail_conteo.util.Fechas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TurnosViewModel : ViewModel() {
    private val turnoRepositorio = TurnoRepository()
    private val usuarioRepositorio = UsuarioRepository()

    private var franjas: List<FranjaResponse> = emptyList()
    private var puestos: List<PuestoResponse> = emptyList()

    private val _uiState = MutableStateFlow(TurnosUIState())
    val uiState: StateFlow<TurnosUIState> = _uiState.asStateFlow()

    init {
        cargarCatalogos()
        onSemanaElegida(proxima = true)
    }

    private fun cargarCatalogos() {
        viewModelScope.launch {
            try {
                franjas = turnoRepositorio.franjas()
                puestos = turnoRepositorio.puestos()
                val trabajadores = usuarioRepositorio.listar("TRABAJADOR")
                _uiState.update { it.copy(trabajadores = trabajadores, puestos = puestos.map { p -> p.codigo }) }
            } catch (e: Exception) {
                _uiState.update { it.copy(mensaje = e.message) }
            }
        }
    }

    fun onSemanaElegida(proxima: Boolean) {
        val dias = Fechas.diasDeLaSemana(Fechas.lunesDeLaSemana(if (proxima) 1 else 0))
        val dia = if (proxima) dias.first() else Fechas.hoy()
        _uiState.update {
            it.copy(esProximaSemana = proxima, diasSemana = dias, diaElegido = dia, turnosSemana = emptyList()).sinFormulario()
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
            turno.yaEmpezo() -> "Ese turno está en curso, no se puede modificar"
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
        viewModelScope.launch {
            try {
                turnoRepositorio.eliminar(turno.id)
                _uiState.update { it.copy(mensaje = "Turno de ${turno.nombreTrabajador} quitado") }
            } catch (e: Exception) {
                _uiState.update { it.copy(mensaje = e.message) }
            }
            recargar()
        }
    }

    fun onPedirPublicar() {
        if (!_uiState.value.semanaCompleta) {
            _uiState.update { it.copy(mensaje = "Completa todos los días de la semana antes de publicar") }
            return
        }
        if (_uiState.value.borradoresSemana == 0) {
            _uiState.update { it.copy(mensaje = "No hay turnos nuevos para publicar") }
            return
        }
        _uiState.update { it.copy(pidiendoPublicar = true) }
    }

    fun onCancelarPublicar() = _uiState.update { it.copy(pidiendoPublicar = false) }

    fun onConfirmarPublicar() {
        val dias = _uiState.value.diasSemana
        _uiState.update { it.copy(pidiendoPublicar = false, mensaje = "Publicando y enviando el horario por correo...") }
        viewModelScope.launch {
            try {
                val resultado = turnoRepositorio.publicar(dias.first(), dias.last())
                val correo = if (resultado.correoEnviado) "y se envió por correo a ${resultado.destinatarios} persona(s)"
                else "pero no se pudo enviar el correo"
                _uiState.update { it.copy(mensaje = "Horario publicado: ${resultado.publicados} turno(s) $correo") }
            } catch (e: Exception) {
                _uiState.update { it.copy(mensaje = e.message) }
            }
            recargar()
        }
    }

    fun onGuardar(idSupervisor: Long) {
        val estado = _uiState.value
        val enEdicion = estado.turnoEnEdicion
        val trabajador = estado.trabajadorElegido
        val franja = estado.franjaElegida
        val puesto = estado.puestoElegido

        if (enEdicion != null && enEdicion.yaEmpezo()) {
            _uiState.update { it.copy(mensaje = "Ese turno ya empezó, no se puede modificar").sinFormulario() }
            return
        }
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

        val idFranja = franjas.firstOrNull { it.nombre == franja }?.id
        val idPuesto = puestos.firstOrNull { it.codigo == puesto }?.id
        if (idFranja == null || idPuesto == null) {
            _uiState.update { it.copy(mensaje = "No se pudieron cargar las franjas y puestos. Vuelve a entrar") }
            return
        }

        viewModelScope.launch {
            try {
                val mensaje = when {
                    enEdicion == null -> {
                        turnoRepositorio.crear(TurnoRequest(trabajador!!.id, idSupervisor, estado.diaElegido, idFranja, idPuesto))
                        "Borrador: ${trabajador.primerNombre()}, ${Fechas.diaCorto(estado.diaElegido)}, $franja, " +
                                legible(puesto!!) + ". Recuerda publicar la semana"
                    }
                    enEdicion.idUsuario == trabajador!!.id && enEdicion.franja == franja && enEdicion.puesto == puesto ->
                        "No cambiaste nada del turno"
                    enEdicion.esBorrador -> {
                        turnoRepositorio.editar(enEdicion.id, TurnoRequest(trabajador.id, idSupervisor, enEdicion.fecha, idFranja, idPuesto))
                        "Borrador actualizado"
                    }
                    else -> {
                        turnoRepositorio.crearSolicitud(SolicitudRequest(enEdicion.id, idSupervisor, trabajador.id, idFranja, idPuesto))
                        val quienes = if (enEdicion.idUsuario == trabajador.id) trabajador.primerNombre()
                        else "${enEdicion.nombreTrabajador.substringBefore(" ")} y ${trabajador.primerNombre()}"
                        "Solicitud enviada por correo. El cambio se aplica cuando $quienes lo acepten"
                    }
                }
                _uiState.update { it.copy(mensaje = mensaje).sinFormulario() }
            } catch (e: Exception) {
                _uiState.update { it.copy(mensaje = e.message) }
            }
            recargar()
        }
    }

    private fun recargar() {
        val estado = _uiState.value
        if (estado.diasSemana.isEmpty()) return
        viewModelScope.launch {
            try {
                val delDia = turnoRepositorio.delDia(estado.diaElegido)
                val semana = turnoRepositorio.semana(estado.diasSemana.first(), estado.diasSemana.last(), soloPublicados = false)
                _uiState.update {
                    it.copy(turnosDelDia = delDia, turnosSemana = semana)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(mensaje = e.message) }
            }
        }
    }

    private fun legible(puesto: String) = if (puesto == Turno.PUESTO_CAJA) "caja" else "puerta $puesto"

    private fun TurnosUIState.sinFormulario() = copy(
        turnoEnEdicion = null, trabajadorElegido = null, franjaElegida = null, puestoElegido = null,
        errorTrabajador = null, errorFranja = null, errorPuesto = null
    )
}
