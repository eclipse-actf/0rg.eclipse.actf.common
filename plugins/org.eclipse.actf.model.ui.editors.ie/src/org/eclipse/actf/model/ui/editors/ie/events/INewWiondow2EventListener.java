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

package org.eclipse.actf.model.ui.editors.ie.events;

import java.util.EventListener;

import org.eclipse.actf.model.ui.editors.ie.internal.events.NewWindow2Parameters;




public interface INewWiondow2EventListener extends EventListener {

    //TODO param
    public void newWindow2(NewWindow2Parameters param);

}
