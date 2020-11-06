package com.cerner.hi.pitc.model;

import com.atlassian.jira.rest.client.api.domain.BasicUser;

import java.util.ArrayList;

public class BasicDetails {
    private String jiraNumber;
    private String jiraSummary;
    private String epic;
    private String plannedVersion;
    private BasicUser strategist;
    private BasicUser developer;
    private BasicUser tester;
    private BasicUser reporter;
    private BasicUser assignee;
    private BasicUser sme;
    private String reqId;
    private String status;
    private String resolution;
    private String issueType;
    private String fixedVersions;
    private String verifiedVersions;
    private String solutionDetail;
    private String jiraGroup;
    private ArrayList<String> components;
    private String clientViewable;


    /**
     * @return the reqId
     */
    public String getReqId() {
        return reqId;
    }

    /**
     * @param reqId the reqId to set
     */
    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    /**
     * @return the state
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param state the state to set
     */
    public void setStatus(String state) {
        this.status = state;
    }

    /**
     * @return the issueType
     */
    public String getIssueType() {
        return issueType;
    }

    /**
     * @param issueType the issueType to set
     */
    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getJiraNumber() {
        return jiraNumber;
    }

    public void setJiraNumber(String jiraNumber) {
        this.jiraNumber = jiraNumber;
    }

    public String getJiraSummary() {
        return jiraSummary;
    }

    public void setJiraSummary(String jiraSummary) {
        this.jiraSummary = jiraSummary;
    }

    public String getEpic() {
        return epic;
    }

    public void setEpic(String epic) {
        this.epic = epic;
    }

    public String getPlannedVersion() {
        return plannedVersion;
    }

    public void setPlannedVersion(String plannedVersion) {
        this.plannedVersion = plannedVersion;
    }

    public BasicUser getStrategist() {
        return strategist;
    }

    public void setStrategist(BasicUser strategist) {
        this.strategist = strategist;
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

    public BasicUser getReporter() {
        return reporter;
    }

    public void setReporter(BasicUser reporter) {
        this.reporter = reporter;
    }
    
    public BasicUser getAssignee() {
        return assignee;
    }

    public void setAssignee(BasicUser assignee) {
        this.assignee = assignee;
    }


    public BasicUser getSme() {
        return sme;
    }

    public void setSme(BasicUser sme) {
        this.sme = sme;
    }

    public ArrayList<String> getComponents() {
        return components;
    }

    public void setComponents(ArrayList<String> components) {
        this.components = components;
    }

    public String getFixedVersions() {
        return fixedVersions;
    }

    public void setFixedVersions(String fixedVersions) {
        this.fixedVersions = fixedVersions;
    }
    public String getVerifiedVersions() {
        return verifiedVersions;
    }

    public void setVerifiedVersions(String verifiedVersions ) {
        this.verifiedVersions = verifiedVersions;
    }

    public String getSolutionDetail() {
        return solutionDetail;
    }

    public void setSolutionDetail(String solutionDetail) {
        this.solutionDetail = solutionDetail;
    }

    public String getJiraGroup() {
        return jiraGroup;
    }

    public void setJiraGroup(String jiraGroup) {
        this.jiraGroup = jiraGroup;
    }

    public String getClientViewable() {
        return clientViewable;
    }

    public void setClientViewable(String clientViewable) {
        this.clientViewable = clientViewable;
    }

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
		
	}
}
