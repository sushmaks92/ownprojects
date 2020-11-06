package com.cerner.hi.pitc.validators;

import java.util.Set;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.cerner.hi.pitc.executor.ValidatorService;
import com.cerner.hi.pitc.model.Parameter;
import com.cerner.hi.pitc.model.ValidationContext;
import com.cerner.hi.pitc.model.ValidationInfo;
import com.cerner.hi.pitc.util.Constants;

public class REQValidatorService extends ValidatorService {

    @Override
    protected void populateValidArgs(Set<Parameter> parameter) {
        Parameter pattern = new Parameter("pattern", Constants.DataTypeForParams.STRING);
        Parameter urlPattern = new Parameter("contentPattern", Constants.DataTypeForParams.STRING);
        parameter.add(pattern);
        parameter.add(urlPattern);
        Parameter excelColumn = new Parameter("excelColumnName", Constants.DataTypeForParams.STRING);
        parameter.add(excelColumn);
    }

    @Override
    protected Constants.ResultVariables getResultVariable() {
        return Constants.ResultVariables.HAS_REQID;
    }

    @Override
    public ValidationInfo serviceOperations(ValidationContext context) {
        ValidationInfo info = new ValidationInfo();
        info.setClazz(this.getClass());
        Issue jira = context.getJira();
        final String desc = jira.getDescription();
        String req = "";
        String reqID = "";
        if(null != desc && !"".equals(desc)) {
        	req = context.getUtils().getMatchingString(desc, getParameterMap().get("pattern"), 2);
        	reqID = context.getUtils().getMatchingString(req, getParameterMap().get("contentPattern"), 2);
        }
        if(!"".equals(req.trim())) {
            boolean result = context.getUtils().isPatternFoundInString(req, getParameterMap().get("contentPattern"));
            if(result)
                info.setInfoType(Constants.ValidationInfoType.SUCCESS);
        }
        context.setReqId(reqID);
        return info;
    }

    @Override
    public void preServiceOperations(ValidationContext context) {
        // TODO Auto-generated method stub

    }

}
