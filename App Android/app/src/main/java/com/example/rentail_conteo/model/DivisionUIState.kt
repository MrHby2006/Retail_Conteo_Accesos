package com.example.rentail_conteo.model

data class AccesoItem(
    val id: Int,
    val codigo: String,
    val nombre: String
)

// Los accesos ya no van escritos aquí: DivisionViewModel los lee desde la BD (tabla "accesos")
data class DivisionUIState (
    val accesosDisponibles: List<AccesoItem> = emptyList(),
    val accesoSeleccionado: AccesoItem? = null,
    val anioSeleccionado: Int = 2026,
    val mesSeleccionado: String = "Oct",
    val diaSeleccionado: Int? = 12,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
