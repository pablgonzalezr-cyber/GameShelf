package GameShelf.ms_roles.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RolNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarRolNoEncontrado(RolNoEncontradoException ex) {

        log.error("Rol no encontrado: {}", ex.getMessage());

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("fecha", LocalDateTime.now());
        respuesta.put("estado", HttpStatus.NOT_FOUND.value());
        respuesta.put("error", "No encontrado");
        respuesta.put("mensaje", ex.getMessage());

        return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DatoDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> manejarDatoDuplicado(DatoDuplicadoException ex) {

        log.error("Dato duplicado o inválido: {}", ex.getMessage());

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("fecha", LocalDateTime.now());
        respuesta.put("estado", HttpStatus.BAD_REQUEST.value());
        respuesta.put("error", "Dato inválido");
        respuesta.put("mensaje", ex.getMessage());

        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidaciones(MethodArgumentNotValidException ex) {

        log.error("Error de validación en DTO");

        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errores.put(error.getField(), error.getDefaultMessage());
        });

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("fecha", LocalDateTime.now());
        respuesta.put("estado", HttpStatus.BAD_REQUEST.value());
        respuesta.put("error", "Validación fallida");
        respuesta.put("mensajes", errores);

        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarErrorGeneral(Exception ex) {

        log.error("Error inesperado: {}", ex.getMessage());

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("fecha", LocalDateTime.now());
        respuesta.put("estado", HttpStatus.INTERNAL_SERVER_ERROR.value());
        respuesta.put("error", "Error interno");
        respuesta.put("mensaje", "Ocurrió un error inesperado en ms-roles");

        return new ResponseEntity<>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}