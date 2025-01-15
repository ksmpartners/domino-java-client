package com.dominodatalab.api.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
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
import com.dominodatalab.api.model.DominoCommonModelsEnvironmentVariables;
import com.dominodatalab.api.model.DominoCommonUserPerson;

class UsersApiTest extends TestClientConfigurer {

    UsersApi usersApi;

    @BeforeAll
    void configureApi() {
        ApiClient client = getInternalTestClient();
        usersApi = new UsersApi(client);
    }

    @Test
    void getCurrentUserSuccess() throws ApiException {
        // Arrange

        // Act
        DominoCommonUserPerson user = usersApi.getCurrentUser();

        // Assert
        assertNotNull(user.getId());
        assertNotNull(user.getUserName());
    }

    @Test
    void listUsersUserIdSuccess() throws ApiException {
        // Arrange
        DominoCommonUserPerson user = usersApi.getCurrentUser();

        // Act
        List<DominoCommonUserPerson> users = usersApi.listUsers(List.of(user.getId()), null, null, null);

        // Assert
        assertEquals(1, users.size());
        assertEquals(user.getId(), users.get(0).getId());
    }

    @Test
    void listUsersUserNameSuccess() throws ApiException {
        // Arrange
        DominoCommonUserPerson user = usersApi.getCurrentUser();

        // Act
        List<DominoCommonUserPerson> users = usersApi.listUsers(null, user.getUserName(), null, null);

        // Assert
        assertEquals(1, users.size());
        DominoCommonUserPerson result = users.get(0);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getUserName(), result.getUserName());
    }
    
    @Test
    void listUserEnvironmentVariablesSuccess() throws ApiException {
        // Arrange

        // Act
        DominoCommonModelsEnvironmentVariables currentVars = usersApi.listUserEnvironmentVariables();

        // Assert
        List<DominoCommonModelsEnvironmentVariable> envVars = currentVars.getVars();

        assertNotNull(envVars);
    }

    @Test
    @Tag("Stateful")
    void userEnvironmentVariables() throws ApiException {
        // Assert initial state - user environment variables do not include test data
        DominoCommonModelsEnvironmentVariables currentVars0 = usersApi.listUserEnvironmentVariables();
        List<DominoCommonModelsEnvironmentVariable> envVars0 = currentVars0.getVars();

        // Pre-condition: Assert test variables are not set
        assertTrue(envVars0.isEmpty() || envVars0.stream().noneMatch(var -> TestData.ENVIRONMENT_VARIABLES.keySet().contains(var.getName())), "Pre-condition failed: User configured environment variables includes test data");

        // At the time of writing, setUserEnvironmentVariables only *overwrites*
        // existing vars, so any pre-existing ones are still kept
        usersApi.setUserEnvironmentVariables(TestData.ENVIRONMENT_VARIABLES);

        DominoCommonModelsEnvironmentVariables currentVars1 = usersApi.listUserEnvironmentVariables();
        List<DominoCommonModelsEnvironmentVariable> envVars1 = currentVars1.getVars();

        // Assert test variables are now set
        assertEquals(TestData.ENVIRONMENT_VARIABLES.size() + envVars0.size(), envVars1.size());
        for(Entry<String, String> testvar : TestData.ENVIRONMENT_VARIABLES.entrySet()) {
            assertTrue(envVars1.stream().anyMatch(var -> var.getName().equals(testvar.getKey()) && var.getValue().equals(testvar.getValue())));
        }

        // Remove test variables
        usersApi.deleteUserEnvironmentVariables();

        DominoCommonModelsEnvironmentVariables currentVars2 = usersApi.listUserEnvironmentVariables();
        List<DominoCommonModelsEnvironmentVariable> envVars2 = currentVars2.getVars();

        assertTrue(envVars2.isEmpty());

        // At the time of writing, there is no option to delete select user
        // environment variables, so we reset any pre-existing user environment
        // variables if they exist
        if (envVars0.size() > 0) {
            HashMap<String, String> resetVars = new HashMap<>();
            for (DominoCommonModelsEnvironmentVariable var : envVars0) {
                resetVars.put(var.getName(), var.getValue());
            }
            usersApi.setUserEnvironmentVariables(resetVars);
        }
    }
    
}
