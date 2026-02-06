package com.dominodatalab.api.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.dominodatalab.TestClientConfigurer;
import com.dominodatalab.TestData;
import com.dominodatalab.api.invoker.ApiClient;
import com.dominodatalab.api.invoker.ApiException;
import com.dominodatalab.api.model.DominoProjectManagementWebFilterProjectGoals;
import com.dominodatalab.api.model.DominoProjectManagementWebLinkJobToGoalRequest;
import com.dominodatalab.api.model.DominoProjectManagementWebUnlinkJobFromGoalRequest;
import com.dominodatalab.api.model.DominoProjectsApiProjectGoal;

class ProjectManagementApiTest extends TestClientConfigurer {
    
    ProjectManagementApi projectManagementApi;

    @BeforeAll
    void configureApi() {
        ApiClient client = getInternalTestClient();
        projectManagementApi = new ProjectManagementApi(client);
    }
    
    @Test
    void getFilteredProjectGoalsSuccess() throws ApiException {
        // Arrange
        String projectId = TestData.VALID_PROJECT_ID_0;
        DominoProjectManagementWebFilterProjectGoals filter = new DominoProjectManagementWebFilterProjectGoals();

        // Act
        List<DominoProjectsApiProjectGoal> goals = projectManagementApi.getFilteredProjectGoals(projectId, filter);

        // Assert
        assertNotNull(goals);
    }
    
    @Test
    void getFilteredProjectGoalsNotFound() {
        // Arrange
        String projectId = TestData.NOT_FOUND_DOMINO_ID;
        DominoProjectManagementWebFilterProjectGoals filter = new DominoProjectManagementWebFilterProjectGoals();

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectManagementApi.getFilteredProjectGoals(projectId, filter));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains("Could not find project with ID '" + projectId + "'");
    }
    
    @Test
    void getFilteredProjectGoalsInvalidCode() {
        // Arrange
        String projectId = TestData.INVALID_DOMINO_ID;
        DominoProjectManagementWebFilterProjectGoals filter = new DominoProjectManagementWebFilterProjectGoals();

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectManagementApi.getFilteredProjectGoals(projectId, filter));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(projectId + " is not a valid ID");
    }
    
    @Test
    void getProjectGoalsSuccess() throws ApiException {
        // Arrange
        String projectId = TestData.VALID_PROJECT_ID_0;

        // Act
        List<DominoProjectsApiProjectGoal> goals = projectManagementApi.getProjectGoals(projectId);

        // Assert
        assertNotNull(goals);
    }
    
    @Test
    void getProjectGoalsNotFound() {
        // Arrange
        String projectId = TestData.NOT_FOUND_DOMINO_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectManagementApi.getProjectGoals(projectId));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains("Could not find project with ID '" + projectId + "'");
    }
    
    @Test
    void getProjectGoalsInvalidCode() {
        // Arrange
        String projectId = TestData.INVALID_DOMINO_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> projectManagementApi.getProjectGoals(projectId));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(projectId + " is not a valid ID");
    }

    @Test
    @Tag("Stateful")
    void linkJobToGoal() throws ApiException {
        // Assert initial state - project goal is not linked to job
        String projectId = TestData.VALID_PROJECT_ID_0;
        String jobId = TestData.VALID_PROJECT_JOB_ID;
        String goalId = TestData.VALID_PROJECT_GOAL_ID;

        List<DominoProjectsApiProjectGoal> goals0 = projectManagementApi.getProjectGoals(projectId);

        // Pre-condition: assert goal is not linked to job
        assertTrue(goals0.stream().noneMatch(goal -> goal.getId().equals(goalId) && goal.getLinkedEntities().stream().anyMatch(link -> link.getEntityLinkType().getValue().equals("job") && getObjValue(link.getMetadata(), "jobId").equals(jobId))), "Pre-condition failed: test goal is already linked to job");
        
        // Link job to goal
        DominoProjectManagementWebLinkJobToGoalRequest linkRequest = new DominoProjectManagementWebLinkJobToGoalRequest();
        linkRequest.setGoalId(goalId);

        projectManagementApi.linkJobToGoal(jobId, linkRequest);

        List<DominoProjectsApiProjectGoal> goals1 = projectManagementApi.getProjectGoals(projectId);

        // Assert goal is now linked
        assertTrue(goals1.stream().anyMatch(goal -> goal.getId().equals(goalId) && goal.getLinkedEntities().stream().anyMatch(link -> link.getEntityLinkType().getValue().equals("job") && getObjValue(link.getMetadata(), "jobId").equals(jobId))));
        
        // Unlink job from goal
        DominoProjectManagementWebUnlinkJobFromGoalRequest unlinkRequest = new DominoProjectManagementWebUnlinkJobFromGoalRequest();
        unlinkRequest.setGoalId(goalId);

        projectManagementApi.unlinkJobFromGoal(jobId, unlinkRequest);

        List<DominoProjectsApiProjectGoal> goals2 = projectManagementApi.getProjectGoals(projectId);

        // Assert goal is no longer linked
        assertTrue(goals2.stream().noneMatch(goal -> goal.getId().equals(goalId) && goal.getLinkedEntities().stream().anyMatch(link -> link.getEntityLinkType().getValue().equals("job") && getObjValue(link.getMetadata(), "jobId").equals(jobId))));
    }

    /**
     * Jackson seems to deserialize objects as {@link LinkedHashMap}.
     * This method co-erces an {@link Object} (assuming some Jackson
     * deserialized object) to try to get a value associated with a key, as a
     * String. For max compatibility, returns empty string instead of null.
     */
    private static String getObjValue(final Object metadata, final Object key) {
        String value = StringUtils.EMPTY;

        if (metadata instanceof LinkedHashMap) {
            Object v = ((LinkedHashMap<?, ?>) metadata).get(key);
            if (v != null) {
                value = (String) v;
            }
        }

        return value;
    }
    

}
