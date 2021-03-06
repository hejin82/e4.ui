/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.demo.viewer;

import org.eclipse.core.databinding.Binding;
import org.eclipse.e4.ui.model.application.MApplicationPackage;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * Basic information for the MStack 
 */
public class PropertiesStack extends PropertiesElement {
	
	private Text policy;
	private Button visible;
	private Binding visibleBinding;
	
	public PropertiesStack(final Composite parent) {
		super(parent);
		visible = createButton(comp, "&Visible", SWT.NONE);
		GridData checkboxData = new GridData(SWT.RIGHT, SWT.TOP, false, false, 2, 1);
		visible.setLayoutData(checkboxData);
	}

	public void selected(EObject selected) {
		super.selected(selected);
		visibleBinding = bind(visible, MApplicationPackage.Literals.UI_ELEMENT__VISIBLE);
	}
	
	protected void clearBindings() {
		super.clearBindings();
		if (visibleBinding != null) {
			visibleBinding.dispose();
			visibleBinding = null;
		}
	}
}