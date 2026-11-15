package cl.rinconsur.notificaciones.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.rinconsur.notificaciones.model.TipoNotificacion;
import cl.rinconsur.notificaciones.repository.TipoNotificacionRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(TipoNotificacionRepository tipoRep) {
        return args -> {
            if (tipoRep.count() > 0) {
                return;
            }
            String[] tipos = {"CUENTA_NUEVA", "CONTRASENA_TEMPORAL", "CODIGO_RECUPERACION", "CONTRASENA_CAMBIADA",
                    "HORARIO_PUBLICADO", "SOLICITUD_CAMBIO", "OTRO"};
            for (String tipo : tipos) {
                tipoRep.save(new TipoNotificacion(null, tipo));
            }
            System.out.println("Tipos de notificación cargados");
        };
    }
}
