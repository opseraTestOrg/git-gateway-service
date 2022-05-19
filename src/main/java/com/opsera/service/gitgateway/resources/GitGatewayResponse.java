package com.opsera.service.gitgateway.resources;

import lombok.Data;

@Data
public class GitGatewayResponse {
    private String customerId;
    private String pipelineId;
    private String stepId;
    private Integer runCount;
    private String status;
    private String message;
    private String pullRequestLink;
    private String tagName;
}
