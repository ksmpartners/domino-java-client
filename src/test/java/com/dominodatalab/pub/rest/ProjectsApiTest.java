package com.dominodatalab.pub.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.dominodatalab.TestClientConfigurer;
import com.dominodatalab.pub.invoker.ApiClient;
import com.dominodatalab.pub.invoker.ApiException;
import com.dominodatalab.pub.model.ProjectEnvelopeV1;

public class ProjectsApiTest extends TestClientConfigurer {
    
    ProjectsApi projectsApi;

    @BeforeAll
    void configureApi() {
        ApiClient client = getPublicTestClient();
        projectsApi = new ProjectsApi(client);
    }

    @Test
    void getProjectById_success() throws ApiException {
        // Arrange
        String projectId = "66313c4cdb6cb00661f1a27b";

        // Act
        ProjectEnvelopeV1 result = projectsApi.getProjectById(projectId);
        
        // Assert
        assertEquals(projectId, result.getProject().getId());
        assertEquals("quick-start", result.getProject().getName());
    }

    @Test
    void testGetProjectById_notFound() {
        // Arrange
        // projectId must conform to "^[0-9a-f]{24}$" to pass input validation
        String projectId = "000000000000000000000000";

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.getProjectById(projectId));

        // Assert
        assertEquals(404, th.getCode());
    }

    @Test
    void testGetProjectById_invalidCode() {
        // Arrange
        // projectId must conform to "^[0-9a-f]{24}$" to pass input validation
        String projectId = "invalid";

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.getProjectById(projectId));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(projectId + " is not a valid ID");
    }
    
}
