package org.camunda.bpm.engine.osgi.web;

import org.camunda.bpm.admin.Admin;
import org.camunda.bpm.admin.plugin.spi.AdminPlugin;
import org.camunda.bpm.cockpit.Cockpit;
import org.camunda.bpm.cockpit.impl.DefaultCockpitRuntimeDelegate;
import org.camunda.bpm.container.RuntimeContainerDelegate;
import org.camunda.bpm.engine.ProcessEngine;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;

public class BundleActivator implements org.osgi.framework.BundleActivator {

  private ServiceReference reference;

  private AdminEnvironment admin;

  private CockpitEnvironment cockpit;

  public ProcessEngine processEngine;
  private OsgiAdminAppRuntimeDelegate adminRuntimeDelegate;
  private OsgiCockpitRuntimeDelegate cockpitRuntimeDelegate;

  @Override
  public void start(BundleContext bundleContext) throws Exception {


    admin = new AdminEnvironment();
    cockpit = new CockpitEnvironment();
    admin.setup();
    cockpit.setup();

    reference = bundleContext.getServiceReference(ProcessEngine.class.getName());
    if (reference != null) {
      processEngine = (ProcessEngine) bundleContext.getService(reference);
      adminRuntimeDelegate.setProcessEngine(processEngine);
      cockpitRuntimeDelegate.setProcessEngine(processEngine);
    } else {
      throw new IllegalStateException("No processEngine instance in the osgi container");
    }

  }

  @Override
  public void stop(BundleContext bundleContext) throws Exception {
    cockpit.tearDown();
    admin.tearDown();
    if (reference != null) {
      bundleContext.ungetService(reference);
    }
  }

  protected class AdminEnvironment {

    public void tearDown() {
      Admin.setAdminRuntimeDelegate(null);
    }

    public void setup() {
      adminRuntimeDelegate = new OsgiAdminAppRuntimeDelegate();
      Admin.setAdminRuntimeDelegate(adminRuntimeDelegate);
    }

    protected RuntimeContainerDelegate getContainerRuntimeDelegate() {
      return RuntimeContainerDelegate.INSTANCE.get();
    }

  }

  protected class CockpitEnvironment {

    public void tearDown() {
      Cockpit.setCockpitRuntimeDelegate(null);
    }

    public void setup() {
      cockpitRuntimeDelegate = new OsgiCockpitRuntimeDelegate();
      Cockpit.setCockpitRuntimeDelegate(cockpitRuntimeDelegate);
    }

    protected RuntimeContainerDelegate getContainerRuntimeDelegate() {
      return RuntimeContainerDelegate.INSTANCE.get();
    }
  }
}
