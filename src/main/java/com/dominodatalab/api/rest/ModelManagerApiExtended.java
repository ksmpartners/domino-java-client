package com.dominodatalab.api.rest;

import java.net.http.HttpRequest;

import com.dominodatalab.api.invoker.ApiClient;
import com.dominodatalab.api.invoker.ApiException;
import com.dominodatalab.api.invoker.ApiResponse;
import com.dominodatalab.client.InputStreamExtractor;

public class ModelManagerApiExtended extends ModelManagerApi {

    public ModelManagerApiExtended(){
        super();
    }

    public ModelManagerApiExtended(ApiClient client){
        super(client);
    }

    @Override
    public ApiResponse<java.io.InputStream> downloadLogsWithHttpInfo(String modelVersionId, Long startMillis, Long endMillis) throws ApiException {
        HttpRequest.Builder localVarRequestBuilder = downloadLogsRequestBuilder(modelVersionId, startMillis, endMillis);
        return InputStreamExtractor.extractInputStreamResponse(memberVarHttpClient, memberVarResponseInterceptor, localVarRequestBuilder);
    }
}