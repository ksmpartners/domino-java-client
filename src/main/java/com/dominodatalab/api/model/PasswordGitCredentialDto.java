package com.dominodatalab.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PasswordGitCredentialDto extends GitCredentialDto {

    private final String accessType = "password";

    private final String type = "PasswordGitCredentialDto";
    
    private String username;

    private String password;

}
