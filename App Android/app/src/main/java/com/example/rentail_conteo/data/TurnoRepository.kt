package com.example.rentail_conteo.data

import com.example.rentail_conteo.data.remote.FranjaResponse
import com.example.rentail_conteo.data.remote.PublicacionRequest
import com.example.rentail_conteo.data.remote.PublicacionResponse
import com.example.rentail_conteo.data.remote.PuestoResponse
import com.example.rentail_conteo.data.remote.RespuestaSolicitudRequest
import com.example.rentail_conteo.data.remote.RetrofitClient
import com.example.rentail_conteo.data.remote.SolicitudRequest
import com.example.rentail_conteo.data.remote.TurnoApi
import com.example.rentail_conteo.data.remote.TurnoRequest
import com.example.rentail_conteo.data.remote.aSolicitud
import com.example.rentail_conteo.data.remote.aTurno
import com.example.rentail_conteo.data.remote.llamarApi
import com.example.rentail_conteo.model.SolicitudCambio
import com.example.rentail_conteo.model.Turno

class TurnoRepository(
    private val api: TurnoApi = RetrofitClient.turnoApi
) {

    suspend fun franjas(): List<FranjaResponse> = llamarApi { api.franjas() }

    suspend fun puestos(): List<PuestoResponse> = llamarApi { api.puestos() }

    suspend fun delDia(fecha: String): List<Turno> =
        llamarApi { api.delDia(fecha) }.map { it.aTurno() }

    suspend fun semana(desde: String, hasta: String, soloPublicados: Boolean): List<Turno> =
        llamarApi { api.semana(desde, hasta, soloPublicados) }.map { it.aTurno() }

    suspend fun turnoDeHoy(idUsuario: Long): Turno? =
        llamarApi { api.turnoDeHoy(idUsuario) }.body()?.aTurno()

    suspend fun proximoTurno(idUsuario: Long): Turno? =
        llamarApi { api.proximoTurno(idUsuario) }.body()?.aTurno()

    suspend fun contarPendientes(idUsuario: Long): Long = llamarApi { api.contarPendientes(idUsuario) }

    suspend fun crear(datos: TurnoRequest): Turno = llamarApi { api.crear(datos) }.aTurno()

    suspend fun editar(id: Long, datos: TurnoRequest): Turno = llamarApi { api.editar(id, datos) }.aTurno()

    suspend fun eliminar(id: Long): String = llamarApi { api.eliminar(id) }.string()

    suspend fun publicar(desde: String, hasta: String): PublicacionResponse =
        llamarApi { api.publicar(PublicacionRequest(desde, hasta)) }

    suspend fun crearSolicitud(datos: SolicitudRequest): SolicitudCambio =
        llamarApi { api.crearSolicitud(datos) }.aSolicitud()

    suspend fun solicitudesPendientes(idUsuario: Long): List<SolicitudCambio> =
        llamarApi { api.solicitudesPendientes(idUsuario) }.map { it.aSolicitud() }

    suspend fun responder(idSolicitud: Long, idUsuario: Long, acepta: Boolean): String =
        llamarApi { api.responder(idSolicitud, RespuestaSolicitudRequest(idUsuario, acepta)) }.string()
}
