package com.dominodatalab.api.rest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.dominodatalab.api.model.DominoProjectsApiProjectSummary;

class ProjectsApiTest extends TestClientConfigurer {

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
    
    @Test
    void testGetProjectEnvironmentVariables_success() throws ApiException {
        // Arrange
        String projectId = TestData.VALID_PROJECT_ID_0;

        // Act
        List<DominoCommonModelsEnvironmentVariable> variables = projectsApi.getProjectEnvironmentVariables(projectId);

        // Assert
        assertNotNull(variables);
        assertEquals(1, variables.size());
    }
    
    @Test
    void testGetProjectEnvironmentVariables_notFound() throws ApiException {
        // Arrange
        String projectId = TestData.NOT_FOUND_PROJECT_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.getProjectEnvironmentVariables(projectId));

        // Assert
        assertEquals(500, th.getCode());
    }
    
    @Test
    void testGetProjectEnvironmentVariables_invalidCode() throws ApiException {
        // Arrange
        String projectId = TestData.INVALID_PROJECT_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.getProjectEnvironmentVariables(projectId));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(projectId + " is not a valid ID");
    }
    
    @Test
    void testUpsertProjectEnvironmentVariable_success() throws ApiException {
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
    void testUpsertProjectEnvironmentVariable_notFound() {
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
    void testUpsertProjectEnvironmentVariable_invalidCode() {
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
    void testUpsertProjectEnvironmentVariable_nullValue() {
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
    void testUpsertProjectEnvironmentVariable_nullName() {
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
    void testDeleteProjectEnvironmentVariable_notFound() {
        // Arrange
        String projectId = TestData.NOT_FOUND_PROJECT_ID;
        String varName = TestData.TEST_ENVIRONMENT_VARIABLE;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.deleteProjectEnvironmentVariable(projectId, varName));

        // Assert
        assertEquals(500, th.getCode());
    }
    
    @Test
    void testDeleteProjectEnvironmentVariable_invalidCode() {
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
    void testDeleteProjectEnvironmentVariable_invalidVar() {
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
    void testProjectEnvironmentVariables() throws ApiException {
        // Assert initial state - user environment variables do not include test data
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
