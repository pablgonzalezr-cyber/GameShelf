package GameShelf.ms_roles.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import GameShelf.ms_roles.dto.RolRequestDTO;
import GameShelf.ms_roles.dto.RolResponseDTO;
import GameShelf.ms_roles.exception.DatoDuplicadoException;
import GameShelf.ms_roles.exception.RolNoEncontradoException;
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

        String nombreRol = rolRequestDTO.getNombre().trim().toUpperCase();

        if (rolRepository.existsByNombre(nombreRol)) {
            log.warn("Intento de crear rol duplicado: {}", nombreRol);
            throw new DatoDuplicadoException("El rol ya existe");
        }

        Rol rol = new Rol();
        rol.setNombre(nombreRol);
        rol.setDescripcion(rolRequestDTO.getDescripcion());
        rol.setEstado(validarEstado(rolRequestDTO.getEstado()));

        Rol rolGuardado = rolRepository.save(rol);

        log.info("Rol creado correctamente con ID: {}", rolGuardado.getId());

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

        log.info("Total de roles encontrados: {}", respuesta.size());

        return respuesta;
    }

    @Override
    public RolResponseDTO buscarPorId(Long id) {

        log.info("Buscando rol con ID: {}", id);

        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RolNoEncontradoException("Rol no encontrado con ID: " + id));

        return convertirAResponseDTO(rol);
    }

    @Override
    public RolResponseDTO actualizarRol(Long id, RolRequestDTO rolRequestDTO) {

        log.info("Actualizando rol con ID: {}", id);

        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RolNoEncontradoException("Rol no encontrado con ID: " + id));

        String nombreRol = rolRequestDTO.getNombre().trim().toUpperCase();

        if (rolRepository.existsByNombreAndIdNot(nombreRol, id)) {
            log.warn("Intento de actualizar con nombre de rol duplicado: {}", nombreRol);
            throw new DatoDuplicadoException("El nombre del rol ya existe");
        }

        rol.setNombre(nombreRol);
        rol.setDescripcion(rolRequestDTO.getDescripcion());
        rol.setEstado(validarEstado(rolRequestDTO.getEstado()));

        Rol rolActualizado = rolRepository.save(rol);

        log.info("Rol actualizado correctamente con ID: {}", rolActualizado.getId());

        return convertirAResponseDTO(rolActualizado);
    }

    @Override
    public void eliminarRol(Long id) {

        log.info("Desactivando rol con ID: {}", id);

         Rol rol = rolRepository.findById(id)
            .orElseThrow(() -> new RolNoEncontradoException("Rol no encontrado con ID: " + id));

        rol.setEstado("INACTIVO");

        rolRepository.save(rol);

        log.info("Rol desactivado correctamente con ID: {}", id);
    }

    @Override
    public List<RolResponseDTO> buscarPorEstado(String estado) {

        String estadoValidado = validarEstado(estado);

        log.info("Buscando roles por estado: {}", estadoValidado);

        List<Rol> roles = rolRepository.findByEstado(estadoValidado);
        List<RolResponseDTO> respuesta = new ArrayList<>();

        for (Rol rol : roles) {
            respuesta.add(convertirAResponseDTO(rol));
        }

        log.info("Roles encontrados con estado {}: {}", estadoValidado, respuesta.size());

        return respuesta;
    }

    @Override
    public List<RolResponseDTO> buscarPorNombre(String nombre) {

        log.info("Buscando roles que contengan: {}", nombre);

        List<Rol> roles = rolRepository.findByNombreContainingIgnoreCase(nombre);
        List<RolResponseDTO> respuesta = new ArrayList<>();

        for (Rol rol : roles) {
            respuesta.add(convertirAResponseDTO(rol));
        }

        log.info("Roles encontrados con bÃºsqueda {}: {}", nombre, respuesta.size());

        return respuesta;
    }

    @Override
    public RolResponseDTO buscarPorNombreExacto(String nombre) {

        String nombreRol = nombre.trim().toUpperCase();

        log.info("Buscando rol exacto por nombre: {}", nombreRol);

        Rol rol = rolRepository.findByNombre(nombreRol)
                .orElseThrow(() -> new RolNoEncontradoException("Rol no encontrado con nombre: " + nombreRol));

        return convertirAResponseDTO(rol);
    }

    @Override
    public boolean validarRolActivo(String nombre) {

        if (nombre == null || nombre.isBlank()) {
            return false;
        }

        String nombreRol = nombre.trim().toUpperCase();

        log.info("Validando si el rol existe y está activo: {}", nombreRol);

        return rolRepository.existsByNombreAndEstado(nombreRol, "ACTIVO");
    }

    private String validarEstado(String estado) {

        if (estado == null || estado.isBlank()) {
            return "ACTIVO";
        }

        String estadoMayuscula = estado.toUpperCase();

        if (!estadoMayuscula.equals("ACTIVO") && !estadoMayuscula.equals("INACTIVO")) {
            throw new DatoDuplicadoException("El estado debe ser ACTIVO o INACTIVO");
        }

        return estadoMayuscula;
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