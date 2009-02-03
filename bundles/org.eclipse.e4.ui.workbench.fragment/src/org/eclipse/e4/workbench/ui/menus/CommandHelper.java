/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.e4.workbench.ui.menus;

import java.util.Iterator;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.e4.extensions.ExtensionUtils;
import org.eclipse.e4.ui.model.application.ApplicationFactory;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.MCommand;
import org.eclipse.e4.ui.model.application.MHandler;
import org.eclipse.e4.ui.model.application.MWindow;
import org.eclipse.emf.common.util.EList;
import org.eclipse.ui.internal.registry.IWorkbenchRegistryConstants;

/**
 *
 *
 */
public class CommandHelper {
	/**
	 * This loads the application with extension data. It doesn't take into
	 * account that if you save the model, you don't need to refill it on next
	 * startup.
	 * 
	 * @param application
	 */
	public static void loadCommands(MApplication<MWindow<?>> application) {
		IConfigurationElement[] cmdExts = ExtensionUtils
				.getExtensions(IWorkbenchRegistryConstants.PL_COMMANDS);
		for (int i = 0; i < cmdExts.length; i++) {
			IConfigurationElement cmd = cmdExts[i];
			MCommand command = ApplicationFactory.eINSTANCE.createMCommand();
			command.setId(cmd.getAttribute(IWorkbenchRegistryConstants.ATT_ID));
			command.setName(cmd
					.getAttribute(IWorkbenchRegistryConstants.ATT_NAME));
			application.getCommand().add(command);
		}
	}

	/**
	 * This loads the handlers. For the moment it loads all of them into each
	 * available MWindow model element. That means it doesn't deal with
	 * conflicts, and it doesn't distribute the handlers down to the individual
	 * parts yet.
	 * 
	 * @param application
	 */
	public static void loadHandlers(MApplication<MWindow<?>> application) {
		IConfigurationElement[] handlerExts = ExtensionUtils
				.getExtensions(IWorkbenchRegistryConstants.PL_HANDLERS);
		for (int i = 0; i < handlerExts.length; i++) {
			IConfigurationElement handlerE = handlerExts[i];
			Iterator<MWindow<?>> j = application.getWindows().iterator();
			while (j.hasNext()) {
				MWindow<?> w = j.next();
				MHandler handler = ApplicationFactory.eINSTANCE.createMHandler();
				handler
						.setCommand(findCommand(
								application,
								handlerE
										.getAttribute(IWorkbenchRegistryConstants.ATT_COMMAND_ID)));
				handler
						.setURI("platform:/plugin/" //$NON-NLS-1$
								+ handlerE.getContributor().getName()
								+ "/" //$NON-NLS-1$
								+ handlerE
										.getAttribute(IWorkbenchRegistryConstants.ATT_CLASS));
				w.getHandlers().add(handler);
			}
		}
	}

	/**
	 * find a command for an ID. Shouldn't the model be doing this for us?
	 * 
	 * @param application
	 * @param id
	 * @return the command
	 */
	private static MCommand findCommand(MApplication<MWindow<?>> application,
			String id) {
		EList<MCommand> cmdList = application.getCommand();
		Iterator<MCommand> i = cmdList.iterator();
		while (i.hasNext()) {
			MCommand command = i.next();
			if (id.equals(command.getId())) {
				return command;
			}
		}
		return null;
	}
}
