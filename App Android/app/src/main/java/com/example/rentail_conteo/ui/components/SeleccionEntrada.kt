package com.example.rentail_conteo.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rentail_conteo.ui.theme.Dimens
import com.example.rentail_conteo.ui.theme.Rentail_ConteoTheme

private val texto: String = "Entrada utilizada"

@Composable
fun <T> SeleccionEntrada(
    opciones: List<T>,
    seleccionada: T,
    onSeleccionar: (T) -> Unit,
    textoDe: (T) -> String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.selectableGroup()) {
        opciones.forEach { opcion ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = opcion == seleccionada,
                        onClick = { onSeleccionar(opcion) },
                        role = Role.RadioButton
                    )
                    .padding(vertical = Dimens.espacioMini),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = opcion == seleccionada, onClick = null)
                Text(
                    text = textoDe(opcion),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = Dimens.espacioChico)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SeleccionEntradaPreview() {
    Rentail_ConteoTheme {
        Column(Modifier.padding(16.dp)) {
            texto
            SeleccionEntrada(
                opciones = listOf("A1", "A2", "A3", "A4"),
                seleccionada = "A3",
                onSeleccionar = {},
                textoDe = { it }
            )
        }
    }
}