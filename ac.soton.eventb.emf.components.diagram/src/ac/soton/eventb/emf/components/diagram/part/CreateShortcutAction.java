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
package ac.soton.eventb.emf.components.diagram.part;

import java.util.List;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.CreateCommand;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequest;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.gmf.tooling.runtime.LogHelper;
import org.eclipse.gmf.tooling.runtime.part.DefaultCreateShortcutHandler;
import org.eclipse.gmf.tooling.runtime.part.DefaultElementChooserDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import ac.soton.eventb.emf.components.diagram.edit.commands.ComponentsCreateShortcutDecorationsCommand;

/**
 * @generated
 */
public class CreateShortcutAction extends DefaultCreateShortcutHandler {
	/**
	 * @generated
	 */
	public CreateShortcutAction() {
		this(ComponentsDiagramEditorPlugin.getInstance().getLogHelper());
	}

	/**
	 * @generated
	 */
	public CreateShortcutAction(LogHelper logHelper) {
		super(logHelper, ComponentsDiagramEditorPlugin.DIAGRAM_PREFERENCES_HINT);
	}

	/**
	 * @generated
	 */
	@Override
	public DefaultElementChooserDialog createChooserDialog(Shell parentShell,
			View view) {
		return new ComponentsElementChooserDialog(parentShell, view);
	}

	/**
	 * @generated
	 */
	@Override
	public ICommand createShortcutDecorationCommand(View view,
			TransactionalEditingDomain editingDomain,
			List<CreateViewRequest.ViewDescriptor> descriptors) {
		return new ComponentsCreateShortcutDecorationsCommand(editingDomain,
				view, descriptors);
	}

}
