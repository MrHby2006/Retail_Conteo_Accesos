package com.example.rentail_conteo.model

import android.graphics.Bitmap

// Estado de la pantalla del cajero.
// Tiene dos "modos":
//   personaElegida == null -> buscar: filtros + lista de personas que entraron y aún no compran
//   personaElegida != null -> confirmar: datos opcionales del cliente + foto de la boleta
data class RegistrosClientesUIState(
    // Filtros (null = sin filtro). Solo lo que el cajero puede VER de la persona.
    val filtroRango: String? = null,
    val filtroColor: String? = null,        // ropa de arriba
    val filtroColorAbajo: String? = null,   // ropa de abajo
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
