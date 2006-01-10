package org.eclipse.pde.internal.ui.editor.target;

import java.util.TreeSet;

import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.jdt.launching.environments.IExecutionEnvironmentsManager;
import org.eclipse.pde.internal.core.itarget.IRuntimeInfo;
import org.eclipse.pde.internal.core.itarget.ITarget;
import org.eclipse.pde.internal.core.itarget.ITargetModel;
import org.eclipse.pde.internal.ui.PDEUIMessages;
import org.eclipse.pde.internal.ui.editor.PDEFormPage;
import org.eclipse.pde.internal.ui.editor.PDESection;
import org.eclipse.pde.internal.ui.launcher.VMHelper;
import org.eclipse.pde.internal.ui.parts.ComboPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class JRESection extends PDESection {
	
	private Button fDefaultJREButton;
	private Button fNamedJREButton;
	private Button fExecEnvButton;
	private ComboPart fNamedJREsCombo;
	private ComboPart fExecEnvsCombo;
	private TreeSet fExecEnvChoices;

	public JRESection(PDEFormPage page, Composite parent) {
		super(page, parent, Section.DESCRIPTION);
		createClient(getSection(), page.getEditor().getToolkit());
	}

	protected void createClient(Section section, FormToolkit toolkit) {
		section.setText(PDEUIMessages.JRESection_title);
		section.setDescription(PDEUIMessages.JRESection_description);
		section.setLayout(new GridLayout());
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.verticalAlignment = SWT.TOP;
		data.horizontalSpan = 2;
		section.setLayoutData(data);

		Composite client = toolkit.createComposite(section);
		client.setLayout(new GridLayout(2, false));
		
		initializeValues();
		IRuntimeInfo info = getRuntimeInfo();
		int jreType = info.getJREType();
		String jreName = info.getJREName();
		
		fDefaultJREButton = toolkit.createButton(client, PDEUIMessages.JRESection_defaultJRE, SWT.RADIO);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		fDefaultJREButton.setLayoutData(gd);
		if (jreType == IRuntimeInfo.TYPE_DEFAULT)
			fDefaultJREButton.setSelection(true);
		fDefaultJREButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateWidgets();
				getRuntimeInfo().setDefaultJRE();
			}
		});
		
		fNamedJREButton = toolkit.createButton(client, PDEUIMessages.JRESection_JREName, SWT.RADIO);
		fNamedJREButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateWidgets();
				getRuntimeInfo().setNamedJRE(getText(fNamedJREsCombo));
			}
		});
		
		fNamedJREsCombo = new ComboPart();
		fNamedJREsCombo.createControl(client, toolkit, SWT.SINGLE | SWT.BORDER);
		fNamedJREsCombo.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		String[] installs = VMHelper.getVMInstallNames();
		fNamedJREsCombo.setItems(installs);
		if (jreType == IRuntimeInfo.TYPE_NAMED) {
			if (fNamedJREsCombo.indexOf(jreName) < 0 )
				fNamedJREsCombo.add(jreName);
			fNamedJREsCombo.setText(jreName);
			fNamedJREButton.setSelection(true);
		} else if (installs.length > 0) {
			fNamedJREsCombo.setText(installs[0]);
		}
		fNamedJREsCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				getRuntimeInfo().setNamedJRE(getText(fNamedJREsCombo));
			}
		});
		
		fExecEnvButton = toolkit.createButton(client, PDEUIMessages.JRESection_ExecutionEnv, SWT.RADIO);
		fExecEnvButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateWidgets();
				getRuntimeInfo().setExecutionEnvJRE(getText(fExecEnvsCombo));
			}
		});
		
		fExecEnvsCombo = new ComboPart();
		fExecEnvsCombo.createControl(client, toolkit, SWT.SINGLE | SWT.BORDER );
		fExecEnvsCombo.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fExecEnvsCombo.setItems((String[])fExecEnvChoices.toArray(new String[fExecEnvChoices.size()]));
		if (jreType == IRuntimeInfo.TYPE_EXECUTION_ENV) {
			if (fExecEnvsCombo.indexOf(jreName) < 0)
				fExecEnvsCombo.add(jreName);
			fExecEnvButton.setSelection(true);
			fExecEnvsCombo.setText(jreName);
		} else if (!fExecEnvChoices.isEmpty()){
			fExecEnvsCombo.setText(fExecEnvChoices.first().toString());
		}
		
		fExecEnvsCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				getRuntimeInfo().setExecutionEnvJRE(getText(fExecEnvsCombo));
			}
		});
		
		updateWidgets();
		section.setClient(client);
	}
	
	protected void initializeValues() {
		fExecEnvChoices = new TreeSet();
		IExecutionEnvironmentsManager manager = JavaRuntime.getExecutionEnvironmentsManager();
		IExecutionEnvironment[] envs = manager.getExecutionEnvironments();
		for (int i = 0; i < envs.length; i++)
			fExecEnvChoices.add(envs[i].getId()); 
	}
	
	protected void updateWidgets() {
		fNamedJREsCombo.setEnabled(fNamedJREButton.getSelection());
		fExecEnvsCombo.setEnabled(fExecEnvButton.getSelection());
	}
	
	private IRuntimeInfo getRuntimeInfo() {
		IRuntimeInfo info = getTarget().getTargetJREInfo();
		if (info == null) {
			info = getModel().getFactory().createJREInfo();
			getTarget().setTargetJREInfo(info);
		}
		return info;
	}
	
	private ITarget getTarget() {
		return getModel().getTarget();
	}
	
	private ITargetModel getModel() {
		return (ITargetModel)getPage().getPDEEditor().getAggregateModel();
	}
	
	private String getText(ComboPart combo) {
		Control control = combo.getControl();
		if (control instanceof Combo)
			return ((Combo) control).getText();
		return ((CCombo) control).getText();
	}

}
