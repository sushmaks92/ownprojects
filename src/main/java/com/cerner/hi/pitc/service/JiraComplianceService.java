package com.cerner.hi.pitc.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.cerner.hi.pitc.contract.IssueDAO;
import com.cerner.hi.pitc.executor.ValidationExecutor;
import com.cerner.hi.pitc.export.ExcelWriter;
import com.cerner.hi.pitc.factories.IssueDAOFactory;
import com.cerner.hi.pitc.model.Validator;
import com.cerner.hi.pitc.model.WorkerProperties;
import com.cerner.hi.pitc.model.config.Configuration;
import com.cerner.hi.pitc.model.result.JIRAComplianceList;
import com.cerner.hi.pitc.parser.ValidatorParser;
import com.cerner.hi.pitc.parser.ValidatorParserFactory;
import com.cerner.hi.pitc.repository.ComplianceRepository;
import com.cerner.hi.pitc.repository.ConfigurationRepository;
import com.cerner.hi.pitc.repository.JRJCIssueDAO;
import com.cerner.hi.pitc.repository.ProjectDAO;
import com.cerner.hi.pitc.util.Constants;

@Service
public class JiraComplianceService {
	
	ProjectDAO projectDAO;

	private ComplianceRepository complianceDAO = new ComplianceRepository();

	private ConfigurationRepository dao = new ConfigurationRepository();
	
	private ExcelWriter excelWriter = new ExcelWriter();
		
    public JIRAComplianceList getCompliance(Configuration config) throws URISyntaxException, IOException, SAXException, ParserConfigurationException {
    	//populateConfiguration("");
    	IssueDAO issueDao = IssueDAOFactory.getIssueDAO();
    	//repopulating necessary things
    	populateJIRACredintials(config);
        List<Issue> issues =  issueDao.getAllIssues(config.getJiraQueryConfiguration());
        List<Validator> validators = getValidators(config);
        ValidationExecutor executor = new ValidationExecutor(Constants.DEFAULT_NUMBER_OF_CORE_THREAD,
        		Constants.DEFAULT_NUMBER_OF_MAX_THREAD, 1, TimeUnit.MINUTES,
                new ArrayBlockingQueue<Runnable>(Constants.QUEUE_SIZE, true),
                new ThreadPoolExecutor.CallerRunsPolicy());
        ArrayList<Future<?>> futures = new ArrayList<>();
        for (Issue issue : issues){
            WorkerProperties properties = new WorkerProperties(issue.getKey(),issue,validators,config);
            Future<?> task = executor.registerWorker(properties);
            futures.add(task);
        }
        JIRAComplianceList result =  executor.monitorTasks(futures);
        executor.shutdown();
        return result;
        
    }
    
    private void populateJIRACredintials(Configuration config) {
    	if(config.getConfigurationId() != null) {
		config.getJiraQueryConfiguration().setCredential(config.getCredentials());
		config.getJiraQueryConfiguration().setFilterId(config.getJiraQueryConfiguration().getFilterId());
    	}
	}

	public void storeCompliance(String configId) throws URISyntaxException, IOException, SAXException, ParserConfigurationException {
    	Configuration config = dao.get(configId);
    	JIRAComplianceList complianceList = getCompliance(config);
    	complianceDAO.store(complianceList,config.getJiraQueryConfiguration().getQueryType()+"_"+config.getConfigurationId());
    	
    	// For excel    	
        excelWriter.writeToExcel(complianceList, configId);
      
      
       
        
    }
    
    public JIRAComplianceList getCompliance(String configId, boolean validateNow) throws URISyntaxException, IOException, SAXException, ParserConfigurationException {
    	Configuration config = dao.get(configId);
    	if(validateNow)
    		return getCompliance(config);

    	return complianceDAO.get(config.getJiraQueryConfiguration().getQueryType()+"_"+config.getConfigurationId());
    }

	private List<Validator> getValidators(Configuration config) throws URISyntaxException, SAXException, ParserConfigurationException, IOException {
		ValidatorParser parser = ValidatorParserFactory.getParser();  
		return parser.getValidators(config.getTypeOfValidation().getType()+Constants.VALIDATOR_NAME_TYPE_SEPARATOR+Constants.VALIDATOR_FILE_SUFFIX);
	}
	

}
