package com.example.rentail_conteo.data

import com.example.rentail_conteo.data.remote.CambioContrasenaRequest
import com.example.rentail_conteo.data.remote.CodigoEnviadoResponse
import com.example.rentail_conteo.data.remote.CuentaCreadaResponse
import com.example.rentail_conteo.data.remote.EmpleadoRequest
import com.example.rentail_conteo.data.remote.LoginRequest
import com.example.rentail_conteo.data.remote.PerfilRequest
import com.example.rentail_conteo.data.remote.RecuperarRequest
import com.example.rentail_conteo.data.remote.RestablecerRequest
import com.example.rentail_conteo.data.remote.RetrofitClient
import com.example.rentail_conteo.data.remote.UsuarioApi
import com.example.rentail_conteo.data.remote.aUsuario
import com.example.rentail_conteo.data.remote.llamarApi
import com.example.rentail_conteo.model.Usuario
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class UsuarioRepository(
    private val api: UsuarioApi = RetrofitClient.usuarioApi
) {

    suspend fun iniciarSesion(rut: String, contrasena: String): Usuario =
        llamarApi { api.iniciarSesion(LoginRequest(rut, contrasena)) }.aUsuario()

    suspend fun obtener(id: Long): Usuario =
        llamarApi { api.obtener(id) }.aUsuario()

    suspend fun listar(codigoRol: String? = null): List<Usuario> =
        llamarApi { api.listar(codigoRol) }.map { it.aUsuario() }

    suspend fun crear(datos: EmpleadoRequest): CuentaCreadaResponse =
        llamarApi { api.crear(datos) }

    suspend fun editar(id: Long, datos: EmpleadoRequest): Usuario =
        llamarApi { api.editar(id, datos) }.aUsuario()

    suspend fun desactivar(id: Long): String =
        llamarApi { api.desactivar(id) }.string()

    suspend fun actualizarPerfil(id: Long, datos: PerfilRequest): Usuario =
        llamarApi { api.actualizarPerfil(id, datos) }.aUsuario()

    suspend fun cambiarContrasena(id: Long, actual: String, nueva: String): Usuario =
        llamarApi { api.cambiarContrasena(id, CambioContrasenaRequest(actual, nueva)) }.aUsuario()

    suspend fun recuperar(rut: String): CodigoEnviadoResponse =
        llamarApi { api.recuperar(RecuperarRequest(rut)) }

    suspend fun restablecer(rut: String, codigo: String, nueva: String): String =
        llamarApi { api.restablecer(RestablecerRequest(rut, codigo, nueva)) }.string()

    suspend fun subirFoto(id: Long, bytes: ByteArray): Usuario {
        val cuerpo = bytes.toRequestBody("image/jpeg".toMediaType())
        val parte = MultipartBody.Part.createFormData("archivo", "perfil.jpg", cuerpo)
        return llamarApi { api.subirFoto(id, parte) }.aUsuario()
    }
}
