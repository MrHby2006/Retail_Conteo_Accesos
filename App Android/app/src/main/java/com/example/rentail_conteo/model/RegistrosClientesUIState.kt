package com.example.rentail_conteo.model

import android.graphics.Bitmap

data class RegistrosClientesUIState(
    val rangos: List<Opcion> = emptyList(),
    val colores: List<Opcion> = emptyList(),
    val filtroRango: Opcion? = null,
    val filtroColor: Opcion? = null,
    val filtroColorAbajo: Opcion? = null,
    val filtroAcceso: AccesoItem? = null,
    val filtroFecha: String? = null,
    val filtroFranja: String? = null,
    val accesosDisponibles: List<AccesoItem> = emptyList(),
    val candidatos: List<PersonaIngreso> = emptyList(),
    val cargando: Boolean = false,
    val personaElegida: PersonaIngreso? = null,
    val numeroBoleta: String = "",
    val nombreCliente: String = "",
    val correoCliente: String = "",
    val fotoBoleta: Bitmap? = null,
    val guardando: Boolean = false,
    val errorBoleta: String? = null,
    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorFoto: String? = null,
    val mensaje: String? = null
)
