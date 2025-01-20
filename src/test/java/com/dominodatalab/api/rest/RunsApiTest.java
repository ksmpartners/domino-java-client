package com.dominodatalab.api.rest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.dominodatalab.TestClientConfigurer;
import com.dominodatalab.api.invoker.ApiClient;
import com.dominodatalab.api.invoker.ApiException;
import com.dominodatalab.api.model.DominoCommonRunInterfacesRunMonolithDTO;

class RunsApiTest extends TestClientConfigurer {

    RunsApi runsApi;

    @BeforeAll
    void configureApi() {
        ApiClient client = getInternalTestClient();
        runsApi = new RunsApi(client);
    }

    @Test
    void listRecentRunsSuccess() throws ApiException {
        // Arrange

        // Act
        List<DominoCommonRunInterfacesRunMonolithDTO> runs = runsApi.listRecentRuns();

        // Assert
        assertNotNull(runs);
        // User may not have had any "recent runs" - checks on list content may break
    }

    
}
