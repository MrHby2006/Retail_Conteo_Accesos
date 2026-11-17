package cl.rinconsur.reportes.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import cl.rinconsur.reportes.dto.UsuarioDTO;

@Component
public class UsuarioClient {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public UsuarioDTO buscar(Long id) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("http://ms-usuarios/api/v1/usuarios/{id}", id)
                    .retrieve()
                    .bodyToMono(UsuarioDTO.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("No se encontró al cajero " + id + " en ms-usuarios");
        }
    }
}
