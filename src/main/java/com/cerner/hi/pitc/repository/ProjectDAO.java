package com.cerner.hi.pitc.repository;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.stereotype.Repository;

import com.cerner.hi.pitc.contract.SerializerDAO;
import com.cerner.hi.pitc.model.project.ComplianceJob;
import com.cerner.hi.pitc.model.project.Project;
import com.cerner.hi.pitc.util.Constants.RiskType;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;



@Repository
public class ProjectDAO implements SerializerDAO<Project> {

    @Override
    public void store(Project project, String name) throws FileNotFoundException {
        Kryo kryo = new Kryo();
        String filename = "project/"+name+".dat";
        kryo.setRegistrationRequired(false);
        Output output = new Output(new FileOutputStream(filename,false));
        kryo.writeObject(output,project);
        output.close();
    }

    @Override
    public Project get(String id) throws Exception {
        return null;
    }

    @Override
    public List<Project> getAll() throws FileNotFoundException {
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        File folder = new File("project/");
        List<Project> projects = new ArrayList<>();
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        File[] files = folder.listFiles();
        for (File i : files){
            Input input = new Input(new FileInputStream(i.getAbsolutePath()));
            projects.add(kryo.readObject(input, Project.class));
            input.close();
        }
        return projects;
    }
    
}
