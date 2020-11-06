package com.cerner.hi.pitc.graphql.datafetchers;

import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;

import com.cerner.hi.pitc.model.result.JIRACompliance;
import com.cerner.hi.pitc.repository.ComplianceRepository;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

public class GetComplianceIdDataFetcher implements DataFetcher<JIRACompliance>{

	@Autowired
	ComplianceRepository repository;
	
	@Override
	public JIRACompliance get(DataFetchingEnvironment environment) {
		try {
			return repository.get("").getJiraComplianceList().stream().findFirst().get();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

}
