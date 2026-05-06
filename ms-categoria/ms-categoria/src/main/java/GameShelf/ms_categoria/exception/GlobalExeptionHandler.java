package GameShelf.ms_categoria.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExeptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntime(RuntimeException ex) {
        Map<String, Object> body = new HashMap<>();
        
        // Adaptamos el mensaje al contexto de categorías
        body.put("error", "Error en proceso de categoría");
        body.put("mensaje", ex.getMessage());
        
        // Retornamos un 400 Bad Request
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
