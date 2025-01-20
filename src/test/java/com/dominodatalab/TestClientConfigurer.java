package com.dominodatalab;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest.Builder;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;

import com.dominodatalab.client.DominoApiClient;
import com.dominodatalab.client.DominoPublicClient;

import lombok.extern.java.Log;
/**
 * Provides ApiClients from each of the respective API packages with the test
 * URL and API key configured to permit integration testing.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("Integration")
@Log
public class TestClientConfigurer {
    
    private String dominoApiUrl;
    private String dominoApiKey;

    private static String DEFAULT_DOMINO_API_BASE_PATH = "/v4";
    private static String DOMINO_API_KEY_HEADER = "X-Domino-Api-Key";
    private static String DOMINO_API_KEY_PROPERTY = "domino.api.key";
    private static String DOMINO_API_URL_PROPERTY = "domino.api.url";

    public TestClientConfigurer() {
        try {
            Properties properties = new Properties();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties");

            if (inputStream != null) {
                properties.load(inputStream);

                System.getProperties().putAll(properties);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public com.dominodatalab.api.invoker.ApiClient getInternalTestClient() {
        com.dominodatalab.api.invoker.ApiClient testClient = DominoApiClient.createApiClient();

        String apiUrl = getDominoApiUrl();
        log.fine("Creating Internal API client using Domino API URL: " + apiUrl);
    
        testClient.updateBaseUri(apiUrl);
        testClient.setRequestInterceptor(this::attachDominoApiKey);

        String basePath = URI.create(testClient.getBaseUri()).getRawPath();
        if (StringUtils.isBlank(basePath)) {
            testClient.setBasePath(DEFAULT_DOMINO_API_BASE_PATH);
        }

        return testClient;
    }

    public com.dominodatalab.pub.invoker.ApiClient getPublicTestClient() {
        com.dominodatalab.pub.invoker.ApiClient testClient = DominoPublicClient.createApiClient();

        String apiUrl = getDominoApiUrl();
        log.fine("Creating Public API client using Domino API URL: " + apiUrl);

        testClient.updateBaseUri(apiUrl);
        testClient.setRequestInterceptor(this::attachDominoApiKey);

        return testClient;
    }

    private Builder attachDominoApiKey(Builder builder) {
        return builder.setHeader(DOMINO_API_KEY_HEADER, getDominoApiKey());
    }

    private String getDominoApiUrl() {
        if (dominoApiUrl == null) {
            dominoApiUrl = getProperty(DOMINO_API_URL_PROPERTY);
        }
        return dominoApiUrl;
    }

    private String getDominoApiKey() {
        if (dominoApiKey == null) {
            dominoApiKey = getProperty(DOMINO_API_KEY_PROPERTY);
        }
        return dominoApiKey;
    }
    
    private String getProperty(final String propertyName) {
        String property = System.getProperty(propertyName);
        String envvar = System.getenv(envify(propertyName));

        if (StringUtils.isAllBlank(property, envvar)) {
            throw new IllegalStateException("Property '" + propertyName + "' is not set!");
        }

        // Use property over environment variable - since these are integration tests
        // user's set environment may not align with expected test environment
        return property != null ? property : envvar;
    }

    /**
     * Get the equivalent environment variable name based on the application
     * property name.
     * 
     * <p>For example, 'domino.api.url' becomes 'DOMINO_API_URL'.
     */
    private static String envify(String propertyName) {
        return propertyName.toUpperCase().replace('.', '_');
    }

}
