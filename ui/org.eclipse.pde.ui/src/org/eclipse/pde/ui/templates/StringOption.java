/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.ui.templates;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

/**
 * 
 * TODO add javadoc
 */
public class StringOption extends TemplateOption {
	private Text text;
	private Label labelControl;
	private boolean ignoreListener;

	/**
	 * The constructor.
	 * @param section the parent section
	 * @param name the unique option name
	 * @param label the translatable label of the option
	 */
	public StringOption(BaseOptionTemplateSection section, String name, String label) {
		super(section, name, label);
		setRequired(true);
	}
	/**
	 * A utility version of the <samp>getValue()</samp> method that
	 * converts the current value into the String object.
	 * @return the string version of the current value.
	 */
	public String getText() {
		if (getValue() != null)
			return getValue().toString();
		return null;
	}
	/**
	 * A utility version of the <samp>setValue</samp> method that
	 * accepts String objects.
	 * @param newText the new text value of the option
	 * @see #setValue(Object)
	 */
	public void setText(String newText) {
		setValue(newText);
		if (text != null)
			text.setText(newText);
	}
	/**
	 * Implements the superclass method by passing the
	 * string value of the new value to the widget
	 * @param value the new option value
	 */
	public void setValue(Object value) {
		super.setValue(value);
		if (text != null) {
			ignoreListener = true;
			String textValue = getText();
			text.setText(textValue != null ? textValue : "");
			ignoreListener = false;
		}
	}
	/**
	 * Creates the string option control.
	 * @param parent parent composite of the string option widget
	 * @param span the number of columns that the widget should span
	 */
	public void createControl(Composite parent, int span) {
		labelControl = createLabel(parent, 1);
		labelControl.setEnabled(isEnabled());
		text = new Text(parent, SWT.SINGLE | SWT.BORDER);
		if (getValue() != null)
			text.setText(getValue().toString());
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = span - 1;
		text.setLayoutData(gd);
		text.setEnabled(isEnabled());
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (ignoreListener)
					return;
				StringOption.super.setValue(text.getText());
				getSection().validateOptions(StringOption.this);
			}
		});
	}
	/**
	 * A string option is empty if its text field contains no text.
	 * @return true if there is no text in the text field.
	 */
	public boolean isEmpty() {
		return getValue() == null || getValue().toString().length() == 0;
	}
	/**
	 * Implements the superclass method by passing the enabled state
	 * to the option's widget.
	 * 
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (labelControl != null) {
			labelControl.setEnabled(enabled);
			text.setEnabled(enabled);
		}
	}
}
