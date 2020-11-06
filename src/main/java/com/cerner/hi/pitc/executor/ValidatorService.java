package com.cerner.hi.pitc.executor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.cerner.hi.pitc.model.Parameter;
import com.cerner.hi.pitc.model.ValidationContext;
import com.cerner.hi.pitc.model.ValidationInfo;
import com.cerner.hi.pitc.util.Constants;
import com.cerner.hi.pitc.util.Constants.DataTypeForParams;


public abstract class ValidatorService {

    private Map<String,List<String>> parameterMap;

    public Map<String, List<String>> getParameterMap(){
        if(parameterMap==null)
            parameterMap = new HashMap<>();
        return parameterMap;
    }

    public void execute(ValidationContext context) throws IllegalArgumentException{
        validateArgs();
        preServiceOperations(context);
        ValidationInfo info = serviceOperations(context);
        info.setResultVariable(getResultVariable());
        if(null!=parameterMap.get("excelColumnName").get(0)) {
        	info.setExcelColumn(parameterMap.get("excelColumnName").get(0));
        }
        context.getResultMap().put(info.getClazz(), info);
    }

    private void validateArgs() {

        Set<Parameter> validArgs = new HashSet<>();
        Parameter param = new Parameter("continueWithError", DataTypeForParams.BOOLEAN);
        validArgs.add(param);
        populateValidArgs(validArgs);

        for(String paramName : getParameterMap().keySet()) {
            Parameter validArg = null;
            try {
                validArg = validArgs.stream().filter(p -> p.getName().equalsIgnoreCase(paramName)).findFirst().get();
            } catch (NoSuchElementException nse) {
                continue;
            }
            List<String> value = getParameterMap().get(paramName);
            final Parameter ValidArg = validArg;
            try {
                value.forEach(v -> validateValue(ValidArg,v));
            }catch(RuntimeException re) {
                throw new IllegalArgumentException("Argument Name :" +validArg.getName());
            }
        }
    }

    private void validateValue(final Parameter validArg, final String value) {
        DataTypeForParams dataType = validArg.getDataType();
        try {
            if(dataType.equals(DataTypeForParams.INTEGER)) {
                Integer.parseInt(value);
            }else if(dataType.equals(DataTypeForParams.DOUBLE)) {
                Double.parseDouble(value);
            }else if(dataType.equals(DataTypeForParams.BOOLEAN)) {
                if(!Boolean.parseBoolean(value)){
                    throw new RuntimeException();
                }
            }
        }catch(NumberFormatException nfe) {
            throw new RuntimeException(nfe);
        }

    }

    protected abstract void populateValidArgs(Set<Parameter> parameter) ;

    protected abstract Constants.ResultVariables getResultVariable();

    public void updateResult(ValidationInfo info) {

    }


    public abstract ValidationInfo serviceOperations(ValidationContext context);

    public abstract void preServiceOperations(ValidationContext context);
}

