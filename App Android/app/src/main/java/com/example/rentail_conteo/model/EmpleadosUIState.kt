package com.example.rentail_conteo.model

data class CuentaCreada(
    val titulo: String,
    val nombre: String,
    val correoEmpresa: String,
    val contrasenaTemporal: String,
    val correoPersonal: String,
    val correoEnviado: Boolean
)

data class EmpleadosUIState(
    val empleados: List<Usuario> = emptyList(),
    val cargando: Boolean = false,
    val mostrandoFormulario: Boolean = false,
    val enEdicion: Usuario? = null,
    val nombres: String = "",
    val apellidoPaterno: String = "",
    val apellidoMaterno: String = "",
    val rut: String = "",
    val cargo: String = "",
    val rolElegido: Long? = null,
    val correoPersonal: String = "",
    val correoEmpresaSugerido: String = "",
    val errorNombres: String? = null,
    val errorApellidoPaterno: String? = null,
    val errorApellidoMaterno: String? = null,
    val errorRut: String? = null,
    val errorCargo: String? = null,
    val errorRol: String? = null,
    val errorCorreoPersonal: String? = null,
    val cuentaCreada: CuentaCreada? = null,
    val porDesactivar: Usuario? = null,
    val mensaje: String? = null
)
