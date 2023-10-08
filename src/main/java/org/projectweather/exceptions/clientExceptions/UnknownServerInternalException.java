package org.projectweather.exceptions.clientExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class UnknownServerInternalException extends RuntimeException {

    public UnknownServerInternalException() {
        super("Произошла неизвестная ошибка. Мы уже работаем над устранением проблемы.");
    }

}
