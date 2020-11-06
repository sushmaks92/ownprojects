package com.cerner.hi.pitc.contract;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;
import java.util.Map;

public interface JsonParser {

    public abstract Issue parseJSON(String json) throws JsonParseException, JsonMappingException, IOException;

    void populateJiraFromRawObject(Issue jira, Map<String, Object> jiraContentMap);

    Map<String, Object> getRawObject(String jsonString) throws IOException, JsonParseException, JsonMappingException;

}
