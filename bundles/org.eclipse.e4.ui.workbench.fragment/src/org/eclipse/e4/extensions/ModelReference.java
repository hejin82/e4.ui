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

package org.eclipse.e4.extensions;

import org.eclipse.e4.ui.model.application.MContributedPart;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.internal.WorkbenchPage;

/**
 * 
 */
public class ModelReference implements IWorkbenchPartReference {

	private MContributedPart<?> modelPart;
	private WorkbenchPage page;

	public ModelReference(MContributedPart<?> model, WorkbenchPage page) {
		modelPart = model;
		this.page = page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPartReference#addPartPropertyListener(org.eclipse
	 * .jface.util.IPropertyChangeListener)
	 */
	public void addPartPropertyListener(IPropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPartReference#addPropertyListener(org.eclipse
	 * .ui.IPropertyListener)
	 */
	public void addPropertyListener(IPropertyListener listener) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPartReference#getContentDescription()
	 */
	public String getContentDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPartReference#getId()
	 */
	public String getId() {
		return modelPart.getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPartReference#getPage()
	 */
	public IWorkbenchPage getPage() {
		return page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPartReference#getPart(boolean)
	 */
	public IWorkbenchPart getPart(boolean restore) {
		Object object = modelPart.getObject();
		if (object instanceof IWorkbenchPart)
			return (IWorkbenchPart) object;
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPartReference#getPartName()
	 */
	public String getPartName() {
		return modelPart.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPartReference#getPartProperty(java.lang.String)
	 */
	public String getPartProperty(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPartReference#getTitle()
	 */
	public String getTitle() {
		return modelPart.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPartReference#getTitleImage()
	 */
	public Image getTitleImage() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPartReference#getTitleToolTip()
	 */
	public String getTitleToolTip() {
		return modelPart.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPartReference#isDirty()
	 */
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPartReference#removePartPropertyListener(org
	 * .eclipse.jface.util.IPropertyChangeListener)
	 */
	public void removePartPropertyListener(IPropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPartReference#removePropertyListener(org.eclipse
	 * .ui.IPropertyListener)
	 */
	public void removePropertyListener(IPropertyListener listener) {
		// TODO Auto-generated method stub

	}

	public MContributedPart<?> getModel() {
		return modelPart;
	}
}
