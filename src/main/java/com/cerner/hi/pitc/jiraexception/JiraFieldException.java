package com.cerner.hi.pitc.jiraexception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class JiraFieldException extends RuntimeException{
    public JiraFieldException(String exception){
        super(exception);
    }
}
