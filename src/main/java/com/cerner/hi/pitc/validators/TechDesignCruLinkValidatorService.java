package com.cerner.hi.pitc.validators;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.JsonSerializer;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.json.JSONArray;

import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueLink;
import com.atlassian.jira.rest.client.api.domain.Subtask;
import com.cerner.hi.pitc.contract.JsonParser;
import com.cerner.hi.pitc.executor.ValidatorService;
import com.cerner.hi.pitc.jira.HttpBasicAuth;
import com.cerner.hi.pitc.model.Parameter;
import com.cerner.hi.pitc.model.ValidationContext;
import com.cerner.hi.pitc.model.ValidationInfo;
import com.cerner.hi.pitc.parser.JsonParserImpl;
import com.cerner.hi.pitc.repository.JRJCIssueDAO;
import com.cerner.hi.pitc.util.Constants;
import com.cerner.hi.pitc.util.Constants.ResultVariables;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;

public class TechDesignCruLinkValidatorService extends ValidatorService {
	@Override
	protected void populateValidArgs(Set<Parameter> parameter) {
		Parameter pattern = new Parameter("pattern", Constants.DataTypeForParams.STRING);
		Parameter synonym = new Parameter("synonym", Constants.DataTypeForParams.STRING);
		parameter.add(pattern);
		parameter.add(synonym);
		Parameter excelColumn = new Parameter("excelColumnName", Constants.DataTypeForParams.STRING);
		parameter.add(excelColumn);

	}

	@Override
	public ValidationInfo serviceOperations(ValidationContext context) {
		ValidationInfo info = new ValidationInfo();
		info.setClazz(this.getClass());
		String jiraKey = context.getJira().getKey();
		Issue jira = context.getJira();
		
		JsonParser parser = new JsonParserImpl();
		Iterable<Subtask> subTasks = jira.getSubtasks();
		subTasks.forEach(st -> {
			if (st.getSummary().toLowerCase().replaceAll("\\s+", "").contains("techdesign")) {

				String MainJiraKey = jira.getKey();
			    /*Validation for TechDesign wiki link or crucible link in Issue Links  of Main Jira*/
			 //   for (final String server : getParameterMap().get("server1")) {
			    	try {
						JsonNode jiraContent = HttpBasicAuth.getIssueLinkResponseJira(context.getConfig().getJiraQueryConfiguration().getJiraServer(),context.getConfig(), jira.getId());
						 jiraContent.getArray().forEach(x->{
							 //check whether each linked issue has Techdoc/techdesign
							 if(context.getUtils().isPatternFoundInString(x.toString(), getParameterMap().get("pattern1"))) {
								try {
									//get the object from the jiracontent
									Map<String, Object> obj = parser.getRawObject(x.toString());
									LinkedHashMap objBody = (LinkedHashMap) obj.get("object");
									String url = objBody.get("url").toString();									  
									//if the url contains wiki link then check for the mainJiraName and set it to success
									if((url.contains("https://wiki.cerner.com"))&&(url.contains(MainJiraKey))) {					
										info.setInfoType(Constants.ValidationInfoType.SUCCESS);	
										
									}
									 
									//if url contains crucible link, fetch all the attachments and check for techdoc attachment
									else if((Constants.ValidationInfoType.SUCCESS != info.getInfoType())&&(url.contains("https://crucible"))) {
										System.out.println(url);			
										context.getConfig().getCrucibleConfiguration().getCrucibleServers().forEach(y->{
											try {
												String ReviewedCrucibleAttachments=HttpBasicAuth.getAttachmentsResponseCrucible(y,context.getConfig(),url.substring(url.lastIndexOf("/")+1));
												if(context.getUtils().isPatternFoundInString(ReviewedCrucibleAttachments, getParameterMap().get("pattern1"))) {
													info.setInfoType(Constants.ValidationInfoType.SUCCESS);	
												}												
												}catch(UnirestException e) {}		
											
										});
										}
									
								}catch(IOException e) {}
							 } 
							
						 });
			    	}catch (UnirestException | JSONException e ) {}					
			    //end of MainJira linksto checks
			    	
			    //if mainJira contains techdoc as an attachment,fetch it
				 if((Constants.ValidationInfoType.SUCCESS != info.getInfoType())&&(null != jira.getAttachments())) {
					jira.getAttachments().forEach(z->{
						 if(context.getUtils().isPatternFoundInString(z.getFilename(), getParameterMap().get("pattern1"))) {
							 info.setInfoType(Constants.ValidationInfoType.SUCCESS);
							
						 }
					});
			
				}
				 
				
				 //to get subTask details
				 Issue subJira = null;
				 if(Constants.ValidationInfoType.SUCCESS != info.getInfoType()){
				 context.getConfig().getJiraQueryConfiguration().setJiraKey(st.getIssueKey()); 
				 JRJCIssueDAO jrcIssueDao = new JRJCIssueDAO();
				
				try {
					subJira = jrcIssueDao.getIssue(context.getConfig().getJiraQueryConfiguration());
				} catch (URISyntaxException e1) {					
					e1.printStackTrace();
				}
				 }

				//check subtask links to section for wiki/crucible link  
				 if(Constants.ValidationInfoType.SUCCESS != info.getInfoType()){
					 try {
						
						JsonNode subJiraContent = HttpBasicAuth.getIssueLinkResponseJira(context.getConfig().getJiraQueryConfiguration().getJiraServer(),context.getConfig(), subJira.getId());						
						subJiraContent.getArray().forEach(x->{
							 //check whether each linked issue has Techdoc/techdesign
							 if(context.getUtils().isPatternFoundInString(x.toString(), getParameterMap().get("pattern1"))) {
								try {
									//get the object from the subJiracontent
									Map<String, Object> obj = parser.getRawObject(x.toString());
									LinkedHashMap objBody = (LinkedHashMap) obj.get("object");
									String url = objBody.get("url").toString();
									//if the url contains wiki link then check for the mainJiraName and set it to success
									if((url.contains("https://wiki.cerner.com"))&&(url.contains(st.getIssueKey()))) {
										info.setInfoType(Constants.ValidationInfoType.SUCCESS);
										
									}
									//if url contains crucible link, fetch all the attachments and check for techdoc attachment
									else if((Constants.ValidationInfoType.SUCCESS != info.getInfoType())&&(url.contains("https://crucible"))) {
										System.out.println(url);			
										context.getConfig().getCrucibleConfiguration().getCrucibleServers().forEach(y->{
											try {
												String ReviewedCrucibleAttachments=HttpBasicAuth.getAttachmentsResponseCrucible(y,context.getConfig(),url.substring(url.lastIndexOf("/")+1));
												if(context.getUtils().isPatternFoundInString(ReviewedCrucibleAttachments, getParameterMap().get("pattern1"))) {
													info.setInfoType(Constants.ValidationInfoType.SUCCESS);	
													
												}
												}catch(UnirestException e) {}		
										});
										}
									
								}catch(IOException e) {}
								
							 } 
							
						 });
					 } catch (UnirestException | JSONException e) {
						e.printStackTrace();
					}
				 }//end of subtask checks
				    
				 //if subtasks contains techdoc as an attachment,fetch it
				 if((Constants.ValidationInfoType.SUCCESS != info.getInfoType())&&(null != subJira.getAttachments())) {
					subJira.getAttachments().forEach(z->{
						 if(context.getUtils().isPatternFoundInString(z.getFilename(), getParameterMap().get("pattern1"))) {
							 info.setInfoType(Constants.ValidationInfoType.SUCCESS);
							
						 }
					});
			
				}
				if(Constants.ValidationInfoType.SUCCESS != info.getInfoType()) {
					info.setInfoType(Constants.ValidationInfoType.FAILURE);
				} 
			}
		});
		return info;

	}

	@Override
	protected ResultVariables getResultVariable() {
		return Constants.ResultVariables.HAS_TECHDESIGN;
	}

	@Override
	public void preServiceOperations(ValidationContext context) {

	}

}
