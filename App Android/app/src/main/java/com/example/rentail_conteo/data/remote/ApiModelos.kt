package com.example.rentail_conteo.data.remote

import com.example.rentail_conteo.model.AccesoItem
import com.example.rentail_conteo.model.PersonaIngreso
import com.example.rentail_conteo.model.Rol
import com.example.rentail_conteo.model.SolicitudCambio
import com.example.rentail_conteo.model.Turno
import com.example.rentail_conteo.model.Usuario

data class LoginRequest(
    val rut: String,
    val contrasena: String
)

data class UsuarioResponse(
    val id: Long,
    val codigo: String,
    val rut: String,
    val nombres: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String?,
    val nombreCompleto: String,
    val cargo: String,
    val correo: String,
    val correoPersonal: String?,
    val telefono: String?,
    val codigoRol: String,
    val nombreRol: String,
    val debeCambiarContrasena: Boolean,
    val fechaNacimiento: String?,
    val fotoUrl: String?,
    val activo: Boolean
)

data class EmpleadoRequest(
    val nombres: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String?,
    val rut: String,
    val cargo: String,
    val codigoRol: String,
    val correoPersonal: String,
    val telefono: String? = null
)

data class CuentaCreadaResponse(
    val usuario: UsuarioResponse,
    val contrasenaTemporal: String,
    val correoEnviado: Boolean
)

data class PerfilRequest(
    val correoPersonal: String,
    val telefono: String?,
    val fechaNacimiento: String?,
    val fotoUrl: String? = null
)

data class CambioContrasenaRequest(
    val actual: String,
    val nueva: String
)

data class RecuperarRequest(
    val rut: String
)

data class RestablecerRequest(
    val rut: String,
    val codigo: String,
    val nueva: String
)

data class CodigoEnviadoResponse(
    val correoOculto: String,
    val correoEnviado: Boolean,
    val minutosValidez: Int
)

data class FranjaResponse(
    val id: Long,
    val nombre: String,
    val horaInicio: String,
    val horaFin: String
)

data class PuestoResponse(
    val id: Long,
    val codigo: String,
    val nombre: String,
    val tipo: String,
    val idAcceso: Long?
)

data class TurnoRequest(
    val idUsuario: Long,
    val idSupervisor: Long,
    val fecha: String,
    val idFranja: Long,
    val idPuesto: Long
)

data class TurnoResponse(
    val id: Long,
    val idUsuario: Long,
    val nombreTrabajador: String,
    val idSupervisor: Long,
    val fecha: String,
    val idFranja: Long,
    val franja: String,
    val horaInicio: String,
    val horaFin: String,
    val idPuesto: Long,
    val codigoPuesto: String,
    val puesto: String,
    val tipoPuesto: String,
    val estado: String,
    val cambioPendiente: Boolean
)

data class PublicacionRequest(
    val desde: String,
    val hasta: String
)

data class PublicacionResponse(
    val publicados: Int,
    val correoEnviado: Boolean,
    val destinatarios: Int
)

data class SolicitudRequest(
    val idTurno: Long,
    val idSupervisor: Long,
    val idUsuarioNuevo: Long,
    val idFranjaNueva: Long,
    val idPuestoNuevo: Long,
    val motivo: String? = null
)

data class SolicitudResponse(
    val id: Long,
    val idTurno: Long,
    val fecha: String,
    val idTitular: Long,
    val nombreTitular: String,
    val franjaActual: String,
    val puestoActual: String,
    val idNuevo: Long,
    val nombreNuevo: String,
    val franjaNueva: String,
    val puestoNuevo: String,
    val aceptaTitular: Boolean,
    val aceptaNuevo: Boolean,
    val estado: String,
    val motivo: String?
)

data class RespuestaSolicitudRequest(
    val idUsuario: Long,
    val acepta: Boolean
)

data class AccesoResponse(
    val id: Long,
    val codigo: String,
    val nombre: String,
    val ubicacion: String?,
    val activo: Boolean
)

data class EstadoAccesoRequest(
    val activo: Boolean
)

data class OpcionResponse(
    val id: Long,
    val nombre: String,
    val codigoHex: String?
)

data class CatalogosResponse(
    val rangos: List<OpcionResponse>,
    val alturas: List<OpcionResponse>,
    val colores: List<OpcionResponse>,
    val companias: List<OpcionResponse>
)

data class IngresoRequest(
    val idAcceso: Long,
    val idObservador: Long,
    val idRango: Long,
    val idAltura: Long,
    val idColorSuperior: Long,
    val idColorInferior: Long,
    val idTipoCompania: Long,
    val detalle: String
)

data class IngresoResponse(
    val id: Long,
    val idAcceso: Long,
    val codigoAcceso: String,
    val idObservador: Long,
    val fechaHora: String,
    val rango: String,
    val altura: String,
    val colorSuperior: String,
    val colorInferior: String,
    val tipoCompania: String,
    val detalle: String?,
    val compro: Boolean,
    val anulado: Boolean
)

data class CompraRequest(
    val idPersona: Long,
    val idCajero: Long,
    val numeroBoleta: String,
    val nombreCliente: String?,
    val correoCliente: String?
)

data class CompraResponse(
    val id: Long,
    val idPersona: Long,
    val idCajero: Long,
    val fechaHora: String,
    val numeroBoleta: String,
    val nombreCliente: String?,
    val correoCliente: String?,
    val rutaBoleta: String?
)

data class FilaIndicadorResponse(
    val grupo: String,
    val ingresos: Int,
    val compradores: Int,
    val conversion: Double
)

data class IndicadoresResponse(
    val desde: String,
    val hasta: String,
    val totalIngresos: Int,
    val compradores: Int,
    val noCompradores: Int,
    val conversion: Double,
    val metaMes: Double?,
    val cumpleMeta: Boolean?,
    val porAcceso: List<FilaIndicadorResponse>,
    val porRango: List<FilaIndicadorResponse>,
    val porCompania: List<FilaIndicadorResponse>,
    val porHora: List<FilaIndicadorResponse>
)

fun UsuarioResponse.aUsuario() = Usuario(
    id = id,
    codigo = codigo,
    nombre = nombreCompleto,
    rut = rut,
    correo = correo,
    cargo = cargo,
    rolId = Rol.desdeCodigo(codigoRol),
    correoPersonal = correoPersonal,
    debeCambiarContrasena = debeCambiarContrasena,
    fotoPerfil = fotoUrl,
    fechaNacimiento = fechaNacimiento,
    nombres = nombres,
    apellidoPaterno = apellidoPaterno,
    apellidoMaterno = apellidoMaterno,
    telefono = telefono
)

fun TurnoResponse.aTurno() = Turno(
    id = id,
    idUsuario = idUsuario,
    nombreTrabajador = nombreTrabajador,
    fecha = fecha,
    franja = franja,
    puesto = codigoPuesto,
    estado = estado,
    cambioPendiente = cambioPendiente
)

fun SolicitudResponse.aSolicitud() = SolicitudCambio(
    id = id,
    idTurno = idTurno,
    fecha = fecha,
    idTitular = idTitular,
    nombreTitular = nombreTitular,
    franjaActual = franjaActual,
    puestoActual = puestoActual,
    idNuevo = idNuevo,
    nombreNuevo = nombreNuevo,
    franjaNueva = franjaNueva,
    puestoNuevo = puestoNuevo,
    aceptaTitular = aceptaTitular,
    aceptaNuevo = aceptaNuevo
)

fun AccesoResponse.aAccesoItem() = AccesoItem(
    id = id.toInt(),
    codigo = codigo,
    nombre = nombre,
    activada = activo
)

fun IngresoResponse.aPersona() = PersonaIngreso(
    id = id,
    accesoId = idAcceso,
    accesoCodigo = codigoAcceso,
    observadorId = idObservador,
    fechaHora = fechaHora,
    rangoEtario = rango,
    altura = altura,
    colorRopa = colorSuperior,
    colorRopaAbajo = colorInferior,
    compania = tipoCompania,
    detalle = detalle ?: "",
    compro = compro
)
