package com.dominodatalab.api.rest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map.Entry;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.dominodatalab.TestClientConfigurer;
import com.dominodatalab.TestData;
import com.dominodatalab.api.invoker.ApiClient;
import com.dominodatalab.api.invoker.ApiException;
import com.dominodatalab.api.model.DominoCommonModelsEnvironmentVariable;
import com.dominodatalab.api.model.DominoCommonUserPerson;
import com.dominodatalab.api.model.DominoNucleusProjectModelsCollaborator;
import com.dominodatalab.api.model.DominoProjectsApiCollaboratorDTO;
import com.dominodatalab.api.model.DominoProjectsApiProjectSummary;
import com.dominodatalab.api.model.DominoNucleusProjectModelsCollaborator.ProjectRoleEnum;

class ProjectsApiTest extends TestClientConfigurer {

    ProjectsApi projectsApi;

    @BeforeAll
    void configureApi() {
        ApiClient client = getInternalTestClient();
        projectsApi = new ProjectsApi(client);
    }

    @Test
    void getProjectSummarySuccess() throws ApiException {
        // Arrange
        String projectId = TestData.VALID_PROJECT_ID_0;

        // Act
        DominoProjectsApiProjectSummary result = projectsApi.getProjectSummary(projectId);
        
        // Assert
        assertEquals(projectId, result.getId());
        assertEquals("quick-start", result.getName());
    }

    @Test
    void getProjectSummaryNotFound() {
        // Arrange
        // projectId must conform to "^[0-9a-f]{24}$" to pass input validation
        String projectId = TestData.NOT_FOUND_PROJECT_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.getProjectSummary(projectId));

        // Assert
        assertEquals(404, th.getCode());
    }

    @Test
    void getProjectSummaryInvalidCode() {
        // Arrange
        String projectId = TestData.INVALID_PROJECT_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.getProjectSummary(projectId));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(projectId + " is not a valid ID");
    }
    
    @Test
    void getProjectCollaboratorsSuccess() throws ApiException {
        // Arrange
        String projectId = TestData.VALID_PROJECT_ID_0;

        // Act
        List<DominoCommonUserPerson> collaborators = projectsApi.getProjectCollaborators(projectId, Boolean.FALSE);

        // Assert
        assertEquals(1, collaborators.size());
    }

    @Test
    void getProjectCollaboratorsNotFound() {
        // Arrange
        String projectId = TestData.NOT_FOUND_PROJECT_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.getProjectCollaborators(projectId, Boolean.FALSE));

        // Assert
        assertEquals(404, th.getCode());
    }

    @Test
    void getProjectCollaboratorsInvalidCode() {
        // Arrange
        String projectId = TestData.INVALID_PROJECT_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.getProjectCollaborators(projectId, Boolean.FALSE));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(projectId + " is not a valid ID");
    }
    
    @Test
    void getProjectEnvironmentVariablesSuccess() throws ApiException {
        // Arrange
        String projectId = TestData.VALID_PROJECT_ID_0;

        // Act
        List<DominoCommonModelsEnvironmentVariable> variables = projectsApi.getProjectEnvironmentVariables(projectId);

        // Assert
        assertNotNull(variables);
        assertEquals(1, variables.size());
    }
    
    @Test
    void getProjectEnvironmentVariablesNotFound() throws ApiException {
        // Arrange
        String projectId = TestData.NOT_FOUND_PROJECT_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.getProjectEnvironmentVariables(projectId));

        // Assert
        assertEquals(500, th.getCode());
    }
    
    @Test
    void getProjectEnvironmentVariablesInvalidCode() throws ApiException {
        // Arrange
        String projectId = TestData.INVALID_PROJECT_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.getProjectEnvironmentVariables(projectId));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(projectId + " is not a valid ID");
    }
    
    @Test
    void upsertProjectEnvironmentVariableSuccess() throws ApiException {
        // Arrange
        String projectId = TestData.VALID_PROJECT_ID_0;
        String varName = TestData.TEST_ENVIRONMENT_VARIABLE;
        DominoCommonModelsEnvironmentVariable envVar = new DominoCommonModelsEnvironmentVariable();
        envVar.setName(varName);
        envVar.setValue("true");

        // Act
        DominoCommonModelsEnvironmentVariable variable = projectsApi.upsertProjectEnvironmentVariable(projectId, envVar);

        // Assert
        assertEquals(envVar.getName(), variable.getName());
        assertEquals(envVar.getValue(), variable.getValue());
    }
    
    @Test
    void upsertProjectEnvironmentVariableNotFound() {
        // Arrange
        String projectId = TestData.NOT_FOUND_PROJECT_ID;
        String varName = TestData.TEST_ENVIRONMENT_VARIABLE;
        DominoCommonModelsEnvironmentVariable envVar = new DominoCommonModelsEnvironmentVariable();
        envVar.setName(varName);
        envVar.setValue("true");

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.upsertProjectEnvironmentVariable(projectId, envVar));

        // Assert
        assertEquals(500, th.getCode());
    }
    
    @Test
    void upsertProjectEnvironmentVariableInvalidCode() {
        // Arrange
        String projectId = TestData.INVALID_PROJECT_ID;
        String varName = TestData.TEST_ENVIRONMENT_VARIABLE;
        DominoCommonModelsEnvironmentVariable envVar = new DominoCommonModelsEnvironmentVariable();
        envVar.setName(varName);
        envVar.setValue("true");

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.upsertProjectEnvironmentVariable(projectId, envVar));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(projectId + " is not a valid ID");
    }
    
    @Test
    void upsertProjectEnvironmentVariableNullValue() {
        // Arrange
        String projectId = TestData.VALID_PROJECT_ID_0;
        String varName = TestData.TEST_ENVIRONMENT_VARIABLE;
        DominoCommonModelsEnvironmentVariable envVar = new DominoCommonModelsEnvironmentVariable();
        envVar.setName(varName);
        envVar.setValue(null);

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.upsertProjectEnvironmentVariable(projectId, envVar));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains("Invalid 'value' for EnvironmentVariable");
    }
    
    @Test
    void upsertProjectEnvironmentVariableNullName() {
        // Arrange
        String projectId = TestData.VALID_PROJECT_ID_0;
        DominoCommonModelsEnvironmentVariable envVar = new DominoCommonModelsEnvironmentVariable();
        envVar.setName(null);
        envVar.setValue(null);

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.upsertProjectEnvironmentVariable(projectId, envVar));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains("Invalid 'value' for EnvironmentVariable");
    }
    
    @Test
    void deleteProjectEnvironmentVariableNotFound() {
        // Arrange
        String projectId = TestData.NOT_FOUND_PROJECT_ID;
        String varName = TestData.TEST_ENVIRONMENT_VARIABLE;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.deleteProjectEnvironmentVariable(projectId, varName));

        // Assert
        assertEquals(500, th.getCode());
    }
    
    @Test
    void deleteProjectEnvironmentVariableInvalidCode() {
        // Arrange
        String projectId = TestData.INVALID_PROJECT_ID;
        String varName = TestData.TEST_ENVIRONMENT_VARIABLE;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.deleteProjectEnvironmentVariable(projectId, varName));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(projectId + " is not a valid ID");
    }
    
    @Test
    void deleteProjectEnvironmentVariableInvalidVar() {
        // Arrange
        String projectId = TestData.VALID_PROJECT_ID_0;
        String varName = TestData.INVALID_PROJECT_ID;

        // Act
        assertDoesNotThrow(() -> projectsApi.deleteProjectEnvironmentVariable(projectId, varName));

        // Assert
        //.
    }
    
    @Test
    @Tag("Stateful")
    void projectCollaborators() throws ApiException {
        // Assert initial state - project collaborators do not include test data
        String projectId = TestData.VALID_PROJECT_ID_0;
        String testCollaboratorId = TestData.VALID_PROJECT_COLLABORATOR_ID;
        ProjectRoleEnum targetRole = ProjectRoleEnum.CONTRIBUTOR;

        List<DominoCommonUserPerson> collaborators0 = projectsApi.getProjectCollaborators(projectId, Boolean.FALSE);

        // Pre-condition: Assert test user is not collaborator
        assertEquals(1, collaborators0.size());
        assertFalse(collaborators0.stream().anyMatch(user -> user.getId().equals(testCollaboratorId)));

        // Assign test user to project
        DominoNucleusProjectModelsCollaborator collaborator = new DominoNucleusProjectModelsCollaborator();
        collaborator.setCollaboratorId(testCollaboratorId);
        collaborator.setProjectRole(targetRole);
        projectsApi.addCollaborator(projectId, collaborator);

        DominoProjectsApiProjectSummary summary = projectsApi.getProjectSummary(projectId);
        // This list does not include owner, so size is one less than getProjectCollaborators would report
        List<DominoProjectsApiCollaboratorDTO> collaborators1 = summary.getCollaborators();

        // Assert test user is now in list of collaborators and role matches
        assertEquals(1, collaborators1.size());
        DominoProjectsApiCollaboratorDTO assignedUser = collaborators1.get(0);
        assertEquals(testCollaboratorId, assignedUser.getCollaboratorId());
        assertEquals(targetRole.name(), assignedUser.getProjectRole().name());

        // Remove test user
        projectsApi.removeCollaborator(projectId, testCollaboratorId);

        List<DominoCommonUserPerson> collaborators2 = projectsApi.getProjectCollaborators(projectId, Boolean.FALSE);

        // Assert test user is not in list of collaborators
        assertEquals(1, collaborators2.size());
        assertFalse(collaborators2.stream().anyMatch(user -> user.getId().equals(testCollaboratorId)));
    }
    
    @Test
    @Tag("Stateful")
    void projectEnvironmentVariables() throws ApiException {
        // Assert initial state - project environment variables do not include test data
        String projectId = TestData.VALID_PROJECT_ID_0;
        List<DominoCommonModelsEnvironmentVariable> envVars0 = projectsApi.getProjectEnvironmentVariables(projectId);

        // Pre-condition: Assert test variables are not set
        assertEquals(1, envVars0.size());
        assertTrue(envVars0.stream().noneMatch(var -> TestData.ENVIRONMENT_VARIABLES.keySet().contains(var.getName())));

        for (Entry<String, String> testvar : TestData.ENVIRONMENT_VARIABLES.entrySet()) {
            DominoCommonModelsEnvironmentVariable variable = new DominoCommonModelsEnvironmentVariable();
            variable.setName(testvar.getKey());
            variable.setValue(testvar.getValue());
            projectsApi.upsertProjectEnvironmentVariable(projectId, variable);
        }

        List<DominoCommonModelsEnvironmentVariable> envVars1 = projectsApi.getProjectEnvironmentVariables(projectId);

        // Assert test variables are now set
        assertEquals(3, envVars1.size());
        for(Entry<String, String> testvar : TestData.ENVIRONMENT_VARIABLES.entrySet()) {
            assertTrue(envVars1.stream().anyMatch(var -> var.getName().equals(testvar.getKey()) && var.getValue().equals(testvar.getValue())));
        }

        // Remove test variables
        for (String testvar : TestData.ENVIRONMENT_VARIABLES.keySet()) {
            projectsApi.deleteProjectEnvironmentVariable(projectId, testvar);
        }

        List<DominoCommonModelsEnvironmentVariable> envVars2 = projectsApi.getProjectEnvironmentVariables(projectId);

        assertEquals(1, envVars2.size());
    }

}
