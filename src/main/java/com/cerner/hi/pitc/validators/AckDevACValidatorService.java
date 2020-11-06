package com.cerner.hi.pitc.validators;

import java.util.Set;

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.cerner.hi.pitc.executor.ValidatorService;
import com.cerner.hi.pitc.model.Parameter;
import com.cerner.hi.pitc.model.ValidationContext;
import com.cerner.hi.pitc.model.ValidationInfo;
import com.cerner.hi.pitc.util.Constants;

public class AckDevACValidatorService extends ValidatorService {


    @Override
    protected void populateValidArgs(Set<Parameter> parameter) {
        Parameter pattern = new Parameter("pattern", Constants.DataTypeForParams.STRING);
        Parameter excelColumn = new Parameter("excelColumnName", Constants.DataTypeForParams.STRING);
        parameter.add(excelColumn);
        parameter.add(pattern);
    }

    @Override
    public ValidationInfo serviceOperations(ValidationContext context) {
        ValidationInfo info = new ValidationInfo();
        info.setClazz(this.getClass());
        final BasicUser engineer = context.getDeveloper();
        if(engineer!=null) {
            final String engineerID = engineer.getName();
            if(engineerID != null && !engineerID.equals("N/A")) {
                boolean found = context.getUtils().isPatternCommentFound(engineerID, context.getJira(), getParameterMap().get("pattern"));
                if(found)
                    info.setInfoType(Constants.ValidationInfoType.SUCCESS);
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
        return Constants.ResultVariables.ACKNOLEDGED_AC_DEV;
    }

}

