package com.cerner.hi.pitc.jira;

import java.util.Base64;

import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.cerner.hi.pitc.model.config.Configuration;
import com.cerner.hi.pitc.model.config.Credential;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class HttpBasicAuth {


	public static String getQueryResponseCrucible(String server, Configuration config, String jiraKey) throws UnirestException {
		String crucibleQuery = server+"/rest/api/1.0/rest-service/search-v1/reviewsForIssue?jiraKey="+jiraKey;
		
		return getResponse(crucibleQuery,config.getCredentials());
	}
	
	public static String getQueryResponseCrucibleServer(String server, Configuration config, String crucibleId) throws UnirestException {
		String crucibleQuery = server+"/rest-service/reviews-v1/"+crucibleId;
		
		return getResponse(crucibleQuery,config.getCredentials());
	}
	
	
	public static String getAttachmentsResponseCrucible(String server, Configuration config, String permaId) throws UnirestException {
		String crucibleQuery = server+"/rest-service/reviews-v1/"+permaId+"/reviewitems";
		return getResponse(crucibleQuery,config.getCredentials());
		
	}
	public static  JsonNode getIssueLinkResponseJira(String server, Configuration config,  Long IssueId) throws UnirestException, JSONException {
		String JiraQuery = server+"/rest/api/2/issue/"+IssueId+"/remotelink";
		 return getResponseAsObject(JiraQuery,config.getCredentials());
		
	}
	

	private static String getResponse(String query, Credential credential)
			throws UnirestException {
		//Credential credential = CredentialManager.INSTANCE.getCredential();
		String userId = credential.getUserid();
		String password = credential.getPassword();
		String encoding = Base64.getEncoder().encodeToString((userId+":"+password).getBytes());
		
		System.out.println("executing request " + query);
		HttpResponse<JsonNode> response = Unirest.get(query)
		.header("Authorization", "Basic " + encoding)
		.header(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString())
		.asJson();
        return response.getBody().toString();
	}
	private static  JsonNode getResponseAsObject(String query, Credential credential)
			throws UnirestException {
		//Credential credential = CredentialManager.INSTANCE.getCredential();
		String userId = credential.getUserid();
		String password = credential.getPassword();
		String encoding = Base64.getEncoder().encodeToString((userId+":"+password).getBytes());
		
		System.out.println("executing request " + query);
		HttpResponse<JsonNode> response = Unirest.get(query)
		.header("Authorization", "Basic " + encoding)
		.header(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString())
		.asJson();
        return response.getBody();
	}
	
	
	
}