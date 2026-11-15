package cl.rinconsur.ventas.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraDTO {

    private Long id;

    private Long idPersona;

    private Long idCajero;

    private LocalDateTime fechaHora;

    private String numeroBoleta;

    private String nombreCliente;

    private String correoCliente;

    private String rutaBoleta;

    private LocalDateTime fechaModificacion;
}
