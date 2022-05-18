package com.opsera.service.gitgateway.config;

import static com.opsera.service.gitgateway.resources.Constants.BITBUCKET;
import static com.opsera.service.gitgateway.resources.Constants.GITHUB;
import static com.opsera.service.gitgateway.resources.Constants.GITLAB;
import com.opsera.service.gitgateway.service.IGitActionsService;
import com.opsera.service.gitgateway.service.impl.BitBucketActionsServiceImpl;
import com.opsera.service.gitgateway.service.impl.GitLabActionsServiceImpl;
import com.opsera.service.gitgateway.service.impl.GithubActionsServiceImpl;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

@Component
public class GitGatewayTypeFactory {
    @Lookup
    public GithubActionsServiceImpl githubActionsService(){
        return  null;
    }
    @Lookup
    public GitLabActionsServiceImpl gitLabActionsService(){
        return null;
    }
    @Lookup
    public BitBucketActionsServiceImpl bitBucketActionsService(){
        return null;
    }

    public IGitActionsService getGitType(String lookup) {
        switch (lookup) {
            case GITHUB:
                return githubActionsService();
            case GITLAB:
                return gitLabActionsService();
            case BITBUCKET:
                return bitBucketActionsService();
            default:
                return null;


        }


    }
}
