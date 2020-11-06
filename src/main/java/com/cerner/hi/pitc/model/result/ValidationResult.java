package com.cerner.hi.pitc.model.result;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.cerner.hi.pitc.model.ValidationInfo;
import lombok.Getter;
import lombok.Setter;


public class ValidationResult {

    private String jiraNumber;
    private String jiraSummary;
    private String epic;
    private String plannedVersion;
    private String fixVersion;
    private String verifiedVersion;
    private BasicUser solDesigner = null;
    private BasicUser developer = null;
    private BasicUser tester = null;
    private BasicUser reporter= null;
    private BasicUser sme= null;
    private String issueType;
    private String status;
    private String resolution;
    private String reqID;
    private String solutionDetail;
    private String jiraGroup;
    private BasicUser assignee;
    private ArrayList<String> components;
    private String clientViewable;
    
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
    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }
    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
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
        if(!hasBeenValidated && null==epic)
            epic = "N/A";
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
    public BasicUser getSolDesigner() throws URISyntaxException {
        if(!hasBeenValidated && null==solDesigner) {
            return getDefaultUser();
        }
        return solDesigner;
    }

    //Need to check to create user.
    private BasicUser getDefaultUser() throws URISyntaxException {
        BasicUser user = new BasicUser(new URI("http:/jira2.cerner.com"),"N/A","N/A");
        return user;
    }
    public void setSolDesigner(BasicUser strategist) {
        this.solDesigner = strategist;
    }
    public BasicUser getDeveloper() throws URISyntaxException {
        if(!hasBeenValidated && null==developer) {
            return getDefaultUser();
        }
        return developer;
    }
    public void setDeveloper(BasicUser developer) {
        this.developer = developer;
    }
    public BasicUser getTester() throws URISyntaxException {
        if(!hasBeenValidated && null==tester) {
            return getDefaultUser();
        }
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
    public BasicUser getSme() throws URISyntaxException {
        if(!hasBeenValidated) {
            return getDefaultUser();
        }
        return sme;
    }
    public void setSme(BasicUser sme) {
        this.sme = sme;
    }

    public String getReqID() {
        if(!hasBeenValidated) {
            reqID = " ";
        }
        return reqID;
    }
    public void setReqID(String reqID) {
        this.reqID = reqID;
    }


    private boolean hasBeenValidated = false;



    public boolean isHasBeenValidated() {
        return hasBeenValidated;
    }
    public void setHasBeenValidated(boolean hasBeenValidated) {
        this.hasBeenValidated = hasBeenValidated;
    }

    @Getter
    @Setter
    private List<ValidationInfo> validationInfoList = new ArrayList<>();

//    private ValidationInfoType usesTemplate;
//    private ValidationInfoType narrative;
//    private ValidationInfoType clientDesc;
//    private ValidationInfoType acceptanceCriteria;
//    private ValidationInfoType techDesign;
//    private ValidationInfoType reqId;
//    private ValidationInfoType hazardAnalysis;
//    private ValidationInfoType ackReqId;
//    private ValidationInfoType ackACDev;
//    private ValidationInfoType ackACSME;
//    private ValidationInfoType ackACTester;
//    private ValidationInfoType unitTest; //need clarification
//    private ValidationInfoType unitTestEvidence;
//    private ValidationInfoType codeReview;
//    private ValidationInfoType testPlanApproval;
//    private ValidationInfoType testArtifacts;
//
//    public ValidationInfoType isUsesTemplate() {
//        return usesTemplate;
//    }
//    public void setUsesTemplate(ValidationInfoType usesTemplate) {
//        this.usesTemplate = usesTemplate;
//    }
//    public ValidationInfoType isNarrative() {
//        return narrative;
//    }
//    public void setNarrative(ValidationInfoType narrative) {
//        this.narrative = narrative;
//    }
//    public ValidationInfoType isAcceptanceCriteria() {
//        return acceptanceCriteria;
//    }
//    public void setAcceptanceCriteria(ValidationInfoType acceptanceCriteria) {
//        this.acceptanceCriteria = acceptanceCriteria;
//    }
//    public ValidationInfoType isTechDesign() {
//        return techDesign;
//    }
//    public void setTechDesign(ValidationInfoType techDesign) {
//        this.techDesign = techDesign;
//    }
//    public ValidationInfoType isReqId() {
//        return reqId;
//    }
//    public void setReqId(ValidationInfoType reqId) {
//        this.reqId = reqId;
//    }
//    public ValidationInfoType isHazardAnalysis() {
//        return hazardAnalysis;
//    }
//    public void setHazardAnalysis(ValidationInfoType hazardAnalysis) {
//        this.hazardAnalysis = hazardAnalysis;
//    }
//    public ValidationInfoType isAckReqId() {
//        return ackReqId;
//    }
//    public void setAckReqId(ValidationInfoType ackReqId) {
//        this.ackReqId = ackReqId;
//    }
//    public ValidationInfoType isAckACDev() {
//        return ackACDev;
//    }
//    public void setAckACDev(ValidationInfoType ackACDev) {
//        this.ackACDev = ackACDev;
//    }
//    public ValidationInfoType isAckACSME() {
//        return ackACSME;
//    }
//    public void setAckACSME(ValidationInfoType ackACSME) {
//        this.ackACSME = ackACSME;
//    }
//    public ValidationInfoType isAckACTester() {
//        return ackACTester;
//    }
//    public void setAckACTester(ValidationInfoType ackACTester) {
//        this.ackACTester = ackACTester;
//    }
//    public ValidationInfoType isUnitTest() {
//        return unitTest;
//    }
//    public void setUnitTest(ValidationInfoType unitTest) {
//        this.unitTest = unitTest;
//    }
//    public ValidationInfoType isUnitTestEvidence() {
//        return unitTestEvidence;
//    }
//    public void setUnitTestEvidence(ValidationInfoType unitTestEvidence) {
//        this.unitTestEvidence = unitTestEvidence;
//    }
//    public ValidationInfoType isCodeReview() {
//        return codeReview;
//    }
//    public void setCodeReview(ValidationInfoType codeReview) {
//        this.codeReview = codeReview;
//    }
//    public ValidationInfoType isTestPlanApproval() {
//        return testPlanApproval;
//    }
//    public void setTestPlanApproval(ValidationInfoType testPlanApproval) {
//        this.testPlanApproval = testPlanApproval;
//    }
//    public ValidationInfoType isTestArtifacts() {
//        return testArtifacts;
//    }
//    public void setTestArtifacts(ValidationInfoType testArtifacts) {
//        this.testArtifacts = testArtifacts;
//    }
//    public ValidationInfoType isClientDesc() {
//        return clientDesc;
//    }
//    public void setClientDesc(ValidationInfoType hasClientDesc) {
//        this.clientDesc = hasClientDesc;
//    }
//
//
//
//
//
//    public ValidationInfoType getUsesTemplate() {
//        return usesTemplate;
//    }
//
//    public ValidationInfoType getNarrative() {
//        return narrative;
//    }
//
//    public ValidationInfoType getClientDesc() {
//        return clientDesc;
//    }
//
//    public ValidationInfoType getAcceptanceCriteria() {
//        return acceptanceCriteria;
//    }
//
//    public ValidationInfoType getTechDesign() {
//        return techDesign;
//    }
//
//    public ValidationInfoType getReqId() {
//        return reqId;
//    }
//
//    public ValidationInfoType getHazardAnalysis() {
//        return hazardAnalysis;
//    }
//
//    public ValidationInfoType getAckReqId() {
//        return ackReqId;
//    }
//
//    public ValidationInfoType getAckACDev() {
//        return ackACDev;
//    }
//
//    public ValidationInfoType getAckACSME() {
//        return ackACSME;
//    }
//
//    public ValidationInfoType getAckACTester() {
//        return ackACTester;
//    }
//
//    public ValidationInfoType getUnitTest() {
//        return unitTest;
//    }
//
//    public ValidationInfoType getUnitTestEvidence() {
//        return unitTestEvidence;
//    }
//
//    public ValidationInfoType getCodeReview() {
//        return codeReview;
//    }
//
//    public ValidationInfoType getTestPlanApproval() {
//        return testPlanApproval;
//    }
//
//    public ValidationInfoType getTestArtifacts() {
//        return testArtifacts;
//    }
    
    public String getSolutionDetail() {
        if(null==solutionDetail)
            solutionDetail = "";
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
    public ArrayList<String> getComponents() {
        return components;
    }
    public void setComponents(ArrayList<String> components) {
        this.components = components;
    }
    public String getClientViewable() {
        return clientViewable;
    }
    public void setClientViewable(String clientViewable) {
        this.clientViewable = clientViewable;
    }
    public String getFixVersion() {
    	if(!hasBeenValidated && null==fixVersion)
    		fixVersion = "N/A";
    	return fixVersion;
    }
    public void setFixVersion(String fixVersion) {
    	this.fixVersion = fixVersion;
    }
    public void setVerifiedVersion(String verifiedVersion) {
    	this.verifiedVersion = verifiedVersion;
    }
    public String getVerifiedVersion() {
    	if(!hasBeenValidated && null==verifiedVersion)
    		verifiedVersion= "N/A";
    	return verifiedVersion;
    }
	public void setResolution(String resolution) {
		this.resolution = resolution;
		
	}
	public String getResolution() {
		return resolution;
	}
}

