package GameShelf.ms_videojuego.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import GameShelf.ms_videojuego.dto.VideoJuegoRequestDTO;
import GameShelf.ms_videojuego.dto.VideoJuegoResponseDTO;
import GameShelf.ms_videojuego.model.VideoJuegoModel;
import GameShelf.ms_videojuego.repository.VideoJuegoRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VideoJuegoServiceImpl implements VideoJuegoService {

    private final VideoJuegoRepository videoJuegoRepository;

    public VideoJuegoServiceImpl(VideoJuegoRepository videoJuegoRepository) {
        this.videoJuegoRepository = videoJuegoRepository;
    }

    @Override
    public VideoJuegoResponseDTO crearVideoJuego(VideoJuegoRequestDTO videoJuegoRequestDTO) {

        log.info("Creando videojuego: {}", videoJuegoRequestDTO.getTitulo());

        if (videoJuegoRepository.existsByTitulo(videoJuegoRequestDTO.getTitulo())) {
            throw new RuntimeException("El videojuego ya existe");
        }

        VideoJuegoModel videoJuego = new VideoJuegoModel();
        videoJuego.setTitulo(videoJuegoRequestDTO.getTitulo());
        videoJuego.setDescripcion(videoJuegoRequestDTO.getDescripcion());
        videoJuego.setPrecio(videoJuegoRequestDTO.getPrecio());
        videoJuego.setCategoriaId(videoJuegoRequestDTO.getCategoriaId());

        if (videoJuegoRequestDTO.getEstado() == null || videoJuegoRequestDTO.getEstado().isEmpty()) {
            videoJuego.setEstado("DISPONIBLE");
        } else {
            videoJuego.setEstado(videoJuegoRequestDTO.getEstado());
        }

        VideoJuegoModel videoJuegoGuardado = videoJuegoRepository.save(videoJuego);

        log.info("Videojuego creado con ID: {}", videoJuegoGuardado.getId());

        return convertirAResponseDTO(videoJuegoGuardado);
    }

    @Override
    public List<VideoJuegoResponseDTO> listarVideoJuegos() {

        log.info("Listando videojuegos");

        List<VideoJuegoModel> videojuegos = videoJuegoRepository.findAll();
        List<VideoJuegoResponseDTO> respuesta = new ArrayList<>();

        for (VideoJuegoModel videoJuego : videojuegos) {
            respuesta.add(convertirAResponseDTO(videoJuego));
        }

        return respuesta;
    }

    @Override
    public VideoJuegoResponseDTO buscarPorId(Long id) {

        log.info("Buscando videojuego con ID: {}", id);

        VideoJuegoModel videoJuego = videoJuegoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Videojuego no encontrado"));

        return convertirAResponseDTO(videoJuego);
    }

    @Override
    public VideoJuegoResponseDTO actualizarVideoJuego(Long id, VideoJuegoRequestDTO videoJuegoRequestDTO) {

        log.info("Actualizando videojuego con ID: {}", id);

        VideoJuegoModel videoJuego = videoJuegoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Videojuego no encontrado"));

        videoJuego.setTitulo(videoJuegoRequestDTO.getTitulo());
        videoJuego.setDescripcion(videoJuegoRequestDTO.getDescripcion());
        videoJuego.setPrecio(videoJuegoRequestDTO.getPrecio());
        videoJuego.setCategoriaId(videoJuegoRequestDTO.getCategoriaId());

        if (videoJuegoRequestDTO.getEstado() != null && !videoJuegoRequestDTO.getEstado().isEmpty()) {
            videoJuego.setEstado(videoJuegoRequestDTO.getEstado());
        }

        VideoJuegoModel videoJuegoActualizado = videoJuegoRepository.save(videoJuego);

        log.info("Videojuego actualizado con ID: {}", videoJuegoActualizado.getId());

        return convertirAResponseDTO(videoJuegoActualizado);
    }

    @Override
    public void eliminarVideoJuego(Long id) {

        log.info("Eliminando videojuego con ID: {}", id);

        VideoJuegoModel videoJuego = videoJuegoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Videojuego no encontrado"));

        videoJuegoRepository.delete(videoJuego);

        log.info("Videojuego eliminado con ID: {}", id);
    }

    @Override
    public List<VideoJuegoResponseDTO> listarPorCategoria(Long categoriaId) {

        log.info("Listando videojuegos por categoría ID: {}", categoriaId);

        List<VideoJuegoModel> videojuegos = videoJuegoRepository.findByCategoriaId(categoriaId);
        List<VideoJuegoResponseDTO> respuesta = new ArrayList<>();

        for (VideoJuegoModel videoJuego : videojuegos) {
            respuesta.add(convertirAResponseDTO(videoJuego));
        }

        return respuesta;
    }

    @Override
    public List<VideoJuegoResponseDTO> buscarPorTitulo(String titulo) {

        log.info("Buscando videojuegos por título: {}", titulo);

        List<VideoJuegoModel> videojuegos = videoJuegoRepository.findByTituloContainingIgnoreCase(titulo);
        List<VideoJuegoResponseDTO> respuesta = new ArrayList<>();

        for (VideoJuegoModel videoJuego : videojuegos) {
            respuesta.add(convertirAResponseDTO(videoJuego));
        }

        return respuesta;
    }

    @Override
    public List<VideoJuegoResponseDTO> listarPorEstado(String estado) {

        log.info("Listando videojuegos por estado: {}", estado);

        List<VideoJuegoModel> videojuegos = videoJuegoRepository.findByEstado(estado);
        List<VideoJuegoResponseDTO> respuesta = new ArrayList<>();

        for (VideoJuegoModel videoJuego : videojuegos) {
            respuesta.add(convertirAResponseDTO(videoJuego));
        }

        return respuesta;
    }

    private VideoJuegoResponseDTO convertirAResponseDTO(VideoJuegoModel videoJuego) {
        return new VideoJuegoResponseDTO(
                videoJuego.getId(),
                videoJuego.getTitulo(),
                videoJuego.getDescripcion(),
                videoJuego.getPrecio(),
                videoJuego.getCategoriaId(),
                videoJuego.getEstado()
        );
    }
}