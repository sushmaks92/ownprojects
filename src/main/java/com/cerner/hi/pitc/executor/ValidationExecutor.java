package com.cerner.hi.pitc.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.cerner.hi.pitc.model.Argument;
import com.cerner.hi.pitc.model.Validator;
import com.cerner.hi.pitc.model.WorkerProperties;
import com.cerner.hi.pitc.model.result.JIRACompliance;
import com.cerner.hi.pitc.model.result.JIRAComplianceList;


public class ValidationExecutor extends ThreadPoolExecutor {

    public ValidationExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, CallerRunsPolicy callerRunsPolicy) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,callerRunsPolicy);
    }

    public Future<?> registerWorker(WorkerProperties properties){
        Future<?> task = submit(getNewWorkerTask(properties));
        return task;
    }

    private Callable getNewWorkerTask(WorkerProperties properties) {
        return new WorkerTask(properties);
    }

    public JIRAComplianceList monitorTasks(List<Future<?>> futures){
    	JIRAComplianceList complianceList = new JIRAComplianceList();
        for (Future<?> task : futures) {
            try {
                JIRACompliance compliance = (JIRACompliance) task.get();
                complianceList.getJiraComplianceList().add(compliance);
            } catch (ExecutionException | InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
		return complianceList;
    }

    private class WorkerTask implements Callable {
        WorkerProperties properties;
        WorkerTask(WorkerProperties properties){
            this.properties = properties;
        }

        @Override
        public JIRACompliance call() {
            ValidatorServiceHandler serviceHandler = new ValidatorServiceHandler(properties);
            try {
                properties.setValidatorServices(convertValidatorsToservices(properties.getValidators()));
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return serviceHandler.handleAllServices();
        }

    }
    private List<ValidatorService> convertValidatorsToservices(List<Validator> validators) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        List<ValidatorService> services = new ArrayList<>();
        for(Validator validator: validators) {
            ValidatorService validatorService = (ValidatorService) Class.forName(validator.getName()).newInstance();
            for(Argument param : validator.getParams()) {
                List<String> values = validatorService.getParameterMap().get(param.getKey());
                if(null==values) {
                    ArrayList<String> valueList = new ArrayList<>();
                    valueList.add(param.getValue());
                    validatorService.getParameterMap().put(param.getKey(), valueList);
                }else {
                    values.add(param.getValue());
                }
            }
            services.add(validatorService);
        }
        return services;
    }

}


