CREATE DATABASE IF NOT EXISTS rinconsur_usuarios CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS rinconsur_turnos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS rinconsur_notificaciones CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS rinconsur_accesos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS rinconsur_ventas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS rinconsur_reportes CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

GRANT ALL PRIVILEGES ON rinconsur_usuarios.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON rinconsur_turnos.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON rinconsur_notificaciones.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON rinconsur_accesos.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON rinconsur_ventas.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON rinconsur_reportes.* TO 'root'@'%';
FLUSH PRIVILEGES;
