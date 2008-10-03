/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.ui.editor.actions;

import org.eclipse.actf.model.ui.util.ModelServiceMessages;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * MenuManager for text size control
 */
public class TextSizeMenu extends MenuManager {

	private static final String[] MENUTEXTS = {
			ModelServiceMessages.getString("MenuConst.Largest_3"),
			ModelServiceMessages.getString("MenuConst.Large_4"),
			ModelServiceMessages.getString("MenuConst.Medium_5"),
			ModelServiceMessages.getString("MenuConst.Small_6"),
			ModelServiceMessages.getString("MenuConst.Smallest_7") };

	/**
	 * Constructor of the class
	 * 
	 * @param window
	 *            target {@link IWorkbenchWindow}
	 */
	public TextSizeMenu(IWorkbenchWindow window) {
		super(ModelServiceMessages.getString("MenuConst.&Font_2"));

		int menuNum = 5;
		TextSizeAction[] displayTextSizeAction = new TextSizeAction[menuNum];

		int fontSize = 5;
		for (int i = 0; i < menuNum; i++) {
			displayTextSizeAction[i] = new TextSizeAction(window);
			displayTextSizeAction[i].setText(MENUTEXTS[i]);
			add(displayTextSizeAction[i]);
			fontSize--;
			displayTextSizeAction[i].setFontSize(fontSize);
			// if(currFontSize == fontSize){
			// }
		}
	}
}
