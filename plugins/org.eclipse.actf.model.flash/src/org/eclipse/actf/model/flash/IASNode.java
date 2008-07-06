/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.flash;

import java.util.Set;

public interface IASNode {

	public abstract String getType();

	public abstract String getClassName();

	public abstract String getObjectName();

	public abstract String getTarget();

	public abstract boolean isUIComponent();

	public abstract String getValue();

	public abstract String getText();

	public abstract String getText(boolean useAccName);

	public abstract String getTitle();

	public abstract Object getObject(String name);

	public abstract IASNode getParent();

	public abstract int getLevel();

	public abstract boolean hasChild(boolean visual);

	public abstract boolean hasChild(boolean visual, boolean debugMode);

	public abstract IASNode[] getChildren(boolean visual, boolean informative);

	public abstract IASNode[] getChildren(boolean visual, boolean informative,
			boolean debugMode);

	public abstract boolean setMarker();

	public abstract IFlashPlayer getPlayer();

	public abstract ASAccInfo getAccInfo();

	public abstract Set<String> getKeys();

	public abstract boolean hasOnRelease();

	public abstract double getX();

	public abstract double getY();

	public abstract double getWidth();

	public abstract double getHeight();

	public abstract int getId();

	public abstract int getDepth();

	public abstract int getCurrentFrame();

	public abstract int getTabIndex();

	public abstract boolean isInputable();

	public abstract boolean isOpaqueObject();

}