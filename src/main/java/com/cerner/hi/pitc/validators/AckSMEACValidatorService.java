package com.cerner.hi.pitc.validators;

import java.util.ArrayList;
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

public class AckSMEACValidatorService extends ValidatorService {

    Constants.ResultVariables returnVariable = null;
    String commentAuthor = null;

    @Override
    protected void populateValidArgs(Set<Parameter> parameter) {
        Parameter pattern = new Parameter("pattern", Constants.DataTypeForParams.STRING);
        parameter.add(pattern);
        Parameter userType =new Parameter("primaryPattern", Constants.DataTypeForParams.STRING);
        parameter.add(userType);
        Parameter acceptanceCriteria = new Parameter("ACPattern", Constants.DataTypeForParams.STRING);
        parameter.add(acceptanceCriteria);
        Parameter excelColumn = new Parameter("excelColumnName", Constants.DataTypeForParams.STRING);
        parameter.add(excelColumn);
    }

    @Override
    public ValidationInfo serviceOperations(ValidationContext context) {
        ValidationInfo info = new ValidationInfo();
        Issue jira = context.getJira();
        info.setClazz(this.getClass());
       /** ArrayList<Comment> comments = new ArrayList<>();
        jira.getComments().forEach(i -> comments.add(i));
        for(Comment comment : comments) {
            final String body = comment.getBody();
            boolean matched = context.getUtils().isPatternFoundInString(body, getParameterMap().get("primaryPattern"));
            if(matched) {
                BasicUser sme = comment.getAuthor();
                if(checkIfSME(sme, context)) {
                    context.setSme(sme);
                    info.setInfoType(Constants.ValidationInfoType.SUCCESS);
                    break;
                }
            }
        }
        if(null==context.getSme()) {
            for(Comment comment : comments) {
                final String body = context.getUtils().getBodyForComment(comment);
                boolean matched = context.getUtils().isPatternFoundInString(body, getParameterMap().get("pattern"));
                if(matched) {
                    BasicUser author = comment.getAuthor();
                    boolean isSME = checkIfSME(author,context);
                    if(isSME) {
                        context.setSme(author);
                        info.setInfoType(Constants.ValidationInfoType.SUCCESS);
                        break;
                    }
                }
            }
        }**/
        List<Comment> smeComments = context.getUtils().getPatternComments(null, jira, getParameterMap().get("primaryPattern"));
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
        if(checkIfSME(changeUser, context)) {
        	context.setSme(changeUser);
            info.setInfoType(Constants.ValidationInfoType.SUCCESS);
        }
        else if(!smeComments.isEmpty()) {
        	DateTime latestSmeDate = new DateTime(Long.MIN_VALUE);
        	BasicUser latestSme = jira.getReporter();
        	for(Comment comment : smeComments) {
        		if (latestSmeDate.isBefore(comment.getUpdateDate())) {
        			latestSmeDate = comment.getUpdateDate();
        			latestSme = comment.getAuthor();
        		}
        	}
            if(latestSme == changeUser) {
            	context.setSme(latestSme);
                info.setInfoType(Constants.ValidationInfoType.SUCCESS);
            }
            else {
            	if(latestSmeDate.isAfter(dateTime)) {
            		context.setSme(latestSme);
                    info.setInfoType(Constants.ValidationInfoType.SUCCESS);
            	}
            }
        }
        else if(null == context.getSme()) {
        	List<Comment> AcComments = context.getUtils().getPatternComments(null, jira, getParameterMap().get("pattern"));
        	for(Comment comment:AcComments) {
        		boolean isSME = checkIfSME(comment.getAuthor(),context);
        		if(isSME) {
        			DateTime commentDate = comment.getUpdateDate();
        			if(commentDate.isAfter(dateTime)) {
        				context.setSme(comment.getAuthor());
                        info.setInfoType(Constants.ValidationInfoType.SUCCESS);
        			}
        		}
        	}
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

    private boolean checkIfSME(BasicUser user, ValidationContext context) {
        if(user.equals(context.getDeveloper()) || user.equals(context.getTester()))
            return false;
        return true;
    }


    @Override
    public void preServiceOperations(ValidationContext context) {
        // TODO Auto-generated method stub

    }


    @Override
    protected Constants.ResultVariables getResultVariable() {
        return Constants.ResultVariables.ACKNOLEDGED_AC_SME;
    }

}
