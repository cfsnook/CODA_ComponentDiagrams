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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EObject;
import org.eventb.emf.core.machine.Event;
import org.eventb.emf.core.machine.Machine;
import org.eventb.emf.core.machine.MachinePackage;

import ac.soton.eventb.decomposition.DecompositionPackage;
import ac.soton.eventb.emf.components.AbstractComponentOperation;
import ac.soton.eventb.emf.components.Component;
import ac.soton.eventb.emf.components.ComponentsPackage;
import ac.soton.eventb.emf.components.DataPacket;
import ac.soton.eventb.emf.components.Method;
import ac.soton.eventb.emf.components.PortWake;
import ac.soton.eventb.emf.components.SelfWake;
import ac.soton.eventb.emf.components.generator.strings.Strings;
import ac.soton.eventb.emf.components.util.ComponentsUtils;
import ac.soton.emf.translator.eventb.rules.AbstractEventBGeneratorRule;
import ac.soton.emf.translator.TranslationDescriptor;
import ac.soton.emf.translator.configuration.IRule;
import ac.soton.emf.translator.eventb.utils.Find;
import ac.soton.emf.translator.eventb.utils.Make;


public class AutonomousOperationRule extends AbstractEventBGeneratorRule  implements IRule {
	
	protected static final EReference allocatedVariables = DecompositionPackage.Literals.ABSTRACT_REGION__ALLOCATED_VARIABLES;


	private Event timerEvent = null;
	
	@Override
	public boolean enabled(EObject sourceElement) throws Exception {
		assert(sourceElement instanceof AbstractComponentOperation);
		return isSynchronised((AbstractComponentOperation)sourceElement);
	}

	/**
	 * @param sourceElement
	 * @return
	 */
	private boolean isSynchronised(AbstractComponentOperation op) {
		return op instanceof SelfWake || op instanceof Method ||
				(op instanceof PortWake && isConnected((PortWake)op)) ; //PortWake is only synchronised if connected
	}

	private boolean isConnected(PortWake pw) {
		for (DataPacket r : pw.getReceives()){
			if (r.getConnector()!=null){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean dependenciesOK(EObject sourceElement, List<TranslationDescriptor> generatedElements) throws Exception{
		Machine machine = (Machine)((AbstractComponentOperation) sourceElement).getContaining(MachinePackage.Literals.MACHINE);
		Component root = (Component) ComponentsUtils.getRootComponent(sourceElement);
		timerEvent = (Event) Find.generatedElement(generatedElements,machine,events,Strings.TE_NAME(root));
		return timerEvent!=null;
	}
	
		
	@Override
	public List<TranslationDescriptor> fire(EObject sourceElement, List<TranslationDescriptor> generatedElements) throws Exception {
		assert(enabled(sourceElement));
		if (!dependenciesOK(sourceElement,generatedElements)) throw new Exception("rule fired before dependencies available (sourceElement:"+sourceElement+", rule:"+AutonomousOperationRule.class+")");
		AbstractComponentOperation op = (AbstractComponentOperation) sourceElement;
		List<TranslationDescriptor> ret = new ArrayList<TranslationDescriptor>();
		
		//find the machine 
		Machine machine = (Machine)op.getContaining(MachinePackage.Literals.MACHINE);
		
		//create the synch flag for this operation
		Event initialisation = (Event) Find.named(machine.getEvents(), "INITIALISATION");
		ret.add(Make.descriptor(machine,variables, Make.variable(Strings.OS_NAME(op), "synch flag for operation"),1));
		ret.add(Make.descriptor((Component)op.getContaining(ComponentsPackage.Literals.COMPONENT), allocatedVariables, Make.variableProxyReference(machine, Strings.OS_NAME(op)) , -10));
		if (sourceElement instanceof SelfWake){
			ret.add(Make.descriptor(machine,invariants,Make.invariant(Strings.OS_TYPE_NAME(op), Strings.OS_TYPE_PRED_SELFWAKE(op, ((SelfWake)sourceElement).getQueue()),""),5)); //lower pri as needs queue 
			ret.add(Make.descriptor(initialisation,actions,Make.action(Strings.OS_ACTION_NAME(op), Strings.OS_EMPTY_EXPR(op)),5));			
		}else{
			ret.add(Make.descriptor(machine,invariants,Make.invariant(Strings.OS_TYPE_NAME(op), Strings.OS_TYPE_PRED(op),""),1));
			ret.add(Make.descriptor(initialisation,actions,Make.action(Strings.OS_ACTION_NAME(op), Strings.OS_TRUE_EXPR(op)),1));
		}
		
		AbstractComponentOperation opa = op.getRefines();
		//  We may need a gluing invariant if the operation has been relocated into a sub-component 
		if (opa!=null && !Strings.OS_NAME(op).equals(Strings.OS_NAME(opa))){
			if (isSynchronised(opa)){
				ret.add(Make.descriptor(machine, invariants, Make.invariant(Strings.OS_REFREL_NAME(op), Strings.OS_REFREL_PRED(op,op.getRefines()), "generated refinement relation for 'done' flag - "+ op.getLabel()), 10));
			}	
		}
		
		
		//in all elaborated events of this op
		for (Event elaboratedEvent : op.getElaborates()){
			if (sourceElement instanceof SelfWake){
				//check/set operation synch 
				ret.add(Make.descriptor(elaboratedEvent,guards,Make.guard(Strings.OS_GUARD_NAME(op), Strings.SW_NOTDONE_PRED(op,((SelfWake)sourceElement).getQueue())),1));
				ret.add(Make.descriptor(elaboratedEvent,actions,Make.action(Strings.OS_ACTION_NAME(op), Strings.SW_DONE_EXPR(op,((SelfWake)sourceElement).getQueue())),1));				
			}else{
				//check/set operation synch 
				ret.add(Make.descriptor(elaboratedEvent,guards,Make.guard(Strings.OS_GUARD_NAME(op), Strings.OS_FALSE_PRED(op)),1));
				ret.add(Make.descriptor(elaboratedEvent,actions,Make.action(Strings.OS_ACTION_NAME(op), Strings.OS_TRUE_EXPR(op)),1));
			}
		
		}
		
		//finished
		return ret;
	}

}
