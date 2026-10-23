package com.example.rentail_conteo.notificaciones

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

// BroadcastReceiver: código que Android ejecuta cuando llega un "aviso" (aquí, la alarma del AlarmManager),
// aunque la app esté cerrada. Solo lee qué turno era y muestra la notificación.
// Está declarado en AndroidManifest.xml (<receiver>), si no Android no lo encuentra.
class AvisoTurnoReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val idTurno = intent.getLongExtra(AvisoTurno.EXTRA_ID_TURNO, -1L)
        if (idTurno != -1L) AvisoTurno.mostrar(context, idTurno)
    }
}
