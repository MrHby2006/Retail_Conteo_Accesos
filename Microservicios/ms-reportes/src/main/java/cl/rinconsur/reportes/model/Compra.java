package cl.rinconsur.reportes.model;

import java.time.LocalDateTime;

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
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra")
    private Long id;

    @Column(name = "id_persona", nullable = false, unique = true)
    private Long idPersona;

    @Column(name = "id_cajero", nullable = false)
    private Long idCajero;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "numero_boleta", nullable = false, unique = true, length = 20)
    private String numeroBoleta;

    @Column(name = "nombre_cliente", length = 60)
    private String nombreCliente;

    @Column(name = "correo_cliente", length = 100)
    private String correoCliente;

    @Column(name = "ruta_boleta")
    private String rutaBoleta;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
}
