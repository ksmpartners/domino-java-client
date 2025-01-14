package com.dominodatalab.api.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.dominodatalab.TestClientConfigurer;
import com.dominodatalab.api.invoker.ApiClient;
import com.dominodatalab.api.invoker.ApiException;
import com.dominodatalab.api.model.DominoCommonUserPerson;

public class UsersApiTest extends TestClientConfigurer {

    UsersApi usersApi;

    @BeforeAll
    void configureApi() {
        ApiClient client = getInternalTestClient();
        usersApi = new UsersApi(client);
    }

    @Test
    void getCurrentUser_success() throws ApiException {
        // Arrange

        // Act
        DominoCommonUserPerson user = usersApi.getCurrentUser();

        // Assert
        assertNotNull(user.getId());
        assertNotNull(user.getUserName());
    }

    @Test
    void listUsers_userId_success() throws ApiException {
        // Arrange
        DominoCommonUserPerson user = usersApi.getCurrentUser();

        // Act
        List<DominoCommonUserPerson> users = usersApi.listUsers(List.of(user.getId()), null, null, null);

        // Assert
        assertEquals(1, users.size());
        assertEquals(user.getId(), users.get(0).getId());
    }

    @Test
    void listUsers_userName_success() throws ApiException {
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
    
}
