package com.example.rentail_conteo.model

import android.graphics.Bitmap

// Estado de la pantalla del cajero.
// Tiene dos "modos":
//   personaElegida == null -> buscar: filtros + lista de personas que entraron y aún no compran
//   personaElegida != null -> confirmar: datos opcionales del cliente + foto de la boleta
data class RegistrosClientesUIState(
    val accesos: List<AccesoItem> = emptyList(),
    // Filtros (null = sin filtro)
    val filtroAcceso: AccesoItem? = null,
    val filtroRango: String? = null,
    val filtroColor: String? = null,
    val candidatos: List<PersonaIngreso> = emptyList(),
    // Confirmar compra
    val personaElegida: PersonaIngreso? = null,
    val nombreCliente: String = "",
    val correoCliente: String = "",
    val fotoBoleta: Bitmap? = null,
    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorFoto: String? = null,
    val mensaje: String? = null
)
