package com.dominodatalab.api.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.dominodatalab.TestClientConfigurer;
import com.dominodatalab.TestData;
import com.dominodatalab.api.invoker.ApiClient;
import com.dominodatalab.api.invoker.ApiException;
import com.dominodatalab.api.model.DominoServerProjectsApiProjectGatewayOverview;
import com.dominodatalab.api.model.DominoServerProjectsApiProjectGatewaySummary;

class GatewayApiTest extends TestClientConfigurer {
    
    GatewayApi gatewayApi;
    UsersApi usersApi;

    @BeforeAll
    void configureApi() {
        ApiClient client = getInternalTestClient();
        gatewayApi = new GatewayApi(client);
        usersApi = new UsersApi(client);
    }

    String getUserName() throws ApiException {
        return usersApi.getCurrentUser().getUserName();
    }

    @Test
    void findProjectByOwnerAndNameSuccess() throws ApiException {
        // Arrange
        String ownerName = getUserName();
        String projectName = "quick-start";

        // Act
        DominoServerProjectsApiProjectGatewayOverview project = gatewayApi.findProjectByOwnerAndName(ownerName, projectName);
        
        // Assert
        assertEquals(projectName, project.getName());
        assertEquals(TestData.VALID_PROJECT_ID_0, project.getId());
    }

    @Test
    void findProjectByOwnerAndNameNotFound() throws ApiException {
        // Arrange
        String ownerName = getUserName();
        String projectName = "project-does-not-exist";

        // Act
        ApiException th = assertThrows(ApiException.class, () -> gatewayApi.findProjectByOwnerAndName(ownerName, projectName));
        
        // Assert
        assertEquals(404, th.getCode());
    }

    @Test
    void listProjectsSuccess() throws ApiException {
        // Arrange
        String relationship = "Owned";

        // Act
        List<DominoServerProjectsApiProjectGatewaySummary> projects = gatewayApi.listProjects(relationship, Boolean.TRUE, null, null);

        // Assert
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
    }

    @Test
    void listProjectsInvalidRelationship() {
        // Arrange
        String relationship = TestData.INVALID_DOMINO_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> gatewayApi.listProjects(relationship, Boolean.TRUE, null, null));

        // Assert
        assertEquals(500, th.getCode());
        assert(th.getMessage()).contains("No value found for '" + relationship + "'");
    }
    
}
