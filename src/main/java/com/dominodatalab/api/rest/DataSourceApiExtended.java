package com.dominodatalab.api.rest;

import java.net.http.HttpRequest;
import java.util.List;

import com.dominodatalab.api.invoker.ApiException;
import com.dominodatalab.api.invoker.ApiResponse;
import com.dominodatalab.client.InputStreamExtractor;

public class DataSourceApiExtended extends DataSourceApi {

    @Override
    public ApiResponse<java.io.InputStream> downloadDataSourceAuditDataWithHttpInfo(Boolean jsonFile) throws ApiException {
        HttpRequest.Builder localVarRequestBuilder = downloadDataSourceAuditDataRequestBuilder(jsonFile);
        return InputStreamExtractor.extractInputStreamResponse(memberVarHttpClient, memberVarResponseInterceptor, localVarRequestBuilder);
    }

    @Override
    public ApiResponse<java.io.InputStream> downloadDataSourceAuditDataInRunsWithHttpInfo(List<String> runIds, Boolean jsonFile) throws ApiException {
        HttpRequest.Builder localVarRequestBuilder = downloadDataSourceAuditDataInRunsRequestBuilder(runIds, jsonFile);
        return InputStreamExtractor.extractInputStreamResponse(memberVarHttpClient, memberVarResponseInterceptor, localVarRequestBuilder);
    }
}