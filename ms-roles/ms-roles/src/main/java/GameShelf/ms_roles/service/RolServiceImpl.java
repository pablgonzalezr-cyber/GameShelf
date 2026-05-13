package GameShelf.ms_roles.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import GameShelf.ms_roles.dto.RolRequestDTO;
import GameShelf.ms_roles.dto.RolResponseDTO;
import GameShelf.ms_roles.model.Rol;
import GameShelf.ms_roles.repository.RolRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;

    public RolServiceImpl(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    public RolResponseDTO crearRol(RolRequestDTO rolRequestDTO) {

        log.info("Creando rol: {}", rolRequestDTO.getNombre());

        if (rolRepository.existsByNombre(rolRequestDTO.getNombre())) {
            throw new RuntimeException("El rol ya existe");
        }

        Rol rol = new Rol();
        rol.setNombre(rolRequestDTO.getNombre());
        rol.setDescripcion(rolRequestDTO.getDescripcion());

        if (rolRequestDTO.getEstado() == null || rolRequestDTO.getEstado().isEmpty()) {
            rol.setEstado("ACTIVO");
        } else {
            rol.setEstado(rolRequestDTO.getEstado());
        }

        Rol rolGuardado = rolRepository.save(rol);

        log.info("Rol creado con ID: {}", rolGuardado.getId());

        return convertirAResponseDTO(rolGuardado);
    }

    @Override
    public List<RolResponseDTO> listarRoles() {

        log.info("Listando roles");

        List<Rol> roles = rolRepository.findAll();
        List<RolResponseDTO> respuesta = new ArrayList<>();

        for (Rol rol : roles) {
            respuesta.add(convertirAResponseDTO(rol));
        }

        return respuesta;
    }

    @Override
    public RolResponseDTO buscarPorId(Long id) {

        log.info("Buscando rol con ID: {}", id);

        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        return convertirAResponseDTO(rol);
    }

    @Override
    public RolResponseDTO actualizarRol(Long id, RolRequestDTO rolRequestDTO) {

        log.info("Actualizando rol con ID: {}", id);

        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        rol.setNombre(rolRequestDTO.getNombre());
        rol.setDescripcion(rolRequestDTO.getDescripcion());

        if (rolRequestDTO.getEstado() != null && !rolRequestDTO.getEstado().isEmpty()) {
            rol.setEstado(rolRequestDTO.getEstado());
        }

        Rol rolActualizado = rolRepository.save(rol);

        log.info("Rol actualizado con ID: {}", rolActualizado.getId());

        return convertirAResponseDTO(rolActualizado);
    }

    @Override
    public void eliminarRol(Long id) {

        log.info("Eliminando rol con ID: {}", id);

        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        rolRepository.delete(rol);

        log.info("Rol eliminado con ID: {}", id);
    }

    @Override
    public List<RolResponseDTO> buscarPorEstado(String estado) {

        log.info("Buscando roles por estado: {}", estado);

        List<Rol> roles = rolRepository.findByEstado(estado);
        List<RolResponseDTO> respuesta = new ArrayList<>();

        for (Rol rol : roles) {
            respuesta.add(convertirAResponseDTO(rol));
        }

        return respuesta;
    }

    @Override
    public List<RolResponseDTO> buscarPorNombre(String nombre) {

        log.info("Buscando roles por nombre: {}", nombre);

        List<Rol> roles = rolRepository.findByNombreContainingIgnoreCase(nombre);
        List<RolResponseDTO> respuesta = new ArrayList<>();

        for (Rol rol : roles) {
            respuesta.add(convertirAResponseDTO(rol));
        }

        return respuesta;
    }

    @Override
    public RolResponseDTO buscarPorNombreExacto(String nombre) {

        log.info("Buscando rol exacto por nombre: {}", nombre);

        Rol rol = rolRepository.findByNombre(nombre)
            .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        return convertirAResponseDTO(rol);
    }

    private RolResponseDTO convertirAResponseDTO(Rol rol) {
        return new RolResponseDTO(
                rol.getId(),
                rol.getNombre(),
                rol.getDescripcion(),
                rol.getEstado()
        );
    }
}
