package com.cerner.hi.pitc.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/*
This is the main spring boot application.
 */

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages={"com.cerner.hi.pitc"})
public class JiraValidatorMain extends SpringBootServletInitializer {
	@Override
	 protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	  return application.sources(JiraValidatorMain.class);
	 }
    public static void main (String[] args){
        SpringApplication.run(JiraValidatorMain.class, args);
    }
}
