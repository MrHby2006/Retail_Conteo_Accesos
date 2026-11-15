package cl.rinconsur.turnos.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import cl.rinconsur.turnos.dto.UsuarioDTO;

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
            throw new RuntimeException("No se encontró al usuario " + id + " en ms-usuarios");
        }
    }

    public List<UsuarioDTO> listar() {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("http://ms-usuarios/api/v1/usuarios")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<UsuarioDTO>>() {})
                    .block();
        } catch (Exception e) {
            System.out.println("No se pudo listar los usuarios: " + e.getMessage());
            return List.of();
        }
    }

    public Map<Long, UsuarioDTO> mapaPorId() {
        Map<Long, UsuarioDTO> mapa = new HashMap<>();
        for (UsuarioDTO u : listar()) {
            mapa.put(u.getId(), u);
        }
        return mapa;
    }
}
