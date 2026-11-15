package cl.rinconsur.turnos.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import cl.rinconsur.turnos.dto.TurnoDTO;

public class PlantillasCorreo {

    private static final String FIRMA = "\n\nSaludos,\nFlujoSur - Tienda Rincón Sur\n(Este correo se envió de forma automática, no lo respondas)";
    private static final DateTimeFormatter DIA = DateTimeFormatter.ofPattern("EEEE dd/MM", Locale.of("es", "CL"));

    public static String dia(LocalDate fecha) {
        String texto = fecha.format(DIA);
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    public static String horario(List<TurnoDTO> turnos) {
        StringBuilder sb = new StringBuilder("Hola a todos:\n\nEl supervisor publicó el horario de la semana. Revisa tu turno:\n");
        LocalDate diaActual = null;
        for (TurnoDTO t : turnos) {
            if (!t.getFecha().equals(diaActual)) {
                diaActual = t.getFecha();
                sb.append("\n").append(dia(diaActual)).append("\n");
            }
            sb.append("  - ").append(t.getFranja())
              .append(" (").append(t.getHoraInicio()).append(" a ").append(t.getHoraFin()).append(") · ")
              .append(t.getPuesto()).append(" · ").append(t.getNombreTrabajador()).append("\n");
        }
        sb.append("\nTambién puedes verlo en la app, en \"Ver horario de la semana\".").append(FIRMA);
        return sb.toString();
    }

    public static String solicitud(String nombres, String descripcion) {
        return "Hola, " + nombres + ":\n\n"
                + "El supervisor propuso este cambio:\n\n"
                + "    " + descripcion + "\n\n"
                + "Entra a FlujoSur para aceptarlo o rechazarlo. El cambio solo se aplica cuando todos los involucrados lo aceptan." + FIRMA;
    }
}
