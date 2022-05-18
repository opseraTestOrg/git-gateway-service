/**
 *
 */
package com.opsera.service.gitgateway.resources;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Hemadri
 *
 */
@Data
public class Configuration implements Serializable {

    private static final long serialVersionUID = -3295200393630171737L;
    private String projectId;
    private String gitToolId;
    private boolean deleteSourceBranch;
    private String repository;
    private String gitBranch;
    private String targetBranch;
}
