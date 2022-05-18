package com.opsera.service.gitgateway.service.impl;

import static com.opsera.service.gitgateway.resources.Constants.CREATE_PULL_REQUEST;
import static com.opsera.service.gitgateway.resources.Constants.CREATE_TAG_REQUEST;
import com.opsera.core.rest.RestTemplateHelper;
import com.opsera.service.gitgateway.resources.GitGatewayRequest;
import com.opsera.service.gitgateway.resources.GitGatewayResponse;
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
public class GitLabActionsServiceImpl implements IGitActionsService {
    @Autowired
    private GitHelper gitHelper;
    @Autowired
    private RestTemplateHelper restTemplateHelper;
    public GitGatewayResponse createPullRequest(GitGatewayRequest request) {
        log.info("Request to create Pull request : {} ", request);
        String readURL = gitHelper.getURL(request.getService()) + CREATE_PULL_REQUEST;
        try {
            restTemplateHelper.postForEntity(String.class, readURL, request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        GitGatewayResponse gitGatewayResponse=new GitGatewayResponse();
        gitGatewayResponse.setStatus("SUCCESS");
        gitGatewayResponse.setMessage("Pull request successfully created");
        return gitGatewayResponse;
    }

    @Override
    public GitGatewayResponse createTag(GitGatewayRequest request) {
        log.info("Request to create tag request : {} ", request);
        String readURL = gitHelper.getURL(request.getService()) + CREATE_TAG_REQUEST;
        try {
            restTemplateHelper.postForEntity(String.class, readURL, request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        GitGatewayResponse gitGatewayResponse=new GitGatewayResponse();
        gitGatewayResponse.setStatus("SUCCESS");
        gitGatewayResponse.setMessage("tag request successfully created");
        return gitGatewayResponse;
    }
}
