package com.example.rentail_conteo.model

// Estado de la pantalla del observador en la puerta (portón).
// El acceso se mantiene elegido entre un registro y otro: el observador está fijo en su puerta.
data class PortonUIState(
    val accesos: List<AccesoItem> = emptyList(),
    val accesoElegido: AccesoItem? = null,
    val rangoEtario: String? = null,
    val altura: String? = null,
    val colorRopa: String? = null,
    val acompanado: Boolean = false,
    val detalle: String = "",
    val registradosHoy: Int = 0,          // cuántos lleva hoy en este acceso
    val errorAcceso: String? = null,
    val errorRango: String? = null,
    val errorAltura: String? = null,
    val errorColor: String? = null,
    val errorDetalle: String? = null,
    val mensaje: String? = null           // "Ingreso registrado" (se muestra una vez)
)
