package com.dominodatalab.api.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.dominodatalab.TestClientConfigurer;
import com.dominodatalab.TestData;
import com.dominodatalab.api.invoker.ApiClient;
import com.dominodatalab.api.invoker.ApiException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminApiTest extends TestClientConfigurer {
    
    AdminApi adminApi;
    InputStream stream;

    @BeforeAll
    void configureApi() {
        ApiClient client = getInternalTestClient();
        // Important - use extended API to override query method, to avoid a
        // JSON read error
        adminApi = new AdminApiExtended(client);
    }

    @AfterEach
    void closeStream() throws IOException {
        if (stream != null) {
            stream.close();
        }
    }

    @Test
    @Order(1)
    void getExecutionSupportBundleSuccess() throws ApiException, IOException, ZipException {
        // Arrange
        String executionId = TestData.VALID_PROJECT_JOB_ID;

        // Act
        stream = adminApi.getExecutionSupportBundle(executionId);

        // Assert
        ZipInputStream zip = new ZipInputStream(stream);
        // InputStream is a zip and has contents
        ZipEntry entry = zip.getNextEntry();
        assertNotNull(entry);
    }

    @Test
    void getExecutionSupportBundleNotFound() {
        // Arrange
        String executionId = TestData.NOT_FOUND_PROJECT_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> adminApi.getExecutionSupportBundle(executionId));

        // Assert
        assertEquals(404, th.getCode());
    }

    @Test
    void getExecutionSupportBundleInvalid() {
        // Arrange
        String executionId = TestData.INVALID_PROJECT_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> adminApi.getExecutionSupportBundle(executionId));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(executionId + " is not a valid ID");
    }

}
