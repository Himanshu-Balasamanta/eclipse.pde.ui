package org.eclipse.pde.internal.ui.model;

import org.eclipse.pde.core.*;

public interface IDocumentKey extends IWritable {
	void setName(String name);
	String getName();
	
	void setOffset(int offset);
	int getOffset();
	
	void setLength(int length);
	int getLength();
	
	String write();
	
}
