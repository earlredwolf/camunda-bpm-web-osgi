package org.camunda.bpm.engine.osgi.web;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.rest.spi.ProcessEngineProvider;

import java.util.HashSet;
import java.util.Set;

public class BlueprintProcessEngineProvider implements ProcessEngineProvider {


    @Override
    public ProcessEngine getDefaultProcessEngine() {
      return null;
        //return BundleActivator.processEngine;
    }

    @Override
    public ProcessEngine getProcessEngine(String s) {
        return null;
    }

    @Override
    public Set<String> getProcessEngineNames() {
//        if (BundleActivator.processEngine != null) {
//            return new HashSet<String>() {{
//                add("default");
//            }};
//        } else {
//            return new HashSet<String>();
//        }
      return null;

    }
}
