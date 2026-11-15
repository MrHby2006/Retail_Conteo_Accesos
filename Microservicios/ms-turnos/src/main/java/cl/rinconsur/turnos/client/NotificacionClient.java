package cl.rinconsur.turnos.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import cl.rinconsur.turnos.dto.CorreoDTO;
import cl.rinconsur.turnos.dto.CorreoMasivoDTO;
import cl.rinconsur.turnos.dto.ResultadoMasivoDTO;

@Component
public class NotificacionClient {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public void enviarCorreo(CorreoDTO correo) {
        try {
            webClientBuilder.build()
                    .post()
                    .uri("http://ms-notificaciones/api/v1/notificaciones/correo")
                    .bodyValue(correo)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            System.out.println("No se pudo llamar a ms-notificaciones: " + e.getMessage());
        }
    }

    public boolean enviarMasivo(CorreoMasivoDTO correo) {
        try {
            ResultadoMasivoDTO resultado = webClientBuilder.build()
                    .post()
                    .uri("http://ms-notificaciones/api/v1/notificaciones/correo-masivo")
                    .bodyValue(correo)
                    .retrieve()
                    .bodyToMono(ResultadoMasivoDTO.class)
                    .block();
            return resultado != null && resultado.isEnviado();
        } catch (Exception e) {
            System.out.println("No se pudo llamar a ms-notificaciones: " + e.getMessage());
            return false;
        }
    }
}
