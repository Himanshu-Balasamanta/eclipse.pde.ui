package org.eclipse.pde.internal.ui.wizards.templates;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.ui.templates.*;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.core.plugin.*;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.jface.wizard.*;
import org.eclipse.pde.ui.IPluginStructureData;

public class PreferencePageTemplate extends PDETemplateSection {
	private static final String NL_TITLE = "PreferencePageTemplate.title";
	private static final String NL_DESC = "PreferencePageTemplate.desc";
	private static final String NL_PACKAGE_NAME =
		"PreferencePageTemplate.packageName";
	private static final String NL_CLASS_NAME = "PreferencePageTemplate.className";
	private static final String NL_PAGE_NAME = "PreferencePageTemplate.pageName";
	private static final String NL_DEFAULT_PAGE_NAME =
		"PreferencePageTemplate.defaultPageName";
	private String mainClassName;

	public PreferencePageTemplate() {
		setPageCount(1);
		createOptions();
	}

	public String getSectionId() {
		return "preferences";
	}
	/*
	 * @see ITemplateSection#getNumberOfWorkUnits()
	 */
	public int getNumberOfWorkUnits() {
		return super.getNumberOfWorkUnits() + 1;
	}

	private void createOptions() {
		// first page
		addOption(
			KEY_PACKAGE_NAME,
			PDEPlugin.getResourceString(NL_PACKAGE_NAME),
			(String) null,
			0);
		addOption(
			"pageClassName",
			PDEPlugin.getResourceString(NL_CLASS_NAME),
			"SamplePreferencePage",
			0);
		addOption(
			"pageName",
			PDEPlugin.getResourceString(NL_PAGE_NAME),
			PDEPlugin.getResourceString(NL_DEFAULT_PAGE_NAME),
			0);
	}

	protected void initializeFields(IPluginStructureData sdata, IFieldData data) {
		// In a new project wizard, we don't know this yet - the
		// model has not been created
		String pluginId = sdata.getPluginId();
		initializeOption(KEY_PACKAGE_NAME, pluginId + ".preferences");
		mainClassName = data.getClassName();
	}
	public void initializeFields(IPluginModelBase model) {
		// In the new extension wizard, the model exists so 
		// we can initialize directly from it
		String pluginId = model.getPluginBase().getId();
		initializeOption(KEY_PACKAGE_NAME, pluginId + ".preferences");
		if (model instanceof IPluginModel) {
			IPlugin plugin = (IPlugin) model.getPluginBase();
			mainClassName = plugin.getClassName();
		}
	}

	public String getReplacementString(String fileName, String key) {
		if (key.equals("fullPluginClassName"))
			return mainClassName;
		if (key.equals("pluginClassName"))
			return getPluginClassName();
		return super.getReplacementString(fileName, key);
	}

	private String getPluginClassName() {
		int dot = mainClassName.lastIndexOf('.');
		if (dot != -1) {
			return mainClassName.substring(dot + 1);
		}
		return mainClassName;
	}

	public boolean isDependentOnFirstPage() {
		return true;
	}

	public void addPages(Wizard wizard) {
		WizardPage page = createPage(0);
		page.setTitle(PDEPlugin.getResourceString(NL_TITLE));
		page.setDescription(PDEPlugin.getResourceString(NL_DESC));
		wizard.addPage(page);
		markPagesAdded();
	}

	public void validateOptions(TemplateOption source) {
		if (source.isRequired() && source.isEmpty()) {
			flagMissingRequiredOption(source);
		} else
			resetPageState();
	}

	public String getUsedExtensionPoint() {
		return "org.eclipse.ui.preferencePages";
	}

	protected void updateModel(IProgressMonitor monitor) throws CoreException {
		IPluginBase plugin = model.getPluginBase();
		IPluginExtension extension = createExtension(getUsedExtensionPoint(), true);
		IPluginModelFactory factory = model.getFactory();

		String fullClassName =
			getStringOption(KEY_PACKAGE_NAME) + "." + getStringOption("pageClassName");

		IPluginElement pageElement = factory.createElement(extension);
		pageElement.setName("page");
		pageElement.setAttribute("id", fullClassName);
		pageElement.setAttribute("name", getStringOption("pageName"));
		pageElement.setAttribute("class", fullClassName);
		extension.add(pageElement);
		if (!extension.isInTheModel())
			plugin.add(extension);
	}
}