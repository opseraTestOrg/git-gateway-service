package com.opsera.service.gitgateway.resources;

import lombok.Data;

@Data
public class PipelineActivities {

    private String id;

    private String userId;

    private String pipelineId;

    private String toolIdentifier;

    private String stepId;

    private String stepName;
    private Integer stepIndex;

    private StepConfiguration stepConfiguration;

    private String message;

    private String status;

    private String action;

    private int runCount;
}
