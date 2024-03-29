package com.opsera.service.gitgateway.controller;

import com.opsera.core.aspects.TrackExecutionTime;
import com.opsera.service.gitgateway.resources.GitGatewayRequest;
import com.opsera.service.gitgateway.resources.GitGatewayResponse;
import com.opsera.service.gitgateway.service.GitHelper;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class GitGatewayController {
    @Autowired
    private GitHelper gitHelper;

    @GetMapping("/status")
    @ApiOperation("To check the service status")
    @TrackExecutionTime
    public String status() {
        return "Git Gateway Service running";
    }

    @PostMapping("/pullRequest")
    @ApiOperation("To create pull request")
    @TrackExecutionTime
    public ResponseEntity<GitGatewayResponse> processCreatePullRequest(@RequestBody GitGatewayRequest request){
        log.info("Received request to create Pull request {}",request);
        GitGatewayResponse response = gitHelper.getGitGatewayResponseForPull(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tag")
    @ApiOperation("To create tag request")
    @TrackExecutionTime
    public ResponseEntity<GitGatewayResponse> processCreateTagRequest(@RequestBody GitGatewayRequest request){
        log.info("Received request to create Pull request {}",request);
        GitGatewayResponse response = gitHelper.getGitGatewayResponseForTag(request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/step/status")
    @ApiOperation("To create tag request")
    @TrackExecutionTime
    public ResponseEntity<GitGatewayResponse> stepStatus(){
        log.info("Received request to check step status");
        GitGatewayResponse gitGatewayResponse=new GitGatewayResponse();
        gitGatewayResponse.setStatus("Success");
        gitGatewayResponse.setMessage("Step completed successfully");
        return ResponseEntity.ok(gitGatewayResponse);
    }


}
