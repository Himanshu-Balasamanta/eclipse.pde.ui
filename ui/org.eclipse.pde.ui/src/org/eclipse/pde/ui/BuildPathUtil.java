package org.eclipse.pde.ui;
/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.pde.internal.core.*;

/**
 * A utility class that can be used by plug-in project
 * wizards to set up the Java build path. The actual
 * entries of the build path are not known in the
 * master wizard. The client wizards need to
 * add these entries depending on the code they
 * generate and the plug-ins they need to reference.
 * This class is typically used from within
 * a plug-in content wizard.
 * <p>
 * <b>Note:</b> This class is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 * </p>
 */
public class BuildPathUtil extends BuildPathUtilCore {
	/**
	 * The default constructor.
	 * <p>
	 * <b>Note:</b> This field is part of an interim API that is still under development and expected to
	 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
	 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
	 * (repeatedly) as the API evolves.
	 * </p>
	 */
	public BuildPathUtil() {
		super();
	}

	/**
	 * Sets the Java build path of the project
	 * using plug-in structure data and
	 * provided entries. These entries are
	 * created in plug-in content wizards
	 * based on the plug-ins required by
	 * the generated code.
	 * @param project the plug-in project handle
	 * @param data structure data passed in by the master wizard
	 * @param libraries an array of the library entries to be set
	 * @param monitor for reporting progress
	 * <p>
	 * <b>Note:</b> This method is part of an interim API that is still under development and expected to
	 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
	 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
	 * (repeatedly) as the API evolves.
	 * </p>
	 */
	public static void setBuildPath(
		IProject project,
		IPluginStructureData data,
		IClasspathEntry[] libraries,
		IProgressMonitor monitor)
		throws JavaModelException, CoreException {

		// Set output folder
		IJavaProject javaProject = JavaCore.create(project);
		IPath path = project.getFullPath().append(data.getJavaBuildFolderName());
		javaProject.setOutputLocation(path, monitor);

		// Set classpath
		Vector result = new Vector();
		// Source folder first
		addSourceFolder(data.getSourceFolderName(), project, result);
		// Then the libraries
		for (int i = 0; i < libraries.length; i++) {
			result.add(libraries[i]);
		}
		// add implicit libraries
		addImplicitLibraries(result, true, data.getPluginId());
		// JRE the last
		addJRE(result);
		IClasspathEntry[] entries = new IClasspathEntry[result.size()];
		result.copyInto(entries);
		javaProject.setRawClasspath(entries, monitor);
	}

}