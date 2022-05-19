package com.opsera.service.gitgateway.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opsera.core.enums.KafkaResponseTopics;
import com.opsera.core.helper.KafkaHelper;
import com.opsera.service.gitgateway.resources.GitGatewayResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class GitGatewayUtil {
    @Autowired
    private KafkaHelper kafkaHelper;
    @Autowired
    private ObjectMapper objectMapper;

    public void writeToLogTopic(GitGatewayResponse gitGatewayResponse) {
        CompletableFuture.runAsync(() -> {
            try {
                kafkaHelper.postNotificationToKafka(KafkaResponseTopics.OPSERA_PIPELINE_LOG.getTopicName(), objectMapper.writeValueAsString(gitGatewayResponse));
            } catch (JsonProcessingException e) {
                log.error("Exception occurred while writing to pipeline log topic {}", e.getMessage());
            }
        });

    }
    public void writeToResponseTopic(GitGatewayResponse gitGatewayResponse) {
        CompletableFuture.runAsync(() -> {
            try {
                kafkaHelper.postNotificationToKafka(KafkaResponseTopics.OPSERA_PIPELINE_RESPONSE.getTopicName(), objectMapper.writeValueAsString(gitGatewayResponse));
            } catch (JsonProcessingException e) {
                log.error("Exception occurred while writing to apigee response topic {}", e.getMessage());
            }
        });
    }
}
