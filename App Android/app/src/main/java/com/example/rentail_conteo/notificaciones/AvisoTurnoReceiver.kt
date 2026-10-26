package com.example.rentail_conteo.notificaciones

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AvisoTurnoReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val idTurno = intent.getLongExtra(AvisoTurno.EXTRA_ID_TURNO, -1L)
        if (idTurno != -1L) AvisoTurno.mostrar(context, idTurno)
    }
}
