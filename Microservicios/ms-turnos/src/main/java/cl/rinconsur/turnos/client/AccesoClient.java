package cl.rinconsur.turnos.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import cl.rinconsur.turnos.dto.AccesoDTO;

@Component
public class AccesoClient {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public List<AccesoDTO> listarActivos() {
        return webClientBuilder.build()
                .get()
                .uri("http://ms-accesos/api/v1/accesos?soloActivos=true")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<AccesoDTO>>() {})
                .block();
    }
}
