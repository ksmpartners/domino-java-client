package com.dominodatalab.client;

import java.net.http.HttpClient;
import java.time.Duration;

import com.dominodatalab.api.invoker.ApiClient;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.openapitools.jackson.nullable.JsonNullableModule;

import lombok.experimental.UtilityClass;

/**
 * Utility class to construct an API client with some defaults.
 */
@UtilityClass
public class DominoApiClient {

   /**
    * Create the API Client for accessing DataMiner over HTTP.
    *
    * @return the {@link ApiClient}
    */
   public ApiClient createApiClient() {
      final HttpClient.Builder httpClient = HttpClient.newBuilder().sslContext(TrustAllManager.createSslContext());
      final ApiClient client = new ApiClient();
      client.setHttpClientBuilder(httpClient);
      client.setObjectMapper(createDefaultObjectMapper());
      client.setConnectTimeout(Duration.ofSeconds(5));
      client.setReadTimeout(Duration.ofSeconds(60));
      return client;
   }

   /**
    * Create the JSON Mapper with some defaults and custom LocalDateTime
    * parsing.
    *
    * @return the {@link ObjectMapper}
    */
   public ObjectMapper createDefaultObjectMapper() {
      final ObjectMapper mapper = new ObjectMapper();
      mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
      mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
      mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
      mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
      mapper.registerModule(new JsonNullableModule());
      final JavaTimeModule module = new JavaTimeModule();
      mapper.registerModule(module);
      return mapper;
   }
}
