package com.example.rentail_conteo.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rentail_conteo.R
import com.example.rentail_conteo.ui.styles.EstiloCampo
import com.example.rentail_conteo.ui.theme.Dimens
import com.example.rentail_conteo.ui.theme.Rentail_ConteoTheme
@Composable
fun CampoReferencia(
    valor: String,
    onValorCambia: (String) -> Unit,
    etiqueta: String,
    @DrawableRes icono: Int,
    modifier: Modifier = Modifier,
    error: String? = null,
    tipoTeclado: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorCambia,
        modifier = modifier.fillMaxWidth(),
        label = { Text(etiqueta) },
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
private fun CampoReferenciaErrorPreview() {
    Rentail_ConteoTheme {
        CampoReferencia(
            valor = "",
            onValorCambia = {},
            etiqueta = "Referencia de cliente que entra",
            icono = R.drawable.ic_persona,
            error = "Ingrese una referencia",
            modifier = Modifier.padding(16.dp)
        )
    }
}