package com.dominodatalab;

import java.util.Map;

public class TestData {

    /** quick-start project - not git-based */
    public static String VALID_PROJECT_ID_0 = "66313c4cdb6cb00661f1a27b";

    /** Project ID must conform to "^[0-9a-f]{24}$" to pass input validation */
    public static String NOT_FOUND_PROJECT_ID = "000000000000000000000000";

    /** Project ID must conform to "^[0-9a-f]{24}$" to pass input validation */
    public static String INVALID_PROJECT_ID = "invalid";

    /** Test environment variables for users or projects */
    public static Map<String, String> ENVIRONMENT_VARIABLES = Map.of(
        "INTEGRATION_0_VAL", "First test value for integration tests",
        "INTEGRATION_1_VAL", "Second test value for integration tests"
    );

    /** Secondary test project environment variable name */
    public static String TEST_ENVIRONMENT_VARIABLE = "ISQUICKSTART";

    /** Test collaborator for project access */
    public static String VALID_PROJECT_COLLABORATOR_ID = "66a90dde501a20571eac34ca";

    /** Valid existing git repository for valid project */
    public static String VALID_PROJECT_GIT_REPO_ID = "6787f5445ba26145e20f397b";

    /** Test imported git repository for valid project */
    public static String VALID_PROJECT_TEST_REPO = "https://github.com/ksmpartners/domino-java-client.git";

    /** Existing job started under quick-start project */
    public static String VALID_PROJECT_JOB_ID = "6789227e9d9d9d5fffc4657d";

    /** Name for test credential on user profile */
    public static String USER_TEST_CREDENTIAL_NAME = "Integration Test Cred";

}
