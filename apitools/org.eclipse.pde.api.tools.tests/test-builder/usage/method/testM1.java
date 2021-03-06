/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
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
package x.y.z;

import m.MethodUsageClass;

/**
 * 
 */
public class testM1 extends MethodUsageClass {
	
	public static class inner {
		/**
		 * Constructor
		 */
		public inner() {
			MethodUsageClass c = new MethodUsageClass();
			c.m1();
			c.m3();
		}
	}
	
	class inner2 {
		/**
		 * Constructor
		 */
		public inner2() {
			MethodUsageClass c = new MethodUsageClass();
			c.m1();
			c.m3();
		}
	}
}

class outer {
	/**
	 * Constructor
	 */
	public outer() {
		MethodUsageClass c = new MethodUsageClass();
		c.m1();
		c.m3();
	}
}
