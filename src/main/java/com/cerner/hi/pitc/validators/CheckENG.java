package com.cerner.hi.pitc.validators;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
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

public class CheckENG extends ValidatorService {
	@Override
	protected void populateValidArgs(Set<Parameter> parameter) {
		Parameter pattern = new Parameter("pattern", Constants.DataTypeForParams.STRING);
		Parameter excelColumn = new Parameter("excelColumnName", Constants.DataTypeForParams.STRING);
		parameter.add(excelColumn);
		parameter.add(pattern);
	}

	boolean flag1, result = false;
	String rqmtitle = "";
	String rqmlink = "";
	String rqmString = "";

	@Override
	public ValidationInfo serviceOperations(ValidationContext context) {

		ValidationInfo info = new ValidationInfo();
		info.setClazz(this.getClass());
		Issue jira = context.getJira();
		Iterable<Subtask> subTasks = jira.getSubtasks();
		subTasks.forEach(a -> {

			if(a.getSummary().toLowerCase().replaceAll("\\s+", "").contains("eng18.02")) {

				context.getConfig().getJiraQueryConfiguration().setJiraKey(a.getIssueKey());
				JRJCIssueDAO jrcIssueDao = new JRJCIssueDAO();
				JsonParser parser = new JsonParserImpl();
				try {
					Issue subJira = jrcIssueDao.getIssue(context.getConfig().getJiraQueryConfiguration());
					if (("Closed").equals(subJira.getStatus().getName())) {
						//linked issues
						JsonNode subJiraContent = HttpBasicAuth.getIssueLinkResponseJira(context.getConfig().getJiraQueryConfiguration().getJiraServer(),context.getConfig(), subJira.getId());						
						subJiraContent.getArray().forEach(x->{
							 //check whether each linked issue has rqm link
							 if(x.toString().contains("rqm")) {
								try {
									//get the object from the subJiracontent
									Map<String, Object> obj = parser.getRawObject(x.toString());
									LinkedHashMap objBody = (LinkedHashMap) obj.get("object");
									String title=objBody.get("title").toString();
									String url = objBody.get("url").toString();
									if((title.toLowerCase().contains("rqm"))&&(url.contains("https://jazz.cerner.com:"))) {
										result = true;					
									}
								}catch(IOException e) {}
								
							 } 							
						 });
						//check description
						final String desc = subJira.getDescription();
						if ((false == result)&&(null != desc) && (!"".equals(desc))) {
							String desc1 = desc.replaceAll("\\s+", "");
							//rqm = context.getUtils().getMatchingString(desc1, getParameterMap().get("pattern"), 1);

							if (desc1.contains("https://jazz.cerner.com:")) {
								result = true;
							}

						}
						//check in comments
						if(false == result) {
				
							Iterable<Comment> subComments = subJira.getComments();
							subComments.forEach(x -> {
								if (x.getBody().contains("https://jazz.cerner.com:")) 
									result = true;

							});
						}
			

						if (result)
							info.setInfoType(Constants.ValidationInfoType.SUCCESS);
						else
							info.setInfoType(Constants.ValidationInfoType.FAILURE);
					} else {
						info.setInfoType(Constants.ValidationInfoType.NOTCLOSED);
					}

					// context.setReqId(reqID);

				} catch (URISyntaxException | UnirestException | JSONException e) {
					e.printStackTrace();
				}

			}else {
				info.setInfoType(Constants.ValidationInfoType.NOTAPPLICABLE);
			}
		});

		return info;
	}

	@Override
	protected Constants.ResultVariables getResultVariable() {
		return Constants.ResultVariables.ACKNOLEDGED_AC_DEV;
	}

	@Override
	public void preServiceOperations(ValidationContext context) {
		// TODO Auto-generated method stub

	}
}
