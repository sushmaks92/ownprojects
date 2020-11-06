package com.cerner.hi.pitc.model.result;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class JIRAComplianceList {

    private ArrayList<JIRACompliance> jiraComplianceList = new ArrayList<>();

    public ArrayList<JIRACompliance> getJiraComplianceList() {
        return jiraComplianceList;
    }
}
