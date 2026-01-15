package y.semina.exeption.handler;

import jakarta.validation.ValidationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import y.semina.exeption.CustomerNotFoundException;
import y.semina.exeption.OrderNotFoundException;
import y.semina.exeption.ProductNotFoundException;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler({
            OrderNotFoundException.class,
            ProductNotFoundException.class,
            CustomerNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(RuntimeException ex) {
        return ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            IllegalArgumentException.class,
            DataIntegrityViolationException.class,
            ValidationException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(Exception ex) {
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Ошибка валидации: " + ex.getMessage())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleBase(Exception ex) {
        return ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Ошибка: " + ex.getMessage())
                .build();
    }

}
