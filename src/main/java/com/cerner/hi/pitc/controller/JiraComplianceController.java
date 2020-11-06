package com.cerner.hi.pitc.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import com.atlassian.httpclient.api.Response;
import com.cerner.hi.pitc.model.config.Configuration;
import com.cerner.hi.pitc.model.project.Project;
import com.cerner.hi.pitc.model.result.JIRAComplianceList;
import com.cerner.hi.pitc.repository.ConfigurationRepository;
import com.cerner.hi.pitc.repository.ProjectDAO;
import com.cerner.hi.pitc.service.JiraComplianceService;

@RequestMapping("/compliance")
@CrossOrigin(origins = "*", allowedHeaders = "*")

@RestController
public class JiraComplianceController {

	@Autowired
	private JiraComplianceService jiraComplianceService;

	@Autowired
	private ProjectDAO projectDAO;

	@Autowired
	private ConfigurationRepository configurationDAO;

	@PostMapping("validate")
	public ResponseEntity<Object> runCompliance(@RequestBody Configuration configuration)
			throws URISyntaxException, ParserConfigurationException, SAXException, IOException {
		JIRAComplianceList result = jiraComplianceService.getCompliance(configuration);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	
	@GetMapping("validate/{idList}")
	public ResponseEntity<Object> runCompliance(@PathVariable(value="idList") List<String> idList)
			throws URISyntaxException, IOException, SAXException, ParserConfigurationException {
		for (String id : idList) {
			jiraComplianceService.storeCompliance(id);
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
		
	}


		
	
	@GetMapping("result/{id}")
	public ResponseEntity<JIRAComplianceList> getCompliance(@PathVariable(value = "id") String id)
			throws URISyntaxException, IOException, SAXException, ParserConfigurationException {
		JIRAComplianceList result = jiraComplianceService.getCompliance(id, false);
		return new ResponseEntity<>(result, HttpStatus.OK);

	}

	@GetMapping("validate/generate/{id}")
	public ResponseEntity<JIRAComplianceList> generateCompliance(@PathVariable(value = "id") String id)
			throws URISyntaxException, IOException, SAXException, ParserConfigurationException {
		JIRAComplianceList result = jiraComplianceService.getCompliance(id, true);
		return new ResponseEntity<>(result, HttpStatus.OK);

	}


	@GetMapping("projects")
	public List<Project> getProjects() throws FileNotFoundException {
		// return projectDAO.getAllDummy();
		return projectDAO.getAll();

	}

	@PostMapping("project/store")
	public ResponseEntity<Object> storeProject(@RequestBody Project project) throws FileNotFoundException {
		projectDAO.store(project, project.getProjectId());
		return new ResponseEntity<>("Project stored.", HttpStatus.OK);
	}

	@GetMapping("configuration/{id}")
	public Configuration getConfiguration(@PathVariable(value = "id") String id) throws FileNotFoundException {
		return configurationDAO.get(id);
	}

	@PostMapping("configuration/store")
	public ResponseEntity<Object> storeConfiguration(@RequestBody Configuration configuration)
			throws FileNotFoundException {
		configurationDAO.store(configuration, configuration.getConfigurationId());
		return new ResponseEntity<>("Configuration Stored", HttpStatus.OK);
	}
	
}
