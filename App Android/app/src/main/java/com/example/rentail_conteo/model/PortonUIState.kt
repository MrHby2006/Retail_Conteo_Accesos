package com.example.rentail_conteo.model

data class PortonUIState(
    val accesos: List<AccesoItem> = emptyList(),
    val rangos: List<Opcion> = emptyList(),
    val alturas: List<Opcion> = emptyList(),
    val colores: List<Opcion> = emptyList(),
    val companias: List<Opcion> = emptyList(),
    val turnoHoy: String? = null,
    val accesoElegido: AccesoItem? = null,
    val accesoFijo: Boolean = false,
    val rangoEtario: Opcion? = null,
    val altura: Opcion? = null,
    val colorRopa: Opcion? = null,
    val colorRopaAbajo: Opcion? = null,
    val compania: Opcion? = null,
    val detalle: String = "",
    val registradosHoy: Int = 0,
    val guardando: Boolean = false,
    val errorAcceso: String? = null,
    val errorRango: String? = null,
    val errorAltura: String? = null,
    val errorColor: String? = null,
    val errorColorAbajo: String? = null,
    val errorCompania: String? = null,
    val errorDetalle: String? = null,
    val mensaje: String? = null
)
