package cl.rinconsur.reportes.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cl.rinconsur.reportes.client.AccesoClient;
import cl.rinconsur.reportes.client.UsuarioClient;
import cl.rinconsur.reportes.dto.CompraDTO;
import cl.rinconsur.reportes.dto.CompraNuevaDTO;
import cl.rinconsur.reportes.model.Compra;
import cl.rinconsur.reportes.repository.CompraRepository;

@Service
public class CompraService {

    @Autowired
    private CompraRepository compraRep;

    @Autowired
    private AccesoClient accesoCli;

    @Autowired
    private UsuarioClient usuarioCli;

    @Value("${boletas.carpeta:boletas}")
    private String carpetaBoletas;

    public CompraDTO registrar(CompraNuevaDTO dto) {
        if (dto.getIdPersona() == null || dto.getIdCajero() == null
                || dto.getNumeroBoleta() == null || dto.getNumeroBoleta().isBlank()) {
            throw new RuntimeException("Faltan la persona, el cajero o el número de boleta");
        }
        if (usuarioCli.buscar(dto.getIdCajero()) == null) {
            throw new RuntimeException("No se encontró al cajero");
        }

        Compra compra = new Compra();
        compra.setIdPersona(dto.getIdPersona());
        compra.setIdCajero(dto.getIdCajero());
        compra.setFechaHora(LocalDateTime.now());
        compra.setNumeroBoleta(dto.getNumeroBoleta().trim());
        compra.setNombreCliente(dto.getNombreCliente());
        compra.setCorreoCliente(dto.getCorreoCliente());
        compra.setRutaBoleta(dto.getRutaBoleta());
        compraRep.save(compra);

        try {
            accesoCli.marcarCompra(dto.getIdPersona());
        } catch (Exception e) {
            compraRep.delete(compra);
            throw new RuntimeException("No se pudo marcar a la persona como compradora, la compra no se guardó");
        }
        return toDTO(compra);
    }

    public List<CompraDTO> listar(LocalDate fecha) {
        List<Compra> compras = fecha == null
                ? compraRep.findAllByOrderByFechaHoraDesc()
                : compraRep.findByFechaHoraBetweenOrderByFechaHoraDesc(fecha.atStartOfDay(), fecha.plusDays(1).atStartOfDay());
        List<CompraDTO> lista = new ArrayList<>();
        for (Compra c : compras) {
            lista.add(toDTO(c));
        }
        return lista;
    }

    public CompraDTO buscarPorId(Long id) {
        return toDTO(buscar(id));
    }

    public CompraDTO editar(Long id, CompraNuevaDTO dto) {
        Compra compra = buscar(id);
        if (dto.getNumeroBoleta() != null && !dto.getNumeroBoleta().isBlank()) {
            compra.setNumeroBoleta(dto.getNumeroBoleta().trim());
        }
        compra.setNombreCliente(dto.getNombreCliente());
        compra.setCorreoCliente(dto.getCorreoCliente());
        compra.setFechaModificacion(LocalDateTime.now());
        return toDTO(compraRep.save(compra));
    }

    public void eliminar(Long id) {
        Compra compra = buscar(id);
        try {
            accesoCli.quitarCompra(compra.getIdPersona());
        } catch (Exception e) {
            throw new RuntimeException("No se pudo actualizar a la persona en ms-accesos, la compra no se eliminó");
        }
        compraRep.delete(compra);
    }

    public CompraDTO guardarFotoBoleta(Long id, MultipartFile archivo) throws IOException {
        Compra compra = buscar(id);
        if (archivo == null || archivo.isEmpty()) {
            throw new RuntimeException("No llegó la foto de la boleta");
        }
        Path carpeta = Paths.get(carpetaBoletas);
        Files.createDirectories(carpeta);
        Path destino = carpeta.resolve("boleta_" + compra.getId() + ".jpg");
        Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        compra.setRutaBoleta(destino.toString());
        compra.setFechaModificacion(LocalDateTime.now());
        return toDTO(compraRep.save(compra));
    }

    public byte[] leerFotoBoleta(Long id) throws IOException {
        Compra compra = buscar(id);
        if (compra.getRutaBoleta() == null || !Files.exists(Paths.get(compra.getRutaBoleta()))) {
            throw new RuntimeException("Esta compra no tiene foto de boleta");
        }
        return Files.readAllBytes(Paths.get(compra.getRutaBoleta()));
    }

    private Compra buscar(Long id) {
        return compraRep.findById(id).orElseThrow(() -> new RuntimeException("Compra no encontrada"));
    }

    private CompraDTO toDTO(Compra c) {
        return new CompraDTO(c.getId(), c.getIdPersona(), c.getIdCajero(), c.getFechaHora(), c.getNumeroBoleta(),
                c.getNombreCliente(), c.getCorreoCliente(), c.getRutaBoleta(), c.getFechaModificacion());
    }
}
