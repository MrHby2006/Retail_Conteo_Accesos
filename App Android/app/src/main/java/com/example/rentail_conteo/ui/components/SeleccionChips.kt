package com.example.rentail_conteo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rentail_conteo.model.Opciones
import com.example.rentail_conteo.ui.theme.Dimens
import com.example.rentail_conteo.ui.theme.Rentail_ConteoTheme

// Un título + un grupo de "chips" (botoncitos) para elegir UNA opción tocándola.
// FlowRow = una fila que salta a la línea siguiente cuando ya no caben más chips.
// Es genérico (<T>): sirve para textos (rango etario, color) y para accesos.
// error: mensaje rojo debajo (null = sin error).
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> SeleccionChips(
    titulo: String,
    opciones: List<T>,
    seleccionada: T?,
    onSeleccionar: (T) -> Unit,
    textoDe: (T) -> String,
    modifier: Modifier = Modifier,
    error: String? = null
) {
    Column(modifier = modifier) {
        Text(titulo, style = MaterialTheme.typography.titleSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
            opciones.forEach { opcion ->
                FilterChip(
                    selected = opcion == seleccionada,
                    onClick = { onSeleccionar(opcion) },
                    label = { Text(textoDe(opcion)) }
                )
            }
        }
        if (error != null) {
            Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SeleccionChipsPreview() {
    Rentail_ConteoTheme {
        SeleccionChips(
            titulo = "Rango de edad",
            opciones = Opciones.rangosEtarios,
            seleccionada = null,
            onSeleccionar = {},
            textoDe = { it },
            error = "Elige un rango de edad",
            modifier = Modifier.padding(16.dp)
        )
    }
}
