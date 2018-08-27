/*******************************************************************************
 * Copyright (c) Aug 20, 2013 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

/**
 *
 */
public class test15 {

	/**
	 * @noreference This annotation is not intended to be referenced by clients.
	 */
	private @interface inner1 {
		
		/**
		 * @noreference
		 */
		@interface inner2 {
			
		}
	}
}
