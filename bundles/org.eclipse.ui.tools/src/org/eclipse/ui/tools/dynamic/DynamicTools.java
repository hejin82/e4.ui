/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.tools.dynamic;

import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.tools.Activator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;

public class DynamicTools {

	public static final Bundle installPlugin(URL dataURL) throws IOException,
			BundleException {
		// Programmatically install a new plugin
		String pluginLocation = "reference:" + dataURL.toExternalForm();
		return installBundle(pluginLocation);
	}

	public static void uninstallPlugin(Bundle bundle) throws BundleException {
		uninstallBundle(bundle);
	}
	
	public static Bundle getBundle(String pluginLocation) {
		Bundle [] bundles = Activator.getDefault().getContext().getBundles();
		for (int i = 0; i < bundles.length; i++) {
			if (pluginLocation.equals(bundles[i].getLocation()))
				return bundles[i];
		}
		return null;
	}

	public static Bundle installBundle(String pluginLocation)
			throws BundleException, IllegalStateException {
		Bundle target = Activator.getDefault().getContext().installBundle(pluginLocation);
		int state = target.getState();
		if (state != Bundle.INSTALLED)
			throw new IllegalStateException("Bundle " + target
					+ " is in a wrong state: " + state);
		refreshPackages(new Bundle[] { target });
		return target;
	}

	public static void uninstallBundle(Bundle target) throws BundleException {
		target.uninstall();
		refreshPackages(null);
	}
	
    public static void refreshPackages(Bundle[] bundles) {
        BundleContext context = Activator.getDefault().getContext();
		ServiceReference packageAdminRef = context
                .getServiceReference(PackageAdmin.class.getName());
        PackageAdmin packageAdmin = null;
        if (packageAdminRef != null) {
            packageAdmin = (PackageAdmin) context.getService(packageAdminRef);
            if (packageAdmin == null)
                return;
        }

        final boolean[] flag = new boolean[] { false };
        FrameworkListener listener = new FrameworkListener() {
            public void frameworkEvent(FrameworkEvent event) {
                if (event.getType() == FrameworkEvent.PACKAGES_REFRESHED)
                    synchronized (flag) {
                        flag[0] = true;
                        flag.notifyAll();
                    }
            }
        };
        context.addFrameworkListener(listener);
        packageAdmin.refreshPackages(bundles);
        synchronized (flag) {
            while (!flag[0]) {
                try {
                    flag.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        context.removeFrameworkListener(listener);
        context.ungetService(packageAdminRef);
    }
}
