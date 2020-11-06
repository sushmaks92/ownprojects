package com.cerner.hi.pitc.model.config;

import java.net.URI;
import java.net.URISyntaxException;

import com.cerner.hi.pitc.util.Constants.JIRAQueryType;

public class JiraQueryConfiguration {
	
	private String jiraServer;
	
	private Credential credential;
	
	private String filterId;
	
	private String jiraKey;
	private String jql;
	private JIRAQueryType queryType;
	//maxResult. defaulted to -1 to get all issues.
	private int maxResult = -1;

	public String getJiraServer() {
		return jiraServer;
	}

	public void setJiraServer(String jiraServer) {
		this.jiraServer = jiraServer;
	}

	public Credential getCredential() {
		return credential;
	}

	public void setCredential(Credential credential) {
		this.credential = credential;
	}

	public String getFilterId() {
		return filterId;
	}

	public void setFilterId(String filterId) {
		this.filterId = filterId;
		if(JIRAQueryType.FILTER==this.getQueryType())
			this.setJql("filter=\""+filterId+"\"");
	}

	public URI getJiraServerURI() {
		if(null!=jiraServer) {
			try {
				return new URI(jiraServer);
			} catch (URISyntaxException e) {
			}
		}
		return null;
	}

	public String getJiraKey() {
		if(null==jiraKey)
			jiraKey = "";
		return jiraKey;
	}

	public void setJiraKey(String jiraKey) {
		this.jiraKey = jiraKey;
		
	}

	public String getJql() {
		return jql;
	}

	public void setJql(String jql) {
		this.jql = jql;
	}

	public JIRAQueryType getQueryType() {
		return queryType;
	}

	public void setQueryType(JIRAQueryType queryType) {
		this.queryType = queryType;
	}
	
	public void setQueryType(String type) {
		for(JIRAQueryType queryType : JIRAQueryType.values()) {
			if(queryType.getType().equalsIgnoreCase(type)) {
				this.queryType = queryType;
			}
		}
	}

	public int getMaxResult() {
		return maxResult;
	}

	public void setMaxResult(int maxResult) {
		this.maxResult = maxResult;
	}

}
