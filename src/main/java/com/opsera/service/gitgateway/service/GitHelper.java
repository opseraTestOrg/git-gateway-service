package com.opsera.service.gitgateway.service;


import static com.opsera.service.gitgateway.resources.Constants.BITBUCKET;
import static com.opsera.service.gitgateway.resources.Constants.CREATE_PULL_REQUEST;
import static com.opsera.service.gitgateway.resources.Constants.CREATE_TAG_REQUEST;
import static com.opsera.service.gitgateway.resources.Constants.DATE;
import static com.opsera.service.gitgateway.resources.Constants.FAILED;
import static com.opsera.service.gitgateway.resources.Constants.GITHUB;
import static com.opsera.service.gitgateway.resources.Constants.GITLAB;
import static com.opsera.service.gitgateway.resources.Constants.RUN_COUNT;
import static com.opsera.service.gitgateway.resources.Constants.SUCCESS;
import static com.opsera.service.gitgateway.resources.Constants.TIMESTAMP;
import static com.opsera.service.gitgateway.resources.Constants.YYYY_MM_DD;
import static com.opsera.service.gitgateway.resources.Constants.YYYY_MM_DD_HH_MM_SS;

import com.opsera.core.exception.ServiceException;
import com.opsera.core.rest.RestTemplateHelper;
import com.opsera.service.gitgateway.config.AppConfig;
import com.opsera.service.gitgateway.resources.Configuration;
import com.opsera.service.gitgateway.resources.GitGatewayRequest;

import com.opsera.service.gitgateway.resources.GitGatewayResponse;
import com.opsera.service.gitgateway.resources.GitIntegratorRequest;
import com.opsera.service.gitgateway.resources.GitIntegratorResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


/**
 * Helper class to facilitate all communications with Git Lab Service
 */
@Service
@Slf4j
public class GitHelper {

    private final DateTimeFormatter formatterwithDate = DateTimeFormatter.ofPattern(YYYY_MM_DD);

    private final DateTimeFormatter formatterWithTimestamp = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
    @Autowired
    private RestTemplateHelper restTemplateHelper;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private ConfigCollector configCollector;


    public String getURL(String serviceType) {
        if (StringUtils.isEmpty(serviceType)) {
            log.info("Invalid git service type {}", serviceType);
        }

        String url = null;
        switch (serviceType) {
            case GITLAB:
                url = appConfig.getGitLabBaseUrl();
                break;
            case GITHUB:
                url = appConfig.getGitHubBaseUrl();
                break;
            case BITBUCKET:
                url = appConfig.getBitBucketBaseUrl();
                break;
            default:
                break;
        }
        return url;
    }

    public GitIntegratorRequest createRequestData(GitGatewayRequest request, Configuration config) throws IOException {
        String repoId = config.getRepoId() != null ? config.getRepoId() : config.getProjectId();
        GitIntegratorRequest gitIntegratorRequest = GitIntegratorRequest.builder().gitToolId(config.getGitToolId()).customerId(request.getCustomerId()).gitBranch(config.getGitBranch()).targetBranch(config.getTargetBranch()).projectId(repoId)//bitbucket repository
                .workspace(config.getWorkspace())//bitbucket workspace
                .build();
        return gitIntegratorRequest;

    }
    public GitIntegratorResponse processGitAction(String readURL,GitIntegratorRequest gitIntegratorRequest) throws IOException {
        return restTemplateHelper.postForEntity(GitIntegratorResponse.class, readURL, gitIntegratorRequest);

    }


    public String getTagDetails(String tagName, String runCount) {
        if (tagName.equalsIgnoreCase(DATE) || tagName.toLowerCase().contains(DATE)) {
            tagName = tagName.replace(DATE, formatterwithDate.format(LocalDateTime.now()));
        }
        if (tagName.equalsIgnoreCase(TIMESTAMP) || tagName.toLowerCase().contains(TIMESTAMP)) {
            tagName = tagName.replace(TIMESTAMP, formatterWithTimestamp.format(LocalDateTime.now()));
        }
        if (tagName.equalsIgnoreCase(RUN_COUNT) || tagName.toLowerCase().contains(RUN_COUNT)) {
            tagName = tagName.replace(RUN_COUNT, runCount);
        }
        return tagName;
    }



    public GitGatewayResponse getGitGatewayResponseForTag(GitGatewayRequest request) {
        log.info("Request to create tag request : {} ", request);
        GitGatewayResponse gitGatewayResponse = new GitGatewayResponse();
        try {
            Configuration config = configCollector.getToolConfigurationDetails(request);
            String readURL = getURL(config.getService()) + CREATE_TAG_REQUEST;
            GitIntegratorRequest gitIntegratorRequest = createRequestData(request,config);
            String tagName = getTagDetails(config.getTag(), request.getRunCount().toString());
            gitIntegratorRequest.setTagName(tagName);
            gitIntegratorRequest.setTargetBranch(config.getGitBranch());
            validateTagRequestData(gitIntegratorRequest);
            GitIntegratorResponse gitResponse = processGitAction(readURL, gitIntegratorRequest);
            if(SUCCESS.equalsIgnoreCase(gitResponse.getStatus())) {
                gitGatewayResponse.setStatus(SUCCESS);
                String message= new StringBuffer().append("Tag ").append(tagName).append(" successfully created on branch ").append(gitIntegratorRequest.getTargetBranch()).toString();
                gitGatewayResponse.setMessage(message);
                gitGatewayResponse.setTagName(tagName);
                log.info("Tag created successfully");
            }else{
                gitGatewayResponse.setStatus(FAILED);
                gitGatewayResponse.setMessage(gitResponse.getMessage());
                log.info("Tag creation failed due to : {}",gitResponse.getMessage());
            }

        } catch (Exception e) {
            gitGatewayResponse.setStatus(FAILED);
            gitGatewayResponse.setMessage("tag creation request failed");
            log.error("tag creation request failed due to",e);
            String errorMsg = new StringBuilder("Error while creating tag  :").append(e.getMessage()).toString();
            throw new ServiceException(errorMsg);

        }

        return gitGatewayResponse;
    }

    public GitGatewayResponse getGitGatewayResponseForPull(GitGatewayRequest request) {
        log.info("Request to create Pull request : {} ", request);
        GitGatewayResponse gitGatewayResponse = new GitGatewayResponse();
        Configuration config =null;

        try {
            if(StringUtils.isEmpty(request.getGitTaskId())) {
                config = configCollector.getToolConfigurationDetails(request);
            }else{
                config=configCollector.getTaskConfiguration(request.getCustomerId(), request.getGitTaskId());
            }
            String readURL = getURL(config.getService()) + CREATE_PULL_REQUEST;
            GitIntegratorRequest gitIntegratorRequest = createRequestData(request,config);
            validatePullRequestData(gitIntegratorRequest);
            GitIntegratorResponse gitResponse = processGitAction(readURL, gitIntegratorRequest);
            if(SUCCESS.equalsIgnoreCase(gitResponse.getStatus())) {
                gitGatewayResponse.setStatus(SUCCESS);
                gitGatewayResponse.setMessage(gitResponse.getMessage());
                gitGatewayResponse.setPullRequestLink(gitResponse.getPullRequestLink());
            }else{
                gitGatewayResponse.setStatus(FAILED);
                gitGatewayResponse.setMessage(gitResponse.getMessage());

            }
        } catch (Exception e) {
            gitGatewayResponse.setStatus(FAILED);
            gitGatewayResponse.setMessage("Pull request creation failed");
            log.error("Pull request failed",e);
            String errorMsg = new StringBuilder("Error while creating pull :").append(e.getMessage()).toString();
            throw new ServiceException(errorMsg);
        }
        log.info("Successfully created Pull request ");
        return gitGatewayResponse;
    }

    private void validatePullRequestData(GitIntegratorRequest request) {
        List<String> missingData = new ArrayList<>();
        if (StringUtils.isEmpty(request.getGitToolId())) missingData.add("Git Tool");
        if (StringUtils.isEmpty(request.getGitBranch())) missingData.add("Source Branch");
        if (StringUtils.isEmpty(request.getTargetBranch())) missingData.add("target Branch");
        if (StringUtils.isEmpty(request.getProjectId())) missingData.add("Project details");

        if (!CollectionUtils.isEmpty(missingData)) {
            throw new ServiceException("Incomplete configuration : Please provide required data for " + missingData + " to raise pull request");
        }
    }

    private void validateTagRequestData(GitIntegratorRequest request) {
        List<String> missingData = new ArrayList<>();
        if (StringUtils.isEmpty(request.getGitToolId())) missingData.add("Git Tool");
        if (StringUtils.isEmpty(request.getProjectId())) missingData.add("Project/Repo details");
        if (StringUtils.isEmpty(request.getTagName())) missingData.add("Tag Name");
        if (StringUtils.isEmpty(request.getTargetBranch())) missingData.add("Git Branch");

        if (!CollectionUtils.isEmpty(missingData)) {
            throw new ServiceException("Incomplete configuration : Please provide required data " + missingData + " to raise tag");
        }
    }

}