package org.projectweather.exceptions.clientExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class AccessRestrictedException extends RuntimeException {

    public AccessRestrictedException(int errorNumber) {
        super(String.format("Произошла ошибка № %d. Ваш доступ к сервису был приостановлен. " +
                "Обратитесь к администратору сервиса.", errorNumber));
    }
}
