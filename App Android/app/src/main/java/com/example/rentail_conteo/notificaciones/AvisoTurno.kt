package com.example.rentail_conteo.notificaciones

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.rentail_conteo.MainActivity
import com.example.rentail_conteo.R
import com.example.rentail_conteo.data.RinconSurDbHelper
import com.example.rentail_conteo.data.SesionPreferencias
import com.example.rentail_conteo.model.Franjas
import com.example.rentail_conteo.model.Turno
import com.example.rentail_conteo.permisos.Permisos
import com.example.rentail_conteo.util.Fechas

object AvisoTurno {
    private const val CANAL = "fin_de_turno"
    private const val AVISO_DESDE = 15
    private const val VENTANA = 10
    const val EXTRA_ID_TURNO = "id_turno"

    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(CANAL, "Fin de turno", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Avisa cuando tu turno está por terminar y quién te reemplaza"
            }
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
        }
    }

    fun programarDeHoy(context: Context, turnos: List<Turno>) {
        turnos.forEach { programar(context, it) }
    }

    private fun programar(context: Context, turno: Turno) {
        val fin = Fechas.aMillisLocal("${turno.fecha} ${Franjas.horaFin(turno.franja)}")
        val desde = fin - AVISO_DESDE * 60_000L
        if (desde <= System.currentTimeMillis()) return

        val alarmas = context.getSystemService(AlarmManager::class.java)
        alarmas.setWindow(AlarmManager.RTC_WAKEUP, desde, VENTANA * 60_000L, intentAlarma(context, turno.id))
    }

    fun cancelarDeHoy(context: Context, turnos: List<Turno>) {
        val alarmas = context.getSystemService(AlarmManager::class.java)
        turnos.forEach { alarmas.cancel(intentAlarma(context, it.id)) }
    }

    private fun intentAlarma(context: Context, idTurno: Long): PendingIntent {
        val intent = Intent(context, AvisoTurnoReceiver::class.java).putExtra(EXTRA_ID_TURNO, idTurno)
        return PendingIntent.getBroadcast(
            context, idTurno.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun mostrar(context: Context, idTurno: Long): Boolean {
        val db = RinconSurDbHelper.obtener(context)
        val turno = db.buscarTurno(idTurno) ?: return false
        if (turno.idUsuario != SesionPreferencias(context).leer()) return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !Permisos.tiene(context, Permisos.NOTIFICACIONES)
        ) return false

        val siguiente = Franjas.siguiente(turno.franja)
        val reemplazo = siguiente?.let { db.reemplazo(turno.puesto, turno.fecha, it) }
        val detalle = when {
            reemplazo != null -> "Te reemplaza ${reemplazo.nombreTrabajador}. Entrégale el puesto antes de irte."
            siguiente != null -> "Nadie tiene este puesto en la tarde: avisa a tu supervisor antes de irte."
            else -> "Es el cierre del día: termina de registrar antes de irte."
        }

        crearCanal(context)
        val notificacion = NotificationCompat.Builder(context, CANAL)
            .setSmallIcon(R.drawable.ic_campana)
            .setContentTitle("Tu turno en ${turno.puestoLegible()} termina a las ${Franjas.horaFin(turno.franja)}")
            .setContentText(detalle)
            .setStyle(NotificationCompat.BigTextStyle().bigText(detalle))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(intentAbrirApp(context))
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(idTurno.toInt(), notificacion)
        return true
    }

    private fun intentAbrirApp(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }
}
