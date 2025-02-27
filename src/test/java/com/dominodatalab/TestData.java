package com.dominodatalab;

import java.util.Map;

public class TestData {

    /** quick-start project - not git-based */
    public static String VALID_PROJECT_ID_0 = "66313c4cdb6cb00661f1a27b";

    /** TestIntgr project - git-based */
    public static String VALID_PROJECT_ID_1 = "678981aa9d9d9d5fffc466e1";

    /** Domino IDs must conform to "^[0-9a-f]{24}$" to pass input validation */
    public static String NOT_FOUND_DOMINO_ID = "000000000000000000000000";

    /** Domino IDs must conform to "^[0-9a-f]{24}$" to pass input validation */
    public static String INVALID_DOMINO_ID = "invalid";

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
    
    /** Existing goal under quick-start project */
    public static String VALID_PROJECT_GOAL_ID = "678fea2218fb6338893705c1";

    /** Name for test credential on user profile */
    public static String USER_TEST_CREDENTIAL_NAME = "Integration Test Cred";

    /** Default dataset under quick-start project */
    public static String VALID_DATASET_ID_0 = "66313c52db6cb00661f1a27f";

    /** Default dataset under TestIntgr project - can be mounted as shared dataset */
    public static String VALID_DATASET_ID_1 = "678981ae9d9d9d5fffc466e7";

}
