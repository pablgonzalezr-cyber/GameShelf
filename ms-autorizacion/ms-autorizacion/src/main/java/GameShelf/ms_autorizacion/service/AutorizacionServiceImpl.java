package GameShelf.ms_autorizacion.service;

import GameShelf.ms_autorizacion.client.RolClient;
import GameShelf.ms_autorizacion.client.UsuarioClient;
import GameShelf.ms_autorizacion.dto.AutorizacionRequestDTO;
import GameShelf.ms_autorizacion.dto.AutorizacionResponseDTO;
import GameShelf.ms_autorizacion.dto.UsuarioResponseDTO;
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
        validarModulo(autorizacionRequestDTO.getModulo());
        validarPermiso(autorizacionRequestDTO.getPermiso());
        validarEstado(autorizacionRequestDTO.getEstado());

        AutorizacionModel autorizacion = new AutorizacionModel();

        autorizacion.setUsuarioId(autorizacionRequestDTO.getUsuarioId());
        autorizacion.setRol(autorizacionRequestDTO.getRol().trim().toUpperCase());
        autorizacion.setModulo(autorizacionRequestDTO.getModulo().trim().toUpperCase());
        autorizacion.setPermiso(autorizacionRequestDTO.getPermiso().trim().toUpperCase());
        autorizacion.setEstado(autorizacionRequestDTO.getEstado().trim().toUpperCase());

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

        AutorizacionModel autorizacion = buscarAutorizacion(id);

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

        AutorizacionModel autorizacion = buscarAutorizacion(id);

        validarUsuario(autorizacionRequestDTO.getUsuarioId());
        validarRol(autorizacionRequestDTO.getRol());
        validarModulo(autorizacionRequestDTO.getModulo());
        validarPermiso(autorizacionRequestDTO.getPermiso());
        validarEstado(autorizacionRequestDTO.getEstado());

        autorizacion.setUsuarioId(autorizacionRequestDTO.getUsuarioId());
        autorizacion.setRol(autorizacionRequestDTO.getRol().trim().toUpperCase());
        autorizacion.setModulo(autorizacionRequestDTO.getModulo().trim().toUpperCase());
        autorizacion.setPermiso(autorizacionRequestDTO.getPermiso().trim().toUpperCase());
        autorizacion.setEstado(autorizacionRequestDTO.getEstado().trim().toUpperCase());

        AutorizacionModel autorizacionActualizada = autorizacionRepository.save(autorizacion);

        return convertirAResponseDTO(autorizacionActualizada);
    }

    @Override
    public void eliminarAutorizacion(Long id) {

        AutorizacionModel autorizacion = buscarAutorizacion(id);

        if (autorizacion.getEstado().equalsIgnoreCase("INACTIVO")) {
            throw new DatoInvalidoException("La autorización ya está inactiva");
        }

        autorizacion.setEstado("INACTIVO");

        autorizacionRepository.save(autorizacion);
    }

    @Override
    public boolean validarAutorizacion(ValidarAutorizacionRequestDTO validarAutorizacionRequestDTO) {

        validarUsuario(validarAutorizacionRequestDTO.getUsuarioId());
        validarModulo(validarAutorizacionRequestDTO.getModulo());
        validarPermiso(validarAutorizacionRequestDTO.getPermiso());

        Long usuarioId = validarAutorizacionRequestDTO.getUsuarioId();
        String modulo = validarAutorizacionRequestDTO.getModulo().trim().toUpperCase();
        String permiso = validarAutorizacionRequestDTO.getPermiso().trim().toUpperCase();

        boolean tienePermisoExacto = autorizacionRepository.existsByUsuarioIdAndModuloIgnoreCaseAndPermisoIgnoreCaseAndEstadoIgnoreCase(
                usuarioId,
                modulo,
                permiso,
                "ACTIVO"
        );

        boolean tienePermisoTotal = autorizacionRepository.existsByUsuarioIdAndModuloIgnoreCaseAndPermisoIgnoreCaseAndEstadoIgnoreCase(
                usuarioId,
                modulo,
                "TOTAL",
                "ACTIVO"
        );

        boolean tienePermisoAdmin = autorizacionRepository.existsByUsuarioIdAndModuloIgnoreCaseAndPermisoIgnoreCaseAndEstadoIgnoreCase(
                usuarioId,
                modulo,
                "ADMIN",
                "ACTIVO"
        );

        return tienePermisoExacto || tienePermisoTotal || tienePermisoAdmin;
    }

    private AutorizacionModel buscarAutorizacion(Long id) {

        return autorizacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Autorización no encontrada"));
    }

    private void validarUsuario(Long usuarioId) {

        if (usuarioId == null) {
            throw new DatoInvalidoException("El usuario es obligatorio");
        }

        UsuarioResponseDTO usuario;

        try {
            usuario = usuarioClient.obtenerUsuarioPorId(usuarioId);
        } catch (Exception e) {
            throw new DatoInvalidoException("El usuario no existe");
        }

        if (usuario == null) {
            throw new DatoInvalidoException("El usuario no existe");
        }

        if (usuario.getEstado() != null && !usuario.getEstado().equalsIgnoreCase("ACTIVO")) {
            throw new DatoInvalidoException("El usuario no está activo");
        }
    }

    private void validarRol(String rol) {

        if (rol == null || rol.trim().isEmpty()) {
            throw new DatoInvalidoException("El rol es obligatorio");
        }

        try {
            Boolean existe = rolClient.validarRol(rol.trim().toUpperCase());

            if (existe == null || !existe) {
                throw new DatoInvalidoException("El rol no existe");
            }

        } catch (DatoInvalidoException e) {
            throw e;
        } catch (Exception e) {
            throw new DatoInvalidoException("No se pudo validar el rol en ms-roles");
        }
    }

    private void validarModulo(String modulo) {

        if (modulo == null || modulo.trim().isEmpty()) {
            throw new DatoInvalidoException("El módulo es obligatorio");
        }

        String moduloLimpio = modulo.trim().toUpperCase();

        boolean valido =
                moduloLimpio.equals("USUARIOS") ||
                moduloLimpio.equals("ROLES") ||
                moduloLimpio.equals("VIDEOJUEGOS") ||
                moduloLimpio.equals("CATEGORIAS") ||
                moduloLimpio.equals("STOCK") ||
                moduloLimpio.equals("PRESTAMOS") ||
                moduloLimpio.equals("MULTAS") ||
                moduloLimpio.equals("RESERVAS") ||
                moduloLimpio.equals("NOTIFICACIONES") ||
                moduloLimpio.equals("AUTORIZACIONES");

        if (!valido) {
            throw new DatoInvalidoException("Módulo inválido");
        }
    }

    private void validarPermiso(String permiso) {

        if (permiso == null || permiso.trim().isEmpty()) {
            throw new DatoInvalidoException("El permiso es obligatorio");
        }

        String permisoLimpio = permiso.trim().toUpperCase();

        boolean valido =
                permisoLimpio.equals("LECTURA") ||
                permisoLimpio.equals("ESCRITURA") ||
                permisoLimpio.equals("ADMIN") ||
                permisoLimpio.equals("TOTAL");

        if (!valido) {
            throw new DatoInvalidoException("El permiso debe ser LECTURA, ESCRITURA, ADMIN o TOTAL");
        }
    }

    private void validarEstado(String estado) {

        if (estado == null || estado.trim().isEmpty()) {
            throw new DatoInvalidoException("El estado es obligatorio");
        }

        String estadoLimpio = estado.trim().toUpperCase();

        if (!estadoLimpio.equals("ACTIVO") && !estadoLimpio.equals("INACTIVO")) {
            throw new DatoInvalidoException("El estado debe ser ACTIVO o INACTIVO");
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