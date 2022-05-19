package com.opsera.service.gitgateway.service.impl;

import com.opsera.service.gitgateway.resources.GitGatewayRequest;
import com.opsera.service.gitgateway.resources.GitGatewayResponse;
import com.opsera.service.gitgateway.service.GitHelper;
import com.opsera.service.gitgateway.service.IGitActionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class BitBucketActionsServiceImpl implements IGitActionsService {
    @Autowired
    private GitHelper gitHelper;

    public GitGatewayResponse createPullRequest(GitGatewayRequest request) {
        log.info("Request to create Pull request : {} ", request);
        GitGatewayResponse gitGatewayResponse = gitHelper.getGitGatewayResponseForPull(request);
        log.info("Successfully created Pull request ");
        return gitGatewayResponse;
    }


    @Override
    public GitGatewayResponse createTag(GitGatewayRequest request) {
        log.info("Request to create tag request : {} ", request);
        GitGatewayResponse gitGatewayResponse = gitHelper.getGitGatewayResponseForTag(request);
        log.info("Successfully created tag");
        return gitGatewayResponse;
    }
}
