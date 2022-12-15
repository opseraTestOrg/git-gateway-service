/**
 * 
 */
package com.opsera.service.gitgateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @author Hemadri
 *
 */
@Configuration
@Getter
public class AppConfig {

    @Value("${pipeline.config.baseurl}")
    private String pipelineConfigBaseUrl;

    @Value("${opsera.client.host}")
    private String opseraClientHost;

    @Value("${retry.interval}")
    private int retryInterval;

    @Value("${retry.count}")
    private int retryCount;

    @Value("${opsera.github.baseurl}")
    private String gitHubBaseUrl;
    @Value("${opsera.gitlab.baseurl}")
    private String gitLabBaseUrl;
    @Value("${opsera.bitbucket.baseurl}")
    private String bitBucketBaseUrl;
    @Value("${opsera.azure.baseurl}")
    private String azureBaseUrl;
    /**
     * Create Bean for ObjectMapper
     * 
     * @return
     */

    @Bean
    public Gson getGson(){
        return new Gson();
    };

}
