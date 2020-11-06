package com.cerner.hi.pitc.contract;

import com.cerner.hi.pitc.model.result.JIRACompliance;
import com.cerner.hi.pitc.model.result.ValidatorServicesResult;

public interface ResultGenerator {

    public JIRACompliance populateResultPool(ValidatorServicesResult serviceResult);
}

