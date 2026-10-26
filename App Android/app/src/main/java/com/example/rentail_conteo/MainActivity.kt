package com.example.rentail_conteo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.rentail_conteo.navigation.AppNavegacion
import com.example.rentail_conteo.ui.theme.Rentail_ConteoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Rentail_ConteoTheme {
                AppNavegacion()
            }
        }
    }
}
