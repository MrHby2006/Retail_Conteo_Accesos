package com.example.rentail_conteo.model

import android.graphics.Bitmap

data class RegistrosClientesUIState(
    val filtroRango: String? = null,
    val filtroColor: String? = null,
    val filtroColorAbajo: String? = null,
    val candidatos: List<PersonaIngreso> = emptyList(),
    val personaElegida: PersonaIngreso? = null,
    val nombreCliente: String = "",
    val correoCliente: String = "",
    val fotoBoleta: Bitmap? = null,
    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorFoto: String? = null,
    val mensaje: String? = null
)
