package com.cerner.hi.pitc.jiraexception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.atlassian.jira.rest.client.api.RestClientException;

@RestControllerAdvice
public class JiraFieldExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> AllExceptions(Exception ex){
        return new ResponseEntity<>("Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(RestClientException.class)
    public final ResponseEntity<Object> RestClientExceptions(RestClientException ex) {
        return new ResponseEntity<>(ex.getMessage(),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(JiraFieldException.class)
    public final ResponseEntity<Object> JiraFieldExceptions(JiraFieldException ex) {
        return new ResponseEntity<>(ex.getMessage(),HttpStatus.NOT_FOUND);
    }
}
