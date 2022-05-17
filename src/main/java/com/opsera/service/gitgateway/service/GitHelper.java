package com.opsera.service.gitgateway.service;


import static com.opsera.service.gitgateway.resources.Constants.BITBUCKET;
import static com.opsera.service.gitgateway.resources.Constants.CREATE_PULL_REQUEST;
import static com.opsera.service.gitgateway.resources.Constants.GITHUB;
import static com.opsera.service.gitgateway.resources.Constants.GITLAB;

import com.opsera.core.rest.RestTemplateHelper;
import com.opsera.service.gitgateway.config.AppConfig;
import com.opsera.service.gitgateway.resources.GitGatewayRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


/**
 * Helper class to facilitate all communications with Git Lab Service
 */
@Service
public class GitHelper {

    public static final Logger LOGGER = LoggerFactory.getLogger(GitHelper.class);

    @Autowired
    private RestTemplateHelper restTemplateHelper;

    @Autowired
    private AppConfig appConfig;


    public String getURL(String serviceType) {
        if (StringUtils.isEmpty(serviceType)) {
            LOGGER.info("Invalid git service type {}", serviceType);
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

    public String createPullRequest(GitGatewayRequest request) throws IOException {
        LOGGER.info("Request to create Pull request : {} ", request);
        String readURL = getURL(request.getService()) + CREATE_PULL_REQUEST;
        return restTemplateHelper.postForEntity(String.class, readURL, request);
    }
}