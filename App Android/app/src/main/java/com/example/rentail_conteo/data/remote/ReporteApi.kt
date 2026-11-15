package com.example.rentail_conteo.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface ReporteApi {

    @GET("api/v1/reportes/indicadores")
    suspend fun indicadores(
        @Query("desde") desde: String,
        @Query("hasta") hasta: String,
        @Query("idAcceso") idAcceso: Long?
    ): IndicadoresResponse
}
