/**
 * 
 */
package com.opsera.service.gitgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 
 * @author Hemadri
 *
 */
@SpringBootApplication
@EnableAsync
@ComponentScan(basePackages = {"com.opsera.core", "com.opsera.service.gitgateway"})
public class GitGatewayApp {

    /**
     * Entry point of the application
     * 
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(GitGatewayApp.class, args);
    }
}
