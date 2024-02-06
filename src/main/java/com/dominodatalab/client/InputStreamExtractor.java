package com.dominodatalab.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

import com.dominodatalab.api.invoker.ApiException;
import com.dominodatalab.api.invoker.ApiResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class InputStreamExtractor {

    public ApiResponse<InputStream> extractInputStreamResponse(HttpClient memberVarHttpClient, Consumer<HttpResponse<InputStream>> memberVarResponseInterceptor, HttpRequest.Builder localVarRequestBuilder) throws ApiException {
        try {
            HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
                    localVarRequestBuilder.build(),
                    HttpResponse.BodyHandlers.ofInputStream());
            if (memberVarResponseInterceptor != null) {
                memberVarResponseInterceptor.accept(localVarResponse);
            }
            if (localVarResponse.statusCode() / 100 != 2) {
                // Get the stack trace
                throw getApiException(getMethodName(), localVarResponse);
            }
            return new ApiResponse<>(
                    localVarResponse.statusCode(),
                    localVarResponse.headers().map(),
                    localVarResponse.body()
            );
        } catch (IOException e) {
            throw new ApiException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(e);
        }
    }

    private String getMethodName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        // The caller method name is at index 3 in the stack trace array
        // Index 0 is the getStackTrace method itself
        // Index 1 is the getCallerMethodName method
        // Index 2 is the getCurrentMethodName method
        // Index 3 is the caller method (the method that called getCurrentMethodName)
        return stackTrace[3].getMethodName();
    }

    public ApiException getApiException(String operationId, HttpResponse<InputStream> response) throws IOException {
        String body = (response.body() == null) ? null : new String(response.body().readAllBytes());
        String message = formatExceptionMessage(operationId, response.statusCode(), body);
        return new ApiException(response.statusCode(), message, response.headers(), body);
    }

    public String formatExceptionMessage(String operationId, int statusCode, String body) {
        if (body == null || body.isEmpty()) {
            body = "[no body]";
        }
        return operationId + " call failed with: " + statusCode + " - " + body;
    }
}