package com.dominodatalab.api.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.dominodatalab.TestClientConfigurer;
import com.dominodatalab.TestData;
import com.dominodatalab.api.invoker.ApiClient;
import com.dominodatalab.api.invoker.ApiException;
import com.dominodatalab.api.model.DominoDatasetrwApiDatasetRwDto;
import com.dominodatalab.api.model.DominoDatasetrwApiDatasetRwFileDetailsRowDto;
import com.dominodatalab.api.model.DominoDatasetrwApiDatasetRwFiletaskTaskDto;
import com.dominodatalab.api.model.DominoDatasetrwApiDatasetRwGrant;
import com.dominodatalab.api.model.DominoDatasetrwApiDatasetRwGrantDetails;
import com.dominodatalab.api.model.DominoDatasetrwApiDatasetRwInfoDto;
import com.dominodatalab.api.model.DominoDatasetrwApiDatasetRwProjectMountDto;
import com.dominodatalab.api.model.DominoDatasetrwApiDatasetRwSnapshotDto;
import com.dominodatalab.api.model.DominoDatasetrwApiDatasetRwSnapshotFilesViewDto;
import com.dominodatalab.api.model.DominoDatasetrwApiDatasetRwSnapshotSummaryDto;
import com.dominodatalab.api.model.DominoDatasetrwWebCreateDownloadArchiveRequest;
import com.dominodatalab.api.model.DominoDatasetrwWebUpdateDatasetGrantsRequest;
import com.dominodatalab.api.model.DominoDatasetrwApiDatasetRwGrant.TargetRoleEnum;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatasetRwApiTest extends TestClientConfigurer {

    DatasetRwApi datasetRwApi;
    InputStream stream;

    @BeforeAll
    void configureApi() {
        ApiClient client = getInternalTestClient();
        datasetRwApi = new DatasetRwApiExtended(client);
    }

    @AfterEach
    void closeStream() throws IOException {
        if (stream != null) {
            stream.close();
        }
    }

    String getDefaultRwSnapshotId(final String datasetId) throws ApiException {
        return datasetRwApi.getDataset(datasetId).getReadWriteSnapshotId();
    }

    @Test
    @Order(1)
    void getFileRawSuccess() throws ApiException {
        // Arrange
        String datasetId = TestData.VALID_DATASET_ID_0;
        String snapshotId = getDefaultRwSnapshotId(datasetId);
        String path = "slim-api.json";

        // Act
        // download parameter seems to do nothing - will still get the file if set to false
        stream = datasetRwApi.getFileRaw(snapshotId, path, Boolean.TRUE);

        // Assert
        assertEquals(7855, new BufferedReader(new InputStreamReader(stream)).lines().count());
    }

    @Test
    @Order(2)
    void getFileRawPathIsDirectory() throws ApiException, IOException {
        // Arrange
        String datasetId = TestData.VALID_DATASET_ID_0;
        String snapshotId = getDefaultRwSnapshotId(datasetId);
        String path = "";

        // Act
        stream = datasetRwApi.getFileRaw(snapshotId, path, Boolean.TRUE);

        // Assert
        byte[] result = stream.readAllBytes();
        assertEquals(0, result.length);
    }

    /**
     * Tests using a download task to download an archive of files.
     */
    @Test
    @Order(3)
    void downloadArchiveToLocal() throws ApiException, InterruptedException, IOException, ZipException {
        // Arrange
        String datasetId = TestData.VALID_DATASET_ID_0;
        String snapshotId = getDefaultRwSnapshotId(datasetId);

        DominoDatasetrwWebCreateDownloadArchiveRequest request = new DominoDatasetrwWebCreateDownloadArchiveRequest();
        request.addRelativePathsItem("slim-api.json");

        // Act
        DominoDatasetrwApiDatasetRwFiletaskTaskDto task = datasetRwApi.createDownloadArchive(snapshotId, request);
        while (!task.getTaskStatus().equals("Succeeded")) {
            TimeUnit.SECONDS.sleep(1l);
            task = datasetRwApi.getDownloadTaskStatus(snapshotId, task.getTaskId());            
        }
        stream = datasetRwApi.downloadArchiveToLocal(snapshotId, task.getTaskKey());

        // Assert
        ZipInputStream zip = new ZipInputStream(stream);
        // InputStream is a zip and has contents
        ZipEntry entry = zip.getNextEntry();
        assertNotNull(entry);
        assertEquals("slim-api.json", entry.getName());
    }

    @Test
    void getFileRawSnapshotNotFound() {
        // Arrange
        String snapshotId = TestData.NOT_FOUND_DOMINO_ID;
        String path = "slim-api.json";

        // Act
        ApiException th = assertThrows(ApiException.class, () -> datasetRwApi.getFileRaw(snapshotId, path, Boolean.TRUE));

        // Assert
        assertEquals(404, th.getCode());
    }

    @Test
    void getFileRawSnapshotInvalid() {
        // Arrange
        String snapshotId = TestData.INVALID_DOMINO_ID;
        String path = "slim-api.json";

        // Act
        ApiException th = assertThrows(ApiException.class, () -> datasetRwApi.getFileRaw(snapshotId, path, Boolean.TRUE));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(snapshotId + " is not a valid ID");
    }

    @Test
    void getFileRawPathInvalid() throws ApiException {
        // Arrange
        String datasetId = TestData.VALID_DATASET_ID_0;
        String snapshotId = getDefaultRwSnapshotId(datasetId);
        String path = "path-does-not-exist";

        // Act
        ApiException th = assertThrows(ApiException.class, () -> datasetRwApi.getFileRaw(snapshotId, path, Boolean.TRUE));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains("file " + path + " does not exist");
    }
    
    @Test
    void getDatasetSuccess() throws ApiException {
        // Arrange
        String datasetId = TestData.VALID_DATASET_ID_0;

        // Act
        DominoDatasetrwApiDatasetRwDto dataset = datasetRwApi.getDataset(datasetId);

        // Assert
        assertEquals(datasetId, dataset.getId());
        assertEquals("quick-start", dataset.getName());
        assertEquals(TestData.VALID_PROJECT_ID_0, dataset.getProjectId());
    }
    
    @Test
    void getDatasetNotFound() {
        // Arrange
        String datasetId = TestData.NOT_FOUND_DOMINO_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> datasetRwApi.getDataset(datasetId));

        // Assert
        assertEquals(404, th.getCode());
    }
    
    @Test
    void getDatasetInvalidCode() {
        // Arrange
        String datasetId = TestData.INVALID_DOMINO_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> datasetRwApi.getDataset(datasetId));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(datasetId + " is not a valid ID");
    }
    
    @Test
    void getDatasetsV2SuccessAll() throws ApiException {
        // Arrange

        // Act
        List<DominoDatasetrwApiDatasetRwInfoDto> datasets = datasetRwApi.getDatasetsV2(Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, null, null, null, null);

        // Assert
        assertNotNull(datasets);
        assertTrue(datasets.size() > 0);
    }
    
    @Test
    void getDatasetsV2SuccessProjectFilter() throws ApiException {
        // Arrange
        String projectId = TestData.VALID_PROJECT_ID_0;

        // Act
        List<DominoDatasetrwApiDatasetRwInfoDto> datasets = datasetRwApi.getDatasetsV2(Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, null, null, List.of(projectId), null);

        // Assert
        assertNotNull(datasets);
        assertEquals(1, datasets.size());
        
        DominoDatasetrwApiDatasetRwInfoDto dataset = datasets.get(0);
        assertEquals(projectId, dataset.getDatasetRwDto().getProjectId());
        assertEquals(TestData.VALID_DATASET_ID_0, dataset.getDatasetRwDto().getId());
    }
    
    @Test
    void getDatasetsV2SuccessDatasetFilter() throws ApiException {
        // Arrange
        String datasetId = TestData.VALID_DATASET_ID_0;

        // Act
        List<DominoDatasetrwApiDatasetRwInfoDto> datasets = datasetRwApi.getDatasetsV2(Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, null, null, null, List.of(datasetId));

        // Assert
        assertNotNull(datasets);
        assertEquals(1, datasets.size());
        
        DominoDatasetrwApiDatasetRwInfoDto dataset = datasets.get(0);
        assertEquals(datasetId, dataset.getDatasetRwDto().getId());
    }
    
    @Test
    void getDatasetsV2SuccessProjectNotFound() throws ApiException {
        // Arrange
        String projectId = TestData.NOT_FOUND_DOMINO_ID;

        // Act
        List<DominoDatasetrwApiDatasetRwInfoDto> datasets = datasetRwApi.getDatasetsV2(Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, null, null, List.of(projectId), null);

        // Assert
        assertNotNull(datasets);
        assertEquals(0, datasets.size());
    }
    
    @Test
    void getDatasetsV2SuccessDatasetNotFound() throws ApiException {
        // Arrange
        String datasetId = TestData.NOT_FOUND_DOMINO_ID;

        // Act
        List<DominoDatasetrwApiDatasetRwInfoDto> datasets = datasetRwApi.getDatasetsV2(Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, null, null, null, List.of(datasetId));

        // Assert
        assertNotNull(datasets);
        assertEquals(0, datasets.size());
    }

    @Test
    void getSnapshotSuccess() throws ApiException {
        // Arrange
        String datasetId = TestData.VALID_DATASET_ID_0;
        String snapshotId = getDefaultRwSnapshotId(datasetId);

        // Act
        DominoDatasetrwApiDatasetRwSnapshotSummaryDto summary = datasetRwApi.getSnapshot(snapshotId, Boolean.FALSE);

        // Assert
        DominoDatasetrwApiDatasetRwSnapshotDto snapshot = summary.getSnapshot();
        assertNotNull(snapshot);
        assertEquals(snapshotId, snapshot.getId());
        assertEquals(datasetId, snapshot.getDatasetId());
        assertEquals(0, snapshot.getVersion());
        assertTrue(snapshot.getIsReadWrite());
    }

    @Test
    void getSnapshotNotFound() {
        // Arrange
        String snapshotId = TestData.NOT_FOUND_DOMINO_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> datasetRwApi.getSnapshot(snapshotId, Boolean.FALSE));

        // Assert
        assertEquals(404, th.getCode());
    }

    @Test
    void getSnapshotInvalidCode() {
        // Arrange
        String snapshotId = TestData.INVALID_DOMINO_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> datasetRwApi.getSnapshot(snapshotId, Boolean.FALSE));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(snapshotId + " is not a valid ID");
    }
    
    @Test
    void getFilesInSnapshotSuccess() throws ApiException {
        // Arrange
        String datasetId = TestData.VALID_DATASET_ID_0;
        String snapshotId = getDefaultRwSnapshotId(datasetId);
        String path = "";

        // Act
        DominoDatasetrwApiDatasetRwSnapshotFilesViewDto result = datasetRwApi.getFilesInSnapshot(snapshotId, path);

        // Assert
        List<DominoDatasetrwApiDatasetRwFileDetailsRowDto> files = result.getRows();
        assertNotNull(files);
        assertEquals(1, files.size());
    }
    
    @Test
    void getFilesInSnapshotNotFound() {
        // Arrange
        String snapshotId = TestData.NOT_FOUND_DOMINO_ID;
        String path = "";

        // Act
        ApiException th = assertThrows(ApiException.class, () -> datasetRwApi.getFilesInSnapshot(snapshotId, path));

        // Assert
        assertEquals(404, th.getCode());
    }
    
    @Test
    void getFilesInSnapshotInvalidCode() {
        // Arrange
        String snapshotId = TestData.INVALID_DOMINO_ID;
        String path = "";

        // Act
        ApiException th = assertThrows(ApiException.class, () -> datasetRwApi.getFilesInSnapshot(snapshotId, path));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(snapshotId + " is not a valid ID");
    }
    
    @Test
    void getFilesInSnapshotInvalidPath() throws ApiException {
        // Arrange
        String datasetId = TestData.VALID_DATASET_ID_0;
        String snapshotId = getDefaultRwSnapshotId(datasetId);
        String path = "/";

        // Act
        ApiException th = assertThrows(ApiException.class, () -> datasetRwApi.getFilesInSnapshot(snapshotId, path));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains("Invalid path input");
    }
    
    @Test
    void getSnapshotsSuccess() throws ApiException {
        // Arrange
        String datasetId = TestData.VALID_DATASET_ID_0;

        // Act
        List<DominoDatasetrwApiDatasetRwSnapshotDto> snapshots = datasetRwApi.getSnapshots(datasetId);

        // Assert
        assertNotNull(snapshots);
        assertEquals(1, snapshots.size());

        String snapshotId = getDefaultRwSnapshotId(datasetId);
        DominoDatasetrwApiDatasetRwSnapshotDto snapshot = snapshots.get(0);
        assertEquals(snapshotId, snapshot.getId());
    }
    
    @Test
    void getSnapshotsNotFound() {
        // Arrange
        String datasetId = TestData.NOT_FOUND_DOMINO_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> datasetRwApi.getSnapshots(datasetId));

        // Assert
        assertEquals(404, th.getCode());
    }
    
    @Test
    void getSnapshotsInvalidCode() {
        // Arrange
        String datasetId = TestData.INVALID_DOMINO_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> datasetRwApi.getSnapshots(datasetId));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(datasetId + " is not a valid ID");
    }
    
    @Test
    @Tag("Stateful")
    void projectSharedDatasets() throws ApiException {
        // Assert intial state - project shared datasets does not include test dataset
        String projectId = TestData.VALID_PROJECT_ID_0;
        String sharedDatasetId = TestData.VALID_DATASET_ID_1;
        List<DominoDatasetrwApiDatasetRwProjectMountDto> mounts0 = datasetRwApi.getSharedDatasetProjectMountsV2(projectId, null, null);

        // Pre-condition: Assert test dataset is not mounted as shared dataset
        assertTrue(mounts0.stream().noneMatch(mount -> mount.getDatasetId().equals(sharedDatasetId)), "Pre-condition failed: project shared dataset mounts includes test dataset");

        datasetRwApi.addSharedDatasetRwEntry(projectId, sharedDatasetId);

        List<DominoDatasetrwApiDatasetRwProjectMountDto> mounts1 = datasetRwApi.getSharedDatasetProjectMountsV2(projectId, null, null);

        // Assert shared dataset mount is now present
        assertEquals(mounts0.size() + 1, mounts1.size());
        assertTrue(mounts1.stream().anyMatch(mount -> mount.getDatasetId().equals(sharedDatasetId)));

        // Remote shared dataset
        datasetRwApi.removeSharedDatasetRwEntry(projectId, sharedDatasetId);

        List<DominoDatasetrwApiDatasetRwProjectMountDto> mounts2 = datasetRwApi.getSharedDatasetProjectMountsV2(projectId, null, null);

        assertEquals(mounts0.size(), mounts2.size());
        assertTrue(mounts2.stream().noneMatch(mount -> mount.getDatasetId().equals(sharedDatasetId)));
    }
    
    /**
     * Test granting and removing a user from a dataset. The only way to add
     * or remove users is with a static grant list (PUT, so it's idempotent),
     * so we have to add/remove the user to/from that list to update it.
     */
    @Test
    @Tag("Stateful")
    void datasetGrants() throws ApiException {
        // Assert initial state - dataset grants do not include test data
        String datasetId = TestData.VALID_DATASET_ID_0;
        String testGrantId = TestData.VALID_PROJECT_COLLABORATOR_ID;
        TargetRoleEnum targetRole = TargetRoleEnum.DATASET_RW_EDITOR;

        List<DominoDatasetrwApiDatasetRwGrantDetails> grants0 = datasetRwApi.getDatasetGrants(datasetId);

        // Pre-condition: Assert test user is not in list of grants
        assertTrue(grants0.stream().noneMatch(grant -> grant.getTargetId().equals(testGrantId)), "Pre-condition failed: dataset includes test user as granted user");

        // Add test user to dataset grants
        DominoDatasetrwWebUpdateDatasetGrantsRequest request0 = new DominoDatasetrwWebUpdateDatasetGrantsRequest();
        for (DominoDatasetrwApiDatasetRwGrantDetails detail : grants0) {
            DominoDatasetrwApiDatasetRwGrant grant = new DominoDatasetrwApiDatasetRwGrant();
            grant.setTargetId(detail.getTargetId());
            grant.setTargetRole(TargetRoleEnum.fromValue(detail.getTargetRole().getValue()));
            request0.addGrantsItem(grant);
        }
        DominoDatasetrwApiDatasetRwGrant newGrant = new DominoDatasetrwApiDatasetRwGrant();
        newGrant.setTargetId(testGrantId);
        newGrant.setTargetRole(targetRole);
        request0.addGrantsItem(newGrant);

        datasetRwApi.updateDatasetGrants(datasetId, request0);

        List<DominoDatasetrwApiDatasetRwGrantDetails> grants1 = datasetRwApi.getDatasetGrants(datasetId);

        // Assert test user is now in list of grants and role matches
        assertEquals(grants0.size() + 1, grants1.size());
        DominoDatasetrwApiDatasetRwGrantDetails assignedUser = grants1.stream().filter(grant -> grant.getTargetId().equals(testGrantId)).findFirst().get();
        assertNotNull(assignedUser);
        assertEquals(targetRole.getValue(), assignedUser.getTargetRole().getValue());

        // Remove test user
        DominoDatasetrwWebUpdateDatasetGrantsRequest request1 = new DominoDatasetrwWebUpdateDatasetGrantsRequest();
        for (DominoDatasetrwApiDatasetRwGrant grant : request0.getGrants()) {
            if (!grant.getTargetId().equals(testGrantId)) {
                request1.addGrantsItem(grant);
            }
        }

        datasetRwApi.updateDatasetGrants(datasetId, request1);

        List<DominoDatasetrwApiDatasetRwGrantDetails> grants2 = datasetRwApi.getDatasetGrants(datasetId);

        // Assert test user is not in list of grants
        assertEquals(grants0.size(), grants2.size());
        assertTrue(grants0.stream().noneMatch(grant -> grant.getTargetId().equals(testGrantId)), "Pre-condition failed: dataset includes test user as granted user");
    }

}
