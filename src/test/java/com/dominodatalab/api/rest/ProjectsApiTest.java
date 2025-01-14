package com.dominodatalab.api.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.dominodatalab.TestClientConfigurer;
import com.dominodatalab.TestData;
import com.dominodatalab.api.invoker.ApiClient;
import com.dominodatalab.api.invoker.ApiException;
import com.dominodatalab.api.model.DominoCommonUserPerson;
import com.dominodatalab.api.model.DominoProjectsApiProjectSummary;

public class ProjectsApiTest extends TestClientConfigurer {

    ProjectsApi projectsApi;

    @BeforeAll
    void configureApi() {
        ApiClient client = getInternalTestClient();
        projectsApi = new ProjectsApi(client);
    }

    @Test
    void testGetProjectSummary_success() throws ApiException {
        // Arrange
        String projectId = TestData.VALID_PROJECT_ID_0;

        // Act
        DominoProjectsApiProjectSummary result = projectsApi.getProjectSummary(projectId);
        
        // Assert
        assertEquals(projectId, result.getId());
        assertEquals("quick-start", result.getName());
    }

    @Test
    void testGetProjectSummary_notFound() {
        // Arrange
        // projectId must conform to "^[0-9a-f]{24}$" to pass input validation
        String projectId = TestData.NOT_FOUND_PROJECT_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.getProjectSummary(projectId));

        // Assert
        assertEquals(404, th.getCode());
    }

    @Test
    void testGetProjectSummary_invalidCode() {
        // Arrange
        String projectId = TestData.INVALID_PROJECT_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.getProjectSummary(projectId));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(projectId + " is not a valid ID");
    }
    
    @Test
    void testGetProjectCollaborators_success() throws ApiException {
        // Arrange
        String projectId = TestData.VALID_PROJECT_ID_0;

        // Act
        List<DominoCommonUserPerson> collaborators = projectsApi.getProjectCollaborators(projectId, Boolean.FALSE);

        // Assert
        assertEquals(1, collaborators.size());
    }

    @Test
    void testGetProjectCollaborators_notFound() {
        // Arrange
        String projectId = TestData.NOT_FOUND_PROJECT_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.getProjectCollaborators(projectId, Boolean.FALSE));

        // Assert
        assertEquals(404, th.getCode());
    }

    @Test
    void testGetProjectCollaborators_invalidCode() {
        // Arrange
        String projectId = TestData.INVALID_PROJECT_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.getProjectCollaborators(projectId, Boolean.FALSE));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(projectId + " is not a valid ID");
    }

}
