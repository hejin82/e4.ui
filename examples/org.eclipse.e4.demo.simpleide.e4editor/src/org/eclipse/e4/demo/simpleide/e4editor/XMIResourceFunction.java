/*******************************************************************************
 * Copyright (c) 2010 BestSolution.at and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl <tom.schindl@bestsolution.at> - initial API and implementation
 ******************************************************************************/
package org.eclipse.e4.demo.simpleide.e4editor;

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.tools.emf.ui.common.IModelResource.ModelListener;
import org.eclipse.e4.tools.emf.ui.common.XMIModelResource;
import org.eclipse.e4.ui.model.application.ui.basic.MInputPart;
import org.eclipse.emf.common.util.URI;

public class XMIResourceFunction extends ContextFunction {
	@Override
	public Object compute(IEclipseContext context) {
		final MInputPart part = context.get(MInputPart.class);
		if( part != null ) {
			final XMIModelResource resource = new XMIModelResource(URI.createPlatformResourceURI(part.getInputURI(),false));
			resource.addModelListener(new ModelListener() {
				
				public void dirtyChanged() {
					part.setDirty(resource.isDirty());
				}

				public void commandStackChanged() {
					
				}
			});
			return resource;			
		}
		
		return null;
	}
}