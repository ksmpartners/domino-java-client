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
import com.dominodatalab.api.model.DominoHardwaretierApiHardwareTierWithCapacityDto;
import com.dominodatalab.api.model.DominoNucleusProjectModelsCollaborator;
import com.dominodatalab.api.model.DominoProjectsApiCollaboratorDTO;
import com.dominodatalab.api.model.DominoProjectsApiProjectGoal;
import com.dominodatalab.api.model.DominoProjectsApiProjectSummary;
import com.dominodatalab.api.model.DominoProjectsApiRepositoriesGitRepositoryDTO;
import com.dominodatalab.api.model.DominoProjectsApiRepositoriesReferenceDTO;
import com.dominodatalab.api.model.DominoProjectsApiUseableProjectEnvironmentsDTO;
import com.dominodatalab.api.model.DominoNucleusProjectModelsCollaborator.ProjectRoleEnum;

class ProjectsApiTest extends TestClientConfigurer {

    ProjectsApi projectsApi;
    GitApi gitApi;

    @BeforeAll
    void configureApi() {
        ApiClient client = getInternalTestClient();
        projectsApi = new ProjectsApi(client);
        gitApi = new GitApi(client);
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
        String projectId = TestData.NOT_FOUND_DOMINO_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.getProjectSummary(projectId));

        // Assert
        assertEquals(404, th.getCode());
    }

    @Test
    void getProjectSummaryInvalidCode() {
        // Arrange
        String projectId = TestData.INVALID_DOMINO_ID;

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
        String projectId = TestData.NOT_FOUND_DOMINO_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.getProjectCollaborators(projectId, Boolean.FALSE));

        // Assert
        assertEquals(404, th.getCode());
    }

    @Test
    void getProjectCollaboratorsInvalidCode() {
        // Arrange
        String projectId = TestData.INVALID_DOMINO_ID;

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
        String projectId = TestData.NOT_FOUND_DOMINO_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.getProjectEnvironmentVariables(projectId));

        // Assert
        assertEquals(500, th.getCode());
    }
    
    @Test
    void getProjectEnvironmentVariablesInvalidCode() throws ApiException {
        // Arrange
        String projectId = TestData.INVALID_DOMINO_ID;

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
        String projectId = TestData.NOT_FOUND_DOMINO_ID;
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
        String projectId = TestData.INVALID_DOMINO_ID;
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

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.upsertProjectEnvironmentVariable(projectId, envVar));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains("Invalid 'value' for EnvironmentVariable");
    }
    
    @Test
    void deleteProjectEnvironmentVariableNotFound() {
        // Arrange
        String projectId = TestData.NOT_FOUND_DOMINO_ID;
        String varName = TestData.TEST_ENVIRONMENT_VARIABLE;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.deleteProjectEnvironmentVariable(projectId, varName));

        // Assert
        assertEquals(500, th.getCode());
    }
    
    @Test
    void deleteProjectEnvironmentVariableInvalidCode() {
        // Arrange
        String projectId = TestData.INVALID_DOMINO_ID;
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
        String varName = TestData.INVALID_DOMINO_ID;

        // Act
        assertDoesNotThrow(() -> projectsApi.deleteProjectEnvironmentVariable(projectId, varName));

        // Assert
        //.
    }
    
    @Test
    void listHardwareTiersForProjectSuccess() throws ApiException {
        // Arrange
        String projectId = TestData.VALID_PROJECT_ID_0;

        // Act
        List<DominoHardwaretierApiHardwareTierWithCapacityDto> tiers = projectsApi.listHardwareTiersForProject(projectId, Boolean.FALSE, Boolean.FALSE);

        // Assert
        assertNotNull(tiers);
        assertFalse(tiers.isEmpty());
    }
    
    @Test
    void listHardwareTiersForProjectNotFound() {
        // Arrange
        String projectId = TestData.NOT_FOUND_DOMINO_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.listHardwareTiersForProject(projectId, Boolean.FALSE, Boolean.FALSE));

        // Assert
        assertEquals(404, th.getCode());
    }
    
    @Test
    void listHardwareTiersForProjectInvalidCode() {
        // Arrange
        String projectId = TestData.INVALID_DOMINO_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.listHardwareTiersForProject(projectId, Boolean.FALSE, Boolean.FALSE));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(projectId + " is not a valid ID");
    }
    
    @Test
    void getUseableEnvironmentsSuccess() throws ApiException {
        // Arrange
        String projectId = TestData.VALID_PROJECT_ID_0;

        // Act
        DominoProjectsApiUseableProjectEnvironmentsDTO environments = projectsApi.getUseableEnvironments(projectId);

        // Assert
        assertNotNull(environments.getCurrentlySelectedEnvironment());
        assertFalse(environments.getEnvironments().isEmpty());
    }
    
    @Test
    void getUseableEnvironmentsNotFound() {
        // Arrange
        String projectId = TestData.NOT_FOUND_DOMINO_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.getUseableEnvironments(projectId));

        // Assert
        assertEquals(404, th.getCode());
    }
    
    @Test
    void getUseableEnvironmentsInvalidCode() {
        // Arrange
        String projectId = TestData.INVALID_DOMINO_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.getUseableEnvironments(projectId));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(projectId + " is not a valid ID");
    }
    
    @Test
    void getGitReposSuccess() throws ApiException {
        // Arrange
        String projectId = TestData.VALID_PROJECT_ID_0;

        // Act
        List<DominoProjectsApiRepositoriesGitRepositoryDTO> repos = projectsApi.getGitRepos(projectId);

        // Assert
        assertNotNull(repos);
        assertFalse(repos.isEmpty());
    }
    
    @Test
    void getGitReposNotFound() {
        // Arrange
        String projectId = TestData.NOT_FOUND_DOMINO_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.getGitRepos(projectId));

        // Assert
        assertEquals(404, th.getCode());
    }
    
    @Test
    void getGitReposInvalidCode() {
        // Arrange
        String projectId = TestData.INVALID_DOMINO_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.getGitRepos(projectId));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(projectId + " is not a valid ID");
    }
    
    @Test
    void updateGitRepositoryDefaultRefSuccess() throws ApiException {
        // Arrange
        String projectId = TestData.VALID_PROJECT_ID_0;
        String repoId = TestData.VALID_PROJECT_GIT_REPO_ID;

        DominoProjectsApiRepositoriesReferenceDTO newRef = new DominoProjectsApiRepositoriesReferenceDTO();
        newRef.setType("branches");
        newRef.setValue("main");

        // Act
        projectsApi.updateGitRepositoryDefaultRef(projectId, repoId, newRef);

        // Assert
        List<DominoProjectsApiRepositoriesGitRepositoryDTO> repos = projectsApi.getGitRepos(projectId);
        assertTrue(repos.size() > 0);
        DominoProjectsApiRepositoriesGitRepositoryDTO updatedRepo = repos.stream().filter(repo -> repoId.equals(repo.getId())).findFirst().get();
        DominoProjectsApiRepositoriesReferenceDTO updatedRef = updatedRepo.getRef();
        assertEquals(newRef.getType(), updatedRef.getType());
        assertEquals(newRef.getValue(), updatedRef.getValue());
    }
    
    @Test
    void updateGitRepositoryDefaultRefProjectNotFound() {
        // Arrange
        String projectId = TestData.NOT_FOUND_DOMINO_ID;
        String repoId = TestData.VALID_PROJECT_GIT_REPO_ID;

        DominoProjectsApiRepositoriesReferenceDTO newRef = new DominoProjectsApiRepositoriesReferenceDTO();
        newRef.setType("branches");
        newRef.setValue("main");

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.updateGitRepositoryDefaultRef(projectId, repoId, newRef));

        // Assert
        assertEquals(404, th.getCode());
    }
    
    @Test
    void updateGitRepositoryDefaultRefProjectInvalidCode() {
        // Arrange
        String projectId = TestData.INVALID_DOMINO_ID;
        String repoId = TestData.VALID_PROJECT_GIT_REPO_ID;

        DominoProjectsApiRepositoriesReferenceDTO newRef = new DominoProjectsApiRepositoriesReferenceDTO();
        newRef.setType("branches");
        newRef.setValue("main");

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.updateGitRepositoryDefaultRef(projectId, repoId, newRef));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(projectId + " is not a valid ID");
    }
    
    @Test
    void markProjectGoalCompleteSuccess() throws ApiException {
        // Arrange
        String projectId = TestData.VALID_PROJECT_ID_0;
        String goalId = TestData.VALID_PROJECT_GOAL_ID;

        // Act
        DominoProjectsApiProjectGoal goal = projectsApi.markProjectGoalComplete(projectId, goalId);

        // Assert
        assertNotNull(goal);
        assertEquals(goalId, goal.getId());
        assertEquals(projectId, goal.getProjectId());
        assertTrue(goal.getIsComplete());
    }
    
    @Test
    void markProjectGoalCompleteProjectNotFound() {
        // Arrange
        String projectId = TestData.NOT_FOUND_DOMINO_ID;
        String goalId = TestData.VALID_PROJECT_GOAL_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.markProjectGoalComplete(projectId, goalId));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains("Could not find project with ID '" + projectId + "'");
    }
    
    @Test
    void markProjectGoalCompleteProjectInvalidCode() {
        // Arrange
        String projectId = TestData.INVALID_DOMINO_ID;
        String goalId = TestData.VALID_PROJECT_GOAL_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.markProjectGoalComplete(projectId, goalId));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(projectId + " is not a valid ID");
    }
    
    @Test
    void markProjectGoalCompleteGoalNotFound() {
        // Arrange
        String projectId = TestData.VALID_PROJECT_ID_0;
        String goalId = TestData.NOT_FOUND_DOMINO_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.markProjectGoalComplete(projectId, goalId));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains("Cannot find project goal with id '" + goalId + "'");
    }
    
    @Test
    void markProjectGoalCompleteGoalInvalidCode() {
        // Arrange
        String projectId = TestData.VALID_PROJECT_ID_0;
        String goalId = TestData.INVALID_DOMINO_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectsApi.markProjectGoalComplete(projectId, goalId));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(goalId + " is not a valid ID");
    }

    /**
     * Tests adding and removing a user as a collaborator of a Domino project.
     * 
     * @throws ApiException if any error code is received from the Domino API.
     */
    @Test
    @Tag("Stateful")
    void projectCollaborators() throws ApiException {
        // Assert initial state - project collaborators do not include test data
        String projectId = TestData.VALID_PROJECT_ID_0;
        String testCollaboratorId = TestData.VALID_PROJECT_COLLABORATOR_ID;
        ProjectRoleEnum targetRole = ProjectRoleEnum.CONTRIBUTOR;

        List<DominoCommonUserPerson> collaborators0 = projectsApi.getProjectCollaborators(projectId, Boolean.FALSE);

        // Pre-condition: Assert test user is not collaborator
        assertEquals(1, collaborators0.size(), "Pre-condition failed: project configuration does not include only one collaborator (owner)");
        assertTrue(collaborators0.stream().noneMatch(user -> user.getId().equals(testCollaboratorId)), "Pre-condition failed: project configuration includes test user as collaborator");

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
        assertTrue(collaborators2.stream().noneMatch(user -> user.getId().equals(testCollaboratorId)));
    }
    
    /**
     * Tests adding and removing environment variables for a Domino project.
     * 
     * @throws ApiException if any error code is received from the Domino API.
     */
    @Test
    @Tag("Stateful")
    void projectEnvironmentVariables() throws ApiException {
        // Assert initial state - project environment variables do not include test data
        String projectId = TestData.VALID_PROJECT_ID_0;
        List<DominoCommonModelsEnvironmentVariable> envVars0 = projectsApi.getProjectEnvironmentVariables(projectId);

        // Pre-condition: Assert test variables are not set
        assertEquals(1, envVars0.size(), "Pre-condition failed: project configuration does not include exactly one environment variable");
        assertTrue(envVars0.stream().noneMatch(var -> TestData.ENVIRONMENT_VARIABLES.keySet().contains(var.getName())), "Pre-condition failed: project configuration includes test environment variables");

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
        assertTrue(envVars2.stream().noneMatch(var -> TestData.ENVIRONMENT_VARIABLES.keySet().contains(var.getName())));
    }
    
    /**
     * Tests adding, updating, and removing an imported git repo for a Domino project.
     * 
     * @throws ApiException if any error code is received from the Domino API.
     */
    @Test
    @Tag("Stateful")
    void importedGitRepos() throws ApiException {
        // Assert initial state - project imported git repos do not include test data
        String projectId = TestData.VALID_PROJECT_ID_0;
        String testRepoUri = TestData.VALID_PROJECT_TEST_REPO;
        String serviceProvider = "github";
        List<DominoProjectsApiRepositoriesGitRepositoryDTO> repos0 = projectsApi.getGitRepos(projectId);
        
        // Pre-condition: Assert test git repo is not present
        assertEquals(1, repos0.size(), "Pre-condition failed: project configuration does not include exactly one imported git repository");
        assertTrue(repos0.stream().noneMatch(repo -> repo.getUri().equals(testRepoUri)), "Pre-condition failed: project configuration includes imported test git repository");

        DominoProjectsApiRepositoriesGitRepositoryDTO testRepo = new DominoProjectsApiRepositoriesGitRepositoryDTO();
        testRepo.setUri(testRepoUri);
        testRepo.setServiceProvider(serviceProvider);
        DominoProjectsApiRepositoriesReferenceDTO ref = new DominoProjectsApiRepositoriesReferenceDTO();
        ref.setType("head");
        testRepo.setRef(ref);
        DominoProjectsApiRepositoriesGitRepositoryDTO result = projectsApi.addGitRepo(projectId, testRepo);
        String resultId = result.getId();
        assertNotNull(resultId);

        List<DominoProjectsApiRepositoriesGitRepositoryDTO> repos1 = projectsApi.getGitRepos(projectId);

        // Assert new imported git repo is now present
        assertEquals(2, repos1.size());
        DominoProjectsApiRepositoriesGitRepositoryDTO targetRepo0 = repos1.stream().filter(repo -> resultId.equals(repo.getId())).findFirst().get();
        assertNotNull(targetRepo0.getName());
        assertEquals(testRepoUri, targetRepo0.getUri());
        assertEquals(serviceProvider, targetRepo0.getServiceProvider());
        assertEquals(ref.getType(), targetRepo0.getRef().getType());

        // Update repo name
        targetRepo0.setName("foobar");
        projectsApi.editGitRepo(projectId, result.getId(), targetRepo0);

        // Update repo ref
        DominoProjectsApiRepositoriesReferenceDTO newRef = new DominoProjectsApiRepositoriesReferenceDTO();
        newRef.setType("branches");
        newRef.setValue("main");
        gitApi.updateGitRepositoryDefaultRef(projectId, targetRepo0.getId(), newRef);

        List<DominoProjectsApiRepositoriesGitRepositoryDTO> repos2 = projectsApi.getGitRepos(projectId);

        // Assert updated imported git repo is now present
        assertEquals(2, repos2.size());
        DominoProjectsApiRepositoriesGitRepositoryDTO targetRepo1 = repos2.stream().filter(repo -> resultId.equals(repo.getId())).findFirst().get();
        assertEquals("foobar", targetRepo1.getName());
        assertEquals(testRepoUri, targetRepo1.getUri());
        assertEquals(serviceProvider, targetRepo1.getServiceProvider());
        assertEquals(newRef.getType(), targetRepo1.getRef().getType());
        assertEquals(newRef.getValue(), targetRepo1.getRef().getValue());
        
        // Remove test imported git repo
        projectsApi.archiveGitRepo(projectId, result.getId());

        List<DominoProjectsApiRepositoriesGitRepositoryDTO> repos3 = projectsApi.getGitRepos(projectId);

        // assert new imported git repo is no longer present
        assertEquals(1, repos3.size());
        assertTrue(repos3.stream().noneMatch(repo -> repo.getUri().equals(testRepoUri)));
    }

}
