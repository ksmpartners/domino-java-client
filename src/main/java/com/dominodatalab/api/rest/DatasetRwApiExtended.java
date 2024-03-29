/*
 * Domino Data Lab API v4
 * This API is going to provide access to all the Domino functions available in the user interface. To authenticate your requests, include your API Key (which you can find on your account page) with the header X-Domino-Api-Key.
 *
 * The version of the OpenAPI document: 4.0.0
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package com.dominodatalab.api.rest;

import java.io.InputStream;
import java.net.http.HttpRequest;

import com.dominodatalab.api.invoker.ApiClient;
import com.dominodatalab.api.invoker.ApiException;
import com.dominodatalab.api.invoker.ApiResponse;
import com.dominodatalab.client.InputStreamExtractor;

/**
 * Override the method because Java Native Client generation does not support Files or InputStream.
 * If content-type =  application/octet-stream we need to handle this special.
 */
public class DatasetRwApiExtended extends DatasetRwApi {

    public DatasetRwApiExtended(){
        super();
    }

    public DatasetRwApiExtended(ApiClient client){
        super(client);
    }

    @Override
    public ApiResponse<InputStream> getFileRawWithHttpInfo(String snapshotId, String path, Boolean download) throws ApiException {
        HttpRequest.Builder localVarRequestBuilder = getFileRawRequestBuilder(snapshotId, path, download);
        return InputStreamExtractor.extractInputStreamResponse(memberVarHttpClient, memberVarResponseInterceptor, localVarRequestBuilder);
    }

    @Override
    public ApiResponse<InputStream> downloadArchiveToLocalWithHttpInfo(String snapshotId, String taskKey) throws ApiException {
        HttpRequest.Builder localVarRequestBuilder = downloadArchiveToLocalRequestBuilder(snapshotId, taskKey);
        return InputStreamExtractor.extractInputStreamResponse(memberVarHttpClient, memberVarResponseInterceptor, localVarRequestBuilder);
    }
}