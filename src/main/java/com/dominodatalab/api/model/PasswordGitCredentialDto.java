package com.dominodatalab.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PasswordGitCredentialDto extends GitCredentialDto {

    final String accessType = "password";

    final String type = "PasswordGitCredentialDto";
    
    private String username;

    private String password;

}
