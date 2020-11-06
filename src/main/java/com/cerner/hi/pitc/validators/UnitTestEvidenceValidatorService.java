package com.cerner.hi.pitc.validators;

import java.util.ArrayList;
import java.util.Set;

import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.cerner.hi.pitc.executor.ValidatorService;
import com.cerner.hi.pitc.model.Parameter;
import com.cerner.hi.pitc.model.ValidationContext;
import com.cerner.hi.pitc.model.ValidationInfo;
import com.cerner.hi.pitc.util.Constants;

public class UnitTestEvidenceValidatorService extends ValidatorService {

    Constants.ResultVariables returnVariable = null;
    String commentAuthor = null;

    @Override
    protected void populateValidArgs(Set<Parameter> parameter) {
        Parameter pattern = new Parameter("specificNames", Constants.DataTypeForParams.STRING);
        Parameter minimumNumber = new Parameter("minimumNumber", Constants.DataTypeForParams.INTEGER);
        Parameter excelColumn = new Parameter("excelColumnName", Constants.DataTypeForParams.STRING);
        parameter.add(excelColumn);
        parameter.add(pattern);
        parameter.add(minimumNumber);
    }

    @Override
    public ValidationInfo serviceOperations(ValidationContext context) {
        ValidationInfo info = new ValidationInfo();
        info.setClazz(this.getClass());
        Issue jira = context.getJira();
        final BasicUser engineer = context.getDeveloper();
        if(!"N/A".equals(engineer.getName())) {
            int minimumAttachments = 1;
            try{
                minimumAttachments = Integer.parseInt(getParameterMap().get("minimumNumber").get(0));
            }catch(Exception e) {
                //do Nothing
            }
            ArrayList<Attachment> attachments = new ArrayList<>();
            jira.getAttachments().forEach(i ->attachments.add(i));
            int numberOfAttachemnts = 0;
            if(null!=attachments) {
                for(Attachment attachment : attachments) {
                    BasicUser author = attachment.getAuthor();
                    String authorId = author.getName();
                    if(authorId.equals(context.getDeveloper().getName())) {
                        ++numberOfAttachemnts;
                        if(numberOfAttachemnts==minimumAttachments) {
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


    @Override
    protected Constants.ResultVariables getResultVariable() {
        // TODO Auto-generated method stub
        return Constants.ResultVariables.HAS_UT_EVIDENCE;
    }

}

