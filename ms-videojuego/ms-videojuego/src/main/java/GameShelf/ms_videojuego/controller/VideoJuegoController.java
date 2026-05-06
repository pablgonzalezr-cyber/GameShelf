package GameShelf.ms_videojuego.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import GameShelf.ms_videojuego.model.VideoJuegoModel;
import GameShelf.ms_videojuego.service.VideoJuegoService;

@RestController
@RequestMapping("/api/videojuegos")
public class VideoJuegoController {
    @Autowired
    private VideoJuegoService videojuegoService;

    @PostMapping
    public ResponseEntity<VideoJuegoModel> crear(@RequestBody VideoJuegoModel videojuego) {
        return new ResponseEntity<>(videojuegoService.guardar(videojuego), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<VideoJuegoModel>> listar() {
        return ResponseEntity.ok(videojuegoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoJuegoModel> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(videojuegoService.buscarPorId(id));
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<VideoJuegoModel>> listarPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(videojuegoService.listarPorCategoria(categoriaId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        videojuegoService.eliminar(id);
        return ResponseEntity.ok("Videojuego eliminado correctamente.");
    }

}
