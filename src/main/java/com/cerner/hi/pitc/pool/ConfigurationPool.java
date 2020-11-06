package com.cerner.hi.pitc.pool;

import com.cerner.hi.pitc.model.config.JiraQueryConfiguration;

public enum ConfigurationPool {
	INSTANCE;
	
	private JiraQueryConfiguration jiraQueryConfiguration;

	public JiraQueryConfiguration getJiraQueryConfiguration() {
		if(null==jiraQueryConfiguration)
			jiraQueryConfiguration = new JiraQueryConfiguration();
		return jiraQueryConfiguration;
	}

	public void setJiraQueryConfiguration(JiraQueryConfiguration jiraQueryConfiguration) {
		this.jiraQueryConfiguration = jiraQueryConfiguration;
	}
}
