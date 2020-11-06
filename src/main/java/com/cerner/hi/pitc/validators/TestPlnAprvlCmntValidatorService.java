package com.cerner.hi.pitc.validators;

import java.util.Set;

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.cerner.hi.pitc.executor.ValidatorService;
import com.cerner.hi.pitc.model.Parameter;
import com.cerner.hi.pitc.model.ValidationContext;
import com.cerner.hi.pitc.model.ValidationInfo;
import com.cerner.hi.pitc.util.Constants;

public class TestPlnAprvlCmntValidatorService extends ValidatorService {

    @Override
    protected void populateValidArgs(Set<Parameter> parameter) {
        Parameter pattern = new Parameter("pattern", Constants.DataTypeForParams.STRING);
        parameter.add(pattern);
        Parameter excelColumn = new Parameter("excelColumnName", Constants.DataTypeForParams.STRING);
        parameter.add(excelColumn);
    }

    @Override
    protected Constants.ResultVariables getResultVariable() {
        return Constants.ResultVariables.HAS_TESTPLAN_APPROVAL;
    }

    @Override
    public ValidationInfo serviceOperations(ValidationContext context) {
        ValidationInfo info = new ValidationInfo();
        info.setClazz(this.getClass());
        final BasicUser user= context.getTester();
        if(!"N/A".equals(user.getName())) {
            final String testerId = user.getName();
            if (testerId != null) {
                Comment comment = context.getUtils().getMatchingComment(context.getJira(), getParameterMap().get("pattern"));
                String author = context.getUtils().getAuthorIdForComment(comment);
                if(author != testerId||author!="")
                    info.setInfoType(Constants.ValidationInfoType.SUCCESS);
            }
        }
        return info;
    }

    @Override
    public void preServiceOperations(ValidationContext context) {
        // TODO Auto-generated method stub

    }

}
