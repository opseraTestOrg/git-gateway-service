package com.opsera.service.gitgateway.service;


import static com.opsera.service.gitgateway.resources.Constants.BITBUCKET;
import static com.opsera.service.gitgateway.resources.Constants.CREATE_PULL_REQUEST;
import static com.opsera.service.gitgateway.resources.Constants.CREATE_TAG_REQUEST;
import static com.opsera.service.gitgateway.resources.Constants.DATE;
import static com.opsera.service.gitgateway.resources.Constants.FAILED;
import static com.opsera.service.gitgateway.resources.Constants.GITHUB;
import static com.opsera.service.gitgateway.resources.Constants.GITLAB;
import static com.opsera.service.gitgateway.resources.Constants.IN_PROGRESS;
import static com.opsera.service.gitgateway.resources.Constants.OTHER;
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
import com.opsera.service.gitgateway.util.GitGatewayUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


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
        GitIntegratorRequest gitIntegratorRequest= GitIntegratorRequest.builder()
                .gitToolId(config.getGitToolId())
                .customerId(request.getCustomerId())
                .gitBranch(config.getGitBranch())
                .targetBranch(config.getTargetBranch())
                .projectId(config.getProjectId())//bitbucket repository
                .workspace(config.getWorkspace())//bitbucket workspace
                .build();
        return gitIntegratorRequest;

    }
    public GitIntegratorResponse processGitAction(String readURL,GitIntegratorRequest gitIntegratorRequest) throws IOException {
        return restTemplateHelper.postForEntity(GitIntegratorResponse.class, readURL, gitIntegratorRequest);

    }

    public String getTagName(Configuration config,String runCount) {
        String tagName = "";
        if (config.isDynamicTag()) {
            tagName = getTagDetails(config.getTagType().get(0).equalsIgnoreCase(OTHER) ? config.getDynamicTagName() :
                    config.getTagType().get(0), runCount);
        } else {
            tagName = getTagDetails(config.getTagName(), runCount);
        }
        return tagName;
    }
    private String getTagDetails(String tagName, String runCount) {
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

    @Autowired
    private GitGatewayUtil gitGatewayUtil;

    public GitGatewayResponse getGitGatewayResponseForTag(GitGatewayRequest request) {
        GitGatewayResponse gitGatewayResponse = new GitGatewayResponse();
        gitGatewayResponse.setCustomerId(request.getCustomerId());
        gitGatewayResponse.setPipelineId(request.getPipelineId());
        gitGatewayResponse.setRunCount(request.getRunCount());
        gitGatewayResponse.setStatus(IN_PROGRESS);
        gitGatewayResponse.setStatus("Create Tag request in Progress");
        gitGatewayUtil.writeToResponseTopic(gitGatewayResponse);
        try {
            Configuration config = configCollector.getToolConfigurationDetails(request);
            String readURL = getURL(request.getService()) + CREATE_TAG_REQUEST;
            GitIntegratorRequest gitIntegratorRequest = createRequestData(request,config);
            String tagName = getTagName(config, request.getRunCount().toString());
            gitIntegratorRequest.setTagName(tagName);
            GitIntegratorResponse gitResponse = processGitAction(readURL, gitIntegratorRequest);
            gitGatewayResponse.setStatus(SUCCESS);
            gitGatewayResponse.setMessage("Tag request successfully created with tag name : "+tagName);
            gitGatewayResponse.setTagName(tagName);
            gitGatewayUtil.writeToResponseTopic(gitGatewayResponse);
        } catch (IOException e) {
            gitGatewayResponse.setStatus(FAILED);
            gitGatewayResponse.setMessage("tag creation request failed");
            log.error("tag creation request failed due to",e.getMessage());
            gitGatewayUtil.writeToLogTopic(gitGatewayResponse);
            gitGatewayUtil.writeToResponseTopic(gitGatewayResponse);
            String errorMsg = new StringBuilder("Error while creating tag  ").append(" Error : ").append(e.getMessage()).toString();
            throw new ServiceException(errorMsg);

        }
        return gitGatewayResponse;
    }

    public GitGatewayResponse getGitGatewayResponseForPull(GitGatewayRequest request) {
        GitGatewayResponse gitGatewayResponse = new GitGatewayResponse();
        gitGatewayResponse.setCustomerId(request.getCustomerId());
        gitGatewayResponse.setPipelineId(request.getPipelineId());
        gitGatewayResponse.setRunCount(request.getRunCount());
        gitGatewayResponse.setStatus(IN_PROGRESS);
        gitGatewayResponse.setStatus("Create pull request in Progress");
        gitGatewayUtil.writeToResponseTopic(gitGatewayResponse);
        try {
            Configuration config = configCollector.getToolConfigurationDetails(request);
            String readURL = getURL(request.getService()) + CREATE_PULL_REQUEST;
            GitIntegratorRequest gitIntegratorRequest = createRequestData(request,config);
            GitIntegratorResponse gitResponse = processGitAction(readURL, gitIntegratorRequest);
            gitGatewayResponse.setStatus(SUCCESS);
            gitGatewayResponse.setMessage("pull request successfully created : "+gitResponse.getPullRequestLink());
            gitGatewayResponse.setPullRequestLink(gitResponse.getPullRequestLink());
            gitGatewayUtil.writeToResponseTopic(gitGatewayResponse);
        } catch (Exception e) {
            gitGatewayResponse.setStatus(FAILED);
            gitGatewayResponse.setMessage("Pull request creation failed");
            log.error("Pull request failed",e.getMessage());
            gitGatewayUtil.writeToLogTopic(gitGatewayResponse);
            gitGatewayUtil.writeToResponseTopic(gitGatewayResponse);
            String errorMsg = new StringBuilder("Error while creating pull ").append(" Error : ").append(e.getMessage()).toString();
            throw new ServiceException(errorMsg);
        }
        return gitGatewayResponse;
    }

}