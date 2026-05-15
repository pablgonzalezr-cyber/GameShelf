package GameShelf.ms_categoria.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import GameShelf.ms_categoria.dto.CategoriaRequestDTO;
import GameShelf.ms_categoria.dto.CategoriaResponseDTO;
import GameShelf.ms_categoria.exception.CategoriaNoEncontradaException;
import GameShelf.ms_categoria.exception.DatoDuplicadoException;
import GameShelf.ms_categoria.exception.DatoInvalidoException;
import GameShelf.ms_categoria.model.CategoriaModel;
import GameShelf.ms_categoria.repository.CategoriaRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service	
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public CategoriaResponseDTO crearCategoria(CategoriaRequestDTO categoriaRequestDTO) {

        log.info("Creando categoría: {}", categoriaRequestDTO.getNombre());

        String nombreLimpio = categoriaRequestDTO.getNombre().trim().toUpperCase();

        if (categoriaRepository.existsByNombre(nombreLimpio)) {
            throw new DatoDuplicadoException("La categoría ya existe");
        }

        CategoriaModel categoria = new CategoriaModel();
        categoria.setNombre(nombreLimpio);
        categoria.setDescripcion(categoriaRequestDTO.getDescripcion().trim());
        categoria.setEstado(validarEstado(categoriaRequestDTO.getEstado()));

        CategoriaModel categoriaGuardada = categoriaRepository.save(categoria);

        log.info("Categoría creada correctamente con ID: {}", categoriaGuardada.getId());

        return convertirAResponseDTO(categoriaGuardada);
    }

    @Override
    public List<CategoriaResponseDTO> listarCategorias() {

        log.info("Listando categorías");

        List<CategoriaModel> categorias = categoriaRepository.findAll();
        List<CategoriaResponseDTO> respuesta = new ArrayList<>();

        for (CategoriaModel categoria : categorias) {
            respuesta.add(convertirAResponseDTO(categoria));
        }

        return respuesta;
    }

    @Override
    public CategoriaResponseDTO buscarPorId(Long id) {

        log.info("Buscando categoría por ID: {}", id);

        CategoriaModel categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNoEncontradaException("Categoría no encontrada"));

        return convertirAResponseDTO(categoria);
    }

    @Override
    public CategoriaResponseDTO buscarPorNombreExacto(String nombre) {

        log.info("Buscando categoría exacta por nombre: {}", nombre);

        String nombreLimpio = nombre.trim().toUpperCase();

        CategoriaModel categoria = categoriaRepository.findByNombre(nombreLimpio)
                .orElseThrow(() -> new CategoriaNoEncontradaException("Categoría no encontrada"));

        return convertirAResponseDTO(categoria);
    }

    @Override
    public CategoriaResponseDTO actualizarCategoria(Long id, CategoriaRequestDTO categoriaRequestDTO) {

        log.info("Actualizando categoría con ID: {}", id);

        CategoriaModel categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNoEncontradaException("Categoría no encontrada"));

        String nombreLimpio = categoriaRequestDTO.getNombre().trim().toUpperCase();

        if (!categoria.getNombre().equals(nombreLimpio) && categoriaRepository.existsByNombre(nombreLimpio)) {
            throw new DatoDuplicadoException("Ya existe otra categoría con ese nombre");
        }

        categoria.setNombre(nombreLimpio);
        categoria.setDescripcion(categoriaRequestDTO.getDescripcion().trim());
        categoria.setEstado(validarEstado(categoriaRequestDTO.getEstado()));

        CategoriaModel categoriaActualizada = categoriaRepository.save(categoria);

        log.info("Categoría actualizada correctamente con ID: {}", categoriaActualizada.getId());

        return convertirAResponseDTO(categoriaActualizada);
    }

    @Override
    public void eliminarCategoria(Long id) {

        log.info("Desactivando categoría con ID: {}", id);

        CategoriaModel categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNoEncontradaException("Categoría no encontrada"));

        categoria.setEstado("INACTIVO");

        categoriaRepository.save(categoria);

        log.info("Categoría desactivada correctamente con ID: {}", id);
    }

    @Override
    public List<CategoriaResponseDTO> buscarPorEstado(String estado) {

        log.info("Buscando categorías por estado: {}", estado);

        String estadoLimpio = validarEstado(estado);

        List<CategoriaModel> categorias = categoriaRepository.findByEstado(estadoLimpio);
        List<CategoriaResponseDTO> respuesta = new ArrayList<>();

        for (CategoriaModel categoria : categorias) {
            respuesta.add(convertirAResponseDTO(categoria));
        }

        return respuesta;
    }

    @Override
    public List<CategoriaResponseDTO> buscarPorNombre(String nombre) {

        log.info("Buscando categorías que contengan el nombre: {}", nombre);

        List<CategoriaModel> categorias = categoriaRepository.findByNombreContainingIgnoreCase(nombre);
        List<CategoriaResponseDTO> respuesta = new ArrayList<>();

        for (CategoriaModel categoria : categorias) {
            respuesta.add(convertirAResponseDTO(categoria));
        }

        return respuesta;
    }

    private String validarEstado(String estado) {

        if (estado == null || estado.trim().isEmpty()) {
            return "ACTIVO";
        }

        String estadoLimpio = estado.trim().toUpperCase();

        if (!estadoLimpio.equals("ACTIVO") && !estadoLimpio.equals("INACTIVO")) {
            throw new DatoInvalidoException("El estado debe ser ACTIVO o INACTIVO");
        }

        return estadoLimpio;
    }

    private CategoriaResponseDTO convertirAResponseDTO(CategoriaModel categoria) {

        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.getEstado());
    }
}
