package com.cerner.hi.pitc.validators;

import java.util.ArrayList;
import java.util.Set;

import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.cerner.hi.pitc.executor.ValidatorService;
import com.cerner.hi.pitc.model.Parameter;
import com.cerner.hi.pitc.model.ValidationContext;
import com.cerner.hi.pitc.model.ValidationInfo;
import com.cerner.hi.pitc.util.Constants;

public class TestArtifactValidatorService extends ValidatorService {

    @Override
    protected void populateValidArgs(Set<Parameter> parameter) {
        // TODO Auto-generated method stub
    	Parameter excelColumn = new Parameter("excelColumnName", Constants.DataTypeForParams.STRING);
        parameter.add(excelColumn);

    }

    @Override
    protected Constants.ResultVariables getResultVariable() {
        return Constants.ResultVariables.HAS_TEST_ARTIFACTS;
    }

    @Override
    public ValidationInfo serviceOperations(ValidationContext context) {
        ValidationInfo info = new ValidationInfo();
        info.setClazz(this.getClass());
        Issue jira = context.getJira();
        final BasicUser tester = context.getTester();
        if (!"N/A".equals(tester.getName())) {
            Comment comment = context.getUtils().getMatchingComment(jira, getParameterMap().get("pattern"));
            String author = context.getUtils().getAuthorIdForComment(comment);
            if (author!=tester.getName() && author!="") {
                info.setInfoType(Constants.ValidationInfoType.SUCCESS);
            }
            if (info.getInfoType() != Constants.ValidationInfoType.SUCCESS) {
                ArrayList<Attachment> attachments = new ArrayList<>();
                jira.getAttachments().forEach(i -> attachments.add(i));
                if (null != attachments) {
                    for (Attachment attachment : attachments) {
                        BasicUser  attachmentAuthor= attachment.getAuthor();
                        String authorId = attachmentAuthor.getName();
                        if (authorId.equals(tester.getName())) {
                            info.setInfoType(Constants.ValidationInfoType.SUCCESS);
                            break;
                        }
                    }
                }
            }
        }
        return info;
    }

    @Override
    public void preServiceOperations(ValidationContext context) {
        // TODO Auto-generated method stub

    }

}
