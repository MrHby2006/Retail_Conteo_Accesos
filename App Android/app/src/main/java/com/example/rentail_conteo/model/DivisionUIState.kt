package com.example.rentail_conteo.model

data class AccesoItem(
    val id: Int,
    val codigo: String,
    val nombre: String
)

data class DivisionUIState (
    val accesosDisponibles: List<AccesoItem> = listOf(
        AccesoItem(1, "A1", "Acceso Principal"),
        AccesoItem(2, "A2", "Acceso Lateral"),
        AccesoItem(3, "A3", "Acceso Estacionamiento"),
        AccesoItem(4, "A4", "Acceso Conexión")
    ),
    val accesoSeleccionado: AccesoItem? = null,
    val anioSeleccionado: Int = 2026,
    val mesSeleccionado: String = "Oct",
    val diaSeleccionado: Int? = 12,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)