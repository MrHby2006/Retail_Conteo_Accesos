package cl.rinconsur.usuarios.util;

public class PlantillasCorreo {

    private static final String FIRMA = "\n\nSaludos,\nFlujoSur - Tienda Rincón Sur\n(Este correo se envió de forma automática, no lo respondas)";

    public static String cuentaNueva(String nombres, String rut, String correoEmpresa, String temporal) {
        return "Hola, " + nombres + ":\n\n"
                + "El administrador creó tu cuenta en la app FlujoSur de Rincón Sur.\n\n"
                + "Correo de la empresa: " + correoEmpresa + "\n"
                + "Para entrar a la app usa tu RUT: " + rut + "\n"
                + "Contraseña temporal: " + temporal + "\n\n"
                + "La primera vez que ingreses la app te pedirá crear una contraseña propia." + FIRMA;
    }

    public static String contrasenaTemporal(String nombres, String rut, String temporal) {
        return "Hola, " + nombres + ":\n\n"
                + "El administrador generó una contraseña temporal nueva para tu cuenta.\n\n"
                + "RUT: " + rut + "\n"
                + "Contraseña temporal: " + temporal + "\n\n"
                + "Al ingresar tendrás que crear una contraseña propia." + FIRMA;
    }

    public static String codigoRecuperacion(String nombres, String codigo, int minutos) {
        return "Hola, " + nombres + ":\n\n"
                + "Pediste recuperar tu contraseña de FlujoSur. Tu código es:\n\n"
                + "    " + codigo + "\n\n"
                + "Vence en " + minutos + " minutos y sirve una sola vez.\n"
                + "Si no fuiste tú, ignora este correo: tu contraseña sigue igual." + FIRMA;
    }

    public static String contrasenaCambiada(String nombres) {
        return "Hola, " + nombres + ":\n\n"
                + "Te avisamos que la contraseña de tu cuenta se cambió.\n"
                + "Si no fuiste tú, avisa de inmediato al administrador de la tienda." + FIRMA;
    }
}
