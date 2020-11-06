package com.cerner.hi.pitc.service;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

@Service
public class GraphQLService {

//	@Value("classpath:complianceList.graphql")
//	Resource resource;
//	
//	private GraphQL graphql;
//
//	@Autowired
//	private DataFetcher getComplianceDataFetcher;
//
//	@Autowired
//	private DataFetcher getComplianceIdDataFetcher;
//	
//	@PostConstruct
//	public void loadSchema() throws IOException{
//		//get the schema
//		File schemaFile = resource.getFile();
//		//parse schema
//		TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(schemaFile);
//		RuntimeWiring wiring = buildRunTimeWiring();
//		GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
//		graphql = GraphQL.newGraphQL(schema).build();
//	}
//
//	private RuntimeWiring buildRunTimeWiring() {
//		return RuntimeWiring.newRuntimeWiring()
//				.type("Query", typeRuntimeWiring -> typeRuntimeWiring
//						.dataFetcher("getCompliance", getComplianceDataFetcher)
//						.dataFetcher("getComplianceId", getComplianceIdDataFetcher)
//				).build();
//	}
}
