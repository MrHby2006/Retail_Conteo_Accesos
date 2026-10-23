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
import com.example.rentail_conteo.model.Franjas
import com.example.rentail_conteo.model.Turno
import com.example.rentail_conteo.permisos.Permisos
import com.example.rentail_conteo.util.Fechas

// RECURSO NATIVO: notificaciones del sistema + AlarmManager.
//
// Para qué: el conteo solo sirve si CADA puerta tiene a alguien anotando todo el tiempo.
// Si el observador se va sin entregar su puerta, las personas que entran en ese rato no quedan
// registradas (el conteo sale incompleto y el % de compradores sale inflado).
// Por eso, poco antes de que termine el turno (entre 15 y 5 minutos antes), el teléfono avisa quién lo reemplaza.
//
//   Inicio de sesión ──programar()──▶ AlarmManager ──(a la hora)──▶ AvisoTurnoReceiver ──mostrar()──▶ notificación
object AvisoTurno {

    private const val CANAL = "fin_de_turno"
    private const val AVISO_DESDE = 15                 // la ventana del aviso parte 15 min antes del fin...
    private const val VENTANA = 10                     // ...y dura 10 min (Android no acepta ventanas más cortas)
    const val EXTRA_ID_TURNO = "id_turno"

    // Android 8+ exige un "canal" para cada tipo de notificación (el usuario puede silenciarlo en Ajustes).
    // Crearlo varias veces no hace nada malo: si ya existe, se ignora.
    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(CANAL, "Fin de turno", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Avisa cuando tu turno está por terminar y quién te reemplaza"
            }
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
        }
    }

    // ---------------------------- PROGRAMAR ----------------------------

    // Deja programado el aviso de cada turno de hoy que todavía no termina
    fun programarDeHoy(context: Context, turnos: List<Turno>) {
        turnos.forEach { programar(context, it) }
    }

    private fun programar(context: Context, turno: Turno) {
        val fin = Fechas.aMillisLocal("${turno.fecha} ${Franjas.horaFin(turno.franja)}")
        val desde = fin - AVISO_DESDE * 60_000L
        if (desde <= System.currentTimeMillis()) return           // ese momento ya pasó

        val alarmas = context.getSystemService(AlarmManager::class.java)
        // setWindow: Android puede mover la alarma, pero SOLO dentro de la ventana [desde, desde + VENTANA].
        // Con 15 y 10 minutos, el aviso llega entre 15 y 5 minutos antes del fin: nunca después de que terminó.
        // (Una alarma "aproximada" normal podía atrasarse hasta 1 hora; una exacta pide un permiso especial.)
        alarmas.setWindow(AlarmManager.RTC_WAKEUP, desde, VENTANA * 60_000L, intentAlarma(context, turno.id))
    }

    // Al cerrar sesión: se borran los avisos pendientes de ese trabajador
    fun cancelarDeHoy(context: Context, turnos: List<Turno>) {
        val alarmas = context.getSystemService(AlarmManager::class.java)
        turnos.forEach { alarmas.cancel(intentAlarma(context, it.id)) }
    }

    // PendingIntent = "un Intent para usar más tarde". Lo dispara el AlarmManager, no la app.
    // requestCode = id del turno: así cada turno tiene su propia alarma y no se pisan.
    private fun intentAlarma(context: Context, idTurno: Long): PendingIntent {
        val intent = Intent(context, AvisoTurnoReceiver::class.java).putExtra(EXTRA_ID_TURNO, idTurno)
        return PendingIntent.getBroadcast(
            context, idTurno.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    // ---------------------------- MOSTRAR ----------------------------

    // Arma el texto con lo que hay en la BD EN ESE MOMENTO (si el supervisor cambió el reemplazo, sale el nuevo).
    // Devuelve false si no se pudo mostrar (sin permiso o el turno ya no existe).
    fun mostrar(context: Context, idTurno: Long): Boolean {
        val db = RinconSurDbHelper.obtener(context)
        val turno = db.buscarTurno(idTurno) ?: return false       // el supervisor lo quitó
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
            .setStyle(NotificationCompat.BigTextStyle().bigText(detalle))   // texto largo sin cortarse
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(intentAbrirApp(context))                      // al tocarla, abre la app
            .setAutoCancel(true)                                            // y desaparece
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
