package com.example.rentail_conteo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rentail_conteo.ui.theme.Dimens
import com.example.rentail_conteo.ui.theme.Rentail_ConteoTheme

@Composable
fun InterruptorConTexto(
    texto: String,
    activado: Boolean,
    onCambio: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
       modifier = modifier
           .fillMaxWidth()
           .toggleable(value = activado, onValueChange = onCambio, role = Role.Switch)
           .padding(vertical = Dimens.espacioMini),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = texto, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Switch(checked = activado, onCheckedChange = null)
    }
}

@Preview(showBackground = true)
@Composable
private fun InterruptorConTextoPreview() {
    Rentail_ConteoTheme {
        InterruptorConTexto(
            texto = "¿Viene acompañado?",
            activado = false,
            onCambio = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
