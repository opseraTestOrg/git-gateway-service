/**
 *
 */
package com.opsera.service.gitgateway.resources;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Hemadri
 *
 */
@Data
public class GitGatewayRequest implements Serializable {
    private static final long serialVersionUID = -6104587402830092103L;

    private String customerId;
    private String pipelineId;
    private String stepId;
    private String gitToolId;
    private String projectId;
    private String branch;
    private String sourceBranch;
    private Boolean deleteSourceBranch;
    private String tagName;
    private String commitId;
    private String toolIdentifier;
    private String action;
    private String service;
}
