package com.example.rentail_conteo.model

// Estado de la pantalla del observador en la puerta (portón).
// El acceso se mantiene elegido entre un registro y otro: el observador está fijo en su puerta.
data class PortonUIState(
    val accesos: List<AccesoItem> = emptyList(),
    val turnoHoy: String? = null,         // "Mañana · Puerta A4" (lo asignó el supervisor)
    val accesoElegido: AccesoItem? = null,
    val rangoEtario: String? = null,
    val altura: String? = null,
    val colorRopa: String? = null,        // parte de arriba
    val colorRopaAbajo: String? = null,   // parte de abajo
    val acompanado: Boolean = false,
    val detalle: String = "",
    val registradosHoy: Int = 0,          // cuántos lleva hoy en este acceso
    val errorAcceso: String? = null,
    val errorRango: String? = null,
    val errorAltura: String? = null,
    val errorColor: String? = null,
    val errorColorAbajo: String? = null,
    val errorDetalle: String? = null,
    val mensaje: String? = null           // "Ingreso registrado" (se muestra una vez)
)
