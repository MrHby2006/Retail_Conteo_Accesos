package com.example.rentail_conteo.data

import com.example.rentail_conteo.data.remote.IndicadoresResponse
import com.example.rentail_conteo.data.remote.ReporteApi
import com.example.rentail_conteo.data.remote.RetrofitClient
import com.example.rentail_conteo.data.remote.llamarApi

class ReporteRepository(
    private val api: ReporteApi = RetrofitClient.reporteApi
) {

    suspend fun indicadores(desde: String, hasta: String, idAcceso: Long?): IndicadoresResponse =
        llamarApi { api.indicadores(desde, hasta, idAcceso) }
}
