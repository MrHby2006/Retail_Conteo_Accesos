package cl.rinconsur.usuarios.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import cl.rinconsur.usuarios.dto.CorreoDTO;
import cl.rinconsur.usuarios.dto.NotificacionDTO;

@Component
public class NotificacionClient {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public boolean enviarCorreo(CorreoDTO correo) {
        try {
            NotificacionDTO respuesta = webClientBuilder.build()
                    .post()
                    .uri("http://ms-notificaciones/api/v1/notificaciones/correo")
                    .bodyValue(correo)
                    .retrieve()
                    .bodyToMono(NotificacionDTO.class)
                    .block();
            return respuesta != null && respuesta.isEnviado();
        } catch (Exception e) {
            System.out.println("No se pudo llamar a ms-notificaciones: " + e.getMessage());
            return false;
        }
    }
}
