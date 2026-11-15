package cl.rinconsur.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccesoDTO {

    private Long id;

    private String codigo;

    private String nombre;

    private boolean activo;
}
