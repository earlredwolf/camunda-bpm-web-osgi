package org.camunda.bpm.engine.osgi.web;

import org.camunda.bpm.admin.Admin;
import org.camunda.bpm.admin.impl.DefaultAdminRuntimeDelegate;
import org.camunda.bpm.admin.plugin.spi.AdminPlugin;
import org.camunda.bpm.cockpit.Cockpit;
import org.camunda.bpm.cockpit.impl.DefaultCockpitRuntimeDelegate;
import org.camunda.bpm.container.RuntimeContainerDelegate;
import org.camunda.bpm.engine.ProcessEngine;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;

public class BundleActivator implements org.osgi.framework.BundleActivator {

    public static ProcessEngine processEngine;

    private ServiceReference reference;

    private CockpitEnvironment cockpitEnvironment;

    private AdminEnvironment adminEnvironment;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        ServiceReference reference = bundleContext.getServiceReference(ProcessEngine.class.getName());
        if (reference != null) {
            processEngine = (ProcessEngine) bundleContext.getService(reference);
        } else {
            throw new IllegalStateException("No processEngine instance in the osgi container");
        }
        cockpitEnvironment = new CockpitEnvironment();
        cockpitEnvironment.setup();
        adminEnvironment = new AdminEnvironment();
        adminEnvironment.setup();

    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        if (reference != null) {
            bundleContext.ungetService(reference);
        }
        cockpitEnvironment.tearDown();
        adminEnvironment.tearDown();
    }

    protected static class CockpitEnvironment {

        public void tearDown() {
            Cockpit.setCockpitRuntimeDelegate(null);
        }

        public void setup() {
            Cockpit.setCockpitRuntimeDelegate(new DefaultCockpitRuntimeDelegate());
        }

        protected RuntimeContainerDelegate getContainerRuntimeDelegate() {
            return RuntimeContainerDelegate.INSTANCE.get();
        }
    }

    protected static class AdminEnvironment {

        public void tearDown() {
            Admin.setAdminRuntimeDelegate(null);
        }

        public void setup() {
            Admin.setAdminRuntimeDelegate(new DefaultAdminRuntimeDelegate());
        }

        protected RuntimeContainerDelegate getContainerRuntimeDelegate() {
            return RuntimeContainerDelegate.INSTANCE.get();
        }
    }

}
