package com.dominodatalab.api.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.dominodatalab.TestClientConfigurer;
import com.dominodatalab.TestData;
import com.dominodatalab.api.invoker.ApiClient;
import com.dominodatalab.api.invoker.ApiException;
import com.dominodatalab.api.model.DominoServerAccountApiGitCredentialAccessorDto;
import com.dominodatalab.api.model.TokenGitCredentialDto;
import com.dominodatalab.api.model.DominoServerAccountApiGitCredentialAccessorDto.GitServiceProviderEnum;

class GitCredentialsApiTest extends TestClientConfigurer {
    
    GitCredentialsApiExtended gitCredentialsApi;
    UsersApi usersApi;

    @BeforeAll
    void configureApi() {
        ApiClient client = getInternalTestClient();
        // This class isn't technically necessary, but provides a method that
        // has the proper expected type parameter for the request body of
        // addGitCredential operation
        gitCredentialsApi = new GitCredentialsApiExtended(client);
        usersApi = new UsersApi(client);
    }

    String getUserId() throws ApiException {
        return usersApi.getCurrentUser().getId();
    }

    @Test
    void getGitCredentialsSuccess() throws ApiException {
        // Arrange
        String userId = getUserId();

        // Act
        List<DominoServerAccountApiGitCredentialAccessorDto> creds = gitCredentialsApi.getGitCredentials(userId);

        // Assert
        assertNotNull(creds);
    }

    @Test
    void getGitCredentialsNotFound() {
        // Arrange
        String userId = TestData.NOT_FOUND_PROJECT_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> gitCredentialsApi.getGitCredentials(userId));

        // Assert
        assertEquals(404, th.getCode());
    }

    @Test
    void getGitCredentialsInvalid() {
        // Arrange
        String userId = TestData.INVALID_PROJECT_ID;

        // Act
        ApiException th = assertThrows(ApiException.class, () -> gitCredentialsApi.getGitCredentials(userId));

        // Assert
        assertEquals(400, th.getCode());
        assert(th.getMessage()).contains(userId + " is not a valid ID");
    }
    
    @Test
    @Tag("Stateful")
    void gitCredentials() throws ApiException {
        // Assert initial state - user git credentials do not include test credential
        String userId = getUserId();
        List<DominoServerAccountApiGitCredentialAccessorDto> creds0 = gitCredentialsApi.getGitCredentials(userId);
        
        // Pre-condition: Assert test credential is not set
        assertTrue(creds0.stream().noneMatch(cred -> cred.getName().equals(TestData.USER_TEST_CREDENTIAL_NAME)), "Pre-condition failed: user configuration includes test git credential");

        TokenGitCredentialDto addCred = TokenGitCredentialDto.builder()
                .name(TestData.USER_TEST_CREDENTIAL_NAME)
                .gitServiceProvider(GitServiceProviderEnum.UNKNOWN.getValue())
                .domain("https://example.com")
                .token("foobar")
                .build();

        gitCredentialsApi.addGitCredential(userId, addCred);

        List<DominoServerAccountApiGitCredentialAccessorDto> creds1 = gitCredentialsApi.getGitCredentials(userId);

        // Assert new test credential is now present
        assertEquals(creds0.size() + 1, creds1.size());
        assertTrue(creds1.stream().anyMatch(cred -> cred.getName().equals(TestData.USER_TEST_CREDENTIAL_NAME)));
        DominoServerAccountApiGitCredentialAccessorDto credential = creds1.stream().filter(cred -> cred.getName().equals(TestData.USER_TEST_CREDENTIAL_NAME)).findFirst().get();

        // Remove test credential
        gitCredentialsApi.deleteGitCredential(userId, credential.getId());

        List<DominoServerAccountApiGitCredentialAccessorDto> creds2 = gitCredentialsApi.getGitCredentials(userId);

        assertEquals(creds0.size(), creds2.size());
        assertTrue(creds2.stream().noneMatch(cred -> cred.getName().equals(TestData.USER_TEST_CREDENTIAL_NAME)));
    }

}
