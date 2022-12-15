package com.opsera.service.gitgateway.resources;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GitIntegratorRequest {
    private String customerId;
    private String projectId;
    private String gitToolId;
    private String repository;
    private String gitBranch;
    private String targetBranch;
    private String tagName;
    private String workspace;
}
