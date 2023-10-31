package org.projectweather.exceptions.clientExceptions.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.projectweather.exceptions.clientExceptions.*;
import org.projectweather.exceptions.clientExceptions.InvalidApiKeyException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;



@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse)
            throws IOException {
        return (
                httpResponse.getStatusCode().is4xxClientError()
                        || httpResponse.getStatusCode().is5xxServerError());
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        WeatherApiErrorResponse weatherApiErrorResponse = objectMapper
                .readValue(httpResponse.getBody(), WeatherApiErrorResponse.class);
        int errorCode = weatherApiErrorResponse.getErrorDto().getCode();
        if (httpResponse.getStatusCode().is4xxClientError()) {
            switch (errorCode) {
                case (1002) -> throw new ApiKeyIsNotProvidedException(errorCode);
                case (1003) -> throw new RegionIsNotIndicatedException(errorCode);
                case (1005) -> throw new InvalidApiRequestUrlException(errorCode);
                case (1006) -> throw new RequestedRegionIsNotFoundException(errorCode);
                case (2006) -> throw new InvalidApiKeyException(errorCode);
                case (2007) -> throw new RequestLimitIsExcededException(errorCode);
                case (2008) -> throw new AccessRestrictedException(errorCode);
                case (2009) -> throw new NoAccessException(errorCode);
                default -> throw new UnknownServerInternalException();
            }

        } else if (httpResponse.getStatusCode().is5xxServerError()) {
            throw new InternalServerException(errorCode);
        }
    }
}
