package org.camunda.bpm.engine.osgi.web;

import org.camunda.bpm.admin.plugin.spi.AdminPlugin;
import org.camunda.bpm.cockpit.CockpitRuntimeDelegate;
import org.camunda.bpm.cockpit.db.CommandExecutor;
import org.camunda.bpm.cockpit.db.QueryService;
import org.camunda.bpm.cockpit.impl.db.CommandExecutorImpl;
import org.camunda.bpm.cockpit.impl.db.QueryServiceImpl;
import org.camunda.bpm.cockpit.impl.plugin.DefaultPluginRegistry;
import org.camunda.bpm.cockpit.plugin.PluginRegistry;
import org.camunda.bpm.cockpit.plugin.spi.CockpitPlugin;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.impl.ProcessEngineImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.webapp.plugin.AppPluginRegistry;
import org.camunda.bpm.webapp.plugin.impl.DefaultAppPluginRegistry;
import org.camunda.bpm.webapp.plugin.resource.PluginResourceOverride;

import java.util.*;

/**
 * Created by elek on 3/28/14.
 */
public class OsgiCockpitRuntimeDelegate implements CockpitRuntimeDelegate {

  private Map<String, CommandExecutor> commandExecutors;

  protected final AppPluginRegistry<CockpitPlugin> pluginRegistry;

  private ProcessEngine processEngine;

  protected List<PluginResourceOverride> resourceOverrides;



  public OsgiCockpitRuntimeDelegate() {
    pluginRegistry = new DefaultAppPluginRegistry<CockpitPlugin>(CockpitPlugin.class);
    this.commandExecutors = new HashMap<String, CommandExecutor>();
  }

  @Override
  public QueryService getQueryService(String processEngineName) {
    CommandExecutor commandExecutor = getCommandExecutor(processEngineName);
    return new QueryServiceImpl(commandExecutor);
  }

  @Override
  public CommandExecutor getCommandExecutor(String processEngineName) {

    CommandExecutor commandExecutor = commandExecutors.get(processEngineName);
    if (commandExecutor == null) {
      commandExecutor = createCommandExecutor(processEngineName);
      commandExecutors.put(processEngineName, commandExecutor);
    }

    return commandExecutor;
  }

  /**
   * Deprecated: use {@link #getAppPluginRegistry()}
   */
  @Deprecated
  public PluginRegistry getPluginRegistry() {
    return new DefaultPluginRegistry(pluginRegistry);
  }

  /**
   * Returns the list of mapping files that should be used to create the
   * session factory for this runtime.
   *
   * @return
   */
  protected List<String> getMappingFiles() {
    List<CockpitPlugin> cockpitPlugins = pluginRegistry.getPlugins();

    List<String> mappingFiles = new ArrayList<String>();
    for (CockpitPlugin plugin : cockpitPlugins) {
      mappingFiles.addAll(plugin.getMappingFiles());
    }

    return mappingFiles;
  }

  /**
   * Create command executor for the engine with the given name
   *
   * @param processEngineName
   * @return
   */
  protected CommandExecutor createCommandExecutor(String processEngineName) {

    ProcessEngine processEngine = getProcessEngine(processEngineName);
    if (processEngine == null) {
      throw new ProcessEngineException("No process engine with name " + processEngineName + " found.");
    }

    ProcessEngineConfigurationImpl processEngineConfiguration = ((ProcessEngineImpl) processEngine).getProcessEngineConfiguration();
    List<String> mappingFiles = getMappingFiles();

    return new CommandExecutorImpl(processEngineConfiguration, mappingFiles);
  }

  protected synchronized void initResourceOverrides() {
    if (resourceOverrides == null) { // double-checked sync, do not remove
      resourceOverrides = new ArrayList<PluginResourceOverride>();
      List<CockpitPlugin> plugins = pluginRegistry.getPlugins();
      for (CockpitPlugin p : plugins) {
        resourceOverrides.addAll(p.getResourceOverrides());
      }
    }
  }


  @Override
  public ProcessEngine getDefaultProcessEngine() {
    return processEngine;
  }

  @Override
  public AppPluginRegistry<CockpitPlugin> getAppPluginRegistry() {
    return pluginRegistry;
  }

  @Override
  public List<PluginResourceOverride> getResourceOverrides() {
    return resourceOverrides;
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
}
