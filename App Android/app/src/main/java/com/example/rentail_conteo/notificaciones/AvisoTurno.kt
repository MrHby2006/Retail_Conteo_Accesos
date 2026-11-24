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
import androidx.core.content.edit
import com.example.rentail_conteo.MainActivity
import com.example.rentail_conteo.R
import com.example.rentail_conteo.data.SesionPreferencias
import com.example.rentail_conteo.model.Franjas
import com.example.rentail_conteo.model.Turno
import com.example.rentail_conteo.permisos.Permisos
import com.example.rentail_conteo.util.Fechas

object AvisoTurno {
    private const val CANAL = "fin_de_turno"
    private const val AVISO_DESDE = 15
    private const val VENTANA = 10
    private const val PREFERENCIAS = "avisos_turno"
    private const val CLAVE_IDS = "ids_programados"
    const val EXTRA_ID_TURNO = "id_turno"
    const val EXTRA_ID_USUARIO = "id_usuario"
    const val EXTRA_TITULO = "titulo"
    const val EXTRA_DETALLE = "detalle"

    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(CANAL, "Fin de turno", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Avisa cuando tu turno está por terminar y quién te reemplaza"
            }
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
        }
    }

    fun programarDeHoy(context: Context, misTurnos: List<Turno>, turnosDelDia: List<Turno>) {
        misTurnos.forEach { programar(context, it, turnosDelDia) }
    }

    private fun programar(context: Context, turno: Turno, turnosDelDia: List<Turno>) {
        val fin = Fechas.aMillisLocal("${turno.fecha} ${Franjas.horaFin(turno.franja)}")
        val desde = fin - AVISO_DESDE * 60_000L
        if (desde <= System.currentTimeMillis()) return

        val (titulo, detalle) = textos(turno, turnosDelDia)
        val alarmas = context.getSystemService(AlarmManager::class.java)
        alarmas.setWindow(
            AlarmManager.RTC_WAKEUP, desde, VENTANA * 60_000L,
            intentAlarma(context, turno.id, turno.idUsuario, titulo, detalle)
        )
        guardarId(context, turno.id)
    }

    fun textos(turno: Turno, turnosDelDia: List<Turno>): Pair<String, String> {
        val siguiente = Franjas.siguiente(turno.franja)
        val reemplazo = siguiente?.let { franja ->
            turnosDelDia.firstOrNull { it.puesto == turno.puesto && it.franja == franja }
        }
        val detalle = when {
            reemplazo != null -> "Te reemplaza ${reemplazo.nombreTrabajador}. Entrégale el puesto antes de irte."
            siguiente != null -> "Nadie tiene este puesto en la tarde: avisa a tu supervisor antes de irte."
            else -> "Es el cierre del día: termina de registrar antes de irte."
        }
        val titulo = "Tu turno en ${turno.puestoLegible()} termina a las ${Franjas.horaFin(turno.franja)}"
        return titulo to detalle
    }

    fun cancelarTodos(context: Context) {
        val preferencias = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE)
        val alarmas = context.getSystemService(AlarmManager::class.java)
        preferencias.getStringSet(CLAVE_IDS, emptySet()).orEmpty().forEach { id ->
            alarmas.cancel(intentAlarma(context, id.toLong(), 0L, "", ""))
        }
        preferencias.edit { remove(CLAVE_IDS) }
    }

    private fun guardarId(context: Context, idTurno: Long) {
        val preferencias = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE)
        val ids = preferencias.getStringSet(CLAVE_IDS, emptySet()).orEmpty() + idTurno.toString()
        preferencias.edit { putStringSet(CLAVE_IDS, ids) }
    }

    private fun intentAlarma(
        context: Context, idTurno: Long, idUsuario: Long, titulo: String, detalle: String
    ): PendingIntent {
        val intent = Intent(context, AvisoTurnoReceiver::class.java)
            .putExtra(EXTRA_ID_TURNO, idTurno)
            .putExtra(EXTRA_ID_USUARIO, idUsuario)
            .putExtra(EXTRA_TITULO, titulo)
            .putExtra(EXTRA_DETALLE, detalle)
        return PendingIntent.getBroadcast(
            context, idTurno.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun mostrar(context: Context, idTurno: Long, idUsuario: Long, titulo: String, detalle: String): Boolean {
        if (idUsuario != SesionPreferencias(context).leer()) return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !Permisos.tiene(context, Permisos.NOTIFICACIONES)
        ) return false

        crearCanal(context)
        val notificacion = NotificationCompat.Builder(context, CANAL)
            .setSmallIcon(R.drawable.ic_campana)
            .setContentTitle(titulo)
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
