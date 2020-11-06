package com.cerner.hi.pitc.model.result;

import java.util.HashMap;
import java.util.Map;

import com.cerner.hi.pitc.executor.ValidatorService;
import com.cerner.hi.pitc.model.BasicDetails;
import com.cerner.hi.pitc.model.ValidationInfo;



public class ValidatorServicesResult {

    private BasicDetails basicDetails = new BasicDetails();
    private boolean hasBeenValidated = false;
    private Map<Class<? extends ValidatorService>, ValidationInfo> validationResultMap = new HashMap<>();

    public BasicDetails getBasicDetails() {
        return basicDetails;
    }
    public void setBasicDetails(BasicDetails basicDetails) {
        this.basicDetails = basicDetails;
    }

    public Map<Class<? extends ValidatorService>, ValidationInfo> getValidationResultMap() {
        return validationResultMap;
    }
    public void setValidationResultMap(Map<Class<? extends ValidatorService>, ValidationInfo> validationResultMap) {
        this.validationResultMap = validationResultMap;
    }

    public boolean isHasBeenValidated() {
        return hasBeenValidated;
    }
    public void setHasBeenValidated(boolean hasBeenValidated) {
        this.hasBeenValidated = hasBeenValidated;
    }
}
