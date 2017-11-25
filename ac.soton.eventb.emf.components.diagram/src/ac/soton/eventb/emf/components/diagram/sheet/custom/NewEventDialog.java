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
package ac.soton.eventb.emf.components.diagram.sheet.custom;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eventb.emf.core.machine.Event;
import org.eventb.emf.core.machine.MachineFactory;

/**
 * New event creation dialog.
 * 
 * @author vitaly
 *
 */
public class NewEventDialog extends Dialog {

	private Text nameText;
	private Text commentText;
	private DecoratedInputValidator nameValidator;
	private boolean nameValid;
	private Event event;
	private String initialName = null;
	private Set<String> conflictingNames = null;

	/**
	 * @param parentShell
	 */
	protected NewEventDialog(Shell parentShell) {
		super(parentShell);
	}
	
	/**
	 * @param parentShell
	 * @param events already existing events, to resolve name conflicts, or null if not required
	 * @param name proposed initial name for event, or null
	 */
	protected NewEventDialog(Shell parentShell, List<Event> events, String name) {
		super(parentShell);
		
		// event's proposed name
		if (name != null)
			initialName = name;
		
		// build up a set of conflicting names
		if (events != null) {
			conflictingNames = new HashSet<String>(events.size());
			for (Event e : events)
				conflictingNames.add(e.getName());
		}
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Add New Event");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		Group group = new Group(composite, SWT.SHADOW_ETCHED_IN);
		group.setText("Event");
		GridLayout layout = new GridLayout(2, false);
		group.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 300;
		group.setLayoutData(gd);
		
		// name
		Label nameLabel = new Label(group, SWT.NONE);
		nameLabel.setText("Name:");
		nameText = new Text(group, SWT.SINGLE | SWT.BORDER);
		nameText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		nameText.setFont(PropertySectionUtil.rodinFont);
		nameText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validateName();
            }
        });
		
		// comment
		Label commentLabel = new Label(group, SWT.NONE);
		commentLabel.setText("Comment:");
		commentText = new Text(group, SWT.SINGLE | SWT.BORDER);
		commentText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		commentText.setFont(PropertySectionUtil.rodinFont);
		
		// validator
		nameValidator = new DecoratedInputValidator(PropertySectionUtil.createDecorator(nameText, "Please enter name", FieldDecorationRegistry.DEC_ERROR, false)) {
			
			@Override
			public String isValidInput(String name) {
				if (name == null || name.trim().isEmpty())
					return "Name cannot be empty";
				if (conflictingNames != null && conflictingNames.contains(name.trim()))
					return "Event with such name already exists";
				return null;
			}
		};
		
		// initial name
		if (initialName != null)
			nameText.setText(initialName);
		
		return composite;
	}

	@Override
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		// only here the buttons are available, so that's where OK can be disabled for initial state
		update();
		return contents;
	}

	/**
	 * Validates name.
	 */
	protected void validateName() {
		String errorMessage = null;
        if (nameValidator != null) {
            errorMessage = nameValidator.isValid(nameText.getText());
            nameValid = errorMessage == null;
        }
		update();
	}

	private void update() {
		Control button = getButton(IDialogConstants.OK_ID);
		if (button != null) {
			button.setEnabled(nameValid);
		}
	}
	
	/**
	 * Returns event created by this dialog.
	 * 
	 * @return new event
	 */
	public Event getEvent() {
		return event;
	}

	@Override
	protected void okPressed() {
		event = MachineFactory.eINSTANCE.createEvent();
		event.setName(nameText.getText().trim());
		event.setComment(commentText.getText());
		super.okPressed();
	}
	
//	private void annotate(EventBObject element) {
//		Annotation rodinInternals = CoreFactory.eINSTANCE.createAnnotation();
//		rodinInternals.setSource(PersistencePlugin.SOURCE_RODIN_INTERNAL_ANNOTATION);
//		element.getAnnotations().add(rodinInternals);
//	}

}
