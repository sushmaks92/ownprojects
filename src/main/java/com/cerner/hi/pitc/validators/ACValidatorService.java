package com.cerner.hi.pitc.validators;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.cerner.hi.pitc.executor.ValidatorService;
import com.cerner.hi.pitc.model.Parameter;
import com.cerner.hi.pitc.model.ValidationContext;
import com.cerner.hi.pitc.model.ValidationInfo;
import com.cerner.hi.pitc.util.Constants;

@Component
public class ACValidatorService extends ValidatorService {

    @Override
    protected void populateValidArgs(Set<Parameter> parameter) {
        Parameter pattern = new Parameter("pattern", Constants.DataTypeForParams.STRING);
        Parameter synonym = new Parameter("synonym", Constants.DataTypeForParams.STRING);
        Parameter excelColumn = new Parameter("excelColumnName", Constants.DataTypeForParams.STRING);
        parameter.add(pattern);
        parameter.add(synonym);
        parameter.add(excelColumn);
    }

    @Override
    protected Constants.ResultVariables getResultVariable() {
        return Constants.ResultVariables.HAS_AC;
    }

    @Override
    public ValidationInfo serviceOperations(ValidationContext context) {
        ValidationInfo info = new ValidationInfo();
        info.setClazz(this.getClass());
        Issue jira = context.getJira();
        final String desc = getDescription(jira);
        final String AC = context.getUtils().getMatchingString(desc, getParameterMap().get("pattern"), 2);
        if (!"".equals(AC.trim())) {
            boolean result = context.getUtils().isPatternFoundInString(AC, getParameterMap().get("synonym"));
            if (!result)
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
