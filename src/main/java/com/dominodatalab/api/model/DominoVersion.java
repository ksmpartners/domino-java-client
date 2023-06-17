package com.dominodatalab.api.model;

import java.util.Date;

/**
 * Model object for the undocumented Version REST API to get the current Domino Version
 */
@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class DominoVersion {

    private String buildId;

    private String buildUrl;

    private String commitId;

    private String commitUrl;

    private Date timestamp;

    private String version;

}