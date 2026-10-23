package com.example.rentail_conteo.model

// Una fila del resumen: para un grupo (ej. "Adulto (26-40)"), cuántos entraron y cuántos compraron.
data class FilaIndicador(
    val grupo: String,
    val ingresos: Int,
    val compradores: Int
) {
    val noCompradores: Int get() = ingresos - compradores

    // % de los que entraron que terminaron comprando (0 si no entró nadie, para no dividir por 0)
    val conversion: Int get() = if (ingresos == 0) 0 else compradores * 100 / ingresos
}

// Estado de la pantalla de indicadores de la JEFATURA
data class IndicadoresUIState(
    val acceso: AccesoItem? = null,           // null = todos los accesos
    val soloHoy: Boolean = false,             // false = todo el registro
    val ingresos: Int = 0,
    val compradores: Int = 0,
    val porRango: List<FilaIndicador> = emptyList(),
    val perfilQueMasCompra: String? = null,   // el rango etario con mejor % de conversión
    val perfilQueNoCompra: String? = null,    // el rango etario con más personas que NO compraron
    val conversionSolos: Int = 0,
    val conversionAcompanados: Int = 0
) {
    val noCompradores: Int get() = ingresos - compradores
    val conversion: Int get() = if (ingresos == 0) 0 else compradores * 100 / ingresos
}
