package org.projectweather.exceptions.clientExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class RegionIsNotIndicatedException extends RuntimeException {

    public RegionIsNotIndicatedException(int errorCode) {
        super(String.format("Произошла ошибка № %d. Для корректной работы необходимо указать регион с " +
                "запрашиваемой погодой.", errorCode));
    }

}
