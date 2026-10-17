package com.example.rentail_conteo.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rentail_conteo.R
import com.example.rentail_conteo.ui.styles.EstiloCampo
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

@Composable
fun CampoAcompaniante(
    valor: String,
    onValorCambia: (String) -> Unit,
    @DrawableRes icono: Int,
    modifier: Modifier = Modifier,
    error: String? = null,
    tipoTeclado: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorCambia,
        modifier = modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(
                painter = painterResource(id = icono),
                contentDescription = null,
                modifier = Modifier.size(Dimens.tamanoIcono)
            )
        },
        isError = error != null,
        supportingText = { if (error != null) Text(error) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = tipoTeclado,
            imeAction = ImeAction.Next
        ),
        shape = EstiloCampo.forma,
        colors = EstiloCampo.colores()
    )
}

@Preview(showBackground = true)
@Composable
private fun CampoAcompanianteDesactivadoPreview() {
    Rentail_ConteoTheme {
        Column(Modifier.padding(16.dp)) {
            InterruptorConTexto(texto = "Tiene acompañante?", activado = false, onCambio = {})
            CampoAcompaniante(
                valor = "",
                onValorCambia = {},
                icono = R.drawable.ic_persona,
                error = null,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}