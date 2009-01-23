/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tatsuya ISHIHARA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.dom.odf.text.impl;

import java.util.Iterator;

import org.eclipse.actf.model.dom.odf.base.ODFDocument;
import org.eclipse.actf.model.dom.odf.base.impl.ODFElementImpl;
import org.eclipse.actf.model.dom.odf.range.ITextElementContainer;
import org.eclipse.actf.model.dom.odf.text.AlphabeticalIndexElement;
import org.w3c.dom.Element;


class AlphabeticalIndexElementImpl extends ODFElementImpl implements
		AlphabeticalIndexElement {
	private static final long serialVersionUID = 2575148179105155904L;

	protected AlphabeticalIndexElementImpl(ODFDocument odfDoc, Element element) {
		super(odfDoc, element);
	}

	public long getContentSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Iterator<ITextElementContainer> getChildIterator() {
		// TODO Auto-generated method stub
		return null;
	}
}