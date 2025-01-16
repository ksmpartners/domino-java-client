package com.dominodatalab.api.model;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public abstract class GitCredentialDto {
    
    private String accessType;

    private String refdetails;

    private String type;
    
    private String gitServiceProvider;

    private String domain;

    private String name;
    
}
