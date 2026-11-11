package com.example.rentail_conteo.util

import java.security.MessageDigest
import java.security.SecureRandom

object Seguridad {
    private const val LETRAS = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ"
    private const val NUMEROS = "23456789"
    private val aleatorio = SecureRandom()

    fun nuevaSal(): String {
        val bytes = ByteArray(16)
        aleatorio.nextBytes(bytes)
        return aHex(bytes)
    }

    fun hash(contrasena: String, sal: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return aHex(digest.digest((sal + contrasena).toByteArray(Charsets.UTF_8)))
    }

    fun coincide(contrasena: String, sal: String, hashGuardado: String): Boolean =
        hash(contrasena, sal) == hashGuardado

    fun contrasenaTemporal(): String {
        val caracteres = mutableListOf<Char>()
        repeat(6) { caracteres.add(LETRAS[aleatorio.nextInt(LETRAS.length)]) }
        repeat(2) { caracteres.add(NUMEROS[aleatorio.nextInt(NUMEROS.length)]) }
        caracteres.shuffle(aleatorio)
        return caracteres.joinToString("")
    }

    fun errorContrasenaNueva(contrasena: String): String? = when {
        contrasena.isBlank() -> "La contraseña es obligatoria"
        contrasena.contains(" ") -> "No puede tener espacios"
        contrasena.length < 8 -> "Mínimo 8 caracteres"
        contrasena.none { it.isLetter() } -> "Debe tener al menos una letra"
        contrasena.none { it.isDigit() } -> "Debe tener al menos un número"
        else -> null
    }

    private fun aHex(bytes: ByteArray): String = bytes.joinToString("") { "%02x".format(it) }
}
