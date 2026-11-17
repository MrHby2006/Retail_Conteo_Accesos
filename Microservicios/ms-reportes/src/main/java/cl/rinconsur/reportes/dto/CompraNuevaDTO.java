package cl.rinconsur.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraNuevaDTO {

    private Long idPersona;

    private Long idCajero;

    private String numeroBoleta;

    private String nombreCliente;

    private String correoCliente;

    private String rutaBoleta;
}
