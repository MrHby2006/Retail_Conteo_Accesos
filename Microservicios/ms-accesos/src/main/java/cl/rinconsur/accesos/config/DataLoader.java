package cl.rinconsur.accesos.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.rinconsur.accesos.model.Acceso;
import cl.rinconsur.accesos.model.Altura;
import cl.rinconsur.accesos.model.Color;
import cl.rinconsur.accesos.model.RangoEtario;
import cl.rinconsur.accesos.model.TipoCompania;
import cl.rinconsur.accesos.repository.AccesoRepository;
import cl.rinconsur.accesos.repository.AlturaRepository;
import cl.rinconsur.accesos.repository.ColorRepository;
import cl.rinconsur.accesos.repository.RangoEtarioRepository;
import cl.rinconsur.accesos.repository.TipoCompaniaRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(AccesoRepository accesoRep, RangoEtarioRepository rangoRep, AlturaRepository alturaRep,
                               ColorRepository colorRep, TipoCompaniaRepository companiaRep) {
        return args -> {
            if (accesoRep.count() > 0) {
                return;
            }
            accesoRep.save(new Acceso(null, "A1", "Acceso Principal", "Frente al pasillo central del mall", true));
            accesoRep.save(new Acceso(null, "A2", "Acceso Lateral", "Costado del patio de comidas", true));
            accesoRep.save(new Acceso(null, "A3", "Acceso Estacionamiento", "Conecta con el estacionamiento subterráneo", true));
            accesoRep.save(new Acceso(null, "A4", "Acceso Conexión", "Comunica con la tienda vecina", true));

            rangoRep.save(new RangoEtario(null, "Niño", 0, 12, 1));
            rangoRep.save(new RangoEtario(null, "Adolescente", 13, 17, 2));
            rangoRep.save(new RangoEtario(null, "Joven", 18, 29, 3));
            rangoRep.save(new RangoEtario(null, "Adulto", 30, 59, 4));
            rangoRep.save(new RangoEtario(null, "Mayor", 60, 120, 5));

            alturaRep.save(new Altura(null, "Baja", 1));
            alturaRep.save(new Altura(null, "Media", 2));
            alturaRep.save(new Altura(null, "Alta", 3));

            colorRep.save(new Color(null, "Negro", "#000000"));
            colorRep.save(new Color(null, "Blanco", "#FFFFFF"));
            colorRep.save(new Color(null, "Gris", "#808080"));
            colorRep.save(new Color(null, "Azul", "#1F3A5F"));
            colorRep.save(new Color(null, "Rojo", "#B22222"));
            colorRep.save(new Color(null, "Verde", "#2E7D32"));
            colorRep.save(new Color(null, "Café", "#6D4C41"));
            colorRep.save(new Color(null, "Otro", null));

            companiaRep.save(new TipoCompania(null, "Sola"));
            companiaRep.save(new TipoCompania(null, "En pareja"));
            companiaRep.save(new TipoCompania(null, "En grupo"));
            companiaRep.save(new TipoCompania(null, "Con niños"));

            System.out.println("Catálogos de ms-accesos cargados");
        };
    }
}
