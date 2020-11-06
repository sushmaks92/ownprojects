package com.cerner.hi.pitc.pool;


import java.util.HashMap;
import java.util.Map;

import com.cerner.hi.pitc.model.result.JIRAComplianceList;
import com.cerner.hi.pitc.model.result.ValidationResult;


public enum ValidationResultPool{
    INSTANCE;

    private final Map<String, ValidationResult> results = new HashMap<>();

    public Map<String, ValidationResult> getResults() {
        return results;
    }

    private JIRAComplianceList jiraComplianceList = new JIRAComplianceList();

    public JIRAComplianceList getJIRAComplianceList(){
        return jiraComplianceList;
    }
}
