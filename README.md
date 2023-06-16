<div align="center">
    <div style="flex-grow: 1; width: 50vw"> 
<a href="https://www.dominodatalab.com/" alt="Domino Data Lab">
   <img class="spinner" loading="lazy" height="80" width="116" src="https://www.dominodatalab.com/hubfs/NBM/domino-logo-spinner.webp" alt="Domino Data Logo - Graphic part">
   <img loading="lazy" height="80" src="https://www.dominodatalab.com/hubfs/NBM/domino-logo-text.webp" alt="Domino Data Logo - Text part">
</a>
    </div>
 
# Domino Data Lab Java Client
</div>
<br>

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![CI](https://github.com/ksmpartners/domino-java-client/actions/workflows/build.yml/badge.svg)](https://github.com/ksmpartners/domino-java-client/actions/workflows/build.yml)

Domino Data Lab Java Client is a library that wrappers the REST API calls for Domino using a Java native HTTP client.

# Requirements

To use this library you will need the following:

- JDK 11+
- Domino API Key to an active Domino instance

# Build and Test

To build the project requires Apache Maven:

```shell
$ mvn clean package
```

# Usage

Using the library is simple, you just need a Domino API Key and URL.

```java
import com.dominodatalab.api.invoker.ApiClient;
import com.dominodatalab.api.invoker.ApiException;
import com.dominodatalab.api.model.DominoCommonUserPerson;
import com.dominodatalab.api.rest.UsersApi;
import com.dominodatalab.client.DominoApiClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DominoTest {

    private static final String API_URL = "https://domino.cloud.com/v4";
    private static final String API_KEY = "74b86eaa8c3e8310748de7714926b0b6a3866d8eb8c493fdf2d8fed7a520b842";

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
```

Result output:

```json
{
  "firstName": "Homer",
  "lastName": "Simpson",
  "fullName": "Homer Simpson",
  "id": "6124ffbfa7db86282dde302a",
  "userName": "hs12345",
  "email": "homer.simpson@springfield.org"
}
```

## Releasing

- Run `mvn versions:set -DgenerateBackupPoms=false -DnewVersion=5.5.1` to update all modules versions
- Commit and push the changes to GitHub
- In GitHub create a new Release titled `5.5.1` to tag this release
- Run `mvn clean deploy -Prelease` to push to Maven Central

### License

***
Licensed under the [MIT](https://en.wikipedia.org/wiki/MIT_License) license.

`SPDX-License-Identifier: MIT`

### Copyright

***
Domino and Domino Data Lab are © 2023 Domino Data Lab, Inc. Made in San Francisco. 
