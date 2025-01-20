package com.dominodatalab.api.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.dominodatalab.TestClientConfigurer;
import com.dominodatalab.TestData;
import com.dominodatalab.api.invoker.ApiClient;
import com.dominodatalab.api.invoker.ApiException;
import com.dominodatalab.api.model.DominoScheduledjobApiLegacyScheduledRunDTO;

class ScheduledRunsApiTest extends TestClientConfigurer {
    
    ScheduledRunsApi scheduledRunsApi;
    UsersApi usersApi;

    @BeforeAll
    void configureApi() {
        ApiClient client = getInternalTestClient();
        scheduledRunsApi = new ScheduledRunsApi(client);
        usersApi = new UsersApi(client);
    }

    String getUserId() throws ApiException {
        return usersApi.getCurrentUser().getId();
    }

    @Test
    void listScheduledRunsSuccess() throws ApiException {
        // Arrange
        String userId = getUserId();

        // Act
        List<DominoScheduledjobApiLegacyScheduledRunDTO> runs = scheduledRunsApi.listScheduledRuns(userId);

        // Assert
        assertNotNull(runs);
        // User may not have any scheduled runs - checks on list content may break
    }

    @Test
    void listScheduledRunsNotFound() {
        // Arrange
        String userId = TestData.NOT_FOUND_DOMINO_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> scheduledRunsApi.listScheduledRuns(userId));

        // Assert
        assertEquals(404, th.getCode());
    }

}
