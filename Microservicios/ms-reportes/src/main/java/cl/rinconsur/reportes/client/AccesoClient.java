package cl.rinconsur.reportes.client;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import cl.rinconsur.reportes.dto.AccesoDTO;
import cl.rinconsur.reportes.dto.IngresoDTO;

@Component
public class AccesoClient {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public List<IngresoDTO> listarIngresos(LocalDate desde, LocalDate hasta, Long idAcceso) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(uri -> uri.scheme("http").host("ms-accesos").path("/api/v1/ingresos")
                            .queryParam("desde", desde)
                            .queryParam("hasta", hasta)
                            .queryParamIfPresent("idAcceso", java.util.Optional.ofNullable(idAcceso))
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<IngresoDTO>>() {})
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("No se pudieron traer los ingresos desde ms-accesos");
        }
    }

    public List<AccesoDTO> listarAccesos() {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("http://ms-accesos/api/v1/accesos")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<AccesoDTO>>() {})
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("No se pudieron traer los accesos desde ms-accesos");
        }
    }
}
