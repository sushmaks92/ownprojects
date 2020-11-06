package com.cerner.hi.pitc.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.cerner.hi.pitc.model.ValidationContext;

public class ValidatorServiceUtils {

	public List<Comment> getComments(Issue issue) {
		List<Comment> comments = new ArrayList<>();
		issue.getComments().forEach(i -> comments.add(i));
		return comments;
	}

	public Comment getMatchingComment(final Issue issue, final List<String> patternStr) {
		List<Comment> comments = getComments(issue);
		for (Comment comment : comments) {
			boolean found = isPatternFoundInString(getBodyForComment(comment), patternStr);
			if (found)
				return comment;
		}
		return null;
	}

	public String getBodyForComment(Comment comment) {
		return comment.getBody();
	}

	public String getAuthorIdForComment(Comment comment) {
		BasicUser author = null;
		if (comment != null) {
			author = comment.getAuthor();
			return author.getName();
		}
		return "";
	}

	public BasicUser getAuthorForComment(Comment comment) {
		BasicUser author = comment.getAuthor();
		return author;
	}

	// What is this for?
	public Object getFieldForComment(Object comment, String field) {
		LinkedHashMap<String, Object> commentContent = (LinkedHashMap<String, Object>) comment;
		if (null != commentContent)
			return commentContent.get(field);
		else
			return null;
	}

	public List<Pattern> createPatternForString(List<String> patternStrings) {
		List<Pattern> patterns = new ArrayList<>();
		patternStrings.forEach(tempPat -> patterns.add(Pattern.compile(tempPat)));
		return patterns;
	}

	public boolean isPatternCommentFound(final String authorId, final Issue issue, final List<String> patternStr) {
		boolean result = false;
		boolean checkPattern = false;
		List<Comment> comments = getComments(issue);
		for (Comment comment : comments) {
			if (authorId == null || authorId.equals("N/A")) {
				checkPattern = true;
			} else {
				String commentAuthor = getAuthorIdForComment(comment);
				if (commentAuthor.equals(authorId)) {
					checkPattern = true;
				}
			}
			if (checkPattern) {
				result = isPatternFoundInString(getBodyForComment(comment), patternStr);
				if (result)
					break;
			}
		}
		return result;
	}

	public boolean isPatternFoundInString(final String str, final List<String> patternStr) {
		boolean result = false;
		if (null != str) {
			List<Pattern> patterns = createPatternForString(patternStr);
			for (Pattern templatePattern : patterns) {
				if (templatePattern.matcher(str).find()) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	public String getMatchingString(final String str, final List<String> patternStr, final int group) {
		String returnStr = "";
		List<Pattern> patterns = createPatternForString(patternStr);
		for (Pattern templatePattern : patterns) {
			Matcher m = templatePattern.matcher(str);
			if (m.matches()) {
				returnStr = m.group(group);
				if (null != returnStr && returnStr.trim().equals("")) {
					break;
				}
			}
		}
		return returnStr;
	}

	public List<Comment> getPatternComments(final String authorId, final Issue issue, final List<String> patternStr) {
		boolean result = false;
		boolean checkPattern = false;
		List<Comment> comments = getComments(issue);
		List<Comment> comment1 = new ArrayList<>();
		for (Comment comment : comments) {
			checkPattern = false;
			if (authorId == null || authorId.equals("N/A")) {
				checkPattern = true;
			} else {
				String commentAuthor = getAuthorIdForComment(comment);
				if (commentAuthor.equals(authorId)) {
					checkPattern = true;
				}
			}
			if (checkPattern) {
				result = isPatternFoundInString(getBodyForComment(comment), patternStr);
				if (result) {
					comment1.add(comment);
				}
			}
		}
		return comment1;
	}

	public String getBasicDetailsExcelValues(IssueField data, String fieldName, boolean b) {
		StringBuilder strBuilder = new StringBuilder();
		if (true == b) {
			Object versions = data.getValue();
			if( null != versions) {
			JSONArray versionArray = (JSONArray) versions;
		
				try {

					for (int i = 0; i < versionArray.length(); i++) {
						strBuilder.append(versionArray.getJSONObject(i).get(fieldName.trim()).toString());
						strBuilder.append(",");
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			
			}
		} else if (false == b) {
			JSONObject versions = (JSONObject) data.getValue();
			try {
				strBuilder.append(versions.get(fieldName.trim()));
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		return strBuilder.toString();
	}
	
	
	public void getUpdatedStatusTime(Issue subJira, ValidationContext context) {
		context.setClosedUpdatedTime(null);
		context.setInProgressUpdatedTime(null);;
		subJira.getChangelog().forEach(x -> {
			x.getItems().forEach(k -> {
				//get latest jira open status time
				if ((k.getField().equals("status")) && (k.getToString().equals("In Progress"))) {
					if ((null == context.getInProgressUpdatedTime())
							|| (x.getCreated().compareTo(context.getInProgressUpdatedTime()) > 0)) {
						context.setInProgressUpdatedTime(x.getCreated());
					}
				}
				//get latest jira closed status time
				if ((k.getField().equals("status")) && (k.getToString().equals("Closed"))) {
					if ((null == context.getClosedUpdatedTime())
							|| (x.getCreated().compareTo(context.getClosedUpdatedTime()) > 0)) {
						context.setClosedUpdatedTime(x.getCreated());

					}
				}
			});
		});
		
		
	}
	

}