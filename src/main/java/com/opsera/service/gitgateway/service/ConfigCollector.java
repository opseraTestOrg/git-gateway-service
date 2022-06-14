package com.opsera.service.gitgateway.service;

import static com.opsera.service.gitgateway.resources.Constants.TASK_CONFIG_ENDPOINT;
import static com.opsera.service.gitgateway.resources.Constants.TOOLS_CONFIG_URL;
import com.opsera.core.rest.RestTemplateHelper;
import com.opsera.service.gitgateway.config.AppConfig;
import com.opsera.service.gitgateway.resources.Configuration;
import com.opsera.service.gitgateway.resources.GitGatewayRequest;
import com.opsera.service.gitgateway.resources.Pipelines;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
public class ConfigCollector {

    @Autowired
    private AppConfig appConfig;
    @Autowired
    private RestTemplateHelper restTemplateHelper;


    /**
     * Returns tool configuration details.
     *
     * @param request
     * @return
     * @throws IOException
     */
    public Configuration getToolConfigurationDetails(GitGatewayRequest request) throws IOException {
        String url = appConfig.getPipelineConfigBaseUrl() + TOOLS_CONFIG_URL;
        return restTemplateHelper.postForEntity(Configuration.class, url, request);
    }

    public Pipelines getPipelineDetails(GitGatewayRequest request) throws IOException {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(appConfig.getPipelineConfigBaseUrl()).path("/v2/pipeline").queryParam("pipelineId", request.getPipelineId())
                .queryParam("customerId", request.getCustomerId());
        return restTemplateHelper.getForEntity(Pipelines.class, uriBuilder.toUriString());

    }

    public Configuration getTaskConfiguration(String customerId, String taskId) throws Exception {
        log.info("Getting the Git task Configuration for gitTask Id {}, customer Id {}", taskId, customerId);
        GitGatewayRequest request = new GitGatewayRequest();
        request.setGitTaskId(taskId);
        request.setCustomerId(customerId);
        String toolsConfigURL = appConfig.getPipelineConfigBaseUrl() + TASK_CONFIG_ENDPOINT;
        Configuration response = restTemplateHelper.postForEntity(Configuration.class, toolsConfigURL, request);
        Optional<Configuration> gitConfiguration = Optional.ofNullable(response);
        if (gitConfiguration.isPresent()) {
            return gitConfiguration.get();
        } else {
            throw new Exception(String.format("Git Task configuration details not found for taskId: %s, customer: %s", taskId, customerId));
        }

    }
    public String getPipelineActivities(GitGatewayRequest request) throws IOException {
        GitGatewayRequest pipelineActivitiesRequest=new GitGatewayRequest();
        pipelineActivitiesRequest.setPipelineId(request.getPipelineId());
        pipelineActivitiesRequest.setCustomerId(request.getCustomerId());
        pipelineActivitiesRequest.setRunCount(request.getRunCount());
        String url = appConfig.getPipelineConfigBaseUrl() + "/pipelineactivitiesforpipeline";
        return restTemplateHelper.postForEntity(String.class,url,request);

    }


}
