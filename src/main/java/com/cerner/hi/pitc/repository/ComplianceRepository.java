package com.cerner.hi.pitc.repository;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.stereotype.Repository;

import com.cerner.hi.pitc.contract.SerializerDAO;
import com.cerner.hi.pitc.model.result.JIRAComplianceList;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;


@Repository
public class ComplianceRepository implements SerializerDAO<JIRAComplianceList> {

    @Override
    public void store(JIRAComplianceList jiraComplianceList, String name) throws FileNotFoundException {
        Kryo kryo = new Kryo();
        String filename = "results/"+name+".dat";
        Output output = new Output(new FileOutputStream(filename));
        kryo.setRegistrationRequired(false);
        kryo.writeObject(output, jiraComplianceList);
        output.close();
    }

    @Override
    public JIRAComplianceList get(String filterId) throws FileNotFoundException {
        String filename = "results/"+filterId+".dat";
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        Input input = new Input(new FileInputStream(filename));
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        JIRAComplianceList jiraComplianceList= kryo.readObject(input, JIRAComplianceList.class);
        input.close();
        return jiraComplianceList;
    }

    @Override
    public List<JIRAComplianceList> getAll() throws Exception {
        return null;
    }
}
