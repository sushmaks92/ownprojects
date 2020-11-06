package com.cerner.hi.pitc.JiraRoles;

import java.net.URISyntaxException;

import org.apache.commons.lang.StringUtils;

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Subtask;
import com.cerner.hi.pitc.model.ValidationContext;
import com.cerner.hi.pitc.repository.JRJCIssueDAO;

public class PopulateJiraUserRoles {

	public void getSubtasksRoles(Issue jira, ValidationContext context) {
		JRJCIssueDAO jrcIssueDao = new JRJCIssueDAO();
		Iterable<Subtask> subTasks = jira.getSubtasks();
		BasicUser basicUser = new BasicUser(null, null, null);
		subTasks.forEach(a -> {
			String subtaskname = a.getSummary();
			Issue subJira = null;

			if (subtaskname.toLowerCase().contains("req")) {
				context.getConfig().getJiraQueryConfiguration().setJiraKey(a.getIssueKey());
				try {
					subJira = jrcIssueDao.getIssue(context.getConfig().getJiraQueryConfiguration());
					context.setSolutionDesigner(subJira.getAssignee());
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			} else if (subtaskname.toLowerCase().contains("develop")) {
				context.getConfig().getJiraQueryConfiguration().setJiraKey(a.getIssueKey());
				try {
					subJira = jrcIssueDao.getIssue(context.getConfig().getJiraQueryConfiguration());
					context.setDeveloper(subJira.getAssignee());
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			} else if (-1 != StringUtils.indexOfAny(subtaskname.toLowerCase().replaceAll("\\s+", ""),
					new String[] { "testcase", "testcaseandcg" })) {

				context.getConfig().getJiraQueryConfiguration().setJiraKey(a.getIssueKey());
				try {
					subJira = jrcIssueDao.getIssue(context.getConfig().getJiraQueryConfiguration());
					context.setTester(subJira.getAssignee());

				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}

		});
		return;

	}
}
