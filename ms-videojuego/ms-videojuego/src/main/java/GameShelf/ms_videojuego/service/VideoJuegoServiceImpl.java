package GameShelf.ms_videojuego.service;

import java.util.ArrayList;
import java.util.List;

import GameShelf.ms_videojuego.client.CategoriaClient;
import GameShelf.ms_videojuego.dto.CategoriaResponseDTO;
import GameShelf.ms_videojuego.dto.VideoJuegoRequestDTO;
import GameShelf.ms_videojuego.dto.VideoJuegoResponseDTO;
import GameShelf.ms_videojuego.exception.ComunicacionCategoriaException;
import GameShelf.ms_videojuego.exception.DatoDuplicadoException;
import GameShelf.ms_videojuego.exception.VideoJuegoNoEncontradoException;
import GameShelf.ms_videojuego.model.VideoJuegoModel;
import GameShelf.ms_videojuego.repository.VideoJuegoRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VideoJuegoServiceImpl implements VideoJuegoService {

    private final VideoJuegoRepository videoJuegoRepository;
    private final CategoriaClient categoriaClient;

    public VideoJuegoServiceImpl(VideoJuegoRepository videoJuegoRepository, CategoriaClient categoriaClient) {
        this.videoJuegoRepository = videoJuegoRepository;
        this.categoriaClient = categoriaClient;
    }

    @Override
    public VideoJuegoResponseDTO crearVideoJuego(VideoJuegoRequestDTO videoJuegoRequestDTO) {

        log.info("Creando videojuego: {}", videoJuegoRequestDTO.getTitulo());

        String titulo = videoJuegoRequestDTO.getTitulo().trim();
        String plataforma = validarPlataforma(videoJuegoRequestDTO.getPlataforma());

        if (videoJuegoRepository.existsByTituloIgnoreCaseAndPlataformaIgnoreCase(titulo, plataforma)) {
            log.warn("Intento de crear videojuego duplicado: {} - {}", titulo, plataforma);
            throw new DatoDuplicadoException("El videojuego ya existe para esa plataforma");
        }

        CategoriaResponseDTO categoria = validarCategoria(videoJuegoRequestDTO.getCategoriaId());

        VideoJuegoModel videojuego = new VideoJuegoModel();
        videojuego.setTitulo(titulo);
        videojuego.setDescripcion(videoJuegoRequestDTO.getDescripcion());
        videojuego.setPrecio(videoJuegoRequestDTO.getPrecio());
        videojuego.setCategoriaId(categoria.getId());
        videojuego.setNombreCategoria(categoria.getNombre());
        videojuego.setPlataforma(plataforma);
        videojuego.setEstado(validarEstado(videoJuegoRequestDTO.getEstado()));

        VideoJuegoModel videojuegoGuardado = videoJuegoRepository.save(videojuego);

        log.info("Videojuego creado correctamente con ID: {}", videojuegoGuardado.getId());

        return convertirAResponseDTO(videojuegoGuardado);
    }

    @Override
    public List<VideoJuegoResponseDTO> listarVideoJuegos() {

        log.info("Listando videojuegos");

        List<VideoJuegoModel> videojuegos = videoJuegoRepository.findAll();
        List<VideoJuegoResponseDTO> respuesta = new ArrayList<>();

        for (VideoJuegoModel videojuego : videojuegos) {
            respuesta.add(convertirAResponseDTO(videojuego));
        }

        log.info("Total de videojuegos encontrados: {}", respuesta.size());

        return respuesta;
    }

    @Override
    public VideoJuegoResponseDTO buscarPorId(Long id) {

        log.info("Buscando videojuego con ID: {}", id);

        VideoJuegoModel videojuego = videoJuegoRepository.findById(id)
                .orElseThrow(() -> new VideoJuegoNoEncontradoException("Videojuego no encontrado con ID: " + id));

        return convertirAResponseDTO(videojuego);
    }

    @Override
    public VideoJuegoResponseDTO actualizarVideoJuego(Long id, VideoJuegoRequestDTO videoJuegoRequestDTO) {

        log.info("Actualizando videojuego con ID: {}", id);

        VideoJuegoModel videojuego = videoJuegoRepository.findById(id)
                .orElseThrow(() -> new VideoJuegoNoEncontradoException("Videojuego no encontrado con ID: " + id));

        String titulo = videoJuegoRequestDTO.getTitulo().trim();
        String plataforma = validarPlataforma(videoJuegoRequestDTO.getPlataforma());

        if (videoJuegoRepository.existsByTituloIgnoreCaseAndPlataformaIgnoreCaseAndIdNot(titulo, plataforma, id)) {
            log.warn("Intento de actualizar con videojuego duplicado: {} - {}", titulo, plataforma);
            throw new DatoDuplicadoException("El videojuego ya existe para esa plataforma");
        }

        CategoriaResponseDTO categoria = validarCategoria(videoJuegoRequestDTO.getCategoriaId());

        videojuego.setTitulo(titulo);
        videojuego.setDescripcion(videoJuegoRequestDTO.getDescripcion());
        videojuego.setPrecio(videoJuegoRequestDTO.getPrecio());
        videojuego.setCategoriaId(categoria.getId());
        videojuego.setNombreCategoria(categoria.getNombre());
        videojuego.setPlataforma(plataforma);
        videojuego.setEstado(validarEstado(videoJuegoRequestDTO.getEstado()));

        VideoJuegoModel videojuegoActualizado = videoJuegoRepository.save(videojuego);

        log.info("Videojuego actualizado correctamente con ID: {}", videojuegoActualizado.getId());

        return convertirAResponseDTO(videojuegoActualizado);
    }

    @Override
    public void eliminarVideoJuego(Long id) {

        log.info("Eliminando videojuego con ID: {}", id);

        VideoJuegoModel videojuego = videoJuegoRepository.findById(id)
                .orElseThrow(() -> new VideoJuegoNoEncontradoException("Videojuego no encontrado con ID: " + id));

        videoJuegoRepository.delete(videojuego);

        log.info("Videojuego eliminado correctamente con ID: {}", id);
    }

    @Override
    public List<VideoJuegoResponseDTO> buscarPorCategoria(Long categoriaId) {

        log.info("Buscando videojuegos por categoría ID: {}", categoriaId);

        List<VideoJuegoModel> videojuegos = videoJuegoRepository.findByCategoriaId(categoriaId);
        List<VideoJuegoResponseDTO> respuesta = new ArrayList<>();

        for (VideoJuegoModel videojuego : videojuegos) {
            respuesta.add(convertirAResponseDTO(videojuego));
        }

        log.info("Videojuegos encontrados para categoría {}: {}", categoriaId, respuesta.size());

        return respuesta;
    }

    @Override
    public List<VideoJuegoResponseDTO> buscarPorTitulo(String titulo) {

        log.info("Buscando videojuegos por título: {}", titulo);

        List<VideoJuegoModel> videojuegos = videoJuegoRepository.findByTituloContainingIgnoreCase(titulo);
        List<VideoJuegoResponseDTO> respuesta = new ArrayList<>();

        for (VideoJuegoModel videojuego : videojuegos) {
            respuesta.add(convertirAResponseDTO(videojuego));
        }

        log.info("Videojuegos encontrados con título {}: {}", titulo, respuesta.size());

        return respuesta;
    }

    @Override
    public List<VideoJuegoResponseDTO> buscarPorEstado(String estado) {

        String estadoValidado = validarEstado(estado);

        log.info("Buscando videojuegos por estado: {}", estadoValidado);

        List<VideoJuegoModel> videojuegos = videoJuegoRepository.findByEstado(estadoValidado);
        List<VideoJuegoResponseDTO> respuesta = new ArrayList<>();

        for (VideoJuegoModel videojuego : videojuegos) {
            respuesta.add(convertirAResponseDTO(videojuego));
        }

        log.info("Videojuegos encontrados con estado {}: {}", estadoValidado, respuesta.size());

        return respuesta;
    }

    @Override
    public List<VideoJuegoResponseDTO> buscarPorPlataforma(String plataforma) {

        String plataformaValidada = validarPlataforma(plataforma);

        log.info("Buscando videojuegos por plataforma: {}", plataformaValidada);

        List<VideoJuegoModel> videojuegos = videoJuegoRepository.findByPlataforma(plataformaValidada);
        List<VideoJuegoResponseDTO> respuesta = new ArrayList<>();

        for (VideoJuegoModel videojuego : videojuegos) {
            respuesta.add(convertirAResponseDTO(videojuego));
        }

        log.info("Videojuegos encontrados con plataforma {}: {}", plataformaValidada, respuesta.size());

        return respuesta;
    }

    private CategoriaResponseDTO validarCategoria(Long categoriaId) {

        try {
            log.info("Validando categoría con ms-categoria ID: {}", categoriaId);

            CategoriaResponseDTO categoria = categoriaClient.buscarCategoriaPorId(categoriaId);

            if (categoria == null) {
                throw new ComunicacionCategoriaException("El microservicio de categoría no devolvió información");
            }

            if (categoria.getEstado() != null && categoria.getEstado().equalsIgnoreCase("INACTIVA")) {
                throw new DatoDuplicadoException("La categoría está inactiva");
            }

            log.info("Categoría validada correctamente: {}", categoria.getNombre());

            return categoria;

        } catch (FeignException e) {
            log.error("No se pudo validar la categoría ID: {}", categoriaId);
            throw new ComunicacionCategoriaException("No se pudo comunicar con ms-categoria");
        }
    }

    private String validarEstado(String estado) {

        if (estado == null || estado.isBlank()) {
            return "DISPONIBLE";
        }

        String estadoMayuscula = estado.toUpperCase();

        if (!estadoMayuscula.equals("DISPONIBLE")
                && !estadoMayuscula.equals("NO_DISPONIBLE")
                && !estadoMayuscula.equals("INACTIVO")) {
            throw new DatoDuplicadoException("El estado debe ser DISPONIBLE, NO_DISPONIBLE o INACTIVO");
        }

        return estadoMayuscula;
    }

    private String validarPlataforma(String plataforma) {

        if (plataforma == null || plataforma.isBlank()) {
            throw new DatoDuplicadoException("La plataforma es obligatoria");
        }

        return plataforma.toUpperCase();
    }

    private VideoJuegoResponseDTO convertirAResponseDTO(VideoJuegoModel videojuego) {

        return new VideoJuegoResponseDTO(
                videojuego.getId(),
                videojuego.getTitulo(),
                videojuego.getDescripcion(),
                videojuego.getPrecio(),
                videojuego.getCategoriaId(),
                videojuego.getNombreCategoria(),
                videojuego.getPlataforma(),
                videojuego.getEstado()
        );
    }
}