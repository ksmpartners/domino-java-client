package com.dominodatalab.pub.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.dominodatalab.pub.invoker.ApiClient;
import com.dominodatalab.pub.invoker.ApiException;
import com.dominodatalab.pub.invoker.ApiResponse;

public class ProjectsFilesApiExtended extends ProjectsFilesApi {

    public ProjectsFilesApiExtended() {
        super();
    }

    public ProjectsFilesApiExtended(ApiClient client) {
        super(client);
    }

    @Override
    public ApiResponse<InputStream> getProjectFileContentsWithHttpInfo(String projectId, String commitId, String path) throws ApiException {
        HttpRequest.Builder localVarRequestBuilder = getProjectFileContentsRequestBuilder(projectId, commitId, path);
        try {
            HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
                    localVarRequestBuilder.build(),
                    HttpResponse.BodyHandlers.ofInputStream());
            if (memberVarResponseInterceptor != null) {
                memberVarResponseInterceptor.accept(localVarResponse);
            }
            if (localVarResponse.statusCode() / 100 != 2) {
                // Get the stack trace
                throw getApiException("ProjectFileContents", localVarResponse);
            }
            return new com.dominodatalab.pub.invoker.ApiResponse<>(
                    localVarResponse.statusCode(),
                    localVarResponse.headers().map(),
                    localVarResponse.body()
            );
        } catch (IOException e) {
            throw new com.dominodatalab.pub.invoker.ApiException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new com.dominodatalab.pub.invoker.ApiException(e);
        }
    }
}