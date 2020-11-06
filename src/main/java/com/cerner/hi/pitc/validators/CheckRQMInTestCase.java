package com.cerner.hi.pitc.validators;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONException;

import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Subtask;
import com.cerner.hi.pitc.JiraRoles.PopulateJiraUserRoles;
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
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;

public class CheckRQMInTestCase extends ValidatorService {
	@Override
	protected void populateValidArgs(Set<Parameter> parameter) {
		Parameter pattern = new Parameter("pattern", Constants.DataTypeForParams.STRING);
		Parameter excelColumn = new Parameter("excelColumnName", Constants.DataTypeForParams.STRING);
		parameter.add(excelColumn);
		parameter.add(pattern);
	}

	boolean flag1, result = false;
	String rqmlink = "";

	@Override
	public ValidationInfo serviceOperations(ValidationContext context) {

		ValidationInfo info = new ValidationInfo();
		info.setClazz(this.getClass());
		Issue jira = context.getJira();
		JsonParser parser = new JsonParserImpl();
		Iterable<Subtask> subTasks = jira.getSubtasks();
		subTasks.forEach(a -> {
			if (-1 != StringUtils.indexOfAny(a.getSummary().toLowerCase().replaceAll("\\s+", ""),
					new String[] { "testcase", "testcaseandcg" })) {
				context.getConfig().getJiraQueryConfiguration().setJiraKey(a.getIssueKey());
				JRJCIssueDAO jrcIssueDao = new JRJCIssueDAO();

				try {
					Issue subJira = jrcIssueDao.getIssue(context.getConfig().getJiraQueryConfiguration());
					if (("Closed").equals(subJira.getStatus().getName())) {
						final String desc = subJira.getDescription();
						//description
						if (null != desc && !"".equals(desc)) {
//							rqmlink = context.getUtils().getMatchingString(desc, getParameterMap().get("pattern"), 1);
//							if (!rqmlink.isEmpty() && (rqmlink.contains("https://jazz.cerner.com"))) {
//								result = true;
//							}
							if ((desc.replaceAll("\\s+", "").contains("https://jazz.cerner.com"))) {
								result = true;
							}
						}
						//comments
						if (false == result) {
							Iterable<Comment> subComments = subJira.getComments();
							subComments.forEach(x -> {
//								rqmlink = context.getUtils().getMatchingString(x.getBody(),
//										getParameterMap().get("pattern"), 1);
//								if (!rqmlink.isEmpty() && (rqmlink.contains("https://jazz.cerner.com"))) {
//									result = true;
//								}
								if ((x.getBody().replaceAll("\\s+", "").contains("https://jazz.cerner.com"))) {
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
												}
												
											}catch(IOException e) {}
											
										 } 
										
									 });
								 } catch (UnirestException | JSONException e) {
									e.printStackTrace();
						}
						}

						if (result)
							info.setInfoType(Constants.ValidationInfoType.SUCCESS);
						else
							info.setInfoType(Constants.ValidationInfoType.FAILURE);
					} else {
						info.setInfoType(Constants.ValidationInfoType.NOTCLOSED);
					}

					// context.setReqId(reqID);

				} catch (URISyntaxException e) {
					e.printStackTrace();
				}

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
