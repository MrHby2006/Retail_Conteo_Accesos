package cl.rinconsur.usuarios.util;

public class RutUtil {

    public static String limpiar(String rut) {
        if (rut == null) {
            return "";
        }
        return rut.replace(".", "").replace("-", "").trim().toUpperCase();
    }

    public static String formatear(String rutLimpio) {
        if (rutLimpio == null || rutLimpio.length() < 2) {
            return rutLimpio;
        }
        return rutLimpio.substring(0, rutLimpio.length() - 1) + "-" + rutLimpio.charAt(rutLimpio.length() - 1);
    }
}
