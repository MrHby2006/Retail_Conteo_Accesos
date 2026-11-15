package cl.rinconsur.accesos.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatalogosDTO {

    private List<OpcionDTO> rangos;

    private List<OpcionDTO> alturas;

    private List<OpcionDTO> colores;

    private List<OpcionDTO> companias;
}
