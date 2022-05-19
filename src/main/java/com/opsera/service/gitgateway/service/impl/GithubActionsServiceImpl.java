package com.opsera.service.gitgateway.service.impl;

import static com.opsera.service.gitgateway.resources.Constants.CREATE_PULL_REQUEST;
import static com.opsera.service.gitgateway.resources.Constants.CREATE_TAG_REQUEST;
import static com.opsera.service.gitgateway.resources.Constants.FAILED;
import static com.opsera.service.gitgateway.resources.Constants.IN_PROGRESS;
import static com.opsera.service.gitgateway.resources.Constants.SUCCESS;
import com.opsera.core.exception.ServiceException;
import com.opsera.core.rest.RestTemplateHelper;
import com.opsera.service.gitgateway.resources.Configuration;
import com.opsera.service.gitgateway.resources.GitGatewayRequest;
import com.opsera.service.gitgateway.resources.GitGatewayResponse;
import com.opsera.service.gitgateway.resources.GitIntegratorRequest;
import com.opsera.service.gitgateway.resources.GitIntegratorResponse;
import com.opsera.service.gitgateway.service.ConfigCollector;
import com.opsera.service.gitgateway.service.GitHelper;
import com.opsera.service.gitgateway.service.IGitActionsService;
import com.opsera.service.gitgateway.util.GitGatewayUtil;
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
    @Autowired
    private GitGatewayUtil gitGatewayUtil;

    public GitGatewayResponse createPullRequest(GitGatewayRequest request) {
        log.info("Request to create Pull request : {} ", request);
        GitGatewayResponse gitGatewayResponse = new GitGatewayResponse();
        gitGatewayResponse.setCustomerId(request.getCustomerId());
        gitGatewayResponse.setPipelineId(request.getPipelineId());
        gitGatewayResponse.setRunCount(request.getRunCount());
        gitGatewayResponse.setStatus(IN_PROGRESS);
        gitGatewayResponse.setStatus("Create pull request in Progress");
        gitGatewayUtil.writeToResponseTopic(gitGatewayResponse);
        try {
            Configuration config = configCollector.getToolConfigurationDetails(request);
            String readURL = gitHelper.getURL(request.getService()) + CREATE_PULL_REQUEST;
            GitIntegratorRequest gitIntegratorRequest = gitHelper.createRequestData(request,config);
            GitIntegratorResponse gitResponse = gitHelper.processGitAction(readURL, gitIntegratorRequest);
            gitGatewayResponse.setStatus(SUCCESS);
            gitGatewayResponse.setMessage("pull request successfully created : "+gitResponse.getPullRequestLink());
            gitGatewayResponse.setPullRequestLink(gitResponse.getPullRequestLink());
            gitGatewayUtil.writeToResponseTopic(gitGatewayResponse);
        } catch (Exception e) {
            gitGatewayResponse.setStatus(FAILED);
            gitGatewayResponse.setMessage("Pull request failed");
            log.error("Pull request failed",e);
            gitGatewayUtil.writeToLogTopic(gitGatewayResponse);
            gitGatewayUtil.writeToResponseTopic(gitGatewayResponse);
            String errorMsg = new StringBuilder("Error while creating pull  ").append(" Error : ").append(e.getMessage()).toString();
            throw new ServiceException(errorMsg);
        }
        return gitGatewayResponse;
    }

    @Override
    public GitGatewayResponse createTag(GitGatewayRequest request) {
        log.info("Request to create tag request : {} ", request);
        GitGatewayResponse gitGatewayResponse = new GitGatewayResponse();
        gitGatewayResponse.setCustomerId(request.getCustomerId());
        gitGatewayResponse.setPipelineId(request.getPipelineId());
        gitGatewayResponse.setRunCount(request.getRunCount());
        gitGatewayResponse.setStatus(IN_PROGRESS);
        gitGatewayResponse.setStatus("Create Tag request in Progress");
        gitGatewayUtil.writeToResponseTopic(gitGatewayResponse);
        try {
            Configuration config = configCollector.getToolConfigurationDetails(request);
            String readURL = gitHelper.getURL(request.getService()) + CREATE_TAG_REQUEST;
            GitIntegratorRequest gitIntegratorRequest = gitHelper.createRequestData(request,config);
            String tagName = gitHelper.getTagName(config, request.getRunCount().toString());
            gitIntegratorRequest.setTagName(tagName);
            GitIntegratorResponse gitResponse = gitHelper.processGitAction(readURL, gitIntegratorRequest);
            gitGatewayResponse.setStatus(SUCCESS);
            gitGatewayResponse.setMessage("Tag request successfully created with tag name : "+tagName);
            gitGatewayResponse.setTagName(tagName);
            gitGatewayUtil.writeToResponseTopic(gitGatewayResponse);
        } catch (IOException e) {
            gitGatewayResponse.setStatus(FAILED);
            gitGatewayResponse.setMessage("tag creation request failed");
            log.error("Pull creating request failed",e);
            gitGatewayUtil.writeToLogTopic(gitGatewayResponse);
            gitGatewayUtil.writeToResponseTopic(gitGatewayResponse);
            String errorMsg = new StringBuilder("Error while creating tag  ").append(" Error : ").append(e.getMessage()).toString();
            throw new ServiceException(errorMsg);

        }
        return gitGatewayResponse;
    }
}
