package com.dominodatalab.api.model;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public abstract class GitCredentialDto {
    
    /** "token" | "password" | "key" set on the subclass credential */
    private String accessType;

    /** ?? This is null on all type requests but is still defined on request object in UI */
    private String refdetails;

    /** This is the DTO name of the subclass credential */
    private String type;

    /**
     * Enum type name of the git service provider
     * 
     * @see com.dominodatalab.api.model.DominoServerAccountApiGitCredentialAccessorDto.GitServiceProviderEnum
     */
    private String gitServiceProvider;

    /** The domain of the service provider. Can be left blank for known service providers like github or bitbucket (non-enterprise). */
    private String domain;

    /** Name of the git credential as is visible for the user (in Settings and on Code pages for projects) */
    private String name;

}
