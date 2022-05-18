package com.opsera.service.gitgateway.service;

import com.opsera.service.gitgateway.resources.GitGatewayRequest;
import com.opsera.service.gitgateway.resources.GitGatewayResponse;

public interface IGitActionsService {
    GitGatewayResponse createPullRequest(GitGatewayRequest request);
    GitGatewayResponse createTag(GitGatewayRequest request);
}
