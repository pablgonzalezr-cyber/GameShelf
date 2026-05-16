package GameShelf.ms_autorizacion.service;

import GameShelf.ms_autorizacion.client.RolClient;
import GameShelf.ms_autorizacion.client.UsuarioClient;
import GameShelf.ms_autorizacion.dto.AutorizacionRequestDTO;
import GameShelf.ms_autorizacion.dto.AutorizacionResponseDTO;
import GameShelf.ms_autorizacion.dto.ValidarAutorizacionRequestDTO;
import GameShelf.ms_autorizacion.exception.DatoInvalidoException;
import GameShelf.ms_autorizacion.exception.RecursoNoEncontradoException;
import GameShelf.ms_autorizacion.model.AutorizacionModel;
import GameShelf.ms_autorizacion.repository.AutorizacionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AutorizacionServiceImpl implements AutorizacionService {

    private final AutorizacionRepository autorizacionRepository;
    private final UsuarioClient usuarioClient;
    private final RolClient rolClient;

    public AutorizacionServiceImpl(AutorizacionRepository autorizacionRepository,
                                   UsuarioClient usuarioClient,
                                   RolClient rolClient) {
        this.autorizacionRepository = autorizacionRepository;
        this.usuarioClient = usuarioClient;
        this.rolClient = rolClient;
    }

    @Override
    public AutorizacionResponseDTO crearAutorizacion(AutorizacionRequestDTO autorizacionRequestDTO) {

        validarUsuario(autorizacionRequestDTO.getUsuarioId());
        validarRol(autorizacionRequestDTO.getRol());
        validarEstado(autorizacionRequestDTO.getEstado());
        validarPermiso(autorizacionRequestDTO.getPermiso());

        AutorizacionModel autorizacion = new AutorizacionModel();
        autorizacion.setUsuarioId(autorizacionRequestDTO.getUsuarioId());
        autorizacion.setRol(autorizacionRequestDTO.getRol().toUpperCase());
        autorizacion.setModulo(autorizacionRequestDTO.getModulo().toUpperCase());
        autorizacion.setPermiso(autorizacionRequestDTO.getPermiso().toUpperCase());
        autorizacion.setEstado(autorizacionRequestDTO.getEstado().toUpperCase());

        AutorizacionModel autorizacionGuardada = autorizacionRepository.save(autorizacion);

        return convertirAResponseDTO(autorizacionGuardada);
    }

    @Override
    public List<AutorizacionResponseDTO> listarAutorizaciones() {
        List<AutorizacionModel> autorizaciones = autorizacionRepository.findAll();
        List<AutorizacionResponseDTO> respuesta = new ArrayList<>();

        for (AutorizacionModel autorizacion : autorizaciones) {
            respuesta.add(convertirAResponseDTO(autorizacion));
        }

        return respuesta;
    }

    @Override
    public AutorizacionResponseDTO obtenerAutorizacionPorId(Long id) {
        AutorizacionModel autorizacion = autorizacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Autorización no encontrada"));

        return convertirAResponseDTO(autorizacion);
    }

    @Override
    public List<AutorizacionResponseDTO> listarPorUsuario(Long usuarioId) {
        validarUsuario(usuarioId);

        List<AutorizacionModel> autorizaciones = autorizacionRepository.findByUsuarioId(usuarioId);
        List<AutorizacionResponseDTO> respuesta = new ArrayList<>();

        for (AutorizacionModel autorizacion : autorizaciones) {
            respuesta.add(convertirAResponseDTO(autorizacion));
        }

        return respuesta;
    }

    @Override
    public AutorizacionResponseDTO actualizarAutorizacion(Long id, AutorizacionRequestDTO autorizacionRequestDTO) {

        AutorizacionModel autorizacion = autorizacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Autorización no encontrada"));

        validarUsuario(autorizacionRequestDTO.getUsuarioId());
        validarRol(autorizacionRequestDTO.getRol());
        validarEstado(autorizacionRequestDTO.getEstado());
        validarPermiso(autorizacionRequestDTO.getPermiso());

        autorizacion.setUsuarioId(autorizacionRequestDTO.getUsuarioId());
        autorizacion.setRol(autorizacionRequestDTO.getRol().toUpperCase());
        autorizacion.setModulo(autorizacionRequestDTO.getModulo().toUpperCase());
        autorizacion.setPermiso(autorizacionRequestDTO.getPermiso().toUpperCase());
        autorizacion.setEstado(autorizacionRequestDTO.getEstado().toUpperCase());

        AutorizacionModel autorizacionActualizada = autorizacionRepository.save(autorizacion);

        return convertirAResponseDTO(autorizacionActualizada);
    }

    @Override
    public void eliminarAutorizacion(Long id) {
        AutorizacionModel autorizacion = autorizacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Autorización no encontrada"));

        autorizacionRepository.delete(autorizacion);
    }

    @Override
    public boolean validarAutorizacion(ValidarAutorizacionRequestDTO validarAutorizacionRequestDTO) {
        validarUsuario(validarAutorizacionRequestDTO.getUsuarioId());
        validarPermiso(validarAutorizacionRequestDTO.getPermiso());

        return autorizacionRepository.existsByUsuarioIdAndModuloIgnoreCaseAndPermisoIgnoreCaseAndEstadoIgnoreCase(
                validarAutorizacionRequestDTO.getUsuarioId(),
                validarAutorizacionRequestDTO.getModulo(),
                validarAutorizacionRequestDTO.getPermiso(),
                "ACTIVO"
        );
    }

    private void validarUsuario(Long usuarioId) {
        try {
            usuarioClient.obtenerUsuarioPorId(usuarioId);
        } catch (Exception e) {
            throw new DatoInvalidoException("El usuario no existe");
        }
    }

    private void validarRol(String rol) {
        try {
            Boolean existe = rolClient.validarRol(rol);

            if (existe == null || !existe) {
                throw new DatoInvalidoException("El rol no existe");
            }

        } catch (DatoInvalidoException e) {
            throw e;
        } catch (Exception e) {
            throw new DatoInvalidoException("No se pudo validar el rol en ms-roles");
        }
    }

    private void validarEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new DatoInvalidoException("El estado es obligatorio");
        }

        if (!estado.equalsIgnoreCase("ACTIVO") && !estado.equalsIgnoreCase("INACTIVO")) {
            throw new DatoInvalidoException("El estado debe ser ACTIVO o INACTIVO");
        }
    }

    private void validarPermiso(String permiso) {
        if (permiso == null || permiso.trim().isEmpty()) {
            throw new DatoInvalidoException("El permiso es obligatorio");
        }

        boolean esLectura = permiso.equalsIgnoreCase("LECTURA");
        boolean esEscritura = permiso.equalsIgnoreCase("ESCRITURA");
        boolean esAdmin = permiso.equalsIgnoreCase("ADMIN");
        boolean esTotal = permiso.equalsIgnoreCase("TOTAL");

        if (!esLectura && !esEscritura && !esAdmin && !esTotal) {
            throw new DatoInvalidoException("El permiso debe ser LECTURA, ESCRITURA, ADMIN o TOTAL");
        }
    }

    private AutorizacionResponseDTO convertirAResponseDTO(AutorizacionModel autorizacion) {
        return new AutorizacionResponseDTO(
                autorizacion.getId(),
                autorizacion.getUsuarioId(),
                autorizacion.getRol(),
                autorizacion.getModulo(),
                autorizacion.getPermiso(),
                autorizacion.getEstado()
        );
    }
}