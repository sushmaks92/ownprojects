package com.cerner.hi.pitc.model;

import org.springframework.stereotype.Component;

import com.cerner.hi.pitc.executor.ValidatorService;
import com.cerner.hi.pitc.util.Constants;

import lombok.Getter;
import lombok.Setter;

@Component
public class ValidationInfo {



    private Constants.ValidationInfoType info = Constants.ValidationInfoType.FAILURE;
    private Class<? extends ValidatorService> clazz;
    @Setter
    @Getter
    private String excelColumn;
    
    public Constants.ValidationInfoType getInfo() {
        return info;
    }

    public void setInfo(Constants.ValidationInfoType info) {
        this.info = info;
    }

    private Constants.ResultVariables resultVariable;

    public Constants.ValidationInfoType getInfoType() {
        return info;
    }

    public void setInfoType(Constants.ValidationInfoType info) {
        this.info = info;
    }

    public Constants.ResultVariables getResultVariable() {
        return resultVariable;
    }

    public void setResultVariable(Constants.ResultVariables resultVariable) {
        this.resultVariable = resultVariable;
    }

    public Class<? extends ValidatorService> getClazz() {
        return clazz;
    }

    public void setClazz(Class<? extends ValidatorService> clazz) {
        this.clazz = clazz;
    }



}
