package cl.rinconsur.ventas.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import cl.rinconsur.ventas.dto.IngresoDTO;

@Component
public class AccesoClient {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public IngresoDTO buscarIngreso(Long idPersona) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("http://ms-accesos/api/v1/ingresos/{id}", idPersona)
                    .retrieve()
                    .bodyToMono(IngresoDTO.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("No se encontró a la persona " + idPersona + " en ms-accesos");
        }
    }

    public void marcarCompra(Long idPersona) {
        webClientBuilder.build()
                .put()
                .uri("http://ms-accesos/api/v1/ingresos/{id}/compra", idPersona)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void quitarCompra(Long idPersona) {
        webClientBuilder.build()
                .put()
                .uri("http://ms-accesos/api/v1/ingresos/{id}/quitar-compra", idPersona)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
