package com.example.rentail_conteo.data.remote

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TurnoApi {

    @GET("api/v1/turnos/franjas")
    suspend fun franjas(): List<FranjaResponse>

    @GET("api/v1/turnos/puestos")
    suspend fun puestos(): List<PuestoResponse>

    @GET("api/v1/turnos")
    suspend fun delDia(@Query("fecha") fecha: String): List<TurnoResponse>

    @GET("api/v1/turnos/semana")
    suspend fun semana(
        @Query("desde") desde: String,
        @Query("hasta") hasta: String,
        @Query("soloPublicados") soloPublicados: Boolean
    ): List<TurnoResponse>

    @GET("api/v1/turnos/usuario/{id}/hoy")
    suspend fun turnoDeHoy(@Path("id") idUsuario: Long): Response<TurnoResponse>

    @GET("api/v1/turnos/usuario/{id}/proximo")
    suspend fun proximoTurno(@Path("id") idUsuario: Long): Response<TurnoResponse>

    @GET("api/v1/turnos/usuario/{id}/pendientes")
    suspend fun contarPendientes(@Path("id") idUsuario: Long): Long

    @POST("api/v1/turnos")
    suspend fun crear(@Body datos: TurnoRequest): TurnoResponse

    @PUT("api/v1/turnos/{id}")
    suspend fun editar(@Path("id") id: Long, @Body datos: TurnoRequest): TurnoResponse

    @DELETE("api/v1/turnos/{id}")
    suspend fun eliminar(@Path("id") id: Long): ResponseBody

    @POST("api/v1/turnos/copiar")
    suspend fun copiarDia(@Body datos: CopiaDiaRequest): Int

    @POST("api/v1/turnos/publicar")
    suspend fun publicar(@Body datos: PublicacionRequest): PublicacionResponse

    @POST("api/v1/solicitudes")
    suspend fun crearSolicitud(@Body datos: SolicitudRequest): SolicitudResponse

    @GET("api/v1/solicitudes/pendientes/{idUsuario}")
    suspend fun solicitudesPendientes(@Path("idUsuario") idUsuario: Long): List<SolicitudResponse>

    @PUT("api/v1/solicitudes/{id}/responder")
    suspend fun responder(@Path("id") id: Long, @Body datos: RespuestaSolicitudRequest): ResponseBody
}
