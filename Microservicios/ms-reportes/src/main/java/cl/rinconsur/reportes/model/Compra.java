package cl.rinconsur.reportes.model;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "compra")
@Schema(description = "Representa una compra dentro del sistema")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra")
    @Schema(description = "Id único de la compra", example = "4")
    private Long id;

    @Column(name = "id_persona", nullable = false, unique = true)
    @Schema(description = "Id de la persona que compra", example = "1")
    private Long idPersona;

    @Column(name = "id_cajero", nullable = false)
    @Schema(description = "Id del cajero que atiende la compra del cliente", example = "3")
    private Long idCajero;

    @Column(name = "fecha_hora", nullable = false)
    @Schema(description = "Fecha y hora en la que se realizo la compra", example = "06/07/2012-08:40")
    private LocalDateTime fechaHora;

    @Column(name = "numero_boleta", nullable = false, unique = true, length = 20)
    @Schema(description = "Número de la boleta de la compra", example = "236567")
    private String numeroBoleta;

    @Column(name = "nombre_cliente", length = 60)
    @Schema(description = "Nombre del cliente que compra", example = "Juan Carlos")
    private String nombreCliente;

    @Column(name = "correo_cliente", length = 100)
    @Schema(description = "Correo del cliente que compra", example = "juanca@gmail.com")
    private String correoCliente;

    @Column(name = "ruta_boleta")
    @Schema(description = "Ruta donde se guarda la foto de la boleta")
    private String rutaBoleta;

    @Column(name = "fecha_modificacion")
    @Schema(description = "Fecha y hora en la que se modifica la compra", example = "14/08/2016-16:50")
    private LocalDateTime fechaModificacion;
}
