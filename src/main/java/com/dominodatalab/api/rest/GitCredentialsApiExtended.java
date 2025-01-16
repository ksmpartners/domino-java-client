package com.dominodatalab.api.rest;

import com.dominodatalab.api.invoker.ApiClient;
import com.dominodatalab.api.invoker.ApiException;
import com.dominodatalab.api.model.DominoServerAccountApiGitCredentialAccessorDto;
import com.dominodatalab.api.model.GitCredentialDto;

public class GitCredentialsApiExtended extends GitCredentialsApi {

    public GitCredentialsApiExtended(){
        super();
    }

    public GitCredentialsApiExtended(ApiClient client) {
        super(client);
    }

    public <T extends GitCredentialDto> DominoServerAccountApiGitCredentialAccessorDto addGitCredential(String userId, T body) throws ApiException {
        return addGitCredential(userId, (Object) body);
    }
}
