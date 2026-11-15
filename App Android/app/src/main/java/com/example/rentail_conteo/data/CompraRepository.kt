package com.example.rentail_conteo.data

import com.example.rentail_conteo.data.remote.CompraApi
import com.example.rentail_conteo.data.remote.CompraRequest
import com.example.rentail_conteo.data.remote.CompraResponse
import com.example.rentail_conteo.data.remote.RetrofitClient
import com.example.rentail_conteo.data.remote.llamarApi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class CompraRepository(
    private val api: CompraApi = RetrofitClient.compraApi
) {

    suspend fun boletaYaRegistrada(numeroBoleta: String): Boolean =
        llamarApi { api.listar() }.any { it.numeroBoleta.equals(numeroBoleta, ignoreCase = true) }

    suspend fun registrar(datos: CompraRequest): CompraResponse = llamarApi { api.registrar(datos) }

    suspend fun subirBoleta(idCompra: Long, bytes: ByteArray): CompraResponse {
        val cuerpo = bytes.toRequestBody("image/jpeg".toMediaType())
        val parte = MultipartBody.Part.createFormData("archivo", "boleta.jpg", cuerpo)
        return llamarApi { api.subirBoleta(idCompra, parte) }
    }
}
