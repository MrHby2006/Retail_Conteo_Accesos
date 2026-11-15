package cl.rinconsur.reportes.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.rinconsur.reportes.model.MetaConversion;
import cl.rinconsur.reportes.model.TipoEvento;
import cl.rinconsur.reportes.repository.MetaConversionRepository;
import cl.rinconsur.reportes.repository.TipoEventoRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(TipoEventoRepository tipoRep, MetaConversionRepository metaRep) {
        return args -> {
            if (tipoRep.count() > 0) {
                return;
            }
            String[] tipos = {"Feriado", "Liquidación", "Evento en el mall", "Acceso cerrado", "Clima"};
            for (String tipo : tipos) {
                tipoRep.save(new TipoEvento(null, tipo));
            }
            LocalDate hoy = LocalDate.now();
            metaRep.save(new MetaConversion(null, hoy.getYear(), hoy.getMonthValue(), new BigDecimal("30.00"), 1L, LocalDateTime.now()));
            System.out.println("Datos de ejemplo de ms-reportes cargados");
        };
    }
}
