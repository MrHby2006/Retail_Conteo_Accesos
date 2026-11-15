package com.example.rentail_conteo.data.remote

import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> llamarApi(peticion: suspend () -> T): T = try {
    peticion()
} catch (e: HttpException) {
    throw Exception(mensajeDelServidor(e))
} catch (e: IOException) {
    throw Exception("No se pudo conectar con el servidor. Revisa tu conexión")
}

private fun mensajeDelServidor(e: HttpException): String {
    val cuerpo = e.response()?.errorBody()?.string().orEmpty().trim()
    if (cuerpo.startsWith("{")) {
        val mensaje = try {
            JSONObject(cuerpo).optString("message")
        } catch (ex: Exception) {
            ""
        }
        return mensaje.ifBlank { "Error del servidor (${e.code()})" }
    }
    return if (cuerpo.isNotBlank() && cuerpo.length < 200) cuerpo else "Error del servidor (${e.code()})"
}
