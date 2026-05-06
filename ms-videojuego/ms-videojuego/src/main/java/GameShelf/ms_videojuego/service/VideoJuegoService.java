package GameShelf.ms_videojuego.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import GameShelf.ms_videojuego.model.VideoJuegoModel;
import GameShelf.ms_videojuego.repository.VideoJuegoRepository;

public class VideoJuegoService {
    
    @Autowired
    private VideoJuegoRepository videojuegoRepository;

    public VideoJuegoModel guardar(VideoJuegoModel videojuego) {
        return videojuegoRepository.save(videojuego);
    }

    public List<VideoJuegoModel> listarTodos() {
        return videojuegoRepository.findAll();
    }

    public VideoJuegoModel buscarPorId(Long id) {
        return videojuegoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Videojuego no encontrado con ID: " + id));
    }

    public List<VideoJuegoModel> listarPorCategoria(Long categoriaId) {
        return videojuegoRepository.findByCategoriaId(categoriaId);
    }

    public void eliminar(Long id) {
        VideoJuegoModel v = buscarPorId(id);
        videojuegoRepository.delete(v);
    }
}
