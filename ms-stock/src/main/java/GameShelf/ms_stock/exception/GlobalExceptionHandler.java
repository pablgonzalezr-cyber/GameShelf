package GameShelf.ms_stock.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StockNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarStockNoEncontrado(StockNoEncontradoException ex) {

        log.error("Stock no encontrado: {}", ex.getMessage());

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("fecha", LocalDateTime.now());
        respuesta.put("estado", 404);
        respuesta.put("error", "Stock no encontrado");
        respuesta.put("mensaje", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    @ExceptionHandler(DatoDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> manejarDatoDuplicado(DatoDuplicadoException ex) {

        log.error("Dato duplicado: {}", ex.getMessage());

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("fecha", LocalDateTime.now());
        respuesta.put("estado", 400);
        respuesta.put("error", "Dato duplicado");
        respuesta.put("mensaje", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }

    @ExceptionHandler(DatoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> manejarDatoInvalido(DatoInvalidoException ex) {

        log.error("Dato inválido: {}", ex.getMessage());

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("fecha", LocalDateTime.now());
        respuesta.put("estado", 400);
        respuesta.put("error", "Dato inválido");
        respuesta.put("mensaje", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }

    @ExceptionHandler(ComunicacionVideojuegoException.class)
    public ResponseEntity<Map<String, Object>> manejarComunicacionVideojuego(ComunicacionVideojuegoException ex) {

        log.error("Error de comunicación con ms-videojuego: {}", ex.getMessage());

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("fecha", LocalDateTime.now());
        respuesta.put("estado", 503);
        respuesta.put("error", "Error de comunicación con ms-videojuego");
        respuesta.put("mensaje", ex.getMessage());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(respuesta);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidaciones(MethodArgumentNotValidException ex) {

        log.error("Error de validación en DTO");

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("fecha", LocalDateTime.now());
        respuesta.put("estado", 400);
        respuesta.put("error", "Error de validación");

        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errores.put(error.getField(), error.getDefaultMessage());
        });

        respuesta.put("mensajes", errores);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarErrorGeneral(Exception ex) {

        log.error("Error interno: {}", ex.getMessage());

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("fecha", LocalDateTime.now());
        respuesta.put("estado", 500);
        respuesta.put("error", "Error interno del servidor");
        respuesta.put("mensaje", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
    }
}

