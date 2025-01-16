package com.dominodatalab.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class SshGitCredentialDto extends GitCredentialDto {
    
    private final String accessType = "key";

    private final String type = "SshGitCredentialDto";

    private String key;

    private String passphrase;

}
