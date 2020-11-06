package com.cerner.hi.pitc.repository;


import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import com.cerner.hi.pitc.contract.IssueDAO;
import com.cerner.hi.pitc.model.config.JiraQueryConfiguration;
import com.cerner.hi.pitc.util.Constants;


@Component
public class JRJCIssueDAO implements IssueDAO{

	private JiraRestClientFactory factory = null;
	
	@Override
    public Issue getIssue(JiraQueryConfiguration config) throws URISyntaxException {
        JiraRestClientFactory factory = getJRJCFactory();
        JiraRestClient restClient = factory.createWithBasicHttpAuthentication(config.getJiraServerURI(), config.getCredential().getUserid(), config.getCredential().getPassword());
        System.out.println(config.getJiraKey());
        return restClient.getIssueClient().getIssue(config.getJiraKey(), Arrays.asList(IssueRestClient.Expandos.CHANGELOG)).claim();
    }

	@Override
    public List<Issue> getAllIssues(JiraQueryConfiguration config) throws URISyntaxException{
    	List<String> jiraKeys = null;
    	if(config.getQueryType()==Constants.JIRAQueryType.JIRA) {
    		jiraKeys = new ArrayList<>();
    		jiraKeys.add(config.getJiraKey());
    	}else {
    	 jiraKeys = getAllIssueKeys(config);
    	}
    	ArrayList<Issue> issues = new ArrayList<>();
    	jiraKeys.forEach(k -> {
			try {
				config.setJiraKey(k);
				issues.add(getIssue(config));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		});
		return issues;
    	
    }
	
	@Override
    public List<String> getAllIssueKeys(JiraQueryConfiguration config) throws URISyntaxException {
        AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        URI jiraUri = config.getJiraServerURI();
        List<String> issues = new ArrayList<>();
        JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraUri, config.getCredential().getUserid(),config.getCredential().getPassword());
        // we use the following try catch block to change the timeout period which is 20 seconds by default.
        try {
			Field f1 = Class.forName("com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient").getDeclaredField("httpClient");
            Field f2 = Class.forName("com.atlassian.jira.rest.client.internal.async.AtlassianHttpClientDecorator").getDeclaredField("httpClient");
            Field f3 = Class.forName("com.atlassian.httpclient.apache.httpcomponents.ApacheAsyncHttpClient").getDeclaredField("httpClient");
            Field f4 = Class.forName("org.apache.http.impl.client.cache.CachingHttpAsyncClient").getDeclaredField("backend");
            Field f5 = Class.forName("org.apache.http.impl.nio.client.InternalHttpAsyncClient").getDeclaredField("defaultConfig");
            Field f6 = Class.forName("org.apache.http.client.config.RequestConfig").getDeclaredField("socketTimeout");
            f1.setAccessible(true);
            f2.setAccessible(true);
            f3.setAccessible(true);
            f4.setAccessible(true);
            f5.setAccessible(true);
            f6.setAccessible(true);
            Object requestConfig = f5.get(f4.get(f3.get(f2.get(f1.get(restClient)))));
            // we can change the amount of seconds (num_of_seconds * 1000) before it times out here.
            f6.setInt(requestConfig, 5000 * 1000);
            f1.setAccessible(false);
            f2.setAccessible(false);
            f3.setAccessible(false);
            f4.setAccessible(false);
            f5.setAccessible(false);
            f6.setAccessible(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Promise<SearchResult> promise = restClient.getSearchClient().searchJql(config.getJql(),config.getMaxResult(),null,null);
        Iterable<Issue> issuesIter = promise.claim().getIssues();
        //int totalIssues = promise.claim().getTotal();
        issuesIter.forEach( i -> issues.add(i.getKey()));
        return issues;
    }
	
	private JiraRestClientFactory getJRJCFactory(){
		if(null==factory) {
			factory = new AsynchronousJiraRestClientFactory();
		}
		return factory;
	}

}