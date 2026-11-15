package com.example.rentail_conteo.data.remote

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface CompraApi {

    @GET("api/v1/compras")
    suspend fun listar(): List<CompraResponse>

    @POST("api/v1/compras")
    suspend fun registrar(@Body datos: CompraRequest): CompraResponse

    @Multipart
    @POST("api/v1/compras/{id}/boleta")
    suspend fun subirBoleta(@Path("id") id: Long, @Part archivo: MultipartBody.Part): CompraResponse
}
