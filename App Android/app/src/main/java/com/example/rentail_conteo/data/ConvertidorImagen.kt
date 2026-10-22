package com.example.rentail_conteo.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import java.io.File

// Fotos de la boleta.
//   Cámara  -> Bitmap ─┐
//                       ├─> archivo JPEG en la carpeta privada de la app -> su RUTA se guarda en SQLite
//   Galería -> Uri ────┘
// La foto NO se guarda dentro de la tabla: la BD se pone lenta con imágenes. Solo se guarda dónde quedó.
object ConvertidorImagen {

    private const val LADO_MAXIMO = 1280
    private const val CALIDAD_JPEG = 85

    // Galería: la galería entrega un Uri (una "dirección" de la imagen); aquí se convierte en Bitmap
    fun desdeUri(context: Context, uri: Uri): Bitmap? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val origen = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(origen) { decoder, _, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            context.contentResolver.openInputStream(uri).use { flujo -> BitmapFactory.decodeStream(flujo) }
        }
    } catch (e: Exception) {
        null     // archivo dañado o que no es imagen
    }

    // Guarda el Bitmap como JPEG en filesDir (carpeta privada: otras apps no la pueden leer).
    // Devuelve el archivo creado, o null si algo falló.
    fun guardar(context: Context, bitmap: Bitmap, nombre: String): File? = try {
        val archivo = File(context.filesDir, nombre)
        archivo.outputStream().use { salida ->
            achicar(bitmap).compress(Bitmap.CompressFormat.JPEG, CALIDAD_JPEG, salida)
        }
        archivo
    } catch (e: Exception) {
        null
    }

    // Si la foto es muy grande se achica, manteniendo la proporción (no se deforma)
    private fun achicar(bitmap: Bitmap): Bitmap {
        val ladoMayor = maxOf(bitmap.width, bitmap.height)
        if (ladoMayor <= LADO_MAXIMO) return bitmap
        val escala = LADO_MAXIMO.toFloat() / ladoMayor
        return Bitmap.createScaledBitmap(
            bitmap, (bitmap.width * escala).toInt(), (bitmap.height * escala).toInt(), true
        )
    }
}
