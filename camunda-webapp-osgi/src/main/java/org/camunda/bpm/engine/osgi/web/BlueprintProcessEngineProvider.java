package org.camunda.bpm.engine.osgi.web;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.rest.spi.ProcessEngineProvider;

import java.util.HashSet;
import java.util.Set;

public class BlueprintProcessEngineProvider implements ProcessEngineProvider {


    @Override
    public ProcessEngine getDefaultProcessEngine() {
        return BundleActivator.processEngine;
    }

    @Override
    public ProcessEngine getProcessEngine(String s) {
        if (s.equals("default")){
            return getDefaultProcessEngine();
        } else {
            return null;
        }
    }

    @Override
    public Set<String> getProcessEngineNames() {
        if (BundleActivator.processEngine != null) {
            return new HashSet<String>() {{
                add("default");
            }};
        } else {
            return new HashSet<String>();
        }

    }
}
