package com.cerner.hi.pitc.model;

import java.util.List;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.cerner.hi.pitc.executor.ValidatorService;
import com.cerner.hi.pitc.model.config.Configuration;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class WorkerProperties {

    private String jiraKey;
    private Issue issue;
    private List<Validator> validators;
    private List<ValidatorService> validatorServices;
    private Configuration config;


    public WorkerProperties(String jiraKey, Issue issue, List<Validator> validators, Configuration config) {
        super();
        this.jiraKey = jiraKey;
        this.issue = issue;
        this.validators = validators;
        this.config = config;
    }
}

