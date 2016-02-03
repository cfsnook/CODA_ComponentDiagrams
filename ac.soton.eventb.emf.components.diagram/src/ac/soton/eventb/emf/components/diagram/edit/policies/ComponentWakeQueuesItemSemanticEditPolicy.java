/*
 * Copyright (c) 2011-2013 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package ac.soton.eventb.emf.components.diagram.edit.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateElementRequest;

import ac.soton.eventb.emf.components.diagram.edit.commands.WakeQueueCreateCommand;
import ac.soton.eventb.emf.components.diagram.providers.ComponentsElementTypes;

/**
 * @generated
 */
public class ComponentWakeQueuesItemSemanticEditPolicy extends
		ComponentsBaseItemSemanticEditPolicy {

	/**
	 * @generated
	 */
	public ComponentWakeQueuesItemSemanticEditPolicy() {
		super(ComponentsElementTypes.Component_2005);
	}

	/**
	 * @generated
	 */
	protected Command getCreateCommand(CreateElementRequest req) {
		if (ComponentsElementTypes.WakeQueue_3018 == req.getElementType()) {
			return getGEFWrapper(new WakeQueueCreateCommand(req));
		}
		return super.getCreateCommand(req);
	}

}
