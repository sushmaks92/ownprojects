package com.cerner.hi.pitc.util;


import com.fasterxml.jackson.annotation.JsonFormat;

public class Constants {

	public static final String VALIDATOR_FILE_SUFFIX = "validators.xml";
	public static final String VALIDATOR_NAME_TYPE_SEPARATOR = "_";  
	
	public enum ValidationType{
    	SOARIAN("soarian"),
    	HEALTHIEINTENT("HealthieIntent"),
		MILLENIUM("Millenium");
    	private String type;
    	
    	private ValidationType(String type) {
    		this.type = type;
    	}

		public String getType() {
			return type;
		}
    }

	
    public enum JIRAQueryType{
    	FILTER("filter"),
    	QUERY("query"),
    	JIRA("jira");
    	
    	private String type;
    	
    	private JIRAQueryType(String type) {
    		this.type = type;
    	}

		public String getType() {
			return type;
		}
    }
    public enum JIRAComponentsToIgnore{
        CHECKINS("Checkins"),
        BUILD("Build & Capture"),
        DOCUMENTATION("Documentation"),
        INSTALL_BVT("Install/BVT");

        private String component;

        private JIRAComponentsToIgnore(String component) {
            this.component = component;
        }

        public String getComponemt() {
            return component;
        }
    }
    public enum IssueTypes{
        SPIKE("Spike"),
        TASK("Task"),
        EPIC("Epic"),
        SUB_TASK("Sub-task");

        private String issueType;

        private IssueTypes(final String issueType) {
            this.issueType = issueType;
        }
        public String getIssueType() {
            return issueType;
        }
    }
    public enum JIRAStatus{
        Submitted
    }
    public enum UserRole{

        ENGINEER("engineer"),
        TESTER("tester"),
        SME("sme");

        private String userRole;

        UserRole(String role){
            this.userRole = role;
        }

        public String getUserRole() {
            return userRole;
        }

    }

    public enum DataTypeForParams{
        INTEGER,DOUBLE,BOOLEAN,STRING
    }

    public enum ResultVariables{
        USED_TEMPLATE,
        HAS_NARRATIVE,
        HAS_CLIENT_VIEW_DESC,
        HAS_AC,
        HAS_TECHDESIGN,
        HAS_REQID,
        HAS_HAZARD,
        ACKNOLEDGED_REQ,
        ACKNOLEDGED_AC_DEV,
        ACKNOLEDGED_AC_SME,
        ACKNOLEDGED_AC_TESTER,
        HAS_UNITTESTING,
        HAS_UT_EVIDENCE,
        CODE_REVIEW,
        HAS_TESTPLAN_APPROVAL,
        HAS_TEST_ARTIFACTS;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum ValidationInfoType {
        SUCCESS("Y"),
        FAILURE("N"),
        PARTIAL("P"),
        NOTCLOSED("NC"),
        NOTAPPLICABLE("N/A");
        private String value = "";

        public String getName(){
            return this.name();
        }
        private ValidationInfoType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
    
    public enum RiskType{
    	HIGHRISK,
    	LOWRISK,
    	MEDIUMRISK;
    }

    public enum JiraFieldMap{

        EPIC("customfield_11000"),
        SUMMARY("summary"),
        ENGINEER("customfield_16201"),
        REPORTER("reporter"),
        PLANNEDVERSIONS("customfield_14812"),
        VERIFIEDVERSIONS("customfield_17703"),
        SOLUTIONDESIGNER("customfield_16200"),
        TESTER("customfield_14500"),
        DESCRIPTION("description"),
        COMMENT("comment"),
        ATTACHMENT("attachment"),
        STATUS("status"),
        ISSUETYPE("issuetype"),
        COMPONENTS("components"),
        FIXVERSIONS("fixVersions"),
        SOLUTIONDETAIL("customfield_14801"),
        JIRAGROUP("customfield_14802"),
        ASSIGNEE("assignee"),
        CLIENT_VIEWABLE("customfield_10002");


        private String fieldName;
        private JiraFieldMap(String fieldName) {
            this.fieldName = fieldName;
        }
        public String getFieldName() {
            return fieldName;
        }

    }
    public static final int DEFAULT_NUMBER_OF_CORE_THREAD = 5;
    public static final int DEFAULT_NUMBER_OF_MAX_THREAD = 10;
    public static final int QUEUE_SIZE = 1000;
    public static final String HOST = "smtplb.cerner.com";
    public static final String senderID = "Pallavi.Karjol@cerner.com";
    public static final String recipientID = "Pallavi.Karjol@cerner.com";

}

