package com.dominodatalab.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class TokenGitCredentialDto extends GitCredentialDto {
    
    private final String accessType = "token";

    private final String type = "TokenGitCredentialDto";

    private String token;

}
