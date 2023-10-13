package org.projectweather.exceptions.clientExceptions.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WeatherApiErrorResponse {

    @JsonProperty("error")
    private ErrorDto errorDto;

    @Data
   class ErrorDto {
        private Integer code;
        private String message;

    }
}

