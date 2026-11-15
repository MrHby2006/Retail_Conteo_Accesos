package com.example.rentail_conteo.data.remote

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface UsuarioApi {

    @POST("api/v1/auth/login")
    suspend fun iniciarSesion(@Body datos: LoginRequest): UsuarioResponse

    @POST("api/v1/auth/recuperar")
    suspend fun recuperar(@Body datos: RecuperarRequest): CodigoEnviadoResponse

    @POST("api/v1/auth/restablecer")
    suspend fun restablecer(@Body datos: RestablecerRequest): ResponseBody

    @GET("api/v1/usuarios")
    suspend fun listar(@Query("rol") rol: String?): List<UsuarioResponse>

    @GET("api/v1/usuarios/{id}")
    suspend fun obtener(@Path("id") id: Long): UsuarioResponse

    @POST("api/v1/usuarios")
    suspend fun crear(@Body datos: EmpleadoRequest): CuentaCreadaResponse

    @PUT("api/v1/usuarios/{id}")
    suspend fun editar(@Path("id") id: Long, @Body datos: EmpleadoRequest): UsuarioResponse

    @DELETE("api/v1/usuarios/{id}")
    suspend fun desactivar(@Path("id") id: Long): ResponseBody

    @PUT("api/v1/usuarios/{id}/perfil")
    suspend fun actualizarPerfil(@Path("id") id: Long, @Body datos: PerfilRequest): UsuarioResponse

    @PUT("api/v1/usuarios/{id}/contrasena")
    suspend fun cambiarContrasena(@Path("id") id: Long, @Body datos: CambioContrasenaRequest): UsuarioResponse

    @POST("api/v1/usuarios/{id}/contrasena-temporal")
    suspend fun contrasenaTemporal(@Path("id") id: Long): CuentaCreadaResponse

    @Multipart
    @POST("api/v1/usuarios/{id}/foto")
    suspend fun subirFoto(@Path("id") id: Long, @Part archivo: MultipartBody.Part): UsuarioResponse
}
