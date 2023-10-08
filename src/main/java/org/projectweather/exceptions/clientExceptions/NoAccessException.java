package org.projectweather.exceptions.clientExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class NoAccessException extends RuntimeException {

    public NoAccessException(int errorCode) {
        super(String.format("Произошла ошибка № %d. У вас отсутствуют права доступа к запрошенной информации.",
                errorCode));
    }
}
