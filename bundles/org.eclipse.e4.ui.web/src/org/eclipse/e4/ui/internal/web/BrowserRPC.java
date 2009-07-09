/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hatem, IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.ui.internal.web;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.ui.web.BrowserRPCHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;

public class BrowserRPC {
	
	private static final String UNDEFINED = "undefined";
	
	private BrowserFunction rpcFunction;
	
	private Map/*String, BrowserRPCHandler*/ handlers = 
		new HashMap();

	private Browser browser;
	
	public BrowserRPC(Browser browser) {
		this.browser = browser;
		final String e4Script;
		try {
			e4Script = new String(E4BrowserUtil.getBytesFromStream(getClass().getClassLoader().getResourceAsStream("js/e4.js")), "ISO-8859-1");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		// we need to register the ipc each time
		// the location for the browser changes
		browser.addLocationListener(new LocationListener() {
			public void changed(LocationEvent event) {
				registerRPC();
			}
			public void changing(LocationEvent event) {
			}
		});
		registerRPC();
		browser.addProgressListener(new ProgressListener() {
			public void completed(ProgressEvent event) {
				if (!BrowserRPC.this.browser.execute(e4Script)) {
					MessageDialog.openError(BrowserRPC.this.browser.getShell(), "Error", "Error executing e4 script");
				}
			}
			public void changed(ProgressEvent event) {
			}
		});
	}
	
	private void registerRPC() {
		if (rpcFunction != null) {
			rpcFunction.dispose();
		}
		rpcFunction = new BrowserFunction(browser, "e4RPC") {
			public Object function (Object[] arguments) {
				BrowserRPCHandler handler = (BrowserRPCHandler) handlers.get(arguments[0]);
				if (handler != null) {
					return handler.handle(arguments);
				}
				return UNDEFINED;
			}
		};
	}
	
	public void addRPCHandler(String function, BrowserRPCHandler handler) {
		if (handlers.get(function) != null) {
			throw new IllegalArgumentException();
		}
		handlers.put(function, handler);
	}
	
	public void removeRPCHandler(String function) {
		handlers.remove(function);
	}
}
