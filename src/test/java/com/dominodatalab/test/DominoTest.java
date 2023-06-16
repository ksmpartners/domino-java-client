package com.dominodatalab.test;

import com.dominodatalab.api.invoker.ApiClient;
import com.dominodatalab.api.invoker.ApiException;
import com.dominodatalab.api.model.DominoCommonUserPerson;
import com.dominodatalab.api.rest.UsersApi;
import com.dominodatalab.client.DominoApiClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DominoTest {

    private static final String API_URL = "https://domino.cloud.com/v4";
    private static final String API_KEY = "74b86eca8c3e8310748de7714926b0b6a3866d8eb8c493fdf2d8fed7a520b842";

    public static void main(String... args) {
        try {
            // initialize client with key
            final ApiClient client = DominoApiClient.createApiClient();
            client.updateBaseUri(API_URL);
            client.setRequestInterceptor(builder -> builder.setHeader("X-Domino-Api-Key", API_KEY));

            // call the get current user API
            UsersApi api = new UsersApi(client);
            DominoCommonUserPerson user = api.getCurrentUser();

            // print response as JSON
            ObjectMapper mapper = DominoApiClient.createDefaultObjectMapper();
            String result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);
            System.out.println(result);
        } catch (ApiException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
