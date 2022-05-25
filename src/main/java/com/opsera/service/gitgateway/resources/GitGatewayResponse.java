package com.opsera.service.gitgateway.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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
