package com.cerner.hi.pitc.validators;

import java.util.Set;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.cerner.hi.pitc.executor.ValidatorService;
import com.cerner.hi.pitc.model.Parameter;
import com.cerner.hi.pitc.model.ValidationContext;
import com.cerner.hi.pitc.model.ValidationInfo;
import com.cerner.hi.pitc.util.Constants;

public class ClientViewableDescValidator extends ValidatorService {

    @Override
    protected void populateValidArgs(Set<Parameter> parameter) {
        Parameter pattern = new Parameter("pattern", Constants.DataTypeForParams.STRING);
        Parameter synonym = new Parameter("synonym", Constants.DataTypeForParams.STRING);
        parameter.add(pattern);
        parameter.add(synonym);
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
        final String desc = jira.getDescription();
        String cvd = context.getUtils().getMatchingString(desc, getParameterMap().get("pattern"), 2);
        if (null != cvd || cvd != "") {
            boolean result = context.getUtils().isPatternFoundInString(cvd, getParameterMap().get("synonym"));
            if (!result)
                info.setInfoType(Constants.ValidationInfoType.SUCCESS);
        }

        return info;
    }

    @Override
    public void preServiceOperations(ValidationContext context) {
        // TODO Auto-generated method stub

    }
}