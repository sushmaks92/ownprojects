package com.cerner.hi.pitc.validators;

import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.ChangelogGroup;
import com.atlassian.jira.rest.client.api.domain.ChangelogItem;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.cerner.hi.pitc.executor.ValidatorService;
import com.cerner.hi.pitc.model.Parameter;
import com.cerner.hi.pitc.model.ValidationContext;
import com.cerner.hi.pitc.model.ValidationInfo;
import com.cerner.hi.pitc.util.Constants;

public class ACUserValidatorService extends ValidatorService {
    @Override
    protected void populateValidArgs(Set<Parameter> parameter) {
        Parameter pattern = new Parameter("pattern", Constants.DataTypeForParams.STRING);
        Parameter ACPattern = new Parameter("ACPattern", Constants.DataTypeForParams.STRING);
        parameter.add(pattern);
        parameter.add(ACPattern);
        Parameter excelColumn = new Parameter("excelColumnName", Constants.DataTypeForParams.STRING);
        parameter.add(excelColumn);
    }

    @Override
    protected Constants.ResultVariables getResultVariable() {
        return Constants.ResultVariables.HAS_CLIENT_VIEW_DESC;
    }

    @Override
    public ValidationInfo serviceOperations(ValidationContext context) {
        ValidationInfo info = new ValidationInfo();
        info.setClazz(this.getClass());
        Issue jira = context.getJira();
        final String desc = getDescription(jira);
        Iterable<ChangelogGroup> changelog = jira.getChangelog();
        final String AC = context.getUtils().getMatchingString(desc, getParameterMap().get("ACPattern"), 2);
        DateTime dateTime = new DateTime();
        BasicUser changeUser = jira.getReporter();
        if(null != AC && !"".equals(AC)) {
        	for(ChangelogGroup changelogItem: changelog){
            	for(ChangelogItem change:changelogItem.getItems()){
                	if("description".equals(change.getField())){
                    	if(dateTime.isAfter(changelogItem.getCreated())&&change.getToString().contains(AC)){
                        	dateTime = changelogItem.getCreated();
                        	changeUser = changelogItem.getAuthor();

                    	}
                	}
            	}
        	}
        }
        BasicUser engineer = context.getDeveloper();
        DateTime commentDateTime = new DateTime(Long.MIN_VALUE);
        if(engineer!=null) {
            final String engineerID = engineer.getName();
            if(engineerID != null && !engineerID.equals("N/A")) {
                List<Comment> comments = context.getUtils().getPatternComments(engineerID, context.getJira(), getParameterMap().get("pattern"));
                boolean found = (!comments.isEmpty());
                if(found) {
                    for (Comment comment : comments) {
                        if (commentDateTime.isBefore(comment.getUpdateDate()))
                            commentDateTime = comment.getUpdateDate();
                    }
                }
            }
        }
        if(dateTime.isBefore(commentDateTime)){
            info.setInfoType(Constants.ValidationInfoType.SUCCESS);
        }
        return info;
    }

    private String getDescription(Issue jira) {
        String res = jira.getDescription();
        if(res==null||res.equals("")){
            res="N/A";
        }
        return res;
    }

    @Override
    public void preServiceOperations(ValidationContext context) {
        // TODO Auto-generated method stub

    }
}
