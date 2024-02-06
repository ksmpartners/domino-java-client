package com.dominodatalab.api.rest;

import java.io.InputStream;
import java.net.http.HttpRequest;

import com.dominodatalab.api.invoker.ApiException;
import com.dominodatalab.api.invoker.ApiResponse;
import com.dominodatalab.client.InputStreamExtractor;

public class AdminApiExtended extends AdminApi {

    public ApiResponse<InputStream> getExecutionSupportBundleWithHttpInfo(String executionId) throws ApiException {
        HttpRequest.Builder localVarRequestBuilder = getExecutionSupportBundleRequestBuilder(executionId);
        return InputStreamExtractor.extractInputStreamResponse(memberVarHttpClient, memberVarResponseInterceptor, localVarRequestBuilder);
    }
}