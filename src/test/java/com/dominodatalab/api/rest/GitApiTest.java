package com.dominodatalab.api.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.dominodatalab.TestClientConfigurer;
import com.dominodatalab.TestData;
import com.dominodatalab.api.invoker.ApiClient;
import com.dominodatalab.api.invoker.ApiException;
import com.dominodatalab.api.model.DominoProjectsApiProjectSummary;
import com.dominodatalab.api.model.DominoProjectsApiRepositoriesReferenceDTO;

class GitApiTest extends TestClientConfigurer {

    GitApi gitApi;
    ProjectsApi projectsApi;

    @BeforeAll
    void configureApi() {
        ApiClient client = getInternalTestClient();
        gitApi = new GitApi(client);
        projectsApi = new ProjectsApi(client);
    }

    @Test
    void updateGitRepositoryDefaultRefMainRepoSuccess() throws ApiException {
        // Arrange
        String projectId = TestData.VALID_PROJECT_ID_1;
        // selects main repo
        String repoId = "_";

        // Get project summary to find current default ref
        DominoProjectsApiProjectSummary project = projectsApi.getProjectSummary(projectId);
        DominoProjectsApiRepositoriesReferenceDTO existingRef = project.getMainRepository().getDefaultRef();

        // Act
        gitApi.updateGitRepositoryDefaultRef(projectId, repoId, existingRef);

        // Assert
        DominoProjectsApiProjectSummary updatedProject = projectsApi.getProjectSummary(projectId);
        DominoProjectsApiRepositoriesReferenceDTO newRef = updatedProject.getMainRepository().getDefaultRef();

        assertEquals(existingRef.getType(), newRef.getType());
        assertEquals(existingRef.getValue(), newRef.getValue());
    }

    @Test
    void updateGitRepositoryDefaultRefProjectNotFound() {
        // Arrange
        String projectId = TestData.NOT_FOUND_DOMINO_ID;
        // selects main repo
        String repoId = "_";

        DominoProjectsApiRepositoriesReferenceDTO ref = new DominoProjectsApiRepositoriesReferenceDTO();
        ref.setType("head");

        // Act
        ApiException th = assertThrows(ApiException.class, () -> gitApi.updateGitRepositoryDefaultRef(projectId, repoId, ref));

        // Assert
        assertEquals(404, th.getCode());
    }

    @Test
    void updateGitRepositoryDefaultRefRepoNotFound() {
        // Arrange
        String projectId = TestData.VALID_PROJECT_ID_1;
        String repoId = TestData.NOT_FOUND_DOMINO_ID;

        DominoProjectsApiRepositoriesReferenceDTO ref = new DominoProjectsApiRepositoriesReferenceDTO();
        ref.setType("head");

        // Act
        ApiException th = assertThrows(ApiException.class, () -> gitApi.updateGitRepositoryDefaultRef(projectId, repoId, ref));

        // Assert
        assertEquals(500, th.getCode());
        assert(th.getMessage()).contains("Unable to find repository " + repoId);
    }

    @Test
    void updateGitRepositoryDefaultRefProjectInvalidCode() {
        // Arrange
        String projectId = TestData.INVALID_DOMINO_ID;
        // selects main repo
        String repoId = "_";

        DominoProjectsApiRepositoriesReferenceDTO ref = new DominoProjectsApiRepositoriesReferenceDTO();
        ref.setType("head");

        // Act
        ApiException th = assertThrows(ApiException.class, () -> gitApi.updateGitRepositoryDefaultRef(projectId, repoId, ref));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(projectId + " is not a valid ID");
    }
    
}
