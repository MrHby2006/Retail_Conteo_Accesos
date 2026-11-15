package cl.rinconsur.ventas.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngresoDTO {

    private Long id;

    private String codigoAcceso;

    private LocalDateTime fechaHora;

    private boolean compro;

    private boolean anulado;
}
