package com.example.rentail_conteo.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.rentail_conteo.model.AccesoItem
import com.example.rentail_conteo.model.FilaIndicador
import com.example.rentail_conteo.model.PersonaIngreso
import com.example.rentail_conteo.model.Turno
import com.example.rentail_conteo.model.Usuario
import com.example.rentail_conteo.util.Fechas

// =====================================================================
//  RinconSurDbHelper.kt  —  Base de datos SQLite de la app (rinconsur.db)
//
//   roles 1───N usuarios 1───N personas_ingreso N───1 accesos
//                  │                 │
//                  │                 1
//                  │                 │
//                  └──────1───N   compras   (una compra por persona)
//
//   usuarios 1───N turnos   (el SUPERVISOR asigna a cada trabajador una puerta o la caja)
//
//   - personas_ingreso: lo que anota el OBSERVADOR en la puerta.
//   - compras: lo que registra el CAJERO cuando encuentra a esa persona (con la foto de la boleta).
//   Las personas que nunca llegan a "compras" son las que entraron y NO compraron.
//
//  Datos precargados: colaboradores y accesos ficticios de los datos de ejemplo del caso.
// =====================================================================
class RinconSurDbHelper(context: Context) :
    SQLiteOpenHelper(context, NOMBRE_BD, null, VERSION_BD) {

    companion object {
        private const val NOMBRE_BD = "rinconsur.db"
        private const val VERSION_BD = 4      // 2 = turnos y compras de ejemplo; 3 = 3 trabajadores más; 4 = color de abajo

        // Singleton: todos los ViewModels usan el MISMO helper (una sola conexión)
        @Volatile private var instancia: RinconSurDbHelper? = null

        fun obtener(context: Context): RinconSurDbHelper =
            instancia ?: synchronized(this) {
                instancia ?: RinconSurDbHelper(context.applicationContext).also { instancia = it }
            }

        // ---------------------------- CREAR TABLAS ----------------------------

        private const val SQL_CREAR_ROLES =
            "CREATE TABLE roles (" +
                    "id_rol INTEGER PRIMARY KEY, " +
                    "nombre_rol TEXT NOT NULL)"

        private const val SQL_CREAR_USUARIOS =
            "CREATE TABLE usuarios (" +
                    "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "codigo TEXT NOT NULL UNIQUE, " +
                    "nombre TEXT NOT NULL, " +
                    "rut TEXT NOT NULL UNIQUE, " +          // sin puntos ni guion: 152348711
                    "contrasena TEXT NOT NULL, " +
                    "cargo TEXT NOT NULL, " +
                    "activo INTEGER NOT NULL DEFAULT 1, " +
                    "id_rol INTEGER NOT NULL REFERENCES roles(id_rol))"

        private const val SQL_CREAR_ACCESOS =
            "CREATE TABLE accesos (" +
                    "id_acceso INTEGER PRIMARY KEY, " +
                    "codigo TEXT NOT NULL UNIQUE, " +
                    "nombre TEXT NOT NULL, " +
                    "ubicacion TEXT NOT NULL)"

        private const val SQL_CREAR_PERSONAS =
            "CREATE TABLE personas_ingreso (" +
                    "id_persona INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_acceso INTEGER NOT NULL REFERENCES accesos(id_acceso), " +
                    "id_observador INTEGER NOT NULL REFERENCES usuarios(id_usuario), " +
                    "fecha_hora TEXT NOT NULL, " +
                    "rango_etario TEXT NOT NULL, " +
                    "altura TEXT NOT NULL, " +
                    "color_ropa TEXT NOT NULL, " +          // parte de arriba
                    "color_ropa_abajo TEXT NOT NULL, " +    // parte de abajo (pantalón, falda...)
                    "acompanado INTEGER NOT NULL DEFAULT 0, " +
                    "detalle TEXT NOT NULL DEFAULT '', " +
                    "compro INTEGER NOT NULL DEFAULT 0)"

        // UNIQUE en id_persona: a una misma persona no se le pueden registrar dos compras
        private const val SQL_CREAR_COMPRAS =
            "CREATE TABLE compras (" +
                    "id_compra INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_persona INTEGER NOT NULL UNIQUE REFERENCES personas_ingreso(id_persona), " +
                    "id_cajero INTEGER NOT NULL REFERENCES usuarios(id_usuario), " +
                    "fecha_hora TEXT NOT NULL, " +
                    "nombre_cliente TEXT, " +               // opcional (NULL si no lo quiso dar)
                    "correo_cliente TEXT, " +               // opcional
                    "ruta_boleta TEXT NOT NULL)"            // dónde quedó guardada la foto

        // puesto = 'A1'..'A4' (puerta) o 'CAJA'. franja = 'Mañana' o 'Tarde'.
        // UNIQUE: un trabajador no puede tener dos turnos en la misma fecha y franja.
        private const val SQL_CREAR_TURNOS =
            "CREATE TABLE turnos (" +
                    "id_turno INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_usuario INTEGER NOT NULL REFERENCES usuarios(id_usuario), " +
                    "id_supervisor INTEGER NOT NULL REFERENCES usuarios(id_usuario), " +
                    "fecha TEXT NOT NULL, " +               // "2026-10-23"
                    "franja TEXT NOT NULL, " +
                    "puesto TEXT NOT NULL, " +
                    "UNIQUE (id_usuario, fecha, franja))"

        // ---------------------------- DATOS DE EJEMPLO ----------------------------
        // Contraseña de prueba para todos: 123456

        private const val SQL_PRECARGAR_ROLES =
            "INSERT INTO roles (id_rol, nombre_rol) VALUES " +
                    "(1, 'Jefatura'), (2, 'Supervisor de piso'), (3, 'Observador'), (4, 'Cajero')"

        private const val SQL_PRECARGAR_USUARIOS =
            "INSERT INTO usuarios (codigo, nombre, rut, contrasena, cargo, id_rol) VALUES " +
                    "('C-01', 'Carolina Fuentes Araya', '152348711', '123456', 'Jefa de tienda', 1), " +
                    "('C-02', 'Rodrigo Saavedra Muñoz', '139874420', '123456', 'Subgerente comercial', 1), " +
                    "('C-03', 'Daniela Pizarro Toro', '176543205', '123456', 'Supervisora de piso (mañana)', 2), " +
                    "('C-04', 'Matías Contreras Vidal', '164430197', '123456', 'Supervisor de piso (tarde)', 2), " +
                    "('C-05', 'Tamara Rojas Espinoza', '198763454', '123456', 'Vendedora, Vestuario Mujer', 3), " +
                    "('C-06', 'Felipe Ibarra Soto', '201456789', '123456', 'Vendedor, Hogar y Deco', 3), " +
                    "('C-07', 'Javiera Morales Cáceres', '183229044', '123456', 'Cajera, Cajas Centrales', 4), " +
                    "('C-08', 'Ignacio Navarro Pérez', '178902342', '123456', 'Vendedor, Deportes', 3), " +
                    "('C-09', 'Valentina Reyes Lagos', '203451180', '123456', 'Cajera, Cajas Centrales', 4), " +
                    "('C-10', 'Sebastián Castillo Mora', '167894550', '123456', 'Vendedor, Electro', 3)"

        private const val SQL_PRECARGAR_ACCESOS =
            "INSERT INTO accesos (id_acceso, codigo, nombre, ubicacion) VALUES " +
                    "(1, 'A1', 'Acceso Principal', 'Frente al pasillo central del mall'), " +
                    "(2, 'A2', 'Acceso Lateral', 'Costado del patio de comidas'), " +
                    "(3, 'A3', 'Acceso Estacionamiento', 'Conecta con el estacionamiento subterráneo'), " +
                    "(4, 'A4', 'Acceso Conexión', 'Comunica con la tienda vecina')"

        // Algunas personas ya anotadas, para que el cajero tenga a quién buscar en la demo
        // (observadores: id 5 = Tamara, id 6 = Felipe)
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
                    // Desde aquí (ids 9 a 16) son personas que SÍ compraron, para que los indicadores tengan datos
                    "(1, 6, '2026-10-22 11:00', 'Adulto (26-40)', 'Media', 'Negro', 'Azul', 1, '', 1), " +
                    "(1, 6, '2026-10-22 11:04', 'Adulto (41-60)', 'Baja', 'Azul', 'Negro', 1, '', 1), " +
                    "(1, 6, '2026-10-22 11:15', 'Mayor (60+)', 'Baja', 'Café', 'Gris', 1, '', 1), " +
                    "(2, 6, '2026-10-22 13:20', 'Adulto (26-40)', 'Alta', 'Blanco', 'Azul', 1, '', 1), " +
                    "(2, 6, '2026-10-22 13:35', 'Adulto (41-60)', 'Media', 'Gris', 'Negro', 0, '', 1), " +
                    "(3, 5, '2026-10-22 16:02', 'Adulto (26-40)', 'Media', 'Rojo', 'Café', 1, '', 1), " +
                    "(3, 5, '2026-10-22 16:10', 'Adulto (41-60)', 'Alta', 'Negro', 'Azul', 1, '', 1), " +
                    "(4, 5, '2026-10-22 17:45', 'Adulto (26-40)', 'Media', 'Azul', 'Negro', 0, '', 1)"

        // Compras de ejemplo de esas 8 personas (cajera id 7 = Javiera). Sin foto: son datos de demo.
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

    // SQLite trae las claves foráneas apagadas: se encienden cada vez que se abre la conexión
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    // Solo la PRIMERA vez (cuando rinconsur.db no existe). Primero las tablas "padre".
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREAR_ROLES)
        db.execSQL(SQL_CREAR_USUARIOS)
        db.execSQL(SQL_CREAR_ACCESOS)
        db.execSQL(SQL_CREAR_PERSONAS)
        db.execSQL(SQL_CREAR_COMPRAS)
        db.execSQL(SQL_CREAR_TURNOS)

        db.execSQL(SQL_PRECARGAR_ROLES)
        db.execSQL(SQL_PRECARGAR_USUARIOS)
        db.execSQL(SQL_PRECARGAR_ACCESOS)
        db.execSQL(SQL_PRECARGAR_PERSONAS)
        db.execSQL(SQL_PRECARGAR_COMPRAS)

        // Dos turnos para HOY (la fecha se calcula al crear la BD): Tamara en A4 y Javiera en caja
        val hoy = Fechas.hoy()
        db.execSQL(
            "INSERT INTO turnos (id_usuario, id_supervisor, fecha, franja, puesto) VALUES " +
                    "(5, 3, ?, 'Mañana', 'A4'), (7, 3, ?, 'Mañana', 'CAJA')",
            arrayOf(hoy, hoy)
        )
    }

    // Si se sube VERSION_BD: se borra todo y se vuelve a crear. Primero las tablas "hijas".
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS turnos")
        db.execSQL("DROP TABLE IF EXISTS compras")
        db.execSQL("DROP TABLE IF EXISTS personas_ingreso")
        db.execSQL("DROP TABLE IF EXISTS accesos")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS roles")
        onCreate(db)
    }

    // ---------------------------- USUARIOS (login) ----------------------------

    // rut sin puntos ni guion. Devuelve null si no existe, si la contraseña no coincide o si está inactivo.
    fun buscarUsuarioParaLogin(rut: String, contrasena: String): Usuario? {
        var usuario: Usuario? = null
        readableDatabase.rawQuery(
            "SELECT * FROM usuarios WHERE rut = ? AND contrasena = ? AND activo = 1",
            arrayOf(rut, contrasena)
        ).use { cursor ->
            if (cursor.moveToFirst()) usuario = leerUsuario(cursor)
        }
        return usuario
    }

    // Para recuperar la sesión guardada en SharedPreferences (que guarda solo el id)
    fun buscarUsuarioPorId(id: Long): Usuario? {
        var usuario: Usuario? = null
        readableDatabase.rawQuery("SELECT * FROM usuarios WHERE id_usuario = ?", arrayOf(id.toString()))
            .use { cursor ->
                if (cursor.moveToFirst()) usuario = leerUsuario(cursor)
            }
        return usuario
    }

    // ---------------------------- ACCESOS ----------------------------

    fun listarAccesos(): List<AccesoItem> {
        val lista = mutableListOf<AccesoItem>()
        readableDatabase.rawQuery("SELECT * FROM accesos ORDER BY id_acceso", null).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(
                    AccesoItem(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id_acceso")),
                        codigo = cursor.getString(cursor.getColumnIndexOrThrow("codigo")),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                    )
                )
            }
        }
        return lista
    }

    // ---------------------------- PERSONAS (observador) ----------------------------

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
        return writableDatabase.insert("personas_ingreso", null, valores)    // -1 si falló
    }

    // Cuántas personas se anotaron HOY en un acceso. LIKE '2026-10-22%' = "empieza con la fecha de hoy"
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

    // ---------------------------- PERSONAS (cajero) ----------------------------

    // Personas que entraron y todavía NO tienen compra, con los filtros que eligió el cajero.
    // Cada filtro es opcional: solo se agrega al WHERE si viene con valor.
    // (No se filtra por acceso: desde la caja no se ve por dónde entró la persona.)
    fun buscarCandidatos(rangoEtario: String?, colorArriba: String?, colorAbajo: String?): List<PersonaIngreso> {
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
        sql += " ORDER BY p.fecha_hora DESC"       // los que entraron más recién, primero

        val lista = mutableListOf<PersonaIngreso>()
        readableDatabase.rawQuery(sql, args.toTypedArray()).use { cursor ->
            while (cursor.moveToNext()) lista.add(leerPersona(cursor))
        }
        return lista
    }

    // ---------------------------- COMPRAS (cajero) ----------------------------

    // Guarda la compra y marca a la persona como "compró". Las dos cosas van en una TRANSACCIÓN:
    // o se guardan ambas, o ninguna (así no queda una compra sin persona marcada, ni al revés).
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
                put("nombre_cliente", nombreCliente)      // null = columna vacía
                put("correo_cliente", correoCliente)
                put("ruta_boleta", rutaBoleta)
            }
            if (db.insert("compras", null, compra) == -1L) return false

            val marca = ContentValues().apply { put("compro", 1) }
            db.update("personas_ingreso", marca, "id_persona = ?", arrayOf(idPersona.toString()))

            db.setTransactionSuccessful()      // sin esta línea, endTransaction() deshace todo
            return true
        } finally {
            db.endTransaction()
        }
    }

    // ---------------------------- TURNOS (supervisor) ----------------------------

    // Trabajadores a los que se les puede asignar turno: observadores y cajeros activos, por nombre
    fun listarTrabajadores(): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        readableDatabase.rawQuery(
            "SELECT * FROM usuarios WHERE activo = 1 AND id_rol IN (3, 4) ORDER BY nombre", null
        ).use { cursor ->
            while (cursor.moveToNext()) lista.add(leerUsuario(cursor))
        }
        return lista
    }

    // Turnos de un día, con el nombre del trabajador (JOIN con usuarios)
    fun listarTurnos(fecha: String): List<Turno> {
        val lista = mutableListOf<Turno>()
        readableDatabase.rawQuery(
            "SELECT t.*, u.nombre AS nombre_trabajador FROM turnos t " +
                    "INNER JOIN usuarios u ON t.id_usuario = u.id_usuario " +
                    "WHERE t.fecha = ? ORDER BY t.franja, t.puesto",      // 'Mañana' antes que 'Tarde'
            arrayOf(fecha)
        ).use { cursor ->
            while (cursor.moveToNext()) lista.add(leerTurno(cursor))
        }
        return lista
    }

    // -1 si falló (por ejemplo, si choca con el UNIQUE: el trabajador ya tiene turno en esa franja)
    fun insertarTurno(idUsuario: Long, idSupervisor: Long, fecha: String, franja: String, puesto: String): Long {
        val valores = ContentValues().apply {
            put("id_usuario", idUsuario)
            put("id_supervisor", idSupervisor)
            put("fecha", fecha)
            put("franja", franja)
            put("puesto", puesto)
        }
        return writableDatabase.insert("turnos", null, valores)
    }

    fun eliminarTurno(idTurno: Long) {
        writableDatabase.delete("turnos", "id_turno = ?", arrayOf(idTurno.toString()))
    }

    // El primer turno de HOY de un trabajador (para que el observador parta con su puerta ya elegida)
    fun turnoDeHoy(idUsuario: Long, hoy: String): Turno? {
        var turno: Turno? = null
        readableDatabase.rawQuery(
            "SELECT t.*, u.nombre AS nombre_trabajador FROM turnos t " +
                    "INNER JOIN usuarios u ON t.id_usuario = u.id_usuario " +
                    "WHERE t.id_usuario = ? AND t.fecha = ? ORDER BY t.franja",
            arrayOf(idUsuario.toString(), hoy)
        ).use { cursor ->
            if (cursor.moveToFirst()) turno = leerTurno(cursor)
        }
        return turno
    }

    // TODOS los turnos de hoy de un trabajador (puede tener mañana y tarde): a cada uno se le programa su aviso
    fun turnosDeHoy(idUsuario: Long, hoy: String): List<Turno> {
        val lista = mutableListOf<Turno>()
        readableDatabase.rawQuery(
            "SELECT t.*, u.nombre AS nombre_trabajador FROM turnos t " +
                    "INNER JOIN usuarios u ON t.id_usuario = u.id_usuario " +
                    "WHERE t.id_usuario = ? AND t.fecha = ? ORDER BY t.franja",
            arrayOf(idUsuario.toString(), hoy)
        ).use { cursor ->
            while (cursor.moveToNext()) lista.add(leerTurno(cursor))
        }
        return lista
    }

    // Un turno por su id (null si el supervisor lo quitó)
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

    // Quién toma el mismo puesto en la franja siguiente (null si nadie: el puesto quedaría sin cubrir)
    fun reemplazo(puesto: String, fecha: String, franja: String): Turno? {
        var turno: Turno? = null
        readableDatabase.rawQuery(
            "SELECT t.*, u.nombre AS nombre_trabajador FROM turnos t " +
                    "INNER JOIN usuarios u ON t.id_usuario = u.id_usuario " +
                    "WHERE t.puesto = ? AND t.fecha = ? AND t.franja = ?",
            arrayOf(puesto, fecha, franja)
        ).use { cursor ->
            if (cursor.moveToFirst()) turno = leerTurno(cursor)
        }
        return turno
    }

    // ---------------------------- INDICADORES (jefatura) ----------------------------

    // Cuenta ingresos y compradores agrupados por un rasgo. idAcceso null = todos los accesos;
    // fechaPrefijo null = todo el registro. SUM(compro) sirve para contar porque compro vale 0 o 1.
    // "columna" nunca viene de lo que escribe el usuario: solo se llama desde las dos funciones de abajo.
    private fun agruparPor(columna: String, idAcceso: Long?, fechaPrefijo: String?): List<FilaIndicador> {
        var sql = "SELECT $columna AS grupo, COUNT(*) AS ingresos, SUM(compro) AS compradores " +
                "FROM personas_ingreso WHERE 1 = 1"          // 1 = 1: para poder ir sumando "AND ..."
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

    // Aquí "grupo" llega como "0" (solo) o "1" (acompañado)
    fun indicadoresPorCompania(idAcceso: Long?, fechaPrefijo: String?) =
        agruparPor("acompanado", idAcceso, fechaPrefijo)

    // ---------------------------- LEER FILAS ----------------------------

    private fun leerUsuario(c: Cursor) = Usuario(
        id = c.getLong(c.getColumnIndexOrThrow("id_usuario")),
        codigo = c.getString(c.getColumnIndexOrThrow("codigo")),
        nombre = c.getString(c.getColumnIndexOrThrow("nombre")),
        rut = c.getString(c.getColumnIndexOrThrow("rut")),
        cargo = c.getString(c.getColumnIndexOrThrow("cargo")),
        rolId = c.getLong(c.getColumnIndexOrThrow("id_rol"))
    )

    private fun leerTurno(c: Cursor) = Turno(
        id = c.getLong(c.getColumnIndexOrThrow("id_turno")),
        idUsuario = c.getLong(c.getColumnIndexOrThrow("id_usuario")),
        nombreTrabajador = c.getString(c.getColumnIndexOrThrow("nombre_trabajador")),
        fecha = c.getString(c.getColumnIndexOrThrow("fecha")),
        franja = c.getString(c.getColumnIndexOrThrow("franja")),
        puesto = c.getString(c.getColumnIndexOrThrow("puesto"))
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
}
