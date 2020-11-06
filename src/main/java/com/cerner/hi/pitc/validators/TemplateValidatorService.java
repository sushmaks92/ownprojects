package com.cerner.hi.pitc.validators;

import java.util.Set;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.cerner.hi.pitc.executor.ValidatorService;
import com.cerner.hi.pitc.model.Parameter;
import com.cerner.hi.pitc.model.ValidationContext;
import com.cerner.hi.pitc.model.ValidationInfo;
import com.cerner.hi.pitc.util.Constants;

public class TemplateValidatorService extends ValidatorService {

    @Override
    protected void populateValidArgs(Set<Parameter> parameterList) {
        Parameter pattern = new Parameter("pattern", Constants.DataTypeForParams.STRING);
        parameterList.add(pattern);
        Parameter excelColumn = new Parameter("excelColumnName", Constants.DataTypeForParams.STRING);
        parameterList.add(excelColumn);
    }

    @Override
    protected Constants.ResultVariables getResultVariable() {
        return Constants.ResultVariables.USED_TEMPLATE;
    }

    @Override
    public ValidationInfo serviceOperations(ValidationContext context) {
        ValidationInfo info = new ValidationInfo();
        info.setClazz(this.getClass());
        Issue jira = context.getJira();
        String desc = jira.getDescription();
        boolean result = context.getUtils().isPatternFoundInString(desc, getParameterMap().get("pattern"));
        if(result) {
            info.setInfoType(Constants.ValidationInfoType.SUCCESS);
        }
        return info;
    }

    @Override
    public void preServiceOperations(ValidationContext context) {
        // TODO Auto-generated method stub

    }

}
