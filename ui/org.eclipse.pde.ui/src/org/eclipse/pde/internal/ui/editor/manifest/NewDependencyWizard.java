package org.eclipse.pde.internal.ui.editor.manifest;
/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */

import org.eclipse.jface.wizard.*;
import org.eclipse.pde.internal.ui.*;
import org.eclipse.pde.core.plugin.*;

public class NewDependencyWizard extends Wizard {
	private IPluginModelBase modelBase;
	private NewDependencyWizardPage mainPage;
	private static final String KEY_WTITLE = "ManifestEditor.ImportListSection.new.wtitle";

public NewDependencyWizard(IPluginModelBase modelBase) {
	this.modelBase = modelBase;
	setWindowTitle(PDEPlugin.getResourceString(KEY_WTITLE));
	setDefaultPageImageDescriptor(PDEPluginImages.DESC_NEWPPRJ_WIZ);
	setDialogSettings(PDEPlugin.getDefault().getDialogSettings());
}

public void addPages() {
	mainPage = new NewDependencyWizardPage(modelBase);
	addPage(mainPage);
}

public boolean performFinish() {
	return mainPage.finish();
}

}
