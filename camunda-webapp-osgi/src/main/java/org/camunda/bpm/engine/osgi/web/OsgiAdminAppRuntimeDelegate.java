package org.camunda.bpm.engine.osgi.web;

import org.camunda.bpm.admin.AdminRuntimeDelegate;
import org.camunda.bpm.admin.plugin.spi.AdminPlugin;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.rest.spi.ProcessEngineProvider;
import org.camunda.bpm.webapp.impl.AbstractAppRuntimeDelegate;
import org.camunda.bpm.webapp.plugin.AppPluginRegistry;
import org.camunda.bpm.webapp.plugin.impl.DefaultAppPluginRegistry;
import org.camunda.bpm.webapp.plugin.resource.PluginResourceOverride;
import org.camunda.bpm.webapp.plugin.spi.AppPlugin;

import java.util.*;

/**
 * Created by elek on 3/28/14.
 */
public class OsgiAdminAppRuntimeDelegate implements AdminRuntimeDelegate {

  private ProcessEngine processEngine;

  protected final AppPluginRegistry<AdminPlugin> pluginRegistry;

  protected List<PluginResourceOverride> resourceOverrides;

  public OsgiAdminAppRuntimeDelegate() {
    pluginRegistry = new DefaultAppPluginRegistry<AdminPlugin>(AdminPlugin.class);
  }


  public AppPluginRegistry<AdminPlugin> getAppPluginRegistry() {
    return pluginRegistry;
  }


  public List<PluginResourceOverride> getResourceOverrides() {
    if (resourceOverrides == null) {
      initResourceOverrides();
    }
    return resourceOverrides;
  }

  protected synchronized void initResourceOverrides() {
    if (resourceOverrides == null) { // double-checked sync, do not remove
      resourceOverrides = new ArrayList<PluginResourceOverride>();
      List<AdminPlugin> plugins = pluginRegistry.getPlugins();
      for (AdminPlugin p : plugins) {
        resourceOverrides.addAll(p.getResourceOverrides());
      }
    }
  }


  @Override
  public ProcessEngine getDefaultProcessEngine() {
    return processEngine;
  }

  @Override
  public Set<String> getProcessEngineNames() {
    Set<String> result = new HashSet<String>();
    if (processEngine != null) {
      result.add("default");
    }
    return result;
  }

  @Override
  public ProcessEngine getProcessEngine(String processEngineName) {
    return processEngine;
  }

  public ProcessEngine getProcessEngine() {
    return processEngine;
  }

  public void setProcessEngine(ProcessEngine processEngine) {
    this.processEngine = processEngine;
  }
};

