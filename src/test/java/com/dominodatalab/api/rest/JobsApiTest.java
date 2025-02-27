package com.dominodatalab.api.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.logging.Level;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.dominodatalab.TestClientConfigurer;
import com.dominodatalab.TestData;
import com.dominodatalab.api.invoker.ApiClient;
import com.dominodatalab.api.invoker.ApiException;
import com.dominodatalab.api.model.DominoJobsInterfaceComputeClusterConfigSpecDtoComputeEnvironmentRevisionSpecOneOf;
import com.dominodatalab.api.model.DominoJobsInterfaceEnvironmentEnvironmentRevisionSpec;
import com.dominodatalab.api.model.DominoJobsInterfaceJob;
import com.dominodatalab.api.model.DominoJobsInterfaceJobRuntimeExecutionDetails;
import com.dominodatalab.api.model.DominoJobsWebStartJobRequest;
import com.dominodatalab.api.model.DominoJobsWebUpdateJobName;

import lombok.extern.java.Log;

@Log
class JobsApiTest extends TestClientConfigurer {
    
    JobsApi jobsApi;

    @BeforeAll
    void configureApi() {
        ApiClient client = getInternalTestClient();
        jobsApi = new JobsApi(client);
    }
    
    @Test
    void getJobSuccess() throws ApiException {
        // Arrange
        String jobId = TestData.VALID_PROJECT_JOB_ID;

        // Act
        DominoJobsInterfaceJob job = jobsApi.getJob(jobId);

        // Assert
        assertNotNull(job);
        assertEquals(jobId, job.getId());
        assertEquals(TestData.VALID_PROJECT_ID_0, job.getProjectId());
    }
    
    @Test
    void getJobNotFound() {
        // Arrange
        String jobId = TestData.NOT_FOUND_DOMINO_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> jobsApi.getJob(jobId));

        // Assert
        assertEquals(404, th.getCode());
    }
    
    @Test
    void getJobInvalidCode() {
        // Arrange
        String jobId = TestData.INVALID_DOMINO_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> jobsApi.getJob(jobId));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(jobId + " is not a valid ID");
    }

    @Test
    void getRuntimeExecutionDetailsSuccess() throws ApiException {
        // Arrange
        String jobId = TestData.VALID_PROJECT_JOB_ID;

        // Act
        DominoJobsInterfaceJobRuntimeExecutionDetails executionDetails = jobsApi.getRuntimeExecutionDetails(jobId);

        // Assert
        assertNotNull(executionDetails);
        assert(executionDetails.getEnvironment().getEnvironmentName()).contains("Domino Standard Environment");
        assertEquals(1, executionDetails.getEnvironment().getRevisionNumber());
        assertEquals("Small", executionDetails.getHardwareTier().getName());

        // The spec schema for this response object includes component
        // domino.jobs.interface.Environment, which uses a oneOf in the OpenAPI
        // spec. The type switches if the environment is ever upgraded to a new
        // revision, so this is a best effort to provide "good enough"
        // validation that the response object is parsed as expected.
        DominoJobsInterfaceEnvironmentEnvironmentRevisionSpec revisionSpec = executionDetails.getEnvironment().getEnvironmentRevisionSpec();
        assertNotNull(revisionSpec);
        log.log(Level.FINE, "Found environment revision spec [{0}] for Domino Standard Environment", revisionSpec.toUrlQueryString());

        Object spec = revisionSpec.getActualInstance();
        if (spec instanceof String) {
            assertTrue(StringUtils.equalsAny((String) spec, "ActiveRevision", "LatestRevision", "RestrictedRevision"));
        } else {
            assertEquals(executionDetails.getEnvironment().getEnvironmentRevisionId(), ((DominoJobsInterfaceComputeClusterConfigSpecDtoComputeEnvironmentRevisionSpecOneOf) spec).getRevisionId());
        }
    }

    @Test
    void getRuntimeExecutionDetailsNotFound() {
        // Arrange
        String jobId = TestData.NOT_FOUND_DOMINO_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> jobsApi.getRuntimeExecutionDetails(jobId));

        // Assert
        assertEquals(404, th.getCode());
    }

    @Test
    void getRuntimeExecutionDetailsInvalidCode() {
        // Arrange
        String jobId = TestData.INVALID_DOMINO_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> jobsApi.getRuntimeExecutionDetails(jobId));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(jobId + " is not a valid ID");
    }

    @Test
    void updateJobSuccess() throws ApiException {
        // Arrange
        String jobId = TestData.VALID_PROJECT_JOB_ID;
        DominoJobsWebUpdateJobName request = new DominoJobsWebUpdateJobName();
        request.setName("Hello World");

        // Act
        DominoJobsInterfaceJob job = jobsApi.updateJob(jobId, request);

        // Assert
        assertNotNull(job);
        assertEquals(jobId, job.getId());
        assertEquals(request.getName(), job.getTitle());
    }

    @Test
    void updateJobNotFound() {
        // Arrange
        String jobId = TestData.NOT_FOUND_DOMINO_ID;
        DominoJobsWebUpdateJobName request = new DominoJobsWebUpdateJobName();
        request.setName("Hello World");

        // Act
        ApiException th = assertThrows(ApiException.class, () -> jobsApi.updateJob(jobId, request));

        // Assert
        assertEquals(404, th.getCode());
    }

    @Test
    void updateJobInvalidCode() {
        // Arrange
        String jobId = TestData.INVALID_DOMINO_ID;
        DominoJobsWebUpdateJobName request = new DominoJobsWebUpdateJobName();
        request.setName("Hello World");

        // Act
        ApiException th = assertThrows(ApiException.class, () -> jobsApi.updateJob(jobId, request));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(jobId + " is not a valid ID");
    }

    @Test
    void startJobProjectNotFound() {
        // Arrange
        String projectId = TestData.NOT_FOUND_DOMINO_ID;
        String command = "echo 'Hello, world!'";

        DominoJobsWebStartJobRequest request = new DominoJobsWebStartJobRequest();
        request.setCommandToRun(command);
        request.setProjectId(projectId);

        // Act
        ApiException th = assertThrows(ApiException.class, () -> jobsApi.startJob(request));

        // Assert
        assertEquals(404, th.getCode());
    }

    @Test
    void startJobProjectInvalidCode() {
        // Arrange
        String projectId = TestData.INVALID_DOMINO_ID;
        String command = "echo 'Hello, world!'";

        DominoJobsWebStartJobRequest request = new DominoJobsWebStartJobRequest();
        request.setCommandToRun(command);
        request.setProjectId(projectId);

        // Act
        ApiException th = assertThrows(ApiException.class, () -> jobsApi.startJob(request));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains("Json validation error");
    }

    /**
     * While this doesn't effectively change state of Domino that would ruin
     * other tests, it does start an execution which incurs other side effects
     * in Domino, so marked as Stateful.
     */
    @Test
    @Tag("Stateful")
    void startJobSuccess() throws ApiException {
        // Arrange
        String projectId = TestData.VALID_PROJECT_ID_0;
        String command = "echo 'Hello, world!'";

        DominoJobsWebStartJobRequest request = new DominoJobsWebStartJobRequest();
        request.setCommandToRun(command);
        request.setProjectId(projectId);

        // Act
        DominoJobsInterfaceJob job = jobsApi.startJob(request);

        // Assert
        assertNotNull(job);
        assertNotNull(job.getId());
        assertNull(job.getTitle());
        assertEquals(projectId, job.getProjectId());
        assertEquals(command, job.getJobRunCommand());
    }

}
