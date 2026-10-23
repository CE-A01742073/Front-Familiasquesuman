package com.familiasquesuman.demo.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.widget.Toast
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

// Todo lo que te saca de la app (WhatsApp, maps, correo, calendario)
private fun Context.abrir(intent: Intent) {
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, "No hay una app para abrir esto", Toast.LENGTH_SHORT).show()
    }
}

fun Context.llamar(telefono: String) =
    abrir(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$telefono")))

fun Context.whatsapp(numero: String) =
    abrir(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/52$numero")))

fun Context.comoLlegar(direccion: String) =
    abrir(Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + Uri.encode(direccion))))

fun Context.correo(destinatario: String, asunto: String, cuerpo: String) =
    abrir(
        Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(destinatario))
            putExtra(Intent.EXTRA_SUBJECT, asunto)
            putExtra(Intent.EXTRA_TEXT, cuerpo)
        }
    )

// RF-07: abre el calendario del teléfono con el evento prellenado (no requiere permisos)
fun Context.agregarAlCalendario(titulo: String, lugar: String, fechaIso: String, horaInicio: String, horaFin: String?) {
    val zona = ZoneId.systemDefault()
    val fecha = runCatching { LocalDate.parse(fechaIso) }.getOrNull() ?: return
    val inicio = runCatching { LocalTime.parse(horaInicio.take(5)) }.getOrDefault(LocalTime.of(9, 0))
    val fin = horaFin?.let { runCatching { LocalTime.parse(it.take(5)) }.getOrNull() } ?: inicio.plusHours(2)
    val ms = { h: LocalTime -> fecha.atTime(h).atZone(zona).toInstant().toEpochMilli() }
    abrir(
        Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, titulo)
            putExtra(CalendarContract.Events.EVENT_LOCATION, lugar)
            putExtra(CalendarContract.Events.DESCRIPTION, "Actividad de voluntariado con Familias que Suman")
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, ms(inicio))
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, ms(fin))
        }
    )
}
