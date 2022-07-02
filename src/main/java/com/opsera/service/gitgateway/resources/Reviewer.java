package com.opsera.service.gitgateway.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(value = Include.NON_NULL)
public class Reviewer implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String value;
}
