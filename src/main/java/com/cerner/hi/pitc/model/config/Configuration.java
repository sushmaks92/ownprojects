package com.cerner.hi.pitc.model.config;

import com.cerner.hi.pitc.util.Constants.ValidationType;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Configuration {

	private String configurationId;
	private JiraQueryConfiguration jiraQueryConfiguration;
	private CrucibleConfiguration crucibleConfiguration;
	private Credential credentials;
	private SchedulerConfiguration schedulerConfiguration;
	// added typeOfConfiguration
	private ValidationType typeOfValidation;
	private String recipientEmailID;
	private String senderEmailID;

}
