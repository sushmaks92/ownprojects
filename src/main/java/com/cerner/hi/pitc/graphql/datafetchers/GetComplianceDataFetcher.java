package com.cerner.hi.pitc.graphql.datafetchers;

import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;

import com.cerner.hi.pitc.model.result.JIRAComplianceList;
import com.cerner.hi.pitc.repository.ComplianceRepository;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

public class GetComplianceDataFetcher implements DataFetcher<JIRAComplianceList> {

	@Autowired
	ComplianceRepository repository;
	
	@Override
	public JIRAComplianceList get(DataFetchingEnvironment environment) {
		try {
			return repository.get("");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
