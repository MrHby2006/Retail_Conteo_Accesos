package com.example.rentail_conteo.model

data class ListaTrabajadoresUIState(
    val trabajadores: List<Usuario> = emptyList(),
    val trabajadorAEliminar: Usuario? = null,
    val mensaje: String? = null
)