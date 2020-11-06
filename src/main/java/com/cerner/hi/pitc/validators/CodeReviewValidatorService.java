package com.cerner.hi.pitc.validators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import com.cerner.hi.pitc.contract.JsonParser;
import com.cerner.hi.pitc.executor.ValidatorService;
import com.cerner.hi.pitc.jira.HttpBasicAuth;
import com.cerner.hi.pitc.model.Parameter;
import com.cerner.hi.pitc.model.ValidationContext;
import com.cerner.hi.pitc.model.ValidationInfo;
import com.cerner.hi.pitc.parser.JsonParserImpl;
import com.cerner.hi.pitc.util.Constants;
import com.mashape.unirest.http.exceptions.UnirestException;

public class CodeReviewValidatorService extends ValidatorService {

    @Override
    protected void populateValidArgs(Set<Parameter> parameter) {
        Parameter pattern = new Parameter("server", Constants.DataTypeForParams.STRING);
        parameter.add(pattern);
        Parameter excelColumn = new Parameter("excelColumnName", Constants.DataTypeForParams.STRING);
        parameter.add(excelColumn);
    }

    @Override
    protected Constants.ResultVariables getResultVariable() {
        return Constants.ResultVariables.CODE_REVIEW;
    }

    @Override
    public ValidationInfo serviceOperations(ValidationContext context) {
        ValidationInfo info = new ValidationInfo();
        info.setClazz(this.getClass());
        String jiraKey = context.getJira().getKey();
        JsonParser parser = new JsonParserImpl();
        Set<String> allowedStates = new HashSet<>();
        allowedStates.add("Closed");
        allowedStates.add("Dead");
        allowedStates.add("Abandoned");
        Object parsedJiraContent = null;
        String jiraContent="";
        ArrayList<Object> reviews = new ArrayList<>();
        for(final String server : getParameterMap().get("server")) {
            try {
            	//need to handle configurations
                jiraContent = HttpBasicAuth.getQueryResponseCrucible(server,context.getConfig(), jiraKey);
                parsedJiraContent = parser.getRawObject(jiraContent);
            } catch (IOException | UnirestException e) {
                e.printStackTrace();
            }
            reviews =(ArrayList<Object>) ((LinkedHashMap)parsedJiraContent).get("reviewData");
            if(null!=reviews && !reviews.isEmpty()) {
                break;
            }
        }
        if(reviews != null && !reviews.isEmpty()) {
            info.setInfo(Constants.ValidationInfoType.SUCCESS);
            for(Object o : reviews) {
                LinkedHashMap<String, Object> review = (LinkedHashMap<String, Object>) o;
                String state = (String) review.get("state");
                if(!allowedStates.contains(state)) {
                    info.setInfo(Constants.ValidationInfoType.FAILURE);
                    break;
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