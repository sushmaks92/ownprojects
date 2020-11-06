package com.cerner.hi.pitc.scheduler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.cerner.hi.pitc.model.project.ComplianceJob;
import com.cerner.hi.pitc.model.project.Project;
import com.cerner.hi.pitc.repository.ConfigurationRepository;
import com.cerner.hi.pitc.repository.ProjectDAO;
import com.cerner.hi.pitc.service.JiraComplianceService;

@Configuration
@EnableScheduling
@Service
/*
 * A daemon thread runs which scans all the configuration files and fetches
 * their Configuration ID. Based on the Configuration ID, the Cron is fetched
 * for all the jobs and the Compliance is run at the scheduled time
 */

public class PitcScheduler implements SchedulingConfigurer {

	//static Logger logger = Logger.getLogger(PitcScheduler.class);
	private JiraComplianceService jiraComplianceService = new JiraComplianceService();
	private ConfigurationRepository configurationDAO = new ConfigurationRepository();
	private ProjectDAO projectDAO = new ProjectDAO();
	TaskScheduler taskScheduler;

	private ScheduledFuture<?> Job;

	@Override

	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		//logger.info("Scheduling the jobs");
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(10); // Set the pool of threads
		threadPoolTaskScheduler.setThreadNamePrefix("scheduler-thread");
		threadPoolTaskScheduler.initialize();
		List<Project> projects;
		try {
			projects = projectDAO.getAll();
			for (Project project : projects) {
				//logger.debug(project);
				List<ComplianceJob> jobs = project.getJobs();
				for (ComplianceJob job : jobs) {
					Job(threadPoolTaskScheduler, job); // Assign the job to the scheduler
				}
			}
			this.taskScheduler = threadPoolTaskScheduler; // this will be used during refreshing the cron expression
															// dynamically
			taskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
		} catch (FileNotFoundException e) {
			//logger.error("File not found " + e);

		}

	}

	void Job(TaskScheduler scheduler, ComplianceJob job) throws FileNotFoundException {

		com.cerner.hi.pitc.model.config.Configuration config = configurationDAO.get(job.getConfigurationId());
		if (config != null && config.getSchedulerConfiguration() != null) {
			Job = scheduler.schedule(new Runnable() {
				@Override
				public void run() {
					try {

						jiraComplianceService.storeCompliance(job.getConfigurationId());

						Thread.sleep(10000);
					} catch (URISyntaxException | IOException | SAXException | ParserConfigurationException
							| InterruptedException e) {
						//logger.error("Exception " + e);

					}

				}
			}, new Trigger() {
				@Override
				public Date nextExecutionTime(TriggerContext triggerContext) {
					Date cron = null;
					try {
						cron = new CronTrigger((configurationDAO.get(job.getConfigurationId()))
								.getSchedulerConfiguration().getCronExpression()).nextExecutionTime(triggerContext);
					} catch (FileNotFoundException e) {
						//logger.error("File not found " + e);
					}
					return cron;
				}
			});

		}
	}
}
