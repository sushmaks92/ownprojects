package com.cerner.hi.pitc.validators;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONException;

import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
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
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;

public class CheckJazzInRequirements extends ValidatorService {
	boolean flag1, result = false;
	String rqmlink = "";
	
	
    @Override
    protected void populateValidArgs(Set<Parameter> parameter) {
        Parameter pattern = new Parameter("pattern", Constants.DataTypeForParams.STRING);
        Parameter urlPattern = new Parameter("contentPattern", Constants.DataTypeForParams.STRING);
        parameter.add(pattern);
        parameter.add(urlPattern);
        Parameter excelColumn = new Parameter("excelColumnName", Constants.DataTypeForParams.STRING);
        parameter.add(excelColumn);
    }

    @Override
    protected Constants.ResultVariables getResultVariable() {
        return Constants.ResultVariables.HAS_REQID;
    }

    @Override
    public ValidationInfo serviceOperations(ValidationContext context) {
        ValidationInfo info = new ValidationInfo();
        info.setClazz(this.getClass());
        Issue jira = context.getJira();
        Iterable<Subtask> subTasks = jira.getSubtasks();
        JsonParser parser = new JsonParserImpl();
		   
    	subTasks.forEach(a -> {
			if (a.getSummary().toLowerCase().contains("req")) {
				String MainJiraKey = jira.getKey();
				context.getConfig().getJiraQueryConfiguration().setJiraKey(a.getIssueKey());
				JRJCIssueDAO jrcIssueDao = new JRJCIssueDAO();				
				try {
					Issue subJira = jrcIssueDao.getIssue(context.getConfig().getJiraQueryConfiguration());
					final String desc = subJira.getDescription();
					//check in description
					if (null != desc && !"".equals(desc)) {
						//rqmlink = context.getUtils().getMatchingString(desc, getParameterMap().get("pattern"), 1);
						if ((desc.replaceAll("\\s+", "").contains("https://jazz.cerner.com"))) {
							result = true;
						}else if((desc.replaceAll("\\s+", "").contains("https://wiki.cerner.com"))){
							result = true;
						}
					}
					//check in comments
					if(false == result){
						Iterable<Comment> subComments = subJira.getComments();
						subComments.forEach(x -> {
//							rqmlink = context.getUtils().getMatchingString(x.getBody(),
//									getParameterMap().get("pattern"), 1);
							if ((x.getBody().replaceAll("\\s+", "").contains("https://jazz.cerner.com"))) {
								result = true;
							}else if((x.getBody().replaceAll("\\s+", "").contains("https://wiki.cerner.com"))){
								result = true;
							}
						});
					}
					//check in linksto section of subjira
					if(false == result) {
						 try {
								
								JsonNode subJiraContent = HttpBasicAuth.getIssueLinkResponseJira(context.getConfig().getJiraQueryConfiguration().getJiraServer(),context.getConfig(), subJira.getId());						
								subJiraContent.getArray().forEach(x->{
									 //check whether each linked issue has rqm link
									 if(context.getUtils().isPatternFoundInString(x.toString(), getParameterMap().get("pattern"))) {
										try {
											//get the object from the subJiracontent
											Map<String, Object> obj = parser.getRawObject(x.toString());
											LinkedHashMap objBody = (LinkedHashMap) obj.get("object");
											String url = objBody.get("url").toString();
											//if the url contains rqm link set it to sucess
											if (!url.isEmpty() && (url.contains("https://jazz.cerner.com"))) {
												result = true;
											}else if((url.contains("https://wiki.cerner.com"))&&(url.contains(MainJiraKey))){
												result = true;
											}
											
										}catch(IOException e) {}
										
									 } 
									
								 });
							 } catch (UnirestException | JSONException e) {
								e.printStackTrace();
					}
					}
					
					
				 } catch (URISyntaxException e) {
						e.printStackTrace();	
					
				 }
				

				if (result)
					info.setInfoType(Constants.ValidationInfoType.SUCCESS);
				else
					info.setInfoType(Constants.ValidationInfoType.FAILURE);
			
			}
			});
        return info;
    }

    @Override
    public void preServiceOperations(ValidationContext context) {
        // TODO Auto-generated method stub

    }

}