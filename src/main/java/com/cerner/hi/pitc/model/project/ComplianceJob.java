package com.cerner.hi.pitc.model.project;

import com.cerner.hi.pitc.util.Constants.RiskType;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class ComplianceJob {
	
	private @NonNull String jobName;
	
	private @NonNull String jobId;
	
	private String jobDescription;
	
	private RiskType riskType;
	
	private String configurationId;
		
}
