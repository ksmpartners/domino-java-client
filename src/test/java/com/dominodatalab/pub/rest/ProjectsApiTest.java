package com.dominodatalab.pub.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.dominodatalab.TestClientConfigurer;
import com.dominodatalab.TestData;
import com.dominodatalab.pub.invoker.ApiClient;
import com.dominodatalab.pub.invoker.ApiException;
import com.dominodatalab.pub.model.PaginatedGitRepositoriesEnvelopeV1;
import com.dominodatalab.pub.model.ProjectEnvelopeV1;

class ProjectsApiTest extends TestClientConfigurer {
    
    ProjectsApi projectsApi;

    @BeforeAll
    void configureApi() {
        ApiClient client = getPublicTestClient();
        projectsApi = new ProjectsApi(client);
    }

    @Test
    void getProjectById_success() throws ApiException {
        // Arrange
        String projectId = TestData.VALID_PROJECT_ID_0;

        // Act
        ProjectEnvelopeV1 result = projectsApi.getProjectById(projectId);
        
        // Assert
        assertEquals(projectId, result.getProject().getId());
        assertEquals("quick-start", result.getProject().getName());
    }

    @Test
    void getProjectByIdNotFound() {
        // Arrange
        String projectId = TestData.NOT_FOUND_PROJECT_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.getProjectById(projectId));

        // Assert
        assertEquals(404, th.getCode());
    }

    @Test
    void getProjectByIdInvalidCode() {
        // Arrange
        String projectId = TestData.INVALID_PROJECT_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.getProjectById(projectId));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(projectId + " is not a valid ID");
    }
    
    @Test
    void getImportedReposSuccess() throws ApiException {
        // Arrange
        String projectId = TestData.VALID_PROJECT_ID_0;

        // Act
        PaginatedGitRepositoriesEnvelopeV1 repos = projectsApi.getImportedRepos(projectId, 0, 10);

        // Assert
        assertEquals(0, repos.getRepositories().size());
    }

    @Test
    void getImportedReposNotFound() {
        // Arrange
        String projectId = TestData.NOT_FOUND_PROJECT_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.getImportedRepos(projectId, 0, 10));

        // Assert
        assertEquals(404, th.getCode());
    }

    @Test
    void getImportedReposInvalidCode() {
        // Arrange
        String projectId = TestData.INVALID_PROJECT_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.getImportedRepos(projectId, 0, 10));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(projectId + " is not a valid ID");
    }
    
}
