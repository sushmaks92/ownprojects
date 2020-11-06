package com.cerner.hi.pitc.executor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONArray;
import java.lang.Object;
import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.cerner.hi.pitc.contract.ResultGenerator;
import com.cerner.hi.pitc.contract.ServiceHandler;
import com.cerner.hi.pitc.model.BasicDetails;
import com.cerner.hi.pitc.model.ValidationContext;
import com.cerner.hi.pitc.model.WorkerProperties;
import com.cerner.hi.pitc.model.result.JIRACompliance;
import com.cerner.hi.pitc.model.result.ValidatorServicesResult;

import com.cerner.hi.pitc.util.Constants;
import com.cerner.hi.pitc.util.ValidatorServiceUtils;
import com.google.gson.Gson;

public class ValidatorServiceHandler implements ServiceHandler {

	WorkerProperties properties;

	public ValidatorServiceHandler(WorkerProperties properties) {
		this.properties = properties;
	}

	@Override
	public JIRACompliance handleAllServices() {
		Issue issue = properties.getIssue();
		ValidationContext context = new ValidationContext();
		context.setJiraKey(properties.getJiraKey());
		context.setJira(issue);
		// adding TypeOfValidation to setter in context
		context.setConfig(properties.getConfig());
		ValidatorServicesResult serviceResult = new ValidatorServicesResult();
		serviceResult.getBasicDetails().setJiraNumber(context.getJiraKey());

		try {
			populateBasicDetails(serviceResult, context);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (isValidationRequired(serviceResult.getBasicDetails())) {
			for (ValidatorService service : properties.getValidatorServices()) {
				try {
					service.execute(context);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			serviceResult.setHasBeenValidated(true);
			populateServicesResult(serviceResult, context);
		}
		ResultGenerator generator = new ResultGeneratorImpl();
		return generator.populateResultPool(serviceResult);

	}

	private boolean isValidationRequired(BasicDetails details) {
		boolean validate = true;
		if (details.getStatus().equals(Constants.JIRAStatus.Submitted.toString())) {
			validate = false;
		} else {
			for (Constants.IssueTypes type : Constants.IssueTypes.values()) {
				if (details.getIssueType().equals(type.getIssueType())) {
					validate = false;
				}
			}
		}
		if (validate) {
			for (Constants.JIRAComponentsToIgnore comp : Constants.JIRAComponentsToIgnore.values()) {
				if (details.getComponents().contains(comp.getComponemt())) {
					validate = false;
					break;
				}
			}
		}
		return validate;
	}

	private void populateServicesResult(ValidatorServicesResult result, ValidationContext context) {
		result.getValidationResultMap().putAll(context.getResultMap());
		result.getBasicDetails().setSme(context.getSme());
		result.getBasicDetails().setReqId(context.getReqId());
	}

	private void populateBasicDetails(ValidatorServicesResult result, ValidationContext context)
			throws URISyntaxException {
		Issue jira = context.getJira();
		BasicUser user = null;
		ValidatorServiceUtils utils =new ValidatorServiceUtils();
		
		result.getBasicDetails().setJiraSummary(jira.getSummary());
		//togetEpic		
		IssueField epic = jira.getField(Constants.JiraFieldMap.EPIC.getFieldName());
		if((null != epic.getValue()) && (null != epic)) {
			result.getBasicDetails().setEpic(epic.getValue().toString());			
		}else {result.getBasicDetails().setEpic(" ");	}
		//
		
		// to get planned versions of jiras
		IssueField plannedVersion = jira.getField(Constants.JiraFieldMap.PLANNEDVERSIONS.getFieldName());
		if(null != plannedVersion) {
			result.getBasicDetails().setPlannedVersion(utils.getBasicDetailsExcelValues(plannedVersion,"name",true));		
		}else {result.getBasicDetails().setPlannedVersion("  ");	}
		//
		
		//get the fix versions
		Iterable<Version> fixVersions = jira.getFixVersions();
		
			StringBuilder strBuilder = new StringBuilder();	
			fixVersions.forEach(x -> {
				strBuilder.append(x.getName().trim().toString());
				strBuilder.append(',');
			});
			result.getBasicDetails().setFixedVersions(strBuilder.toString());
		
		
		
		//
		
		//get Solutiondetail
		IssueField solDel = jira.getField(Constants.JiraFieldMap.SOLUTIONDETAIL.getFieldName());
		if(null != solDel.getValue()) {
			JSONArray temp=(JSONArray)solDel.getValue();			
			try {
				result.getBasicDetails().setSolutionDetail(temp.get(0).toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else {result.getBasicDetails().setSolutionDetail(" ");	}
		//
		
		//getJiragroup
		IssueField jiraGrp = jira.getField(Constants.JiraFieldMap.JIRAGROUP.getFieldName());
		if(null != jiraGrp.getValue()) {
			JSONArray temp=(JSONArray)jiraGrp.getValue();	
			try {
				result.getBasicDetails().setJiraGroup(temp.get(0).toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else {result.getBasicDetails().setJiraGroup(" ");	}
		//
		
		//basic component
		ArrayList<String> setComponents = new ArrayList<>();
		Iterable<BasicComponent> components = jira.getComponents();
		for (BasicComponent component : components) {
			setComponents.add(component.getName().toString());
		}
		result.getBasicDetails().setComponents(setComponents);
		//
		
		//clientViewable
		IssueField clientView = jira.getField(Constants.JiraFieldMap.CLIENT_VIEWABLE.getFieldName());
		if(null != clientView) {
			result.getBasicDetails().setClientViewable(utils.getBasicDetailsExcelValues(clientView, "value", false));
		}else {result.getBasicDetails().setClientViewable("NA");	}
		//
		
	

		user = jira.getReporter();	
		result.getBasicDetails().setReporter(user);
		user = jira.getAssignee();
		result.getBasicDetails().setAssignee(user);
	//	user=getUser(jira, Constants.JiraFieldMap.ASSIGNEE.getFieldName()).orElse(new BasicUser(null, null, null));
		//result.getBasicDetails().setAssignee(user);
		
		user = getUser(jira, Constants.JiraFieldMap.ENGINEER.getFieldName()).orElse(new BasicUser(null, null, null));
		result.getBasicDetails().setDeveloper(user);
		context.setDeveloper(user);
		user = getUser(jira, Constants.JiraFieldMap.TESTER.getFieldName()).orElse(new BasicUser(null, null, null));
		result.getBasicDetails().setTester(user);
		context.setTester(user);
		user = getUser(jira, Constants.JiraFieldMap.SOLUTIONDESIGNER.getFieldName())
				.orElse(new BasicUser(null, null, null));
		result.getBasicDetails().setStrategist(user);
		result.getBasicDetails().setStatus(jira.getStatus().getName());
		//to get the resolution of the jira
		try {
			String reso = jira.getResolution().getName();
			if(null != reso) {result.getBasicDetails().setResolution(reso);}else {result.getBasicDetails().setResolution(" ");}
		}catch(NullPointerException e) {}
		
	
		
		result.getBasicDetails().setIssueType(jira.getIssueType().getName());
		
	}

	private Optional<BasicUser> getUser(Issue jira, String fieldname) throws URISyntaxException {
		IssueField field = jira.getField(fieldname);
		Optional<BasicUser> optional = Optional.empty();
		if (null != field.getValue()) {
			BasicUser user = (User) parseJsontoObject(field.getValue().toString(), User.class);
			optional = Optional.ofNullable(user);
		}
		return optional;
	}

	private Object parseJsontoObject(String jsonStr, Class returnType) {
		Gson gson = new Gson();
		jsonStr = jsonStr.replace("avatarUrls", "avatarUris");
		return gson.fromJson(jsonStr, User.class);
	}
}
