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
    val cargando: Boolean = false,
    val error: String? = null,
    val ingresos: Int = 0,
    val compradores: Int = 0,
    val porRango: List<FilaIndicador> = emptyList(),
    val porAcceso: List<FilaIndicador> = emptyList(),
    val accesoMasDePaso: FilaIndicador? = null,
    val perfilQueMasCompra: String? = null,
    val perfilQueNoCompra: String? = null,
    val metaMes: Double? = null,
    val cumpleMeta: Boolean? = null
) {
    val noCompradores: Int get() = ingresos - compradores
    val conversion: Int get() = if (ingresos == 0) 0 else compradores * 100 / ingresos
}
