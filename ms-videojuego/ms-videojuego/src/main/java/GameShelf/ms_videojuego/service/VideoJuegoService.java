package GameShelf.ms_videojuego.service;

import java.util.List;

import GameShelf.ms_videojuego.dto.VideoJuegoRequestDTO;
import GameShelf.ms_videojuego.dto.VideoJuegoResponseDTO;

public interface VideoJuegoService {

    VideoJuegoResponseDTO crearVideoJuego(VideoJuegoRequestDTO videoJuegoRequestDTO);

    List<VideoJuegoResponseDTO> listarVideoJuegos();

    VideoJuegoResponseDTO buscarPorId(Long id);

    VideoJuegoResponseDTO actualizarVideoJuego(Long id, VideoJuegoRequestDTO videoJuegoRequestDTO);

    void eliminarVideoJuego(Long id);

    List<VideoJuegoResponseDTO> listarPorCategoria(Long categoriaId);

    List<VideoJuegoResponseDTO> buscarPorTitulo(String titulo);

    List<VideoJuegoResponseDTO> listarPorEstado(String estado);
}