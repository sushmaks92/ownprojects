package com.cerner.hi.pitc.contract;

import java.net.URISyntaxException;
import java.util.List;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.cerner.hi.pitc.model.config.JiraQueryConfiguration;

public interface IssueDAO {

	Issue getIssue(JiraQueryConfiguration config) throws URISyntaxException;

	List<Issue> getAllIssues(JiraQueryConfiguration config) throws URISyntaxException;

	List<String> getAllIssueKeys(JiraQueryConfiguration config) throws URISyntaxException;

}
