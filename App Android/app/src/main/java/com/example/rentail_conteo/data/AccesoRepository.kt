package com.example.rentail_conteo.data

import com.example.rentail_conteo.data.remote.AccesoApi
import com.example.rentail_conteo.data.remote.CatalogosResponse
import com.example.rentail_conteo.data.remote.EstadoAccesoRequest
import com.example.rentail_conteo.data.remote.IngresoRequest
import com.example.rentail_conteo.data.remote.RetrofitClient
import com.example.rentail_conteo.data.remote.aAccesoItem
import com.example.rentail_conteo.data.remote.aPersona
import com.example.rentail_conteo.data.remote.llamarApi
import com.example.rentail_conteo.model.AccesoItem
import com.example.rentail_conteo.model.PersonaIngreso

class AccesoRepository(
    private val api: AccesoApi = RetrofitClient.accesoApi
) {

    suspend fun listar(soloActivos: Boolean = false): List<AccesoItem> =
        llamarApi { api.listar(soloActivos) }.map { it.aAccesoItem() }

    suspend fun cambiarEstado(idAcceso: Int, activo: Boolean): AccesoItem =
        llamarApi { api.cambiarEstado(idAcceso.toLong(), EstadoAccesoRequest(activo)) }.aAccesoItem()

    suspend fun catalogos(): CatalogosResponse = llamarApi { api.catalogos() }

    suspend fun registrarIngreso(datos: IngresoRequest): PersonaIngreso =
        llamarApi { api.registrar(datos) }.aPersona()

    suspend fun contarDelDia(idAcceso: Long, fecha: String): Int =
        llamarApi { api.contar(idAcceso, fecha) }

    suspend fun candidatos(
        idRango: Long?, idColorSuperior: Long?, idColorInferior: Long?,
        idAcceso: Long?, fecha: String?, franja: String?
    ): List<PersonaIngreso> =
        llamarApi { api.candidatos(idRango, idColorSuperior, idColorInferior, idAcceso, fecha, franja) }
            .map { it.aPersona() }
}
