package cl.rinconsur.accesos.model;

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
@Table(name = "rango_etario")
public class RangoEtario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rango")
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String nombre;

    @Column(name = "edad_min", nullable = false)
    private int edadMin;

    @Column(name = "edad_max", nullable = false)
    private int edadMax;

    @Column(nullable = false)
    private int orden;
}
