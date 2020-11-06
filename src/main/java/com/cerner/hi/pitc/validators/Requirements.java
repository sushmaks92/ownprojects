package com.cerner.hi.pitc.validators;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.xml.stream.StreamFilter;

import org.joda.time.DateTime;

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

import com.cerner.hi.pitc.util.ValidatorServiceUtils;

public class Requirements extends ValidatorService {
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
		ValidatorServiceUtils utils = new ValidatorServiceUtils();
		info.setClazz(this.getClass());
		Issue jira = context.getJira();
		PopulateJiraUserRoles users = new PopulateJiraUserRoles();
		
		// to get all the user roles
		users.getSubtasksRoles(jira, context);

		Iterable<Subtask> subTasks = jira.getSubtasks();

		subTasks.forEach(a -> {
			if (a.getSummary().toLowerCase().contains("req")) {
				context.getConfig().getJiraQueryConfiguration().setJiraKey(a.getIssueKey());
				JRJCIssueDAO jrcIssueDao = new JRJCIssueDAO();
				try {
					Issue subJira = jrcIssueDao.getIssue(context.getConfig().getJiraQueryConfiguration());

					if (("Closed").equals(subJira.getStatus().getName())) {						
						//to get the updated status time
						utils.getUpdatedStatusTime(subJira, context);					
						Iterable<Comment> subComments = subJira.getComments();
						// to check whether Engineer and TA has given +1
						checkPlusOneComment(subComments, context);

						if (flag1 && flag2) {
							info.setInfoType(Constants.ValidationInfoType.SUCCESS);
						} else if (flag1 != flag2) {
							info.setInfoType(Constants.ValidationInfoType.PARTIAL);
						} else {
							info.setInfoType(Constants.ValidationInfoType.FAILURE);
						}

					} else {
						info.setInfoType(Constants.ValidationInfoType.NOTCLOSED);
					}
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		return info;
	}

	private void checkPlusOneComment(Iterable<Comment> subComments, ValidationContext context) {
//		List<String> patternToCheck=getParameterMap().get("pattern");
		List<Comment> plusOneComments = StreamSupport.stream(subComments.spliterator(), false)
				.filter(i -> context.getUtils().isPatternFoundInString(i.getBody(), getParameterMap().get("pattern")))
				.collect(Collectors.toList());

		plusOneComments.forEach(x -> {
			if ((x.getAuthor().getDisplayName().equals(context.getDeveloper().getDisplayName()))
					&& (x.getUpdateDate().isAfter(context.getInProgressUpdatedTime()))
					&& (null != context.getDeveloper().getDisplayName())
				//	&& (x.getUpdateDate().isBefore(context.getClosedUpdatedTime()))
					)
				flag1 = true;
		});
		plusOneComments.forEach(x -> {
			if ((x.getAuthor().getDisplayName().equals(context.getTester().getDisplayName()))
					&& (x.getUpdateDate().isAfter(context.getInProgressUpdatedTime()))
					&& (null != context.getTester().getDisplayName())
					//&& (x.getUpdateDate().isBefore(context.getClosedUpdatedTime()))
					)
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