package com.example.rentail_conteo.model

data class CuentaCreada(
    val nombre: String,
    val correoEmpresa: String,
    val contrasenaTemporal: String
)

data class EmpleadosUIState(
    val empleados: List<Usuario> = emptyList(),
    val mostrandoFormulario: Boolean = false,
    val nombre: String = "",
    val rut: String = "",
    val cargo: String = "",
    val rolElegido: Long? = null,
    val correoPersonal: String = "",
    val correoEmpresaSugerido: String = "",
    val errorNombre: String? = null,
    val errorRut: String? = null,
    val errorCargo: String? = null,
    val errorRol: String? = null,
    val errorCorreoPersonal: String? = null,
    val cuentaCreada: CuentaCreada? = null,
    val porDesactivar: Usuario? = null,
    val mensaje: String? = null
)
