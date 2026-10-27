package com.example.rentail_conteo.model

data class AccesoItem(
    val id: Int,
    val codigo: String,
    val nombre: String
)

data class DivisionUIState (
    val accesosDisponibles: List<AccesoItem> = emptyList(),
    val accesoSeleccionado: AccesoItem? = null,
    val errorMessage: String? = null
)
