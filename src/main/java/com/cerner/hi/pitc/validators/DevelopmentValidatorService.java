package com.cerner.hi.pitc.validators;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.ChangelogGroup;
import com.atlassian.jira.rest.client.api.domain.ChangelogItem;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Resolution;
import com.atlassian.jira.rest.client.api.domain.Status;
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
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;

public class DevelopmentValidatorService extends ValidatorService {
	public String crucibleId = "";

	@Override
	protected void populateValidArgs(Set<Parameter> parameter) {
		Parameter pattern = new Parameter("server", Constants.DataTypeForParams.STRING);
		parameter.add(pattern);
		Parameter excelColumn = new Parameter("excelColumnName", Constants.DataTypeForParams.STRING);
		parameter.add(excelColumn);
	}

	@Override
	protected ResultVariables getResultVariable() {
		return Constants.ResultVariables.CODE_REVIEW;
	}

	@Override
	public ValidationInfo serviceOperations(ValidationContext context) {
		ValidationInfo info = new ValidationInfo();
		info.setClazz(this.getClass());
		String jiraKey = context.getJira().getKey();
		Issue jira = context.getJira();
		Set<String> allowedStates = new HashSet<>();
		allowedStates.add("Closed");

		JsonParser parser = new JsonParserImpl();
		Iterable<Subtask> subTasks = jira.getSubtasks();
		subTasks.forEach(st -> {
			if (st.getSummary().toLowerCase().replaceAll("\\s+", "").contains("development")) {
				if ((("Closed").equals(st.getStatus().getName()))) {
					context.getConfig().getJiraQueryConfiguration().setJiraKey(st.getIssueKey());
					JRJCIssueDAO jrcIssueDao = new JRJCIssueDAO();
					try {
						Issue subJira = jrcIssueDao.getIssue(context.getConfig().getJiraQueryConfiguration());
						Status status = subJira.getStatus();
						String SubTaskState = status.getName();

						if (status.getName().equals("Closed")) {
							DateTime SubJiraClosedDate = new DateTime();
							DateTime crucibleDate = new DateTime();
							String state = "";

							String commentBody = "";
							Iterable<ChangelogGroup> changelog = subJira.getChangelog();
							for (ChangelogGroup changelogItem : changelog) {
								for (ChangelogItem change : changelogItem.getItems()) {
									if ("Closed".equals(change.getToString())) {
										SubJiraClosedDate = changelogItem.getCreated();

									}
								}
							}
							// to check issuelinks of mainJira
							try {
								JsonNode jiraContent = HttpBasicAuth.getIssueLinkResponseJira(
										context.getConfig().getJiraQueryConfiguration().getJiraServer(),
										context.getConfig(), jira.getId());
								jiraContent.getArray().forEach(x -> {
									// check whether each linked issue has cruciblelink/github
									if (context.getUtils().isPatternFoundInString(x.toString(),
											getParameterMap().get("pattern3"))) {
										try {
											// get the object from the jiracontent
											Map<String, Object> obj = parser.getRawObject(x.toString());
											LinkedHashMap objBody = (LinkedHashMap) obj.get("object");
											String url = objBody.get("url").toString();

											// if the url contains github link
											if ((url.contains("https://github"))) {
												info.setInfoType(Constants.ValidationInfoType.SUCCESS);

											}

											// if url contains crucible link, fetch all the attachments and check for
											// techdoc attachment
											else if ((Constants.ValidationInfoType.SUCCESS != info.getInfoType())
													&& (url.contains("https://crucible"))) {
												System.out.println(url);
												crucibleId = url.substring(url.indexOf("cru/") + 4, url.length());
												info.setInfoType(Constants.ValidationInfoType.SUCCESS);
											}

										} catch (IOException e) {
										}
									}

								});
							} catch (UnirestException | JSONException e) {
							}
							// end of MainJira linksto checks

							// description of subjira
							String Description = subJira.getDescription();
							// crucible link in description
							if ((context.getUtils().isPatternFoundInString(Description,
									getParameterMap().get("pattern"))) && (Description != "")) {
								crucibleId = Description.substring(Description.indexOf("cru/") + 4,
										Description.length());
								info.setInfoType(Constants.ValidationInfoType.SUCCESS);
							}

							// comments of subjira
							subJira.getComments();
							Comment comment = context.getUtils().getMatchingComment(subJira,
									getParameterMap().get("pattern"));
							if (comment != null) {
								commentBody = comment.getBody();
								if ((context.getUtils().isPatternFoundInString(commentBody,
										getParameterMap().get("pattern")))
										&& (info.getInfoType() != Constants.ValidationInfoType.SUCCESS)) {
									crucibleId = commentBody.substring(commentBody.indexOf("cru/") + 4,
											commentBody.length() - 1);
									info.setInfoType(Constants.ValidationInfoType.SUCCESS);
								}
							}
							// check linksto Section of the subJira
							if (info.getInfoType() != Constants.ValidationInfoType.SUCCESS) {
								try {
									JsonNode subJiraContent = HttpBasicAuth.getIssueLinkResponseJira(
											context.getConfig().getJiraQueryConfiguration().getJiraServer(),
											context.getConfig(), subJira.getId());
									subJiraContent.getArray().forEach(x -> {
										// check whether each linked issue crucible link
										if (context.getUtils().isPatternFoundInString(x.toString(),
												getParameterMap().get("pattern3"))) {

											try {
												// get the object from the jiracontent
												Map<String, Object> obj = parser.getRawObject(x.toString());
												LinkedHashMap objBody = (LinkedHashMap) obj.get("object");
												String url = objBody.get("url").toString();

												// if url contains crucible link,
												if (url.contains("https://crucible")) {
													System.out.println(url);
													crucibleId = url.substring(url.indexOf("cru/") + 4,
															url.length() - 1);
													info.setInfoType(Constants.ValidationInfoType.SUCCESS);
												} // if the url contains github link
												else if ((url.contains("https://github"))) {
													info.setInfoType(Constants.ValidationInfoType.SUCCESS);

												}

											} catch (IOException e) {
											}
										}

									});
								} catch (UnirestException | JSONException e) {
								}

							} // endof linksto Section of subJira

							if (info.getInfoType() == Constants.ValidationInfoType.SUCCESS) {

								for (final String Crucibleserver : context.getConfig().getCrucibleConfiguration()
										.getCrucibleServers()) {
									String CrucibleContent = "{\"reviewData\":["
											+ HttpBasicAuth.getQueryResponseCrucibleServer(Crucibleserver,
													context.getConfig(), crucibleId)
											+ "]}";
									Object parsedJiraContent = parser.getRawObject(CrucibleContent);
									ArrayList<Object> reviews = new ArrayList<>();
									reviews = (ArrayList<Object>) ((LinkedHashMap) parsedJiraContent).get("reviewData");
									for (Object o : reviews) {
										LinkedHashMap<String, Object> review = (LinkedHashMap<String, Object>) o;
										state = (String) review.get("state");
										if ("Closed".equals(state)) {
											break;
										}
										info.setInfo(Constants.ValidationInfoType.FAILURE);

									}

								}

								if ((info.getInfoType() == Constants.ValidationInfoType.SUCCESS)
										&& ("Closed".equals(state))) {
									for (final String Crucibleserver : context.getConfig().getCrucibleConfiguration()
											.getCrucibleServers()) {
										String CrucibleContent = "{\"reviewData\":["
												+ HttpBasicAuth.getQueryResponseCrucibleServer(Crucibleserver,
														context.getConfig(), crucibleId)
												+ "]}";
										Object parsedJiraContent = parser.getRawObject(CrucibleContent);
										ArrayList<Object> reviews = new ArrayList<>();
										reviews = (ArrayList<Object>) ((LinkedHashMap) parsedJiraContent)
												.get("reviewData");
										for (Object O : reviews) {
											LinkedHashMap<String, Object> Cruciblereview = (LinkedHashMap<String, Object>) O;
											String crucibleCloseDate = (String) Cruciblereview.get("closeDate");
											crucibleDate = DateTime.parse(crucibleCloseDate);
										}
										if (SubJiraClosedDate.isBefore(crucibleDate))
											info.setInfo(Constants.ValidationInfoType.FAILURE);
									}
								}

							}

							// github link in description
							if ((context.getUtils().isPatternFoundInString(Description,
									getParameterMap().get("pattern2"))) && (Description != "")) {
								info.setInfoType(Constants.ValidationInfoType.SUCCESS);
							}
							// github link in comments
							if (Constants.ValidationInfoType.SUCCESS != info.getInfoType()) {
								Comment comment1 = context.getUtils().getMatchingComment(subJira,
										getParameterMap().get("pattern2"));
								if (comment1 != null) {
									commentBody = comment1.getBody();
									if (context.getUtils().isPatternFoundInString(commentBody,
											getParameterMap().get("pattern2"))) {
										info.setInfoType(Constants.ValidationInfoType.SUCCESS);
									}
								}
							}

						}
					} catch (URISyntaxException | UnirestException | IOException e) {
						e.printStackTrace();
					}
				} else {
					info.setInfoType(Constants.ValidationInfoType.NOTCLOSED);
				}
			}

		});
		return info;
	}

	@Override
	public void preServiceOperations(ValidationContext context) {

	}

}
