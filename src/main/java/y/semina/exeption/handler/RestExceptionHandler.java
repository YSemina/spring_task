package y.semina.exeption.handler;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import y.semina.exeption.CustomerNotFoundException;
import y.semina.exeption.OrderNotFoundException;
import y.semina.exeption.ProductNotFoundException;

@RestControllerAdvice
@Slf4j
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

    @ExceptionHandler(LockedException.class)
    @ResponseStatus(HttpStatus.LOCKED)
    public ErrorResponse handleLockedException(LockedException ex) {
        log.warn("Account locked: {}", ex.getMessage());
        return ErrorResponse.builder()
                .status(HttpStatus.LOCKED.value())
                .message("Учетная запись заблокирована из-за многочисленных неудачных попыток входа в систему.")
                .build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("Invalid credentials: {}", ex.getMessage());
        return ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("Неверное имя пользователя или пароль")
                .build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException ex) {
        log.error("RestExceptionHandler caught AccessDeniedException: {}", ex.getMessage());
        return ErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message("Доступ запрещен")
                .build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Invalid request body: {}", ex.getMessage());

        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Некорректный запрос: отсутствует или неверный формат тела запроса")
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleBase(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Ошибка: " + ex.getMessage())
                .build();
    }

}
