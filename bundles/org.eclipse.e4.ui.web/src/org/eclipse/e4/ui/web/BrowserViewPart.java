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
package org.eclipse.e4.ui.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.internal.web.Base64;
import org.eclipse.e4.ui.internal.web.BrowserRPC;
import org.eclipse.e4.ui.internal.web.E4BrowserUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.CloseWindowListener;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

public abstract class BrowserViewPart extends ViewPart implements ISaveablePart2 {

	private Browser browser;
	private BrowserRPC browserRPC;
	private boolean isDirty;
	private List menuItems = new ArrayList(3);
	private SaveableProxy saveable;
	
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		browser = new Browser(parent, SWT.NONE);
		browserRPC = new BrowserRPC(browser);
		saveable = new SaveableProxy(browser);
		browser.addOpenWindowListener(new OpenWindowListener() {
			public void open(WindowEvent event) {
				BrowserViewPart view = openWindow(event);
				if (view != null) {
					event.browser = view.getBrowser();
					event.required = true;
				}
			}
		});
		
		browser.addCloseWindowListener(new CloseWindowListener() {
			public void close(WindowEvent event) {
				isDirty = false;
				getViewSite().getPage().hideView(BrowserViewPart.this);
			}
		});
		
		browserRPC.addRPCHandler("dialogs", new BrowserRPCHandler() {
			public Object handle(Object[] args) {
				if ("confirm".equals(args[1])) {
					IViewSite site = getViewSite();
					String title = "Confirmation - " + BrowserViewPart.this.getTitle();
					return Boolean.valueOf(MessageDialog.openConfirm(site.getShell(), title, (String)args[2]));
				}
				return null;
			}
		});
		
		browserRPC.addRPCHandler("clipboard", new BrowserRPCHandler() {
			public Object handle(Object[] args) {
				if ("getContents".equals(args[1])) {
					Clipboard cb = new Clipboard(browser.getDisplay());
					FileTransfer ft = FileTransfer.getInstance();
					String[] files = (String[])cb.getContents(ft);
					if (files.length > 0) {
						File file = new File(files[0]);
						if (file.exists()) {
							byte[] data;
							try {
								data = E4BrowserUtil.getBytesFromFile(file);
								byte[] encoded = Base64.encode(data);
								return new String(encoded);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
				return null;
			}
		});
		
		browserRPC.addRPCHandler("menus", new BrowserRPCHandler() {
			public Object handle(Object[] args) {
				if ("addContextMenuItem".equals(args[1])) {
					menuItems.add(new MenuItemProxy((String)args[2], (String)args[3]));
				}
				return null;
			}
		});
		
		browserRPC.addRPCHandler("status", new BrowserRPCHandler() {
			public Object handle(Object[] args) {
				if ("setMessage".equals(args[1])) {
					IStatusLineManager slm = getViewSite().getActionBars().getStatusLineManager();
					slm.setMessage((String)args[2]);
				}
				else if ("setDirty".equals(args[1])) {
					isDirty = ((Boolean)args[2]).booleanValue();
					firePropertyChange(PROP_DIRTY);
				}
				return null;
			}
		});
		
		browserRPC.addRPCHandler("saveable", new BrowserRPCHandler() {
			public Object handle(Object[] args) {
				if ("promptToSaveOnClose".equals(args[1])) {
					saveable.setPromptCallback((String)args[2]);
				}
				else if ("doSave".equals(args[1])) {
					saveable.setDoSaveCallback((String)args[2]);
				}
				return null;
			}
		});
		
		// context menu
		hookContextMenu();
	}
	
	protected abstract String getNewWindowViewId();
	
	protected BrowserViewPart openWindow(WindowEvent event) {
		BrowserViewPart view = null;
		try {
			view = (BrowserViewPart)getViewSite().getPage().showView(
					getNewWindowViewId(), 
					String.valueOf(System.currentTimeMillis()), 
					IWorkbenchPage.VIEW_ACTIVATE);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return view;
	}
	
	public void setUrl(String url) {
		browser.setUrl(url);
	}
	
	public Browser getBrowser() {
		return browser;
	}

	public void setFocus() {
		browser.setFocus();
	}
	
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				// fill context menu
				Iterator itr = menuItems.iterator();
				while(itr.hasNext()) {
					final MenuItemProxy mi = (MenuItemProxy)itr.next();
					Action action = new Action(mi.getLabel()) {
						public void run() {
							browser.execute(mi.getCallback());
						}
					};
					manager.add(action);
				}
			}
		});
		Menu menu = menuMgr.createContextMenu(browser);
		browser.setMenu(menu);
		
		// TODO implement a selection provider wrapper for browser
		//getSite().registerContextMenu(menuMgr, browser);
	}
	
	public int promptToSaveOnClose() {
		return saveable.promptToSaveOnClose();
	}

	public void doSave(IProgressMonitor monitor) {
		saveable.doSave(monitor);
	}

	public void doSaveAs() {
	}

	public boolean isDirty() {
		return isDirty;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public boolean isSaveOnCloseNeeded() {
		return isDirty();
	}
	
	private class MenuItemProxy {
		private String label;
		private String callback;
		public MenuItemProxy(String label, String callback) {
			this.label = label;
			this.callback = callback;
		}
		public String getLabel() {
			return label;
		}
		public String getCallback() {
			return callback;
		}
	}
	
	private class SaveableProxy {
		private String promptCallback;
		private String dosaveCallback;
		private Browser browser;
		public SaveableProxy(Browser browser) {
			this.browser = browser;
		}
		public void setPromptCallback(String promptCallback) {
			this.promptCallback = promptCallback;
		}
		public void setDoSaveCallback(String dosaveCallback) {
			this.dosaveCallback = dosaveCallback;
		}
		public int promptToSaveOnClose() {
			if (promptCallback != null) {
				Double i = (Double)browser.evaluate(promptCallback);
				if (i != null) {
					return i.intValue();
				}
			}
			return ISaveablePart2.DEFAULT;
		}
		public void doSave(IProgressMonitor monitor) {
			if (dosaveCallback != null) {
				browser.execute(dosaveCallback);
			}
		}
	}
}
