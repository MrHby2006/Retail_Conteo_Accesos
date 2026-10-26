package com.example.rentail_conteo.model

data class PortonUIState(
    val accesos: List<AccesoItem> = emptyList(),
    val turnoHoy: String? = null,
    val accesoElegido: AccesoItem? = null,
    val rangoEtario: String? = null,
    val altura: String? = null,
    val colorRopa: String? = null,
    val colorRopaAbajo: String? = null,
    val acompanado: Boolean = false,
    val detalle: String = "",
    val registradosHoy: Int = 0,
    val errorAcceso: String? = null,
    val errorRango: String? = null,
    val errorAltura: String? = null,
    val errorColor: String? = null,
    val errorColorAbajo: String? = null,
    val errorDetalle: String? = null,
    val mensaje: String? = null
)
