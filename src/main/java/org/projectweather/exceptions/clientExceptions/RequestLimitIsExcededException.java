package org.projectweather.exceptions.clientExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class RequestLimitIsExcededException extends RuntimeException {

    public RequestLimitIsExcededException(int errorCode) {
        super(String.format("Произошла ошибка № %d. Превышен лимит запросов в месяц.", errorCode));
    }
}
