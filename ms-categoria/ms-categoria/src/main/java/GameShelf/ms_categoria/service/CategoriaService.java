package GameShelf.ms_categoria.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import GameShelf.ms_categoria.model.CategoriaModel;
import GameShelf.ms_categoria.repository.CategoriaRepository;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public CategoriaModel crearCategoria(CategoriaModel categoria) {
        if (categoriaRepository.existsByNombre(categoria.getNombre())) {
            throw new RuntimeException("La categoría '" + categoria.getNombre() + "' ya existe.");
        }
        return categoriaRepository.save(categoria);
    }

    public List<CategoriaModel> listarTodas() {
        return categoriaRepository.findAll();
    }

    public CategoriaModel buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada."));
    }

    public void eliminarCategoria(Long id) {
        CategoriaModel categoria = buscarPorId(id);
        categoriaRepository.delete(categoria);
    }
}
