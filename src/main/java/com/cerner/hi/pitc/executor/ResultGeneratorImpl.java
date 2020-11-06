package com.cerner.hi.pitc.executor;

import java.util.Map;

import com.cerner.hi.pitc.contract.ResultGenerator;
import com.cerner.hi.pitc.model.BasicDetails;
import com.cerner.hi.pitc.model.ValidationInfo;
import com.cerner.hi.pitc.model.result.JIRACompliance;
import com.cerner.hi.pitc.model.result.ValidationResult;
import com.cerner.hi.pitc.model.result.ValidatorServicesResult;
import com.cerner.hi.pitc.pool.ValidationResultPool;

public class ResultGeneratorImpl implements ResultGenerator {


    @Override
    public JIRACompliance populateResultPool(ValidatorServicesResult serviceResult) {
        ValidationResult result = createValidationResult(serviceResult);
        ValidationResultPool.INSTANCE.getResults().put(result.getJiraNumber(), result);
        return createCompliance(result);
    }

    private JIRACompliance createCompliance(ValidationResult result) {
        JIRACompliance compliance = new JIRACompliance();
        compliance.setValidationResult(result);
        return compliance;
    }

    private ValidationResult createValidationResult(ValidatorServicesResult serviceResult) {
        ValidationResult result = new ValidationResult();
        populateBasicDetails(result,serviceResult.getBasicDetails());
        populateValidationInfo(result,serviceResult.getValidationResultMap());
        result.setHasBeenValidated(serviceResult.isHasBeenValidated());
        return result;
    }

    private void populateValidationInfo(ValidationResult result,
                                        Map<Class<? extends ValidatorService>, ValidationInfo> map) {
        result.getValidationInfoList().addAll(map.values());
    	
    }

    private void populateBasicDetails(ValidationResult result, BasicDetails basicDetails) {
        result.setJiraNumber(basicDetails.getJiraNumber());
        result.setJiraSummary(basicDetails.getJiraSummary());
        result.setEpic(basicDetails.getEpic());
        result.setPlannedVersion(basicDetails.getPlannedVersion());
        result.setDeveloper(basicDetails.getDeveloper());
        result.setTester(basicDetails.getTester());
        result.setReporter(basicDetails.getReporter());
        result.setAssignee(basicDetails.getAssignee());
        result.setSme(basicDetails.getSme());
        result.setSolDesigner(basicDetails.getStrategist());
        result.setIssueType(basicDetails.getIssueType());
        result.setStatus(basicDetails.getStatus());
        result.setResolution(basicDetails.getResolution());
        result.setReqID(basicDetails.getReqId());
        result.setFixVersion(basicDetails.getFixedVersions());
        result.setSolutionDetail(basicDetails.getSolutionDetail());
        result.setJiraGroup(basicDetails.getJiraGroup());
        result.setComponents(basicDetails.getComponents());
        result.setClientViewable(basicDetails.getClientViewable());    
    }
}

