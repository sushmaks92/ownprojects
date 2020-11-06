package com.cerner.hi.pitc.model.project;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;



public class Project {
	
	private @Setter @Getter @NonNull String projectName;
	
	private @Setter @Getter @NonNull String projectId;
	
	private @Getter List<ComplianceJob> jobs = new ArrayList<ComplianceJob>();

}
