package com.cerner.hi.pitc.model;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.cerner.hi.pitc.executor.ValidatorService;
import com.cerner.hi.pitc.model.config.Configuration;
import com.cerner.hi.pitc.util.ValidatorServiceUtils;

import lombok.Getter;
import lombok.Setter;


public class ValidationContext {

    private String jiraKey;
    private Issue issue;
    private BasicUser developer;
    private BasicUser tester;
    private BasicUser sme;
    @Getter
    @Setter
    private BasicUser solutionDesigner;
    private String reqId;
    private ValidatorServiceUtils utils = new ValidatorServiceUtils();
    @Setter
    @Getter
    private Configuration config;
    @Setter
    @Getter
    private DateTime inProgressUpdatedTime;
    @Setter
    @Getter
    private DateTime closedUpdatedTime;
    /**
     * @return the utils
     */
    public ValidatorServiceUtils getUtils() {
        return utils;
    }

    public BasicUser getDeveloper() {
        return developer;
    }
    public void setDeveloper(BasicUser developer) {
        this.developer = developer;
    }
    public BasicUser getTester() {
        return tester;
    }
    public void setTester(BasicUser tester) {
        this.tester = tester;
    }
    public BasicUser getSme() {
        return sme;
    }
    public void setSme(BasicUser sme) {
        this.sme = sme;
    }
    private Map<Class<? extends ValidatorService>,ValidationInfo> resultMap = new HashMap<>();

    public String getJiraKey() {
        return jiraKey;
    }
    public void setJiraKey(String jiraKey) {
        this.jiraKey = jiraKey;
    }
    public Map<Class<? extends ValidatorService>, ValidationInfo> getResultMap() {
        return resultMap;
    }
    public Issue getJira() {
        return issue;
    }
   
    public void setJira(Issue issue) {
        this.issue = issue;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

}