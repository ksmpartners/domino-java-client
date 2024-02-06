package com.dominodatalab.client;

import java.net.http.HttpClient;
import java.time.Duration;

import com.dominodatalab.pub.invoker.ApiClient;
import lombok.experimental.UtilityClass;

/**
 * Utility class to construct an API client with some defaults.
 */
@UtilityClass
public class DominoPublicClient extends DominoClient {

    /**
     * Create the API Client for accessing DataMiner over HTTP.
     *
     * @return the {@link ApiClient}
     */
    public ApiClient createApiClient() {
        final ApiClient client = new ApiClient();
        client.setHttpClientBuilder(HttpClient.newBuilder().sslContext(TrustAllManager.createSslContext()));
        client.setObjectMapper(createDefaultObjectMapper());
        client.setConnectTimeout(Duration.ofSeconds(5));
        client.setReadTimeout(Duration.ofSeconds(60));
        return client;
    }

}