package com.cerner.hi.pitc.model.config;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public enum GlobalConfiguration {
	INSTANCE;
	
	private @Getter @Setter List<String> jiraServers;
	private @Getter @Setter List<String> crucibleServers;
	private @Getter @Setter String dioServer;

}
