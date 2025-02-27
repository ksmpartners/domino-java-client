package com.dominodatalab.api.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.dominodatalab.TestClientConfigurer;
import com.dominodatalab.TestData;
import com.dominodatalab.api.invoker.ApiClient;
import com.dominodatalab.api.invoker.ApiException;
import com.dominodatalab.api.model.DominoJobsInterfaceLogContent;
import com.dominodatalab.api.model.DominoJobsInterfaceLogSet;
import com.dominodatalab.api.model.DominoJobsInterfaceLogsWithProblemSuggestion;

class LogsWithProblemSuggestionApiTest extends TestClientConfigurer {
    
    LogsWithProblemSuggestionApi logsWithProblemSuggestionApi;

    @BeforeAll
    void configureApi() {
        ApiClient client = getInternalTestClient();
        logsWithProblemSuggestionApi = new LogsWithProblemSuggestionApi(client);
    }

    @Test
    void getLogsWithProblemSuggestionsSuccess() throws ApiException {
        // Arrange
        String jobId = TestData.VALID_PROJECT_JOB_ID;
        String logType = "stdout";
        BigDecimal limit = new BigDecimal(10000l);
        BigDecimal offset = BigDecimal.ZERO;
        String latestTimeNano = null;

        // Act
        DominoJobsInterfaceLogsWithProblemSuggestion logs = logsWithProblemSuggestionApi.getLogsWithProblemSuggestions(jobId, logType, limit, offset, latestTimeNano);

        // Assert
        assertNotNull(logs);

        DominoJobsInterfaceLogSet logset = logs.getLogset();
        assertNotNull(logset);
        assertTrue(logset.getIsComplete());

        List<DominoJobsInterfaceLogContent> logContent = logset.getLogContent();
        assertNotNull(logContent);
        assertFalse(logContent.isEmpty());
        // stdout still contains logs for environment preparation - actual
        // output is contained among the job logs
        assertTrue(logContent.stream().anyMatch(log -> log.getLog().equals("Hello, world!")));
    }

    @Test
    void getLogsWithProblemSuggestionsNotFound() {
        // Arrange
        String jobId = TestData.NOT_FOUND_DOMINO_ID;
        String logType = "stdout";
        BigDecimal limit = new BigDecimal(10000l);
        BigDecimal offset = BigDecimal.ZERO;
        String latestTimeNano = "0";

        // Act
        ApiException th = assertThrows(ApiException.class, () -> logsWithProblemSuggestionApi.getLogsWithProblemSuggestions(jobId, logType, limit, offset, latestTimeNano));

        // Assert
        assertEquals(404, th.getCode());
    }

    @Test
    void getLogsWithProblemSuggestionsInvalidCode() {
        // Arrange
        String jobId = TestData.INVALID_DOMINO_ID;
        String logType = "stdout";
        BigDecimal limit = new BigDecimal(10000l);
        BigDecimal offset = BigDecimal.ZERO;
        String latestTimeNano = "0";

        // Act
        ApiException th = assertThrows(ApiException.class, () -> logsWithProblemSuggestionApi.getLogsWithProblemSuggestions(jobId, logType, limit, offset, latestTimeNano));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(jobId + " is not a valid ID");
    }

}
