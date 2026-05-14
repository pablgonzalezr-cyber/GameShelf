package GameShelf.ms_categoria.service;

import java.util.List;

import GameShelf.ms_categoria.dto.CategoriaRequestDTO;
import GameShelf.ms_categoria.dto.CategoriaResponseDTO;

public interface CategoriaService {

    CategoriaResponseDTO crearCategoria(CategoriaRequestDTO categoriaRequestDTO);

    List<CategoriaResponseDTO> listarCategorias();

    CategoriaResponseDTO buscarPorId(Long id);

    CategoriaResponseDTO buscarPorNombreExacto(String nombre);

    CategoriaResponseDTO actualizarCategoria(Long id, CategoriaRequestDTO categoriaRequestDTO);

    void eliminarCategoria(Long id);

    List<CategoriaResponseDTO> buscarPorEstado(String estado);

    List<CategoriaResponseDTO> buscarPorNombre(String nombre);
}