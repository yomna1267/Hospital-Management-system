package com.example.userManagementService.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
public class CustomFeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();

        try {
            String responseBody = StreamUtils.copyToString(response.body().asInputStream(), StandardCharsets.UTF_8);
            switch (status) {
                case 403:
                    return new ResponseStatusException(
                            HttpStatus.FORBIDDEN,
                            responseBody
                    );
                case 409:
                    return new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            responseBody
                    );
                case 404:
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            responseBody
                    );
                case 400:
                    return new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            responseBody
                    );
                default:
                    return new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            responseBody
                    );
            }
        } catch (IOException e) {
            return new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to process error response"
            );
        }
    }
}
