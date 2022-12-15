package com.opsera.service.gitgateway.resources;

public class Constants {
    private Constants() {
        // do nothing
    }
    public static final String GITHUB = "github";
    public static final String GITLAB = "gitlab";
    public static final String BITBUCKET = "bitbucket";
    public static final String AZURE = "azure-devops";
    public static final String PULL = "create-pull-request";
    public static final String TAG = "add-tag";
    public static final String CREATE_PULL_REQUEST = "/pullRequest";
    public static final String CREATE_TAG_REQUEST = "/tag";
    public static final String TOOLS_CONFIG_URL = "/tools/configuration";
    public static final String DATE = "date";
    public static final String TIMESTAMP = "timestamp";
    public static final String RUN_COUNT = "run_count";
    public static final String OTHER = "other";
    public static final String YYYY_MM_DD = "yyyyMMdd";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyyMMddHHmmss";
    public static final String FAILED = "Failed";
    public static final String SUCCESS = "Success";
    public static final String IN_PROGRESS = "In Progress";
    public static final String TASK_CONFIG_ENDPOINT = "/gittask/configuration";
    public static final String OPSERA_PIPELINE_SUMMARY_URL = "https://%s/workflow/details/%s/summary";
    public static final String PIPELINE_ACTIVITIES_URL="/v2/get/pipelineactivities";
    public static final String PIPELINE_DETAILS="/v2/pipeline";



}
