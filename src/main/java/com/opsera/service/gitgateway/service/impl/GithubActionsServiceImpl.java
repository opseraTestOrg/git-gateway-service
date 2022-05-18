package com.opsera.service.gitgateway.service.impl;

import static com.opsera.service.gitgateway.resources.Constants.CREATE_PULL_REQUEST;
import static com.opsera.service.gitgateway.resources.Constants.CREATE_TAG_REQUEST;
import com.opsera.core.rest.RestTemplateHelper;
import com.opsera.service.gitgateway.resources.Configuration;
import com.opsera.service.gitgateway.resources.GitGatewayRequest;
import com.opsera.service.gitgateway.resources.GitGatewayResponse;
import com.opsera.service.gitgateway.resources.GitIntegratorRequest;
import com.opsera.service.gitgateway.resources.GitIntegratorResponse;
import com.opsera.service.gitgateway.service.ConfigCollector;
import com.opsera.service.gitgateway.service.GitHelper;
import com.opsera.service.gitgateway.service.IGitActionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class GithubActionsServiceImpl implements IGitActionsService {
    @Autowired
    private GitHelper gitHelper;
    @Autowired
    private RestTemplateHelper restTemplateHelper;
    @Autowired
    private ConfigCollector configCollector;

    public GitGatewayResponse createPullRequest(GitGatewayRequest request) {
        log.info("Request to create Pull request : {} ", request);
        GitGatewayResponse gitGatewayResponse = new GitGatewayResponse();
        try {
            Configuration config = configCollector.getToolConfigurationDetails(request);
            String readURL = gitHelper.getURL(request.getService()) + CREATE_PULL_REQUEST;
            GitIntegratorRequest gitIntegratorRequest = gitHelper.createRequestData(request,config);
            GitIntegratorResponse gitResponse = gitHelper.processGitAction(readURL, gitIntegratorRequest);
            gitGatewayResponse.setStatus("SUCCESS");
            gitGatewayResponse.setMessage("Pull request successfully created");
        } catch (IOException e) {
            gitGatewayResponse.setStatus("Failed");
            gitGatewayResponse.setMessage("Pull request failed");
            log.error("Pull request failed",e);
        }
        return gitGatewayResponse;
    }

    @Override
    public GitGatewayResponse createTag(GitGatewayRequest request) {
        log.info("Request to create tag request : {} ", request);
        GitGatewayResponse gitGatewayResponse = new GitGatewayResponse();
        try {
            Configuration config = configCollector.getToolConfigurationDetails(request);
            String readURL = gitHelper.getURL(request.getService()) + CREATE_TAG_REQUEST;
            GitIntegratorRequest gitIntegratorRequest = gitHelper.createRequestData(request,config);
            gitIntegratorRequest.setTagName("gateway-tag");//to-do
            GitIntegratorResponse gitResponse = gitHelper.processGitAction(readURL, gitIntegratorRequest);
            gitGatewayResponse.setStatus("SUCCESS");
            gitGatewayResponse.setMessage("Tag request successfully created");
        } catch (IOException e) {
            gitGatewayResponse.setStatus("Failed");
            gitGatewayResponse.setMessage("Tag request failed");
            log.error("Tag request failed",e);

        }
        return gitGatewayResponse;
    }
}
