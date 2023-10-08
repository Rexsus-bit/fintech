package org.projectweather.exceptions.clientExceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class InternalServerException extends RuntimeException {

    public InternalServerException(int errorCode) {
        super(String.format("Произошла ошибка № %d. Мы уже работаем над устранением проблемы.", errorCode));
    }
}
