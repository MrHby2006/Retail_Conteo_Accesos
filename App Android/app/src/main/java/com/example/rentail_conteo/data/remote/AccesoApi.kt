package com.example.rentail_conteo.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AccesoApi {

    @GET("api/v1/accesos")
    suspend fun listar(@Query("soloActivos") soloActivos: Boolean): List<AccesoResponse>

    @PUT("api/v1/accesos/{id}/estado")
    suspend fun cambiarEstado(@Path("id") id: Long, @Body datos: EstadoAccesoRequest): AccesoResponse

    @GET("api/v1/accesos/catalogos")
    suspend fun catalogos(): CatalogosResponse

    @POST("api/v1/ingresos")
    suspend fun registrar(@Body datos: IngresoRequest): IngresoResponse

    @GET("api/v1/ingresos/contar")
    suspend fun contar(@Query("idAcceso") idAcceso: Long, @Query("fecha") fecha: String): Int

    @GET("api/v1/ingresos/candidatos")
    suspend fun candidatos(
        @Query("idRango") idRango: Long?,
        @Query("idColorSuperior") idColorSuperior: Long?,
        @Query("idColorInferior") idColorInferior: Long?,
        @Query("fecha") fecha: String?
    ): List<IngresoResponse>
}
