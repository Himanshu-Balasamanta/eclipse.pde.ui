package org.eclipse.pde.internal.ui.wizards.exports;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.pde.core.IModel;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.*;

/**
 * Insert the type's description here.
 * @see Wizard
 */
public abstract class BaseExportWizard extends Wizard implements IExportWizard {
	private IStructuredSelection selection;
	private BaseExportWizardPage page1;
	protected static PrintWriter writer;
	protected static File logFile;

	/**
	 * The constructor.
	 */
	public BaseExportWizard() {
		PDEPlugin.getDefault().getLabelProvider().connect(this);
		IDialogSettings masterSettings = PDEPlugin.getDefault().getDialogSettings();
		setNeedsProgressMonitor(true);
		setDialogSettings(getSettingsSection(masterSettings));
	}

	private static void createLogWriter() {
		try {
			String path =
				PDEPlugin
					.getDefault()
					.getStateLocation()
					.addTrailingSeparator()
					.toOSString();
			logFile = new File(path + "exportLog.txt");
			if (logFile.exists()) {
				logFile.delete();
				logFile.createNewFile();
			}
			writer = new PrintWriter(new FileWriter(logFile), true);
		} catch (IOException e) {
		}
	}

	public static PrintWriter getWriter() {
		if (writer == null)
			createLogWriter();
		return writer;
	}

	public void addPages() {
		page1 = createPage1();
		addPage(page1);
	}

	protected abstract BaseExportWizardPage createPage1();

	protected HashMap createProperties(String destination) {
		HashMap map = new HashMap(5);
		map.put("build.result.folder", destination + Path.SEPARATOR + "build_result");
		map.put("temp.folder", destination + Path.SEPARATOR + "temp");
		map.put("destination.temp.folder", destination + Path.SEPARATOR + "temp");
		map.put("plugin.destination", destination);
		map.put("feature.destination", destination);
		return map;
	}

	public void dispose() {
		PDEPlugin.getDefault().getLabelProvider().disconnect(this);
		super.dispose();
	}

	protected abstract void doExport(
		boolean exportZip,
		boolean exportSource,
		String destination,
		String zipFileName,
		IModel model,
		IProgressMonitor monitor);

	protected void doPerformFinish(
		boolean exportZip,
		boolean exportSource,
		String destination,
		String zipFileName,
		Object[] items,
		IProgressMonitor monitor)
		throws InvocationTargetException {
		File file = new File(destination);
		if (!file.exists() || !file.isDirectory())
			if (!file.mkdirs()) {
				throw new InvocationTargetException(
					new Exception(PDEPlugin.getResourceString("ExportWizard.badDirectory")));
			}

		monitor.beginTask("", items.length);
		ArrayList statusEntries = new ArrayList();
		for (int i = 0; i < items.length; i++) {
			IModel model = (IModel) items[i];
			doExport(
				exportZip,
				exportSource,
				destination,
				zipFileName,
				model,
				new SubProgressMonitor(monitor, 1));
		}
	}

	public IStructuredSelection getSelection() {
		return selection;
	}

	protected abstract IDialogSettings getSettingsSection(IDialogSettings masterSettings);

	/**
	 * @see Wizard#init
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	/**
	 * @see Wizard#performFinish
	 */
	public boolean performFinish() {
		page1.saveSettings();
		final boolean exportZip = page1.getExportZip();
		final boolean exportSource = page1.getExportSource();
		final String destination = page1.getDestination();
		final String zipFileName = page1.getFileName();
		final Object[] items = page1.getSelectedItems();
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException{
				doPerformFinish(exportZip, exportSource, destination, zipFileName, items, monitor);
				zipAll(zipFileName, createProperties(destination), monitor);
			}
		};
		try {
			createLogWriter();
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			return false;
		} finally {
			if (writer != null)
				writer.close();
		}

			
		if (logFile != null && logFile.exists() && logFile.length() > 0) {
			if (MessageDialog
				.openQuestion(
					getContainer().getShell(),
					getWindowTitle(),
					PDEPlugin.getResourceString("ExportWizard.error.message")))
				Program.launch(logFile.getAbsolutePath());
			return false;
		}

		return true;
	}
	
	protected abstract void zipAll(String filename, HashMap properties, IProgressMonitor monitor);

}