package cl.rinconsur.usuarios.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.rinconsur.usuarios.dto.RolDTO;
import cl.rinconsur.usuarios.model.Permiso;
import cl.rinconsur.usuarios.model.Rol;
import cl.rinconsur.usuarios.repository.RolRepository;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRep;

    public List<RolDTO> listar() {
        List<RolDTO> lista = new ArrayList<>();
        for (Rol rol : rolRep.findAll()) {
            List<String> permisos = new ArrayList<>();
            for (Permiso permiso : rol.getPermisos()) {
                permisos.add(permiso.getCodigo());
            }
            lista.add(new RolDTO(rol.getId(), rol.getCodigo(), rol.getNombre(), rol.getDescripcion(), permisos));
        }
        return lista;
    }
}
