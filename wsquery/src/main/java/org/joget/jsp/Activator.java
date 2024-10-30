package org.joget.jsp;

import java.util.ArrayList;
import java.util.Collection;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    protected Collection<ServiceRegistration> registrationList;

    public void start(BundleContext context) {
        registrationList = new ArrayList<ServiceRegistration>();

        //Register plugin here GetDetailProduk
        registrationList.add(context.registerService(GetSisaStock.class.getName(), new GetSisaStock(), null));
        registrationList.add(context.registerService(GetDetailProduk.class.getName(), new GetDetailProduk(), null));
        registrationList.add(context.registerService(GetSisaPreform.class.getName(), new GetSisaPreform(), null));
    }

    public void stop(BundleContext context) {
        for (ServiceRegistration registration : registrationList) {
            registration.unregister();
        }
    }
}