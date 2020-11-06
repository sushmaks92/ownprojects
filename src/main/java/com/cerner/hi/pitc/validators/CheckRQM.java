package com.cerner.hi.pitc.validators;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang.StringUtils;

import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Subtask;
import com.cerner.hi.pitc.JiraRoles.PopulateJiraUserRoles;
import com.cerner.hi.pitc.executor.ValidatorService;
import com.cerner.hi.pitc.model.Parameter;
import com.cerner.hi.pitc.model.ValidationContext;
import com.cerner.hi.pitc.model.ValidationInfo;
import com.cerner.hi.pitc.repository.JRJCIssueDAO;
import com.cerner.hi.pitc.util.Constants;
import com.cerner.hi.pitc.util.Constants.ResultVariables;

public class CheckRQM extends ValidatorService{
	@Override
	protected void populateValidArgs(Set<Parameter> parameter) {
		Parameter pattern = new Parameter("pattern", Constants.DataTypeForParams.STRING);
		Parameter excelColumn = new Parameter("excelColumnName", Constants.DataTypeForParams.STRING);
		parameter.add(excelColumn);
		parameter.add(pattern);
	}

	boolean flag1,result = false;
	String rqmtitle ="";
	String rqmlink = "";
	String rqmString = "";
	@Override
	public ValidationInfo serviceOperations(ValidationContext context) {

		ValidationInfo info = new ValidationInfo();
		info.setClazz(this.getClass());
		Issue jira = context.getJira();
		
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
			
					        if(null != desc && !"".equals(desc)) {
					        	rqmlink = context.getUtils().getMatchingString(desc, getParameterMap().get("pattern"), 1);
					        	if(!rqmlink.isEmpty() && (rqmlink.contains("https://jazz.cerner.com"))) {					        				   
					        			result=true;
					        	}
					        }
					       
						   					        
						         if(("".equals(rqmlink))) {
						        	Iterable<Comment> subComments = subJira.getComments();
						        	subComments.forEach(x->{
						        		rqmlink = context.getUtils().getMatchingString(x.getBody(), getParameterMap().get("pattern"), 1);
							        	if(!rqmlink.isEmpty() && (rqmlink.contains("https://jazz.cerner.com"))) {					        				   
							        			result=true;
							        	}
						        	});
						        }
						        
						            if(result)
						                info.setInfoType(Constants.ValidationInfoType.SUCCESS);
						            else
						            	info.setInfoType(Constants.ValidationInfoType.FAILURE);
						        }   
					else {
						info.setInfoType(Constants.ValidationInfoType.NOTCLOSED);
					}
						  
					      
					        //context.setReqId(reqID);
					 
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
