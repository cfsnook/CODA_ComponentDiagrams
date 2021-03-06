/*******************************************************************************
 * (c) Crown owned copyright (2017) (UK Ministry of Defence)
 *
 * All rights reserved. This program and the accompanying materials are 
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      University of Southampton - Initial API and implementation
 *******************************************************************************/
package ac.soton.eventb.emf.components.generator.rules;

import org.eclipse.emf.ecore.EObject;

import ac.soton.eventb.emf.components.ComponentsPackage;
import ac.soton.eventb.emf.components.util.ComponentsUtils;
import ac.soton.emf.translator.eventb.rules.AbstractEventBGeneratorRule;
import ac.soton.emf.translator.configuration.IRule;
import ac.soton.eventb.statemachines.Statemachine;

public abstract class AbstractSynchronousStatemachineRule extends AbstractEventBGeneratorRule  implements IRule {
	
	protected Statemachine rootSm = null;
	
	@Override
	public boolean enabled(EObject sourceElement) throws Exception {
		rootSm = ComponentsUtils.getRootStatemachine(sourceElement);
		return  
				//sourceElement is inside a state machine that is owned by a component (not necessarily directly owned)
				rootSm != null 
				&&
				//state machine is a synchronous state machine
				rootSm.eContainingFeature() == ComponentsPackage.Literals.COMPONENT__SYNCHRONOUS_STATEMACHINES;
	}
		


}
