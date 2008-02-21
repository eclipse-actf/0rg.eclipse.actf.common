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
package org.eclipse.actf.model.dom.odf.style.impl;

import org.eclipse.actf.model.dom.odf.base.ODFDocument;
import org.eclipse.actf.model.dom.odf.base.impl.ODFElementImpl;
import org.eclipse.actf.model.dom.odf.style.PageLayoutElement;
import org.eclipse.actf.model.dom.odf.style.StyleConstants;
import org.w3c.dom.Element;


class PageLayoutElementImpl extends ODFElementImpl implements PageLayoutElement {
	private static final long serialVersionUID = 2353952707698473250L;

	protected PageLayoutElementImpl(ODFDocument odfDoc, Element element) {
		super(odfDoc, element);
	}

	public String getAttrStyleName() {
		if (hasAttributeNS(StyleConstants.STYLE_NAMESPACE_URI,
				StyleConstants.ATTR_NAME))
			return getAttributeNS(StyleConstants.STYLE_NAMESPACE_URI,
					StyleConstants.ATTR_NAME);
		return null;
	}
}