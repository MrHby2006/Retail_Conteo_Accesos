package com.example.rentail_conteo.data.remote

import com.example.rentail_conteo.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    val URL_BASE: String = BuildConfig.API_URL

    private val clienteHttp = OkHttpClient.Builder()
        .addInterceptor { cadena ->
            val peticion = cadena.request().newBuilder()
                .header("X-Tunnel-Skip-AntiPhishing-Page", "true")
                .build()
            cadena.proceed(peticion)
        }
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val usuarioApi: UsuarioApi by lazy { retrofit.create(UsuarioApi::class.java) }

    val turnoApi: TurnoApi by lazy { retrofit.create(TurnoApi::class.java) }

    val accesoApi: AccesoApi by lazy { retrofit.create(AccesoApi::class.java) }

    val compraApi: CompraApi by lazy { retrofit.create(CompraApi::class.java) }

    val reporteApi: ReporteApi by lazy { retrofit.create(ReporteApi::class.java) }

    fun urlCompleta(ruta: String?): String? = ruta?.let { URL_BASE + it }
}
