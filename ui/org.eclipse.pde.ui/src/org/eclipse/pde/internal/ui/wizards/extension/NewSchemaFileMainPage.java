package org.eclipse.pde.internal.ui.wizards.extension;
/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */

import java.lang.reflect.*;
import org.eclipse.pde.internal.ui.*;
import org.eclipse.jface.operation.*;
import org.eclipse.core.resources.*;

public class NewSchemaFileMainPage extends BaseExtensionPointMainPage {
	public static final String KEY_TITLE = "NewSchemaFileWizard.title";
	public static final String KEY_DESC = "NewSchemaFileWizard.desc";

public NewSchemaFileMainPage(IContainer container) {
	super(container);
	setTitle(PDEPlugin.getResourceString(KEY_TITLE));
	setDescription(PDEPlugin.getResourceString(KEY_DESC));
}
public boolean finish() {
	IRunnableWithProgress operation = getOperation();
	try {
		getContainer().run(false, true, operation);
	} catch (InvocationTargetException e) {
		PDEPlugin.logException(e);
		return false;
	} catch (InterruptedException e) {
		return false;
	}
	return true;
}
protected boolean isPluginIdNeeded() {
	return true;
}
}
