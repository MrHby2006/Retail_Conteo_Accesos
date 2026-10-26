package com.example.rentail_conteo.model

data class FilaIndicador(
    val grupo: String,
    val ingresos: Int,
    val compradores: Int
) {
    val noCompradores: Int get() = ingresos - compradores

    val conversion: Int get() = if (ingresos == 0) 0 else compradores * 100 / ingresos
}

data class IndicadoresUIState(
    val acceso: AccesoItem? = null,
    val soloHoy: Boolean = false,
    val ingresos: Int = 0,
    val compradores: Int = 0,
    val porRango: List<FilaIndicador> = emptyList(),
    val perfilQueMasCompra: String? = null,
    val perfilQueNoCompra: String? = null,
    val conversionSolos: Int = 0,
    val conversionAcompanados: Int = 0
) {
    val noCompradores: Int get() = ingresos - compradores
    val conversion: Int get() = if (ingresos == 0) 0 else compradores * 100 / ingresos
}
