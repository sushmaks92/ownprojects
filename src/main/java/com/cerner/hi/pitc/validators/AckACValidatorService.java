package com.cerner.hi.pitc.validators;

import java.util.Set;

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.cerner.hi.pitc.executor.ValidatorService;
import com.cerner.hi.pitc.model.Parameter;
import com.cerner.hi.pitc.model.ValidationContext;
import com.cerner.hi.pitc.model.ValidationInfo;
import com.cerner.hi.pitc.util.Constants;

public class AckACValidatorService extends ValidatorService {

    @Override
    protected void populateValidArgs(Set<Parameter> parameter) {
        Parameter pattern = new Parameter("pattern", Constants.DataTypeForParams.STRING);
        Parameter excelColumn = new Parameter("excelColumnName", Constants.DataTypeForParams.STRING);
        parameter.add(pattern);
        parameter.add(excelColumn);
    }

    @Override
    public ValidationInfo serviceOperations(ValidationContext context) {
        ValidationInfo info = new ValidationInfo();
        info.setClazz(this.getClass());
        BasicUser user = context.getTester();
        if(context.getConfig().getTypeOfValidation()==Constants.ValidationType.HEALTHIEINTENT)
        	user = context.getJira().getAssignee();
        if(user!=null) {
            final String commentAuthor = user.getName();
            if(commentAuthor != null && !commentAuthor.equals("N/A")) {
                boolean found = context.getUtils().isPatternCommentFound(commentAuthor, context.getJira(), getParameterMap().get("pattern"));
                if(found)
                    info.setInfoType(Constants.ValidationInfoType.SUCCESS);
            }
        }
        return info;
    }

    @Override
    public void preServiceOperations(ValidationContext context) {

    }


    @Override
    protected Constants.ResultVariables getResultVariable() {
        return Constants.ResultVariables.ACKNOLEDGED_AC_TESTER;
    }

}
