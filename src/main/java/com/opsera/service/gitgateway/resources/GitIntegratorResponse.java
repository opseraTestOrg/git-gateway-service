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
public class GitIntegratorResponse implements Serializable {

    private static final long serialVersionUID = 5983466883002203161L;

    private String commitId;
    private String pullRequestLink;
    private Integer pullRequestNum;
    private String message;
}
