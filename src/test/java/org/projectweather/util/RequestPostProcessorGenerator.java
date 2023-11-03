package org.projectweather.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Base64;

public class RequestPostProcessorGenerator {

    @NotNull
    public static RequestPostProcessor getRequestPostProcessor(String name, String password) {
        String credentials = name + ":" + password;
        byte[] encodedCredentials = Base64.getEncoder().encode(credentials.getBytes());

        return mockRequest -> {
            mockRequest.addHeader("Authorization",
                    "Basic " + new String(encodedCredentials));
            return mockRequest;
        };
    }
}
