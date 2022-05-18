package com.opsera.service.gitgateway.listener;


import static com.opsera.service.gitgateway.resources.Constants.OPSERA_PIPELINE_GITACTIONS_REQUEST;
import static com.opsera.service.gitgateway.resources.Constants.PULL;
import static com.opsera.service.gitgateway.resources.Constants.TAG;
import com.google.gson.Gson;
import com.opsera.service.gitgateway.config.GitGatewayTypeFactory;
import com.opsera.service.gitgateway.resources.GitGatewayRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;

//@Component
public class GitGatewayKafkaListener {
    public static final Logger LOGGER = LoggerFactory.getLogger(GitGatewayKafkaListener.class);



    @Autowired
    private Gson gson;
    @Autowired
    private GitGatewayTypeFactory gitGatewayTypeFactory;


    @KafkaListener(topics = OPSERA_PIPELINE_GITACTIONS_REQUEST, containerFactory = "kafkaListenerContainerFactory", groupId = "snaplogic-kubernetes-group")
    public void consumeGitActionsRequest(@Payload String message) {
        LOGGER.info("Message Received from kubernetes jobs topic for gitactions, Message:{}", message);
        try {
            GitGatewayRequest request = gson.fromJson(message, GitGatewayRequest.class);
            ;

            switch ( request.getAction()) {
                case PULL:
                    gitGatewayTypeFactory.getGitType(request.getService()).createPullRequest(request);
                    break;
                case TAG:
                    gitGatewayTypeFactory.getGitType(request.getService()).createTag(request);
                    break;
                default:
                    break;
            }

        } catch (Exception ex) {
            LOGGER.error("Error while processing Kafka notification request for gitactions Jobs", ex);
        } finally {
            LOGGER.info("Finished sending gitactions Jobs request");
        }
    }

}
