package org.projectweather.exceptions.clientExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class RequestedRegionIsNotFoundException extends RuntimeException {

    public RequestedRegionIsNotFoundException(int errorCode) {
        super(String.format("Произошла ошибка № %d. Запрашиваемый регион не найден, проверьте корректность запрашиваемых" +
                " данных.", errorCode));
    }


}
