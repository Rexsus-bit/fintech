package org.projectweather.exceptions;

import org.springframework.http.HttpStatus;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = {"org.projectweather.controller"})
public class ErrorHandler {

    private static final String CONFLICT = "Объект Weather с указанными параметрами уже существует.";
    private static final String NOT_FOUND = "Требуемый объект не найден.";

    @ExceptionHandler({WeatherIsNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final Exception e) {
        return new ApiError(e.getMessage(),
                NOT_FOUND,
                HttpStatus.NOT_FOUND,
                LocalDateTime.now(),
                e.getStackTrace());
    }

    @ExceptionHandler({WeatherIsExistedException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(final Exception e) {
        return new ApiError(e.getMessage(),
                CONFLICT,
                HttpStatus.CONFLICT,
                LocalDateTime.now(),
                e.getStackTrace());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }


}
