package com.opsera.service.gitgateway.service;

import static com.opsera.service.gitgateway.resources.Constants.TOOLS_CONFIG_URL;
import com.opsera.core.rest.RestTemplateHelper;
import com.opsera.service.gitgateway.config.AppConfig;
import com.opsera.service.gitgateway.resources.Configuration;
import com.opsera.service.gitgateway.resources.GitGatewayRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

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


}
