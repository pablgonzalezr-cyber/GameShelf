package GameShelf.ms_categoria.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import GameShelf.ms_categoria.dto.CategoriaRequestDTO;
import GameShelf.ms_categoria.dto.CategoriaResponseDTO;
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

        if (categoriaRepository.existsByNombre(categoriaRequestDTO.getNombre())) {
            throw new RuntimeException("La categoría ya existe");
        }
        
        CategoriaModel categoria = new CategoriaModel();
        categoria.setNombre(categoriaRequestDTO.getNombre());
        categoria.setDescripcion(categoriaRequestDTO.getDescripcion());

        if (categoriaRequestDTO.getEstado() == null || categoriaRequestDTO.getEstado().isEmpty()) {
            categoria.setEstado("ACTIVA");
        } else {
            categoria.setEstado(categoriaRequestDTO.getEstado());
        }

        CategoriaModel categoriaGuardada = categoriaRepository.save(categoria);

        log.info("Categoría creada con ID: {}", categoriaGuardada.getId());

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

        log.info("Buscando categoría con ID: {}", id);

        CategoriaModel categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        return convertirAResponseDTO(categoria);
    }

    @Override
    public CategoriaResponseDTO buscarPorNombreExacto(String nombre) {

        log.info("Buscando categoría exacta por nombre: {}", nombre);

        CategoriaModel categoria = categoriaRepository.findByNombre(nombre)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        return convertirAResponseDTO(categoria);
    }

    @Override
    public CategoriaResponseDTO actualizarCategoria(Long id, CategoriaRequestDTO categoriaRequestDTO) {

        log.info("Actualizando categoría con ID: {}", id);

        CategoriaModel categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        categoria.setNombre(categoriaRequestDTO.getNombre());
        categoria.setDescripcion(categoriaRequestDTO.getDescripcion());

        if (categoriaRequestDTO.getEstado() != null && !categoriaRequestDTO.getEstado().isEmpty()) {
            categoria.setEstado(categoriaRequestDTO.getEstado());
        }

        CategoriaModel categoriaActualizada = categoriaRepository.save(categoria);

        log.info("Categoría actualizada con ID: {}", categoriaActualizada.getId());

        return convertirAResponseDTO(categoriaActualizada);
    }

    @Override
    public void eliminarCategoria(Long id) {

        log.info("Eliminando categoría con ID: {}", id);

        CategoriaModel categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        categoriaRepository.delete(categoria);

        log.info("Categoría eliminada con ID: {}", id);
    }

    @Override
    public List<CategoriaResponseDTO> buscarPorEstado(String estado) {

        log.info("Buscando categorías por estado: {}", estado);

        List<CategoriaModel> categorias = categoriaRepository.findByEstado(estado);
        List<CategoriaResponseDTO> respuesta = new ArrayList<>();

        for (CategoriaModel categoria : categorias) {
            respuesta.add(convertirAResponseDTO(categoria));
        }

        return respuesta;
    }

    @Override
    public List<CategoriaResponseDTO> buscarPorNombre(String nombre) {

        log.info("Buscando categorías por nombre: {}", nombre);

        List<CategoriaModel> categorias = categoriaRepository.findByNombreContainingIgnoreCase(nombre);
        List<CategoriaResponseDTO> respuesta = new ArrayList<>();

        for (CategoriaModel categoria : categorias) {
            respuesta.add(convertirAResponseDTO(categoria));
        }

        return respuesta;
    }

    private CategoriaResponseDTO convertirAResponseDTO(CategoriaModel categoria) {
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.getEstado()
        );
    }
}