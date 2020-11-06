package com.cerner.hi.pitc.validators;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

public class CheckDevelopmentSubtask extends ValidatorService{
	@Override
	protected void populateValidArgs(Set<Parameter> parameter) {
		Parameter pattern = new Parameter("pattern", Constants.DataTypeForParams.STRING);
		Parameter excelColumn = new Parameter("excelColumnName", Constants.DataTypeForParams.STRING);
		parameter.add(excelColumn);
		parameter.add(pattern);
	}

	boolean flag1, flag2 = false;

	@Override
	public ValidationInfo serviceOperations(ValidationContext context) {

		ValidationInfo info = new ValidationInfo();
		info.setClazz(this.getClass());
		Issue jira = context.getJira();

		Iterable<Subtask> subTasks = jira.getSubtasks();
		subTasks.forEach(a -> {
			if (a.getSummary().toLowerCase().equals("development")) {
				context.getConfig().getJiraQueryConfiguration().setJiraKey(a.getIssueKey());
				JRJCIssueDAO jrcIssueDao = new JRJCIssueDAO();
				
				try {
					Issue subJira = jrcIssueDao.getIssue(context.getConfig().getJiraQueryConfiguration());
					if (subJira.getStatus().getName().equals("Closed")) {
						
						final String desc = subJira.getDescription();
						
						Iterable<Comment> subComments = subJira.getComments();
						// to check whether Engineer and SD has given +1
						checkPlusOneComment(subComments, context);		
						
						if (flag1 && flag2) {
							info.setInfoType(Constants.ValidationInfoType.SUCCESS);
						} else if(flag1 != flag2) {
							info.setInfoType(Constants.ValidationInfoType.PARTIAL);
						}else {
							info.setInfoType(Constants.ValidationInfoType.FAILURE);
						}
					} else {
						info.setInfoType(Constants.ValidationInfoType.NOTCLOSED);
					}
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}

			}
		});

		return info;
	}


	private void checkPlusOneComment(Iterable<Comment> subComments, ValidationContext context) {

		List<Comment> plusOneComments = StreamSupport.stream(subComments.spliterator(), false)
				.filter(i -> context.getUtils().isPatternFoundInString(i.getBody(), getParameterMap().get("pattern")))
				.collect(Collectors.toList());

		plusOneComments.forEach(x -> {
			if (x.getAuthor().getDisplayName().equals(context.getDeveloper().getDisplayName()))
				flag1 = true;
		});
		plusOneComments.forEach(x -> {
			if (x.getAuthor().getDisplayName().equals(context.getSolutionDesigner().getDisplayName()))
				flag2 = true;
		});
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
