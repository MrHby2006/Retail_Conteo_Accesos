package com.example.rentail_conteo.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.rentail_conteo.model.AccesoItem
import com.example.rentail_conteo.model.FilaIndicador
import com.example.rentail_conteo.model.PersonaIngreso
import com.example.rentail_conteo.model.SolicitudCambio
import com.example.rentail_conteo.model.Turno
import com.example.rentail_conteo.model.Usuario
import com.example.rentail_conteo.util.Fechas
import com.example.rentail_conteo.util.Seguridad

class RinconSurDbHelper(context: Context) :
    SQLiteOpenHelper(context, NOMBRE_BD, null, VERSION_BD) {
    companion object {
        private const val NOMBRE_BD = "rinconsur.db"
        private const val VERSION_BD = 6

        @Volatile private var instancia: RinconSurDbHelper? = null

        fun obtener(context: Context): RinconSurDbHelper =
            instancia ?: synchronized(this) {
                instancia ?: RinconSurDbHelper(context.applicationContext).also { instancia = it }
            }

        private const val SQL_CREAR_ROLES =
            "CREATE TABLE roles (" +
                    "id_rol INTEGER PRIMARY KEY, " +
                    "nombre_rol TEXT NOT NULL)"

        private const val SQL_CREAR_USUARIOS =
            "CREATE TABLE usuarios (" +
                    "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "codigo TEXT NOT NULL UNIQUE, " +
                    "nombre TEXT NOT NULL, " +
                    "rut TEXT NOT NULL UNIQUE, " +
                    "correo TEXT NOT NULL UNIQUE, " +
                    "contrasena TEXT NOT NULL, " +
                    "cargo TEXT NOT NULL, " +
                    "activo INTEGER NOT NULL DEFAULT 1, " +
                    "id_rol INTEGER NOT NULL REFERENCES roles(id_rol), " +
                    "correo_personal TEXT, " +
                    "sal TEXT NOT NULL DEFAULT '', " +
                    "debe_cambiar_contrasena INTEGER NOT NULL DEFAULT 0)"

        private const val SQL_CREAR_ACCESOS =
            "CREATE TABLE accesos (" +
                    "id_acceso INTEGER PRIMARY KEY, " +
                    "codigo TEXT NOT NULL UNIQUE, " +
                    "nombre TEXT NOT NULL, " +
                    "ubicacion TEXT NOT NULL, " +
                    "activada INTEGER NOT NULL DEFAULT 1)"

        private const val SQL_AGREGAR_ACTIVA_ACCESOS =
            "ALTER TABLE accesos ADD COLUMN activada INTEGER NOT NULL DEFAULT 1"

        private const val SQL_CREAR_PERSONAS =
            "CREATE TABLE personas_ingreso (" +
                    "id_persona INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_acceso INTEGER NOT NULL REFERENCES accesos(id_acceso), " +
                    "id_observador INTEGER NOT NULL REFERENCES usuarios(id_usuario), " +
                    "fecha_hora TEXT NOT NULL, " +
                    "rango_etario TEXT NOT NULL, " +
                    "altura TEXT NOT NULL, " +
                    "color_ropa TEXT NOT NULL, " +
                    "color_ropa_abajo TEXT NOT NULL, " +
                    "acompanado INTEGER NOT NULL DEFAULT 0, " +
                    "detalle TEXT NOT NULL DEFAULT '', " +
                    "compro INTEGER NOT NULL DEFAULT 0)"

        private const val SQL_CREAR_COMPRAS =
            "CREATE TABLE compras (" +
                    "id_compra INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_persona INTEGER NOT NULL UNIQUE REFERENCES personas_ingreso(id_persona), " +
                    "id_cajero INTEGER NOT NULL REFERENCES usuarios(id_usuario), " +
                    "fecha_hora TEXT NOT NULL, " +
                    "nombre_cliente TEXT, " +
                    "correo_cliente TEXT, " +
                    "ruta_boleta TEXT NOT NULL)"

        private const val SQL_CREAR_TURNOS =
            "CREATE TABLE turnos (" +
                    "id_turno INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_usuario INTEGER NOT NULL REFERENCES usuarios(id_usuario), " +
                    "id_supervisor INTEGER NOT NULL REFERENCES usuarios(id_usuario), " +
                    "fecha TEXT NOT NULL, " +
                    "franja TEXT NOT NULL, " +
                    "puesto TEXT NOT NULL, " +
                    "UNIQUE (id_usuario, fecha, franja))"

        private const val SQL_AGREGAR_ESTADO_TURNOS =
            "ALTER TABLE turnos ADD COLUMN estado TEXT NOT NULL DEFAULT 'PUBLICADO'"

        private const val SQL_CREAR_SOLICITUDES =
            "CREATE TABLE solicitudes_cambio (" +
                    "id_solicitud INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_turno INTEGER NOT NULL REFERENCES turnos(id_turno), " +
                    "id_supervisor INTEGER NOT NULL REFERENCES usuarios(id_usuario), " +
                    "id_usuario_nuevo INTEGER NOT NULL REFERENCES usuarios(id_usuario), " +
                    "franja_nueva TEXT NOT NULL, " +
                    "puesto_nuevo TEXT NOT NULL, " +
                    "acepta_titular INTEGER NOT NULL DEFAULT 0, " +
                    "acepta_nuevo INTEGER NOT NULL DEFAULT 0, " +
                    "estado TEXT NOT NULL DEFAULT 'PENDIENTE', " +
                    "fecha_creacion TEXT NOT NULL)"

        private const val SQL_AGREGAR_CORREO_PERSONAL =
            "ALTER TABLE usuarios ADD COLUMN correo_personal TEXT"

        private const val SQL_AGREGAR_SAL =
            "ALTER TABLE usuarios ADD COLUMN sal TEXT NOT NULL DEFAULT ''"

        private const val SQL_AGREGAR_DEBE_CAMBIAR =
            "ALTER TABLE usuarios ADD COLUMN debe_cambiar_contrasena INTEGER NOT NULL DEFAULT 0"

        private const val SQL_UNIR_OBSERVADOR_Y_CAJERO = "UPDATE usuarios SET id_rol = 3 WHERE id_rol = 4"

        private const val SQL_BORRAR_ROL_CAJERO = "DELETE FROM roles WHERE id_rol = 4"

        private const val SQL_RENOMBRAR_ADMINISTRADOR = "UPDATE roles SET nombre_rol = 'Administrador' WHERE id_rol = 1"

        private const val SQL_RENOMBRAR_TRABAJADOR = "UPDATE roles SET nombre_rol = 'Trabajador' WHERE id_rol = 3"

        private const val SELECT_TURNOS =
            "SELECT t.*, u.nombre AS nombre_trabajador, " +
                    "(SELECT COUNT(*) FROM solicitudes_cambio s WHERE s.id_turno = t.id_turno AND s.estado = 'PENDIENTE') AS cambios_pendientes " +
                    "FROM turnos t INNER JOIN usuarios u ON t.id_usuario = u.id_usuario "

        private const val SELECT_SOLICITUDES =
            "SELECT s.*, t.fecha, t.franja, t.puesto, t.id_usuario AS id_titular, " +
                    "ut.nombre AS nombre_titular, un.nombre AS nombre_nuevo " +
                    "FROM solicitudes_cambio s " +
                    "INNER JOIN turnos t ON s.id_turno = t.id_turno " +
                    "INNER JOIN usuarios ut ON t.id_usuario = ut.id_usuario " +
                    "INNER JOIN usuarios un ON s.id_usuario_nuevo = un.id_usuario "

        private const val SQL_AGREGAR_COLOR_ABAJO =
            "ALTER TABLE personas_ingreso ADD COLUMN color_ropa_abajo TEXT NOT NULL DEFAULT ''"

        private const val SQL_PRECARGAR_ROLES =
            "INSERT INTO roles (id_rol, nombre_rol) VALUES " +
                    "(1, 'Administrador'), (2, 'Supervisor de piso'), (3, 'Trabajador')"

        private const val SQL_PRECARGAR_USUARIOS =
            "INSERT INTO usuarios (codigo, nombre, rut, correo, contrasena, cargo, id_rol) VALUES " +
                    "('C-01', 'Carolina Fuentes Araya', '152348711', 'carolina@rinconsur.cl', '123456', 'Jefa de tienda', 1), " +
                    "('C-02', 'Rodrigo Saavedra Muñoz', '139874420', 'rodrigo@rinconsur.cl', '123456', 'Subgerente comercial', 1), " +
                    "('C-03', 'Daniela Pizarro Toro', '176543205', 'daniela@rinconsur.cl', '123456', 'Supervisora de piso (mañana)', 2), " +
                    "('C-04', 'Matías Contreras Vidal', '164430197', 'matias@rinconsur.cl', '123456', 'Supervisor de piso (tarde)', 2), " +
                    "('C-05', 'Tamara Rojas Espinoza', '198763454', 'tamara@rinconsur.cl', '123456', 'Vendedora, Vestuario Mujer', 3), " +
                    "('C-06', 'Felipe Ibarra Soto', '201456789', 'felipe@rinconsur.cl', '123456', 'Vendedor, Hogar y Deco', 3), " +
                    "('C-07', 'Javiera Morales Cáceres', '183229044', 'javiera@rinconsur.cl', '123456', 'Cajera, Cajas Centrales', 3)"

        private const val SQL_PRECARGAR_TRABAJADORES_NUEVOS =
            "INSERT INTO usuarios (codigo, nombre, rut, correo, contrasena, cargo, id_rol) VALUES " +
                    "('C-08','Ignacio Navarro Pérez', '178902342', 'ignacio@rinconsur.cl', '123456', 'Vendedor, Deportes', 3), " +
                    "('C-09', 'Valentina Reyes Lagos', '203451180', 'valentina@rinconsur.cl', '123456', 'Cajera, Cajas Centrales', 3), " +
                    "('C-10', 'Sebastián Castillo Mora', '167894550', 'sebastian@rinconsur.cl', '123456', 'Vendedor, Electro', 3)"

        private const val SQL_PRECARGAR_ACCESOS =
            "INSERT INTO accesos (id_acceso, codigo, nombre, ubicacion) VALUES " +
                    "(1, 'A1', 'Acceso Principal', 'Frente al pasillo central del mall'), " +
                    "(2, 'A2', 'Acceso Lateral', 'Costado del patio de comidas'), " +
                    "(3, 'A3', 'Acceso Estacionamiento', 'Conecta con el estacionamiento subterráneo'), " +
                    "(4, 'A4', 'Acceso Conexión', 'Comunica con la tienda vecina')"

        private const val SQL_PRECARGAR_PERSONAS =
            "INSERT INTO personas_ingreso (id_acceso, id_observador, fecha_hora, rango_etario, altura, color_ropa, color_ropa_abajo, acompanado, detalle, compro) VALUES " +
                    "(4, 5, '2026-10-22 10:05', 'Adulto (26-40)', 'Media', 'Azul', 'Negro', 0, 'Mochila negra', 0), " +
                    "(4, 5, '2026-10-22 10:07', 'Joven (13-25)', 'Alta', 'Negro', 'Azul', 1, '', 0), " +
                    "(4, 5, '2026-10-22 10:12', 'Adulto (41-60)', 'Baja', 'Rojo', 'Gris', 0, 'Bolsa de la tienda vecina', 0), " +
                    "(1, 6, '2026-10-22 10:20', 'Mayor (60+)', 'Media', 'Gris', 'Café', 1, 'Usa bastón', 0), " +
                    "(1, 6, '2026-10-22 10:26', 'Adulto (26-40)', 'Alta', 'Blanco', 'Azul', 0, '', 0), " +
                    "(1, 6, '2026-10-22 10:31', 'Joven (13-25)', 'Media', 'Verde', 'Negro', 1, 'Gorro', 0), " +
                    "(2, 6, '2026-10-22 13:02', 'Adulto (26-40)', 'Media', 'Azul', 'Negro', 1, 'Coche de bebé', 0), " +
                    "(2, 6, '2026-10-22 13:10', 'Adulto (41-60)', 'Alta', 'Negro', 'Gris', 0, '', 0), " +
                    "(1, 6, '2026-10-22 11:00', 'Adulto (26-40)', 'Media', 'Negro', 'Azul', 1, '', 1), " +
                    "(1, 6, '2026-10-22 11:04', 'Adulto (41-60)', 'Baja', 'Azul', 'Negro', 1, '', 1), " +
                    "(1, 6, '2026-10-22 11:15', 'Mayor (60+)', 'Baja', 'Café', 'Gris', 1, '', 1), " +
                    "(2, 6, '2026-10-22 13:20', 'Adulto (26-40)', 'Alta', 'Blanco', 'Azul', 1, '', 1), " +
                    "(2, 6, '2026-10-22 13:35', 'Adulto (41-60)', 'Media', 'Gris', 'Negro', 0, '', 1), " +
                    "(3, 5, '2026-10-22 16:02', 'Adulto (26-40)', 'Media', 'Rojo', 'Café', 1, '', 1), " +
                    "(3, 5, '2026-10-22 16:10', 'Adulto (41-60)', 'Alta', 'Negro', 'Azul', 1, '', 1), " +
                    "(4, 5, '2026-10-22 17:45', 'Adulto (26-40)', 'Media', 'Azul', 'Negro', 0, '', 1)"

        private const val SQL_PRECARGAR_COMPRAS =
            "INSERT INTO compras (id_persona, id_cajero, fecha_hora, nombre_cliente, correo_cliente, ruta_boleta) VALUES " +
                    "(9, 7, '2026-10-22 11:20', NULL, NULL, ''), " +
                    "(10, 7, '2026-10-22 11:31', 'Cliente Demo Uno', NULL, ''), " +
                    "(11, 7, '2026-10-22 11:40', NULL, NULL, ''), " +
                    "(12, 7, '2026-10-22 13:50', NULL, 'demo2@correo.cl', ''), " +
                    "(13, 7, '2026-10-22 13:58', NULL, NULL, ''), " +
                    "(14, 7, '2026-10-22 16:30', NULL, NULL, ''), " +
                    "(15, 7, '2026-10-22 16:41', 'Cliente Demo Tres', NULL, ''), " +
                    "(16, 7, '2026-10-22 18:05', NULL, NULL, '')"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREAR_ROLES)
        db.execSQL(SQL_CREAR_USUARIOS)
        db.execSQL(SQL_CREAR_ACCESOS)
        db.execSQL(SQL_CREAR_PERSONAS)
        db.execSQL(SQL_CREAR_COMPRAS)
        db.execSQL(SQL_CREAR_TURNOS)
        db.execSQL(SQL_AGREGAR_ESTADO_TURNOS)
        db.execSQL(SQL_CREAR_SOLICITUDES)

        db.execSQL(SQL_PRECARGAR_ROLES)
        db.execSQL(SQL_PRECARGAR_USUARIOS)
        db.execSQL(SQL_PRECARGAR_TRABAJADORES_NUEVOS)
        db.execSQL(SQL_PRECARGAR_ACCESOS)
        db.execSQL(SQL_PRECARGAR_PERSONAS)
        db.execSQL(SQL_PRECARGAR_COMPRAS)

        val hoy = Fechas.hoy()
        db.execSQL(
            "INSERT INTO turnos (id_usuario, id_supervisor, fecha, franja, puesto) VALUES " +
                    "(5, 3, ?, 'Mañana', 'A4'), (7, 3, ?, 'Mañana', 'CAJA'), " +
                    "(6, 4, ?, 'Tarde', 'A1'), (9, 4, ?, 'Tarde', 'CAJA')",
            arrayOf(hoy, hoy, hoy, hoy)
        )

        protegerContrasenas(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL(SQL_CREAR_TURNOS)
        }
        if (oldVersion < 3) {
            db.execSQL(SQL_PRECARGAR_TRABAJADORES_NUEVOS)
        }
        if (oldVersion < 4) {
            db.execSQL(SQL_AGREGAR_COLOR_ABAJO)
        }
        if (oldVersion < 6) {
            db.execSQL(SQL_AGREGAR_CORREO_PERSONAL)
            db.execSQL(SQL_AGREGAR_SAL)
            db.execSQL(SQL_AGREGAR_DEBE_CAMBIAR)
            db.execSQL(SQL_UNIR_OBSERVADOR_Y_CAJERO)
            db.execSQL(SQL_BORRAR_ROL_CAJERO)
            db.execSQL(SQL_RENOMBRAR_ADMINISTRADOR)
            db.execSQL(SQL_RENOMBRAR_TRABAJADOR)
            db.execSQL(SQL_AGREGAR_ESTADO_TURNOS)
            db.execSQL(SQL_CREAR_SOLICITUDES)
            protegerContrasenas(db)
        }
    }

    private fun protegerContrasenas(db: SQLiteDatabase) {
        val pendientes = mutableListOf<Pair<Long, String>>()
        db.rawQuery("SELECT id_usuario, contrasena FROM usuarios WHERE sal = ''", null).use { cursor ->
            while (cursor.moveToNext()) pendientes.add(cursor.getLong(0) to cursor.getString(1))
        }
        pendientes.forEach { (id, contrasena) ->
            val sal = Seguridad.nuevaSal()
            val valores = ContentValues().apply {
                put("sal", sal)
                put("contrasena", Seguridad.hash(contrasena, sal))
            }
            db.update("usuarios", valores, "id_usuario = ?", arrayOf(id.toString()))
        }
    }

    fun buscarUsuarioParaLogin(correo: String, contrasena: String): Usuario? {
        var usuario: Usuario? = null
        readableDatabase.rawQuery(
            "SELECT * FROM usuarios WHERE correo = ? AND activo = 1",
            arrayOf(correo.trim().lowercase())
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                val sal = cursor.getString(cursor.getColumnIndexOrThrow("sal"))
                val hashGuardado = cursor.getString(cursor.getColumnIndexOrThrow("contrasena"))
                if (Seguridad.coincide(contrasena, sal, hashGuardado)) usuario = leerUsuario(cursor)
            }
        }
        return usuario
    }

    fun buscarUsuarioPorId(id: Long): Usuario? {
        var usuario: Usuario? = null
        readableDatabase.rawQuery("SELECT * FROM usuarios WHERE id_usuario = ?", arrayOf(id.toString()))
            .use { cursor ->
                if (cursor.moveToFirst()) usuario = leerUsuario(cursor)
            }
        return usuario
    }

    fun listarAccesos(soloActivos: Boolean = false): List<AccesoItem> {
        val lista = mutableListOf<AccesoItem>()
        val sql = if (soloActivos) {
            "SELECT * FROM accesos WHERE activada = 1 ORDER BY id_accseso"
        } else {
            "SELECT * FROM accesos ORDER BY id_acceso"
        }
        readableDatabase.rawQuery("SELECT * FROM accesos ORDER BY id_acceso", null).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(
                    AccesoItem(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id_acceso")),
                        codigo = cursor.getString(cursor.getColumnIndexOrThrow("codigo")),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        activada = cursor.getInt(cursor.getColumnIndexOrThrow("activada")) == 1
                    )
                )
            }
        }
        return lista
    }

    fun insertarPersona(persona: PersonaIngreso): Long {
        val valores = ContentValues().apply {
            put("id_acceso", persona.accesoId)
            put("id_observador", persona.observadorId)
            put("fecha_hora", persona.fechaHora)
            put("rango_etario", persona.rangoEtario)
            put("altura", persona.altura)
            put("color_ropa", persona.colorRopa)
            put("color_ropa_abajo", persona.colorRopaAbajo)
            put("acompanado", if (persona.acompanado) 1 else 0)
            put("detalle", persona.detalle)
        }
        return writableDatabase.insert("personas_ingreso", null, valores)
    }

    fun contarIngresosDeHoy(idAcceso: Long, hoy: String): Int {
        var total = 0
        readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM personas_ingreso WHERE id_acceso = ? AND fecha_hora LIKE ?",
            arrayOf(idAcceso.toString(), "$hoy%")
        ).use { cursor ->
            if (cursor.moveToFirst()) total = cursor.getInt(0)
        }
        return total
    }

    fun buscarCandidatos(rangoEtario: String?, colorArriba: String?, colorAbajo: String?, idAcceso: Long?, fecha: String?, franja: String?): List<PersonaIngreso> {
        var sql = "SELECT p.*, a.codigo AS codigo_acceso FROM personas_ingreso p " +
                "INNER JOIN accesos a ON p.id_acceso = a.id_acceso " +
                "WHERE p.compro = 0"
        val args = mutableListOf<String>()
        if (rangoEtario != null) {
            sql += " AND p.rango_etario = ?"
            args.add(rangoEtario)
        }
        if (colorArriba != null) {
            sql += " AND p.color_ropa = ?"
            args.add(colorArriba)
        }
        if (colorAbajo != null) {
            sql += " AND p.color_ropa_abajo = ?"
            args.add(colorAbajo)
        }
        if (idAcceso != null) {
            sql += " AND p.fecha_hora LIKE ?"
            args.add(idAcceso.toString())
        }
        if (fecha != null) {
            sql += " AND p.fecha_hora LIKE ?"
            args.add("$fecha%")
        }
        sql += " ORDER BY p.fecha_hora DESC"

        val lista = mutableListOf<PersonaIngreso>()
        readableDatabase.rawQuery(sql, args.toTypedArray()).use { cursor ->
            while (cursor.moveToNext()) lista.add(leerPersona(cursor))
        }
        return lista
    }

    fun registrarCompra(
        idPersona: Long, idCajero: Long, fechaHora: String,
        nombreCliente: String?, correoCliente: String?, rutaBoleta: String
    ): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val compra = ContentValues().apply {
                put("id_persona", idPersona)
                put("id_cajero", idCajero)
                put("fecha_hora", fechaHora)
                put("nombre_cliente", nombreCliente)
                put("correo_cliente", correoCliente)
                put("ruta_boleta", rutaBoleta)
            }
            if (db.insert("compras", null, compra) == -1L) return false

            val marca = ContentValues().apply { put("compro", 1) }
            db.update("personas_ingreso", marca, "id_persona = ?", arrayOf(idPersona.toString()))

            db.setTransactionSuccessful()
            return true
        } finally {
            db.endTransaction()
        }
    }

    fun listarTrabajadores(): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        readableDatabase.rawQuery(
            "SELECT * FROM usuarios WHERE activo = 1 AND id_rol = 3 ORDER BY nombre", null
        ).use { cursor ->
            while (cursor.moveToNext()) lista.add(leerUsuario(cursor))
        }
        return lista
    }

    fun listarTurnos(fecha: String): List<Turno> {
        val lista = mutableListOf<Turno>()
        readableDatabase.rawQuery(
            SELECT_TURNOS + "WHERE t.fecha = ? ORDER BY t.franja, t.puesto",
            arrayOf(fecha)
        ).use { cursor ->
            while (cursor.moveToNext()) lista.add(leerTurno(cursor))
        }
        return lista
    }

    fun listarTurnosPublicados(desde: String, hasta: String): List<Turno> {
        val lista = mutableListOf<Turno>()
        readableDatabase.rawQuery(
            SELECT_TURNOS + "WHERE t.fecha BETWEEN ? AND ? AND t.estado = 'PUBLICADO' " +
                    "ORDER BY t.fecha, t.franja, t.puesto",
            arrayOf(desde, hasta)
        ).use { cursor ->
            while (cursor.moveToNext()) lista.add(leerTurno(cursor))
        }
        return lista
    }

    fun contarBorradores(desde: String, hasta: String): Int {
        var total = 0
        readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM turnos WHERE fecha BETWEEN ? AND ? AND estado = 'BORRADOR'",
            arrayOf(desde, hasta)
        ).use { cursor ->
            if (cursor.moveToFirst()) total = cursor.getInt(0)
        }
        return total
    }

    fun publicarTurnos(desde: String, hasta: String): Int {
        val valores = ContentValues().apply { put("estado", Turno.ESTADO_PUBLICADO) }
        return writableDatabase.update(
            "turnos", valores, "fecha BETWEEN ? AND ? AND estado = 'BORRADOR'", arrayOf(desde, hasta)
        )
    }

    fun borrarTurnoBorrador(idTurno: Long): Boolean =
        writableDatabase.delete(
            "turnos", "id_turno = ? AND estado = 'BORRADOR'", arrayOf(idTurno.toString())
        ) == 1

    fun insertarTurno(idUsuario: Long, idSupervisor: Long, fecha: String, franja: String, puesto: String): Long {
        val valores = ContentValues().apply {
            put("id_usuario", idUsuario)
            put("id_supervisor", idSupervisor)
            put("fecha", fecha)
            put("franja", franja)
            put("puesto", puesto)
            put("estado", Turno.ESTADO_BORRADOR)
        }
        return writableDatabase.insert("turnos", null, valores)
    }

    fun actualizarTurno(idTurno: Long, idUsuario: Long, franja: String, puesto: String): Boolean {
        val valores = ContentValues().apply {
            put("id_usuario", idUsuario)
            put("franja", franja)
            put("puesto", puesto)
        }
        return writableDatabase.update("turnos", valores, "id_turno = ?", arrayOf(idTurno.toString())) == 1
    }

    fun turnoDeHoy(idUsuario: Long, hoy: String): Turno? =
        turnosDeHoy(idUsuario, hoy).firstOrNull { !it.yaTermino() }

    fun proximoTurno(idUsuario: Long, hoy: String): Turno? {
        var turno: Turno? = null
        readableDatabase.rawQuery(
            SELECT_TURNOS + "WHERE t.id_usuario = ? AND t.fecha > ? AND t.estado = 'PUBLICADO' " +
                    "ORDER BY t.fecha, t.franja LIMIT 1",
            arrayOf(idUsuario.toString(), hoy)
        ).use { cursor ->
            if (cursor.moveToFirst()) turno = leerTurno(cursor)
        }
        return turno
    }

    fun turnosDeHoy(idUsuario: Long, hoy: String): List<Turno> {
        val lista = mutableListOf<Turno>()
        readableDatabase.rawQuery(
            "SELECT t.*, u.nombre AS nombre_trabajador FROM turnos t " +
                    "INNER JOIN usuarios u ON t.id_usuario = u.id_usuario " +
                    "WHERE t.id_usuario = ? AND t.fecha = ? AND t.estado = 'PUBLICADO' ORDER BY t.franja",
            arrayOf(idUsuario.toString(), hoy)
        ).use { cursor ->
            while (cursor.moveToNext()) lista.add(leerTurno(cursor))
        }
        return lista
    }

    fun buscarTurno(idTurno: Long): Turno? {
        var turno: Turno? = null
        readableDatabase.rawQuery(
            "SELECT t.*, u.nombre AS nombre_trabajador FROM turnos t " +
                    "INNER JOIN usuarios u ON t.id_usuario = u.id_usuario WHERE t.id_turno = ?",
            arrayOf(idTurno.toString())
        ).use { cursor ->
            if (cursor.moveToFirst()) turno = leerTurno(cursor)
        }
        return turno
    }

    fun reemplazo(puesto: String, fecha: String, franja: String): Turno? {
        var turno: Turno? = null
        readableDatabase.rawQuery(
            "SELECT t.*, u.nombre AS nombre_trabajador FROM turnos t " +
                    "INNER JOIN usuarios u ON t.id_usuario = u.id_usuario " +
                    "WHERE t.puesto = ? AND t.fecha = ? AND t.franja = ? AND t.estado = 'PUBLICADO'",
            arrayOf(puesto, fecha, franja)
        ).use { cursor ->
            if (cursor.moveToFirst()) turno = leerTurno(cursor)
        }
        return turno
    }

    fun crearSolicitudCambio(
        idTurno: Long, idSupervisor: Long, idUsuarioNuevo: Long, franjaNueva: String, puestoNuevo: String
    ): Long {
        val valores = ContentValues().apply {
            put("id_turno", idTurno)
            put("id_supervisor", idSupervisor)
            put("id_usuario_nuevo", idUsuarioNuevo)
            put("franja_nueva", franjaNueva)
            put("puesto_nuevo", puestoNuevo)
            put("estado", SolicitudCambio.PENDIENTE)
            put("fecha_creacion", Fechas.ahora())
        }
        return writableDatabase.insert("solicitudes_cambio", null, valores)
    }

    fun solicitudesPendientesPara(idUsuario: Long, hoy: String): List<SolicitudCambio> {
        val lista = mutableListOf<SolicitudCambio>()
        readableDatabase.rawQuery(
            SELECT_SOLICITUDES +
                    "WHERE s.estado = 'PENDIENTE' AND t.fecha >= ? AND " +
                    "((t.id_usuario = ? AND s.acepta_titular = 0) OR (s.id_usuario_nuevo = ? AND s.acepta_nuevo = 0)) " +
                    "ORDER BY t.fecha",
            arrayOf(hoy, idUsuario.toString(), idUsuario.toString())
        ).use { cursor ->
            while (cursor.moveToNext()) lista.add(leerSolicitud(cursor))
        }
        return lista
    }

    private fun buscarSolicitud(db: SQLiteDatabase, idSolicitud: Long): SolicitudCambio? {
        var solicitud: SolicitudCambio? = null
        db.rawQuery(SELECT_SOLICITUDES + "WHERE s.id_solicitud = ?", arrayOf(idSolicitud.toString())).use { cursor ->
            if (cursor.moveToFirst()) solicitud = leerSolicitud(cursor)
        }
        return solicitud
    }

    fun responderSolicitud(idSolicitud: Long, idUsuario: Long, acepta: Boolean): String {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val solicitud = buscarSolicitud(db, idSolicitud) ?: return "La solicitud ya no existe"
            val id = arrayOf(idSolicitud.toString())

            if (!acepta) {
                db.update("solicitudes_cambio", ContentValues().apply { put("estado", SolicitudCambio.RECHAZADA) }, "id_solicitud = ?", id)
                db.setTransactionSuccessful()
                return "Rechazaste el cambio. El turno queda como estaba"
            }

            val aceptaTitular = solicitud.aceptaTitular || idUsuario == solicitud.idTitular
            val aceptaNuevo = solicitud.aceptaNuevo || idUsuario == solicitud.idNuevo || !solicitud.cambiaPersona
            db.update(
                "solicitudes_cambio",
                ContentValues().apply {
                    put("acepta_titular", if (aceptaTitular) 1 else 0)
                    put("acepta_nuevo", if (aceptaNuevo) 1 else 0)
                },
                "id_solicitud = ?", id
            )

            if (!aceptaTitular || !aceptaNuevo) {
                db.setTransactionSuccessful()
                val falta = if (!aceptaTitular) solicitud.nombreTitular else solicitud.nombreNuevo
                return "Aceptaste. Falta que ${falta.substringBefore(" ")} acepte para aplicar el cambio"
            }

            val choque = choqueDelCambio(db, solicitud)
            if (choque != null) {
                db.update("solicitudes_cambio", ContentValues().apply { put("estado", SolicitudCambio.RECHAZADA) }, "id_solicitud = ?", id)
                db.setTransactionSuccessful()
                return "No se pudo aplicar el cambio: $choque"
            }

            db.update(
                "turnos",
                ContentValues().apply {
                    put("id_usuario", solicitud.idNuevo)
                    put("franja", solicitud.franjaNueva)
                    put("puesto", solicitud.puestoNuevo)
                },
                "id_turno = ?", arrayOf(solicitud.idTurno.toString())
            )
            db.update("solicitudes_cambio", ContentValues().apply { put("estado", SolicitudCambio.ACEPTADA) }, "id_solicitud = ?", id)
            db.setTransactionSuccessful()
            return "Cambio aceptado por ambos. El turno ya quedó actualizado"
        } finally {
            db.endTransaction()
        }
    }

    private fun choqueDelCambio(db: SQLiteDatabase, solicitud: SolicitudCambio): String? {
        var choque: String? = null
        db.rawQuery(
            "SELECT id_usuario, puesto FROM turnos WHERE fecha = ? AND franja = ? AND id_turno <> ?",
            arrayOf(solicitud.fecha, solicitud.franjaNueva, solicitud.idTurno.toString())
        ).use { cursor ->
            while (cursor.moveToNext() && choque == null) {
                val idUsuario = cursor.getLong(0)
                val puesto = cursor.getString(1)
                if (idUsuario == solicitud.idNuevo) {
                    choque = "${solicitud.nombreNuevo.substringBefore(" ")} ya tiene otro turno en esa franja"
                } else if (puesto == solicitud.puestoNuevo && puesto != Turno.PUESTO_CAJA) {
                    choque = "la puerta $puesto ya tiene a alguien en esa franja"
                }
            }
        }
        return choque
    }

    fun listarEmpleados(): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        readableDatabase.rawQuery("SELECT * FROM usuarios WHERE activo = 1 ORDER BY id_rol, nombre", null).use { cursor ->
            while (cursor.moveToNext()) lista.add(leerUsuario(cursor))
        }
        return lista
    }

    private fun existe(sql: String, valor: String): Boolean {
        var existe = false
        readableDatabase.rawQuery(sql, arrayOf(valor)).use { cursor -> existe = cursor.moveToFirst() }
        return existe
    }

    fun rutEnUso(rutLimpio: String): Boolean = existe("SELECT 1 FROM usuarios WHERE rut = ?", rutLimpio)

    fun correoEmpresaEnUso(correo: String): Boolean =
        existe("SELECT 1 FROM usuarios WHERE correo = ?", correo.trim().lowercase())

    fun correoPersonalEnUso(correo: String): Boolean =
        existe("SELECT 1 FROM usuarios WHERE correo_personal = ?", correo.trim().lowercase())

    private fun siguienteCodigo(): String {
        var mayor = 0
        readableDatabase.rawQuery("SELECT codigo FROM usuarios", null).use { cursor ->
            while (cursor.moveToNext()) {
                val numero = cursor.getString(0).substringAfter("C-").toIntOrNull() ?: 0
                if (numero > mayor) mayor = numero
            }
        }
        return "C-%02d".format(mayor + 1)
    }

    fun insertarEmpleado(
        nombre: String, rutLimpio: String, correoEmpresa: String, correoPersonal: String,
        cargo: String, rolId: Long, contrasenaTemporal: String
    ): Long {
        val sal = Seguridad.nuevaSal()
        val valores = ContentValues().apply {
            put("codigo", siguienteCodigo())
            put("nombre", nombre.trim())
            put("rut", rutLimpio)
            put("correo", correoEmpresa)
            put("correo_personal", correoPersonal.trim().lowercase())
            put("cargo", cargo.trim())
            put("id_rol", rolId)
            put("sal", sal)
            put("contrasena", Seguridad.hash(contrasenaTemporal, sal))
            put("debe_cambiar_contrasena", 1)
        }
        return writableDatabase.insert("usuarios", null, valores)
    }

    fun contarTurnosDesde(idUsuario: Long, desde: String): Int {
        var total = 0
        readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM turnos WHERE id_usuario = ? AND fecha >= ?",
            arrayOf(idUsuario.toString(), desde)
        ).use { cursor ->
            if (cursor.moveToFirst()) total = cursor.getInt(0)
        }
        return total
    }

    fun desactivarEmpleado(idUsuario: Long): Boolean {
        val valores = ContentValues().apply { put("activo", 0) }
        return writableDatabase.update("usuarios", valores, "id_usuario = ?", arrayOf(idUsuario.toString())) == 1
    }

    fun contrasenaCoincide(idUsuario: Long, contrasena: String): Boolean {
        var coincide = false
        readableDatabase.rawQuery(
            "SELECT sal, contrasena FROM usuarios WHERE id_usuario = ?", arrayOf(idUsuario.toString())
        ).use { cursor ->
            if (cursor.moveToFirst()) coincide = Seguridad.coincide(contrasena, cursor.getString(0), cursor.getString(1))
        }
        return coincide
    }

    fun cambiarContrasena(idUsuario: Long, nueva: String): Boolean {
        val sal = Seguridad.nuevaSal()
        val valores = ContentValues().apply {
            put("sal", sal)
            put("contrasena", Seguridad.hash(nueva, sal))
            put("debe_cambiar_contrasena", 0)
        }
        return writableDatabase.update("usuarios", valores, "id_usuario = ?", arrayOf(idUsuario.toString())) == 1
    }

    private fun agruparPor(columna: String, idAcceso: Long?, fechaPrefijo: String?): List<FilaIndicador> {
        var sql = "SELECT $columna AS grupo, COUNT(*) AS ingresos, SUM(compro) AS compradores " +
                "FROM personas_ingreso WHERE 1 = 1"
        val args = mutableListOf<String>()
        if (idAcceso != null) {
            sql += " AND id_acceso = ?"
            args.add(idAcceso.toString())
        }
        if (fechaPrefijo != null) {
            sql += " AND fecha_hora LIKE ?"
            args.add("$fechaPrefijo%")
        }
        sql += " GROUP BY $columna"

        val lista = mutableListOf<FilaIndicador>()
        readableDatabase.rawQuery(sql, args.toTypedArray()).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(
                    FilaIndicador(
                        grupo = cursor.getString(cursor.getColumnIndexOrThrow("grupo")),
                        ingresos = cursor.getInt(cursor.getColumnIndexOrThrow("ingresos")),
                        compradores = cursor.getInt(cursor.getColumnIndexOrThrow("compradores"))
                    )
                )
            }
        }
        return lista
    }

    fun indicadoresPorRango(idAcceso: Long?, fechaPrefijo: String?) =
        agruparPor("rango_etario", idAcceso, fechaPrefijo)

    fun indicadoresPorCompania(idAcceso: Long?, fechaPrefijo: String?) =
        agruparPor("acompanado", idAcceso, fechaPrefijo)

    fun indicadoresPorAcceso(fechaPrefijo: String?): List<FilaIndicador> {
        var sql = "SELECT a.codigo || ' · ' || a.nombre AS grupo, COUNT(p.id_persona) AS ingresos, " +
                "COALESCE(SUM(p.compro), 0) AS compradores " +
                "FROM accesos a LEFT JOIN personas_ingreso p ON p.id_acceso = a.id_acceso"
        val args = mutableListOf<String>()
        if (fechaPrefijo != null) {
            sql += " AND p.fecha_hora LIKE ?"
            args.add("$fechaPrefijo%")
        }
        sql += " GROUP BY a.id_acceso ORDER BY a.id_acceso"

        val lista = mutableListOf<FilaIndicador>()
        readableDatabase.rawQuery(sql, args.toTypedArray()).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(
                    FilaIndicador(
                        grupo = cursor.getString(cursor.getColumnIndexOrThrow("grupo")),
                        ingresos = cursor.getInt(cursor.getColumnIndexOrThrow("ingresos")),
                        compradores = cursor.getInt(cursor.getColumnIndexOrThrow("compradores"))
                    )
                )
            }
        }
        return lista
    }

    fun existeCorreo(correo: String): Boolean {
        var existe = false
        readableDatabase.rawQuery(
            "SELECT 1 FROM usuarios WHERE correo = ? AND activo = 1",
            arrayOf(correo.trim().lowercase())
        ).use { cursor ->
            existe = cursor.moveToFirst()
        }
        return existe
    }

    private fun leerUsuario(c: Cursor) = Usuario(
        id = c.getLong(c.getColumnIndexOrThrow("id_usuario")),
        codigo = c.getString(c.getColumnIndexOrThrow("codigo")),
        nombre = c.getString(c.getColumnIndexOrThrow("nombre")),
        rut = c.getString(c.getColumnIndexOrThrow("rut")),
        correo = c.getString(c.getColumnIndexOrThrow("correo")),
        cargo = c.getString(c.getColumnIndexOrThrow("cargo")),
        rolId = c.getLong(c.getColumnIndexOrThrow("id_rol")),
        correoPersonal = c.getString(c.getColumnIndexOrThrow("correo_personal")),
        debeCambiarContrasena = c.getInt(c.getColumnIndexOrThrow("debe_cambiar_contrasena")) == 1
    )

    private fun leerTurno(c: Cursor): Turno {
        val columnaPendientes = c.getColumnIndex("cambios_pendientes")
        return Turno(
            id = c.getLong(c.getColumnIndexOrThrow("id_turno")),
            idUsuario = c.getLong(c.getColumnIndexOrThrow("id_usuario")),
            nombreTrabajador = c.getString(c.getColumnIndexOrThrow("nombre_trabajador")),
            fecha = c.getString(c.getColumnIndexOrThrow("fecha")),
            franja = c.getString(c.getColumnIndexOrThrow("franja")),
            puesto = c.getString(c.getColumnIndexOrThrow("puesto")),
            estado = c.getString(c.getColumnIndexOrThrow("estado")),
            cambioPendiente = columnaPendientes >= 0 && c.getInt(columnaPendientes) > 0
        )
    }

    private fun leerSolicitud(c: Cursor) = SolicitudCambio(
        id = c.getLong(c.getColumnIndexOrThrow("id_solicitud")),
        idTurno = c.getLong(c.getColumnIndexOrThrow("id_turno")),
        fecha = c.getString(c.getColumnIndexOrThrow("fecha")),
        idTitular = c.getLong(c.getColumnIndexOrThrow("id_titular")),
        nombreTitular = c.getString(c.getColumnIndexOrThrow("nombre_titular")),
        franjaActual = c.getString(c.getColumnIndexOrThrow("franja")),
        puestoActual = c.getString(c.getColumnIndexOrThrow("puesto")),
        idNuevo = c.getLong(c.getColumnIndexOrThrow("id_usuario_nuevo")),
        nombreNuevo = c.getString(c.getColumnIndexOrThrow("nombre_nuevo")),
        franjaNueva = c.getString(c.getColumnIndexOrThrow("franja_nueva")),
        puestoNuevo = c.getString(c.getColumnIndexOrThrow("puesto_nuevo")),
        aceptaTitular = c.getInt(c.getColumnIndexOrThrow("acepta_titular")) == 1,
        aceptaNuevo = c.getInt(c.getColumnIndexOrThrow("acepta_nuevo")) == 1
    )

    private fun leerPersona(c: Cursor) = PersonaIngreso(
        id = c.getLong(c.getColumnIndexOrThrow("id_persona")),
        accesoId = c.getLong(c.getColumnIndexOrThrow("id_acceso")),
        accesoCodigo = c.getString(c.getColumnIndexOrThrow("codigo_acceso")),
        observadorId = c.getLong(c.getColumnIndexOrThrow("id_observador")),
        fechaHora = c.getString(c.getColumnIndexOrThrow("fecha_hora")),
        rangoEtario = c.getString(c.getColumnIndexOrThrow("rango_etario")),
        altura = c.getString(c.getColumnIndexOrThrow("altura")),
        colorRopa = c.getString(c.getColumnIndexOrThrow("color_ropa")),
        colorRopaAbajo = c.getString(c.getColumnIndexOrThrow("color_ropa_abajo")),
        acompanado = c.getInt(c.getColumnIndexOrThrow("acompanado")) == 1,
        detalle = c.getString(c.getColumnIndexOrThrow("detalle")),
        compro = c.getInt(c.getColumnIndexOrThrow("compro")) == 1
    )

    fun actualizarEstadoAccesos(idAcceso: Int, activada: Boolean):  Boolean {
        val valores = ContentValues().apply {
            put("activada", if (activada) 1 else 0)
        }
        return writableDatabase.update("accesos", valores, "id_acceso = ?", arrayOf(idAcceso.toString())) == 1
    }

    fun listarRoles(): List<Pair<Long, String>> {
        val roles = mutableListOf<Pair<Long, String>>()
        readableDatabase.rawQuery("SELECT id_rol, nombre_rol FROM roles ORDER BY id_rol", null).use { cursor ->
            while (cursor.moveToNext()) {
                roles.add(
                    Pair(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id_rol")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre_rol"))
                    )
                )
            }
        }
        return roles
    }

    fun insertarUsuario(
        nombre: String,
        rut: String,
        correo: String,
        contrasena: String,
        cargo: String,
        idRol: Long
    ): Boolean {
        val sal = Seguridad.nuevaSal()
        val valores = ContentValues().apply {
            put("codigo", siguienteCodigo())
            put("nombre", nombre)
            put("rut", rut)
            put("correo", correo.lowercase())
            put("sal", sal)
            put("contrasena", Seguridad.hash(contrasena, sal))
            put("debe_cambiar_contrasena", 1)
            put("cargo", cargo)
            put("id_rol", idRol)
            put("activo", 1)
        }
        return writableDatabase.insert("usuarios", null, valores) != -1L
    }

    fun eliminarUsuario(idUsuario: Long): Boolean = desactivarEmpleado(idUsuario)

    fun actualizarUsuario(
        idUsuario: Long,
        nombre: String,
        correo: String,
        cargo: String,
        idRol: Long
    ): Boolean {
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("correo", correo.lowercase())
            put("cargo", cargo)
            put("id_rol", idRol)
        }
        return writableDatabase.update("usuarios", valores, "id_usuario = ?", arrayOf(idUsuario.toString())) > 0
    }
}
