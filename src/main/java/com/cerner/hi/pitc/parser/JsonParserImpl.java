package com.cerner.hi.pitc.parser;

import java.io.IOException;
import java.util.Map;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.cerner.hi.pitc.contract.JsonParser;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonParserImpl implements JsonParser {


    @Override
    public Issue parseJSON(String json) throws JsonParseException, JsonMappingException, IOException {
        return null;
    }

    @Override
    public void populateJiraFromRawObject(Issue jira, Map<String, Object> jiraContentMap) {

    }

    @Override
    public Map<String, Object> getRawObject(String jsonString) throws IOException, JsonParseException, JsonMappingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonString,
                new TypeReference<Map<String,Object>>(){});
    }

}

